package org.myapp.groovie.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AccountDtoIn;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.dto.out.PersonalDetailDtoOut;
import org.myapp.groovie.dto.out.UserDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.PersonalDetailRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.IUserService;
import org.myapp.groovie.utility.specification.EntitySpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PersonalDetailRepository personalDetailRepository;

    private final IGroupService groupService;
    private final IS3Service s3Service;

    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Value("${spring.data.aws.s3.user-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.user-cover-route}")
    private String coverRoute;

    @Override
    public List<User> getAllUsers() throws ApiCallException{
        return userRepository.findAll();
    }

    @Override
    public User getOneById(UUID userId) throws ApiCallException {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            return user.get();
        }
        throw new ApiCallException("No user with id:" + userId, HttpStatus.NOT_FOUND);
    }

    @Override
    public User create(UserDtoIn userDtoIn) throws ApiCallException {
        String username = userDtoIn.getUsername();
        String password = passwordEncoder.encode(userDtoIn.getPassword());
        String email = userDtoIn.getEmail();

        Set<UUID> groupIds = userDtoIn.getGroupIds()
                .parallelStream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        User userCheckName = userRepository.getUserByUsername(username);
        User userCheckMail = userRepository.getUserByEmail(email);

        if(userCheckName != null){
            throw new ApiCallException("User with username: " + username + " already existed", HttpStatus.NOT_FOUND);
        }
        if(userCheckMail != null){
            throw new ApiCallException("User with email: " + email + " already existed", HttpStatus.NOT_FOUND);
        }

        Set<Group> groups = groupService.getGroupsByIds(groupIds);
        PersonalDetail personalDetail = PersonalDetail.builder()
                .uuid(UUID.randomUUID())
                .name(username)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        //save new user
        User newUser = User.builder()
                .uuid(UUID.randomUUID())
                .username(username)
                .password(password)
                .email(email)
                .personalDetail(personalDetail)
                .build();

        //update relations
        newUser.addToGroups(groups);

        personalDetailRepository.save(personalDetail);
//        groupRepository.save(group);

        return userRepository.save(newUser);
        }

    @Override
    public User getOneByUsername(String username) throws ApiCallException {
        User user = userRepository.getUserByUsername(username);
        if(user != null){
            return user;
        }
        throw new ApiCallException("User with username: " + username + " doesn't exists", HttpStatus.NOT_FOUND);
    }

    @Override
    public Page<UserDtoOut> searchUser(String userInfoName, String role, int pageNumber, int pageSize) throws ApiCallException {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        String roleToSearch = null;
        if (role != null && !role.isBlank()) {
            try {
                roleToSearch = Role.valueOf(role.toUpperCase().trim()).name();
            } catch (IllegalArgumentException e) {
                throw new ApiCallException("Invalid role: " + role, HttpStatus.BAD_REQUEST);
            }
        }

        Page<PersonalDetail> personalDetailPage = personalDetailRepository.findByDisplayNameAndRole(
                userInfoName,
                roleToSearch,
                pageable
        );

        if (!personalDetailPage.isEmpty()) {
            List<UserDtoOut> userDtoOutList = personalDetailPage.getContent()
                    .stream()
                    .map(upd -> {
                        String userCoverId = coverRoute + "/" + upd.getUser().getUuid() + ".jpeg";
                        return UserDtoOut.fromUser(upd.getUser(), s3Service.getPresignedUrl(bucketName, userCoverId));
                    })
                    .toList();

            return new PageImpl<>(userDtoOutList, pageable, personalDetailPage.getTotalElements());
        }

        throw new ApiCallException("No user found", HttpStatus.NOT_FOUND);
    }

    @Override
    @Transactional
    public String deleteUser(UUID userUuid) throws ApiCallException {
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new ApiCallException("User not found", HttpStatus.NOT_FOUND));

        UUID pdUuid = user.getPersonalDetail().getUuid();

        entityManager.createNativeQuery("""
      DELETE FROM song_specs
      WHERE uuid IN (
        SELECT ss.uuid
          FROM song_specs ss
          JOIN songs s ON ss.song_id = s.uuid
          JOIN albums a ON s.album_uuid = a.uuid
         WHERE a.user_uuid = :userUuid
      )
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
      DELETE FROM song_playlist_relations
      WHERE playlist_uuid IN (
        SELECT p.uuid FROM playlists p WHERE p.user_uuid = :userUuid
      )
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
    DELETE FROM song_genre_relations
    WHERE song_uuid IN (
      SELECT s.uuid
        FROM songs s
        JOIN albums a ON s.album_uuid = a.uuid
       WHERE a.user_uuid = :userUuid
    )
""")
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
      DELETE FROM songs
      WHERE album_uuid IN (
        SELECT a.uuid FROM albums a WHERE a.user_uuid = :userUuid
      )
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
      DELETE FROM albums WHERE user_uuid = :userUuid
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
      DELETE FROM playlists WHERE user_uuid = :userUuid
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
      DELETE FROM user_group
      WHERE user_uuid = :userUuid
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
        DELETE FROM users WHERE uuid = :userUuid
    """)
                .setParameter("userUuid", userUuid)
                .executeUpdate();

        entityManager.createNativeQuery("""
        DELETE FROM personal_details WHERE uuid = :pdUuid
    """)
                .setParameter("pdUuid", pdUuid)
                .executeUpdate();

        return "Deleted User Successfully";
    }

}
