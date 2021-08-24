package com.example.ShareTheBook.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class BookIsAddedToFavoriteException extends RuntimeException{

    public BookIsAddedToFavoriteException(String message) {
        super(message);
    }
}