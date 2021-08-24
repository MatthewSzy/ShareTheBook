package com.example.ShareTheBook.dto.Book;

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
public class AddBookData {

    private String title;

    private String author;

    private Date releaseDate;

    private String description;

    private Set<String> categories;
}
