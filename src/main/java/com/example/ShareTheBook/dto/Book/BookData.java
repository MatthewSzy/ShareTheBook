package com.example.ShareTheBook.dto.Book;

import com.example.ShareTheBook.entity.BookCatergoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookData {

    private Long bookId;

    private String title;

    private String author;

    private Date releaseDate;

    private String description;

    private Date addDate;

    private byte[] coverImage;

    private Set<BookCatergoryEntity> categories;
}
