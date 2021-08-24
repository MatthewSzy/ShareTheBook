package com.example.ShareTheBook.dto.Book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBookToFavorite {

    private Long userId;

    private Long bookId;
}
