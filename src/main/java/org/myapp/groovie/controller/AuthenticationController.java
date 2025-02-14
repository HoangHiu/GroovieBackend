package org.myapp.groovie.controller;

import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired()
    AuthService authService;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @PostMapping("/login")
    public ResponseEntity<ApiCallResponse<Object>> login(@RequestBody UserDtoIn userDtoIn){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(authService.authenticate(userDtoIn));
        });
    }

    @PostMapping("/register")
    public ResponseEntity<ApiCallResponse<Object>> register(@RequestBody UserDtoIn userDtoIn){
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(authService.register(userDtoIn));
        });
    }
}
