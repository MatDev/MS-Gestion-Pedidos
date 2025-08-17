package com.matdev.menuservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import matdev.user.user_service.exception.ExceptionDTO;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppException extends RuntimeException {

     private final String code;
    private final int responseCode;
    private final List<ExceptionDTO> errorList = new ArrayList<>();

    public AppException(String message, String code, int responseCode) {
        super(message);
        this.code = code;
        this.responseCode = responseCode;
    }

    
   
}
