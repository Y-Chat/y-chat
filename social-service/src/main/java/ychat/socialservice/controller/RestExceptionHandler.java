package ychat.socialservice.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import ychat.socialservice.util.IllegalUserInputException;
import ychat.socialservice.util.LimitReachedException;

/**
 * Global exception handler for all REST endpoints. This potentially leaks implementation details
 * which is ignored for development purposes
 */
@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler({IllegalUserInputException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiResponse(description = "Illegal user input", responseCode = "400", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleIllegalUserInputException(Exception e) {
        return e.getMessage();
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ApiResponse(description = "Entity exists already", responseCode = "409", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleEntityExistsException(EntityExistsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ApiResponse(description = "Entity not found", responseCode = "404", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleEntityNotFoundException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(LimitReachedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(description = "Limit reached", responseCode = "403", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleLimitReachedException(LimitReachedException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(description = "Access denied", responseCode = "403", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleAccessDeniedException(AccessDeniedException e) {
        return e.getMessage();
    }

    /**
     * By default, all not explicitly handled exceptions are treated as not the users fault
     * <p>
     * Especially, also the SQL execution errors of JPA. The application should filter out
     * constraint violations before storing them in the DB.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ApiResponse(description = "Unexpected exception thrown", responseCode = "500", content = {
        @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    })
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
