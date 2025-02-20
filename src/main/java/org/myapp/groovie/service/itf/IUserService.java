package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.UserDtoIn;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    List<User> getAllUsers() throws ApiCallException;
    User getOneById(UUID userId) throws ApiCallException;
    User create(UserDtoIn userDtoIn) throws ApiCallException;
    User getOneByUsername(String username) throws ApiCallException;
}
