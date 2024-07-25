package org.example.githubapirestclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedMediaTypeException(UnsupportedMediaTypeException ex) {
        Map<String, Object> body = Map.of(
                "status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(body, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}