package com.vawndev.spring_boot_readnovel.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {

    private ErrorCode errorCode;
    private Object[] args;

    public AppException(ErrorCode errorCode, Object... args) {
        super(); // Không gọi super với message vì ta sẽ ghi đè getMessage()
        this.errorCode = errorCode;
        this.args = args;
    }
    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getFormattedMessage(args);
    }
}
