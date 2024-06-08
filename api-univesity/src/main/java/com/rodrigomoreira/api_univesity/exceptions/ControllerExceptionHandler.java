package com.rodrigomoreira.api_univesity.exceptions;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @Autowired
    private AtomicLong idCounter;
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity threatDuplicateEntry(DataIntegrityViolationException exception){
        
        String message = exception.getRootCause().getMessage();
        String newMessage = "";

        if (message.contains("USERS(EMAIL")){
            idCounter.decrementAndGet();
            newMessage = "E-mail already registered";
        } else if (message.contains("USERS(DOCUMENT")){
            idCounter.decrementAndGet();
            newMessage = "Document already registered";
        } else if (message.contains("COURSES(NAME")){
            newMessage = "Course already registered";
        }

        ExceptionDTO exceptionDTO = new ExceptionDTO(newMessage, "400");
        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity threat404(EntityNotFoundException exception){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity threatGeneralException(Exception exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage(), "500");
        return ResponseEntity.internalServerError().body(exceptionDTO);
    }

}
