package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PersonalDetailDtoIn;
import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.dto.out.PageInfoDtoOut;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.AuthService;
import org.myapp.groovie.service.itf.IPersonalDetailService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.user-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.user-cover-route}")
    private String coverRoute;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
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

    @GetMapping("/{uuid}/cover")
    public ResponseEntity<ApiCallResponse<Object>> getUserCover(
            @PathVariable(name = "uuid") String userId
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(s3Service.getPresignedUrl(bucketName,
                    coverRoute + "/" + userId + ".jpeg"));
        });
    }

    @GetMapping("/me/info")
    public ResponseEntity<ApiCallResponse<Object>> getPersonalInfo(
            Authentication authentication
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.getOneByUsername(authentication.getName()));
        });
    }

    @GetMapping("/me/cover")
    public ResponseEntity<ApiCallResponse<Object>> getPersonalCover(
            Authentication authentication
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            String userId = userService.getOneByUsername(authentication.getName()).getUuid().toString();

            return new ApiCallResponse<>(s3Service.getPresignedUrl(bucketName,
                    coverRoute + "/" + userId + ".jpeg"));
        });
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<ApiCallResponse<Object>> createUser(
            @RequestBody UserDtoIn userDtoIn
            ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.create(userDtoIn));
        });
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    @PutMapping("/info")
    public ResponseEntity<ApiCallResponse<Object>> updateUserInfo(
            @RequestBody PersonalDetailDtoIn personalDetailDtoIn,
            @RequestParam(name = "user_id") String userId,
            Authentication authentication
            ){
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(personalDetailService.update(UUID.fromString(userId), personalDetailDtoIn));
        });
    }

    @PutMapping("/me/info")
    public ResponseEntity<ApiCallResponse<Object>> updatePersonalInfo(
            @RequestBody PersonalDetailDtoIn personalDetailDtoIn,
            Authentication authentication
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(personalDetailService.update(
                    authentication.getName(),
                    personalDetailDtoIn));
        });
    }

    @PutMapping("/me/cover")
    public ResponseEntity<ApiCallResponse<Object>> updatePersonalCover(
            Authentication authentication
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            String userId = userService.getOneByUsername(authentication.getName()).getUuid().toString();

            return new ApiCallResponse<>(s3Service.createPresignedUrl(bucketName,
                    coverRoute + "/" + userId + ".jpeg"));
        });
    }

    @PutMapping("/{uuid}/cover")
    public ResponseEntity<ApiCallResponse<Object>> updateUserCover(
            @PathVariable(name = "uuid") String userId
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(s3Service.createPresignedUrl(bucketName,
                    coverRoute + "/" + userId + ".jpeg"));
        });
    }

    @GetMapping("/search")
    public ResponseEntity<ApiCallResponse<Object>> searchUser(
            @RequestParam(name = "info_name", required = false) String userInfoName,
            @RequestParam(name = "page_number") int pageNumber,
            @RequestParam(name = "page_size") int pageSize,
            @RequestParam(name = "role", required = false) String role
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(
                    PageInfoDtoOut.fromPage(userService.searchUser(userInfoName, role, pageNumber, pageSize)));
        });
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> deleteUser(
            @PathVariable(name = "uuid") String userId
    ) throws ApiCallException {
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(userService.deleteUser(UUID.fromString(userId)));
        });
    }
}
