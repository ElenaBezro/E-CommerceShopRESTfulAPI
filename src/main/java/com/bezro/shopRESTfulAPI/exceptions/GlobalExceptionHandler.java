package com.bezro.shopRESTfulAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({PasswordMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiException> handlePasswordMismatchException(PasswordMismatchException exception) {
        ApiException apiException = new ApiException(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiException> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
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
}