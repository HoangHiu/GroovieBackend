package org.myapp.groovie.controller;

import org.myapp.groovie.dtos.in.UserDtoIn;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired()
    UserService userService;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @PostMapping("/login")
    public ResponseEntity<ApiCallResponse<Object>> login(@RequestBody UserDtoIn userDtoIn){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.authenticate(userDtoIn));
        });
    }
}
