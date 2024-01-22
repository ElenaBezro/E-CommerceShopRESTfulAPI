package com.bezro.shopRESTfulAPI.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            UserAlreadyExistsException.class,
            UserWithEmailAlreadyExistsException.class,
            InvalidMethodArgumentsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiException> handlePasswordMismatchException(RuntimeException exception) {
        ApiException apiException = new ApiException(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidLoginCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiException> handleInvalidLoginCredentialsException(InvalidLoginCredentialsException exception) {
        ApiException apiException = new ApiException(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({RoleNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiException> handleInvalidRoleNameException(RoleNotFoundException exception) {
        ApiException apiException = new ApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiRequestException> handleNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        exception.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        ApiRequestException apiRequestException = new ApiRequestException(HttpStatus.BAD_REQUEST.value(), errors);
        return new ResponseEntity<>(apiRequestException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoContentException.class})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiException> handleNoContentException(NoContentException exception) {
        ApiException apiException = new ApiException(HttpStatus.NO_CONTENT.value(), exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.NO_CONTENT);
    }
}