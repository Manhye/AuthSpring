package com.example.authspring.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    public final ErrorCode code;

    public ApiException(ErrorCode code){
        super(code.message);
        this.code = code;
    }
}
