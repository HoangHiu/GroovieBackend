package org.myapp.groovie;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GroovieApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroovieApplication.class, args);
    }
}
