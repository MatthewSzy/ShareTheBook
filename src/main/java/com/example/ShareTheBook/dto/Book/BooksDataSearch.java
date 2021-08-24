package com.example.ShareTheBook.dto.Book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BooksDataSearch {

    private Long bookId;

    private String title;

    private String author;

    private byte[] coverImage;
}
