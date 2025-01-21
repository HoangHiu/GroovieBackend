package org.myapp.groovie.service;

import org.myapp.groovie.dto.UserDtoIn;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    public List<User> getAllUsers(){
        return userRepository.findAll();
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
}
