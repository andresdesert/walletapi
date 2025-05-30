package com.cyberwallet.walletapi.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private int statusCode;
    private ErrorCode errorCode;
    private String message;
    private String details;
    private String path;
    private LocalDateTime timestamp;
}
