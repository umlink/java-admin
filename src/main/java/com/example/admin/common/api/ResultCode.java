package com.example.admin.common.api;

public interface ResultCode {
    Integer SUCCESS = 200;
    Integer BAD_REQUEST = 400;
    Integer UNAUTHORIZED = 401;
    Integer FORBIDDEN = 403;
    Integer NOT_FOUND = 404;
    Integer ERROR = 500;
    Integer BUSINESS_ERROR = 1001;
}