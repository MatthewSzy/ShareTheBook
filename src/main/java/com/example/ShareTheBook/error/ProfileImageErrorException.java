package com.example.ShareTheBook.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class ProfileImageErrorException extends RuntimeException {

    public ProfileImageErrorException(String message) {
        super(message);
    }
}