package com.example.ShareTheBook.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @NotBlank
    @Size(max = 50)
    private String title;

    @NotBlank
    @Size(max = 60)
    private String author;

    @NotBlank
    private Date releaseDate;

    @NotBlank
    @Size(max = 512)
    private String description;

    @NotBlank
    private Date addDate;

    @NotBlank
    private String coverPath;

    @NotBlank
    private String bookPath;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "books_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<BookCatergoryEntity> categories = new HashSet<>();


}
