package com.example.ShareTheBook.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class PatternErrorException extends RuntimeException {

    public PatternErrorException(String message) {
        super(message);
    }
}
