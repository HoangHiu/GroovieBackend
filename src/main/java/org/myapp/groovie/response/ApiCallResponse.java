package org.myapp.groovie.response;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ApiCallResponse<T>{
    private int code;
    private boolean isSuccess;
    private String message;
    private T data;

    public ApiCallResponse(T data){
        this.data = data;
    }

    public ResponseEntity<ApiCallResponse<T>> toResponseEntity(HttpStatus httpStatus){
        return new ResponseEntity<>(this, httpStatus);
    }
}
