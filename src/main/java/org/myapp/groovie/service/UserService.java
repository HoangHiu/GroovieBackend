package org.myapp.groovie.service;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.entity.user.Group;
import org.myapp.groovie.entity.user.PersonalDetail;
import org.myapp.groovie.entity.user.Role;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.GroupRepository;
import org.myapp.groovie.repository.PersonalDetailRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PersonalDetailRepository personalDetailRepository;

    private final JwtService jwtService;
    private final IGroupService groupService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getOneByUsername(String username) throws ApiCallException{
        User user = userRepository.getUserByUsername(username);
        if(user != null){
            return user;
        }
        throw new ApiCallException("No user with username:" + username, HttpStatus.NOT_FOUND);
    }

    public String authenticate(UserDtoIn userDtoIn) throws ApiCallException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDtoIn.getUsername(), userDtoIn.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(userDtoIn.getUsername());
        }else{
            throw new ApiCallException("No user found!!!", HttpStatus.BAD_REQUEST);
        }

    }

    public User register(UserDtoIn userDtoIn) throws ApiCallException{
        String username = userDtoIn.getUsername();
        User userCheck = userRepository.getUserByUsername(username);

        if(userCheck != null){
            throw new ApiCallException("User with username: " + username + " already existsted", HttpStatus.NOT_FOUND);
        }

        Group group = groupService.getGroupByRole(Role.REGULAR);
        PersonalDetail personalDetail = PersonalDetail.builder()
                .uuid(UUID.randomUUID())
                .name(userDtoIn.getUsername())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        //save new user
        User newUser = User.builder()
                .uuid(UUID.randomUUID())
                .username(userDtoIn.getUsername())
                .password(passwordEncoder.encode(userDtoIn.getPassword()))
                .personalDetail(personalDetail)
                .build();

        //update relations
        newUser.addToGroup(group);

        personalDetailRepository.save(personalDetail);
//        groupRepository.save(group);

        return userRepository.save(newUser);
    }
}
