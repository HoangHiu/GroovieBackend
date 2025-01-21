package org.myapp.groovie.controller;

import org.myapp.groovie.dto.UserDtoIn;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MvcController {
    ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @Autowired
    UserService userService;

    @GetMapping("/v1/denied")
    public String accessDenied(Model model){
        model.addAttribute("message", "Access denied");
        return "denied";
    }

    @GetMapping("/v1/mvc/login/get")
    public String loginMvcGet(Model model){
        model.addAttribute("userDtoIn", new UserDtoIn());
        return "login";
    }
}
