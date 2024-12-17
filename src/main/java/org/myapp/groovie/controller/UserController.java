package org.myapp.groovie.controller;

import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllUsers() throws ApiCallException {
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(userService.getAllUsers());
        });
    }
}
