package com.example.admin.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 1001;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}