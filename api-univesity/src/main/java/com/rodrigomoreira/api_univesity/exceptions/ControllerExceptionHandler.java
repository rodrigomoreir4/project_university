package com.rodrigomoreira.api_univesity.exceptions;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
        } 
        
        ExceptionDTO exceptionDTO = new ExceptionDTO(newMessage);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDTO);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationException(MethodArgumentNotValidException exception){
        
        String message = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
        String newMessage = "";
        
        if(message.contains("domain.users")){
            newMessage = "Fill in all fields";
        } else if (message.contains("domain.courses")){
            newMessage = "The course must have a name";
        }
        ExceptionDTO exceptionDTO = new ExceptionDTO(newMessage);
        return ResponseEntity.badRequest().body(exceptionDTO);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> emptyUserType(HttpMessageNotReadableException exception){
        ExceptionDTO exceptionDTO = new ExceptionDTO("User type cannot be empty");
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
