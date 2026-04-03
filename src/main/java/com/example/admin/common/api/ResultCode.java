package com.example.admin.common.api;

public interface ResultCode {
    Integer SUCCESS = 200;
    Integer BAD_REQUEST = 400;
    Integer UNAUTHORIZED = 401;
    Integer FORBIDDEN = 403;
    Integer NOT_FOUND = 404;
    Integer METHOD_NOT_ALLOWED = 405;
    Integer NOT_ACCEPTABLE = 406;
    Integer UNSUPPORTED_MEDIA_TYPE = 415;
    Integer ERROR = 500;
    Integer BUSINESS_ERROR = 1001;
}