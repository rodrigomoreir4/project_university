package com.rodrigomoreira.api_univesity.exceptions;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @Autowired
    private AtomicLong idCounter;
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> threatDuplicateEntry(DataIntegrityViolationException exception){
        
        String message = exception.getRootCause() != null ? exception.getRootCause().getMessage() : exception.getMessage();
        String newMessage = "";
        
        if (message.contains("USERS(EMAIL")){
            idCounter.decrementAndGet();
            newMessage = "E-mail already registered";
        } else if (message.contains("USERS(DOCUMENT")){
            idCounter.decrementAndGet();
            newMessage = "Document already registered";
        } else if (message.contains("COURSES(NAME")){
            newMessage = "Course already registered";
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
        
        ExceptionDTO exceptionDTO = new ExceptionDTO(newMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationException(MethodArgumentNotValidException exception){
        String message = "The course must have a name";
        ExceptionDTO exceptionDTO = new ExceptionDTO(message);
        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> threat404(EntityNotFoundException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> threatGeneralException(Exception exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return ResponseEntity.internalServerError().body(exceptionDTO);
    }

}
