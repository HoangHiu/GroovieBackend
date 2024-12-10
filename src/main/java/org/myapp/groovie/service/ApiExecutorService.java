package org.myapp.groovie.service;

import jakarta.servlet.http.HttpServletResponse;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiExecutorService<T> {

    public ResponseEntity<ApiCallResponse<T>> execute( ApiCallExecutor<T> apiCallExecutor){
        ApiCallResponse<T> apiCallResponse = new ApiCallResponse<>();
        try{
            apiCallResponse = apiCallExecutor.call();
            apiCallResponse.setCode(HttpStatus.OK.value());
            apiCallResponse.setSuccess(true);

            return apiCallResponse.toResponseEntity(HttpStatus.OK);
        }catch (ApiCallException e){
            apiCallResponse.setCode(e.getCode().value());
            apiCallResponse.setData(null);
            apiCallResponse.setMessage(e.getMessage());
            apiCallResponse.setSuccess(false);
            return apiCallResponse.toResponseEntity(e.getCode());
        }
    }
}
