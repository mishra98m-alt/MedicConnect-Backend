package com.medicconnect.exceptions;

import com.medicconnect.utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------- Resource Not Found -----------------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                ResponseUtils.error(ex.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }

    // ----------------- Bad Request / Illegal Arguments -----------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ResponseUtils.error(ex.getMessage(), null),
                HttpStatus.BAD_REQUEST
        );
    }

    // ----------------- Validation Errors from @Valid -----------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(
                ResponseUtils.error("Validation Failed", fieldErrors),
                HttpStatus.BAD_REQUEST
        );
    }

    // ----------------- All Other Uncaught Exceptions -----------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return new ResponseEntity<>(
                ResponseUtils.error(ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
