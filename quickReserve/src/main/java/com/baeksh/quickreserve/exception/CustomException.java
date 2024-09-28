package com.baeksh.quickreserve.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	
	//ErrorCode 반환

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

