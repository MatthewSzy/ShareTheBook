package com.example.ShareTheBook.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class CategoryNotExistsException  extends RuntimeException {

    public CategoryNotExistsException (String message) {
        super(message);
    }
}
