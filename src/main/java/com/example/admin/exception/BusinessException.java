package com.example.admin.exception;

import com.example.admin.common.api.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.BUSINESS_ERROR;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}