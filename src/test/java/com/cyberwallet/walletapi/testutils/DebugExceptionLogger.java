package com.cyberwallet.walletapi.testutils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@Slf4j
@RestControllerAdvice
@Profile("test")
public class DebugExceptionLogger {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", ex.getMessage());
        body.put("exception", ex.getClass().getSimpleName());
        // Removemos stack trace innecesario del cuerpo
        //body.put("stackTrace", Arrays.stream(ex.getStackTrace()).limit(3).map(StackTraceElement::toString).toList());

        // Logging menos verboso (sin pasar excepción completa)
        log.error("🔴 [EXCEPTION] {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

        return ResponseEntity.status(500).body(body);
    }

}
