package de.viadee.vpw.analyzer.api.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class UnhandledExceptionHandler {

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity handleConflict(HttpMessageNotReadableException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return ResponseEntity.badRequest().body(bodyOfResponse);
    }
}
