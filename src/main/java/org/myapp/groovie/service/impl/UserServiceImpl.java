package org.myapp.groovie.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AccountDtoIn;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.PersonalDetailRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    private final PasswordEncoder passwordEncoder;

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
            Set<UUID> groupIds = userDtoIn.getGroupIds()
                    .parallelStream()
                    .map(UUID::fromString)
                    .collect(Collectors.toSet());

            User userCheck = userRepository.getUserByUsername(username);

            if(userCheck != null){
                throw new ApiCallException("User with username: " + username + " already existsted", HttpStatus.NOT_FOUND);
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
                    .personalDetail(personalDetail)
                    .build();

            //update relations
            newUser.addToGroups(groups);

            personalDetailRepository.save(personalDetail);
//        groupRepository.save(group);

            return userRepository.save(newUser);
        }

}
