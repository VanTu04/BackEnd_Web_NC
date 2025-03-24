package com.vawndev.spring_boot_readnovel.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode, Object... args) {
        super(errorCode.getFormattedMessage(args));
        this.errorCode = errorCode;
        System.out.println("🚀 Exception Created -> " + super.getMessage());
    }
    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
