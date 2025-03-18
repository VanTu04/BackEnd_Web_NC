package com.vawndev.spring_boot_readnovel.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.NOT_FOUND),
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
    INVALID_CHAPTER(1017,"Invalid chapter",HttpStatus.NOT_FOUND),
    OBJECT_EXISTED(1016, "Object already existed", HttpStatus.CONFLICT),
    INVALID_CATE(1018, "Not found category", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(1019, "File not found", HttpStatus.NOT_FOUND),
    NOT_FOUND(1020, "{row} not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
