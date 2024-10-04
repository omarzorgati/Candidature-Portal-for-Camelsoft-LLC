package com.camelsoft.portal.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED,"No code"),
    BAD_CREDENTIALS(304,FORBIDDEN,"email or password are incorrect"),

    ;

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus httpStatus,String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
