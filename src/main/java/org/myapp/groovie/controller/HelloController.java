package org.myapp.groovie.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("")
    public ResponseEntity<String> helloDeveloper(HttpServletRequest request){
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(request.getRemoteUser() + "\n" + request.getSession(), HttpStatusCode.valueOf(302));

//        return request.getRemoteUser() + "\n" + request.getSession();
        return responseEntity;
    }
}
