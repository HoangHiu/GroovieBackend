package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.out.PageInfoDtoOut;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/group")
@RequiredArgsConstructor
public class GroupController {
    private final IGroupService groupService;

    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllGroups(
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(groupService.getAllGroups());
        });
    }
}
