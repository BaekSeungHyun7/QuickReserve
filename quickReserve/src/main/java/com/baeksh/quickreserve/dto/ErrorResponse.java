package com.baeksh.quickreserve.dto;

import com.baeksh.quickreserve.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data  
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;
    private String message;

}
