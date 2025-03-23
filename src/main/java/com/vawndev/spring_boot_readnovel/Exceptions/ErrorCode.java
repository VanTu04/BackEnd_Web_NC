package com.vawndev.spring_boot_readnovel.Exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.text.MessageFormat;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {0} characters", HttpStatus.BAD_REQUEST),
    OBJECT_NOT_EXISTED(1005, "{0} not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {0}", HttpStatus.BAD_REQUEST),
    BLANK_EMAIL(1009, "Please enter a valid email", HttpStatus.BAD_REQUEST),
    BLANK_PASSWORD(1010, "Please enter a valid password", HttpStatus.BAD_REQUEST),
    MISS_TOKEN(1011, "Missing token", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1012, "Invalid token", HttpStatus.UNAUTHORIZED),
    INVALID_STORY(1013, "Invalid story", HttpStatus.NOT_FOUND),
    SERVER_ERROR(1014, "server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_SAVE_DATA(1015, "Error saving data", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_CREATE_HMACSHA512(1017, "Error creating HMACSHA512", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_ENCODE(1018, "Error encode data", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_CHAPTER(1019,"Invalid chapter",HttpStatus.NOT_FOUND),
    OBJECT_EXISTED(1020, "{0} already existed", HttpStatus.CONFLICT),
    OBJECT_NOT_FOUND(1021, "{0} not found", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(1022, "File not found", HttpStatus.NOT_FOUND),
    INVALID_CATE(2024, "Invalid category", HttpStatus.BAD_REQUEST),
    FAILED_PAYMENT(2025, "Failed to payment", HttpStatus.BAD_REQUEST),
    CONFLICT(2026, "CONFLICT", HttpStatus.CONFLICT),
    CONFLICT_SUBSCRIPTION(2023, "Your subscription has not expired. Please wait until your subscription expires to upgrade.", HttpStatus.CONFLICT),
    OBJECT_INVAILD(1027, "{0} must be a value", HttpStatus.BAD_REQUEST),

    INVALID(1028, "Invalid: ", HttpStatus.BAD_REQUEST),
    BLANK_NAME(1029,"Name must be fill",HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1030,"Password must match",HttpStatus.BAD_REQUEST),
    NOT_ENOUGH(1031,"{0} not enough",HttpStatus.BAD_REQUEST),


    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    public String getFormattedMessage(Object... args) {
        return MessageFormat.format(this.message, args);
    }
}
