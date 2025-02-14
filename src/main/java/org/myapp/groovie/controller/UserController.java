package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.AuthService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllUsers() throws ApiCallException {
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(userService.getAllUsers());
        });
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> getOneUser(
            @PathVariable(name = "uuid") String userId
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.getOneById(UUID.fromString(userId)));
        });
    }
}
