package com.matdev.menuservice.exception.handler;


import com.matdev.menuservice.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExeptionHandler {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GlobalExeptionHandler.class);
    
    private ResponseEntity<ExceptionDTO> buildResponseEntity(HttpStatus status, String message, WebRequest request){
    ExceptionDTO error= new ExceptionDTO();
    error.setStatus(status.value());
    error.setError(status.getReasonPhrase());
    error.setMessage(message);
    error.setPath(request.getDescription(false).substring(4)); //substring para quitar el path
    return ResponseEntity.status(status).body(error);
    }



    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionDTO> handleAppException(AppException ex, WebRequest request){
        LOGGER.error("Error de aplicación: {}",ex.getMessage());
        return buildResponseEntity(HttpStatus.valueOf(ex.getResponseCode()), ex.getMessage(), request);
    }
    


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionDTO> handleBadRequestException(BadRequestException ex, WebRequest request){
        LOGGER.error("Error de petición:  {}",ex.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }



    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ExceptionDTO> handleInternalServerErrorException(InternalServerErrorException ex, WebRequest request){
        LOGGER.error("Error interno del servidor:  {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionDTO> handleNotFoundException(NotFoundException ex, WebRequest request){
        LOGGER.error("Recurso no encontrado: {} ",ex.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<ExceptionDTO> handleUnauthorizedUserException(UnauthorizedUserException ex, WebRequest request){
        LOGGER.error("Usuario no autorizado: {} ",ex.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDTO> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
        LOGGER.error("Error de validación: {} ",ex.getMessage());
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }


    


}
