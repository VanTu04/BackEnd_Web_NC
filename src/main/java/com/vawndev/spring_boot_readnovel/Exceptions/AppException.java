package com.vawndev.spring_boot_readnovel.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    private ErrorCode errorCode;
    private String messageString;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(errorCode.getMessage()); 
        this.errorCode = errorCode;
        this.messageString = customMessage;
    }
}
