package com.matdev.menuservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InternalServerErrorException extends RuntimeException {
    private String code;
    public InternalServerErrorException(String message) {
        super(message);
    }
    public InternalServerErrorException(String message, String code) {
        super(message);
        this.code = code;
    }


}
