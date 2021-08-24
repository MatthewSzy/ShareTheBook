package com.example.ShareTheBook.repository;

import com.example.ShareTheBook.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findBookEntityByBookId(Long id);
}
