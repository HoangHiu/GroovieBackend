package org.myapp.groovie.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiCallException extends Exception {
    private HttpStatus code;
    private boolean isSuccess;
    private String message;

    public ApiCallException(String message, HttpStatus code){
        this.message = message;
        this.code = code;
    }
}
