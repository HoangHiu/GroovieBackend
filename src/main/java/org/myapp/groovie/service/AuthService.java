package org.myapp.groovie.service;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AccountDtoIn;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.PersonalDetailRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PersonalDetailRepository personalDetailRepository;

    private final JwtService jwtService;
    private final IGroupService groupService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String authenticate(AccountDtoIn accountDtoIn) throws ApiCallException {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(accountDtoIn.getUsername(), accountDtoIn.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(accountDtoIn.getUsername());
            } else {
                throw new ApiCallException("No user found!!!", HttpStatus.BAD_REQUEST);
            }
        } catch (AuthenticationException e) {
            throw new ApiCallException("Invalid username or password!", HttpStatus.BAD_REQUEST);
        }
    }

    public User register(AccountDtoIn accountDtoIn) throws ApiCallException{
        String username = accountDtoIn.getUsername();
        User userCheck = userRepository.getUserByUsername(username);

        if(userCheck != null){
            throw new ApiCallException("User with username: " + username + " already existsted", HttpStatus.NOT_FOUND);
        }

        Group group = groupService.getGroupByRole(Role.REGULAR);
        PersonalDetail personalDetail = PersonalDetail.builder()
                .uuid(UUID.randomUUID())
                .name(accountDtoIn.getUsername())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        //save new user
        User newUser = User.builder()
                .uuid(UUID.randomUUID())
                .username(accountDtoIn.getUsername())
                .password(passwordEncoder.encode(accountDtoIn.getPassword()))
                .personalDetail(personalDetail)
                .build();

        //update relations
        newUser.addToGroups(Set.of(group));

        personalDetailRepository.save(personalDetail);
//        groupRepository.save(group);

        return userRepository.save(newUser);
    }
}
