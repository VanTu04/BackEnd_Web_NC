package com.vawndev.spring_boot_readnovel.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003, "Email invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    BLANK_EMAIL(1009, "Please enter a valid email", HttpStatus.BAD_REQUEST),
    BLANK_PASSWORD(1010, "Please enter a valid password", HttpStatus.BAD_REQUEST),
    MISS_TOKEN(1011, "Missing token", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1012, "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_STORY(1013, "Invalid story", HttpStatus.NOT_FOUND),
    SERVER_ERROR(1014, "server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_SAVE_DATA(1015, "Error saving data", HttpStatus.INTERNAL_SERVER_ERROR),

    ERROR_CREATE_HMACSHA512(1017, "Error creating HMACSHA512", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_ENCODE(1018, "Error encode data", HttpStatus.INTERNAL_SERVER_ERROR),

    OBJECT_EXISTED(1016, "Object already existed", HttpStatus.CONFLICT),
    NOT_FOUND(1017, "Object tot found", HttpStatus.NOT_FOUND),

    INVALID(1018, "Invalid: {name}", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    public String getFormattedMessage(String name) {
        return this.message.replace("{name}", name);
    }

}
