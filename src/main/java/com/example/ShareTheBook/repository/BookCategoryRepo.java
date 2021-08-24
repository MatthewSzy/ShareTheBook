package com.example.ShareTheBook.repository;

import com.example.ShareTheBook.entity.BookCatergoryEntity;
import com.example.ShareTheBook.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookCategoryRepo extends JpaRepository<BookCatergoryEntity, Long> {

    Optional<BookCatergoryEntity> findBookCatergoryEntityByName(Categories name);
}
