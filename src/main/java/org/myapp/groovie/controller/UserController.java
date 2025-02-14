package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PersonalDetailDtoIn;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.AuthService;
import org.myapp.groovie.service.itf.IPersonalDetailService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IPersonalDetailService personalDetailService;

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

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createUser(
            @RequestBody UserDtoIn userDtoIn
            ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.create(userDtoIn));
        });
    }

    @PutMapping("/info")
    public ResponseEntity<ApiCallResponse<Object>> updateUserInfo(
            @RequestBody PersonalDetailDtoIn personalDetailDtoIn,
            @RequestParam(name = "userid") String userId,
            Authentication authentication
            ){
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(personalDetailService.update(UUID.fromString(userId), personalDetailDtoIn));
        });
    }
}
