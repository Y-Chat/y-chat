package ychat.socialservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ychat.socialservice.exceptions.EntityAlreadyExistsException;
import ychat.socialservice.exceptions.EntityNotFoundException;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
