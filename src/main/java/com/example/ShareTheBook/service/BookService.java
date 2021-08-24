package com.example.ShareTheBook.service;

import com.example.ShareTheBook.dto.Book.*;
import com.example.ShareTheBook.dto.StringInfo;
import com.example.ShareTheBook.entity.BookCatergoryEntity;
import com.example.ShareTheBook.entity.BookEntity;
import com.example.ShareTheBook.entity.Categories;
import com.example.ShareTheBook.entity.UserEntity;
import com.example.ShareTheBook.error.BookIsAddedToFavoriteException;
import com.example.ShareTheBook.error.BookNotExistsException;
import com.example.ShareTheBook.error.CategoryNotExistsException;
import com.example.ShareTheBook.error.UserNotExistsException;
import com.example.ShareTheBook.repository.BookRepo;
import com.example.ShareTheBook.repository.BookCategoryRepo;
import com.example.ShareTheBook.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    BookRepo bookRepo;

    @Autowired
    BookCategoryRepo bookCategoryRepo;

    public StringInfo AddBookData(AddBookData addBookData) {

        Set<BookCatergoryEntity> newCategoriesSet = new HashSet<>();
        addBookData.getCategories().forEach(bookCategory -> {
            for(Categories category : Categories.values()) {
                if (category.name().equals(bookCategory)) {
                    Optional<BookCatergoryEntity> bookCatergoryEntity = bookCategoryRepo.findBookCatergoryEntityByName(category);
                    if (bookCatergoryEntity.isEmpty()) {
                        throw new CategoryNotExistsException("Nie ma takiej kategorii!");
                    }

                    newCategoriesSet.add(bookCatergoryEntity.get());
                }
            }
        });

        BookEntity bookEntity = BookEntity.builder()
                .title(addBookData.getTitle())
                .author(addBookData.getAuthor())
                .releaseDate(addBookData.getReleaseDate())
                .description(addBookData.getDescription())
                .addDate(Date.valueOf(LocalDate.now()))
                .categories(newCategoriesSet)
                .coverPath("")
                .bookPath("")
                .build();

        bookRepo.save(bookEntity);
        return new StringInfo("Dodano nową książkę o tytule: " + addBookData.getTitle());
    }

    public StringInfo AddBookFile(Long id, MultipartFile[] files) {

        Optional<BookEntity> bookEntity = bookRepo.findBookEntityByBookId(id);
        if (bookEntity.isEmpty()) {
            throw new BookNotExistsException("Nie znaleziono książki!");
        }

        byte[] coverBytes = null;
        try {
            coverBytes = files[0].getBytes();
        } catch (IOException e) {}

        String path = "src\\main\\resources\\Books" + "\\" + bookEntity.get().getTitle();
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        File coverFile = new File(path + "\\cover." + files[0].getOriginalFilename().split("\\.")[1]);
        try (OutputStream outputStream = new FileOutputStream(coverFile)) {
            outputStream.write(coverBytes);
        } catch (IOException e) {}

        byte[] bookBytes = null;
        try {
            bookBytes = files[1].getBytes();
        } catch (IOException e) {}

        File bookFile = new File(path + "\\" + files[1].getOriginalFilename());
        try (OutputStream outputStream = new FileOutputStream(bookFile)) {
            outputStream.write(bookBytes);
        } catch (IOException e) {}

        bookEntity.get().setCoverPath(path + "\\cover." + files[0].getOriginalFilename().split("\\.")[1]);
        bookEntity.get().setBookPath(path + "\\" + files[1].getOriginalFilename());

        bookRepo.save(bookEntity.get());
        return new StringInfo("Zapisano pliki związane z książką!");
    }

    public StringInfo AddBookToFavorite(AddBookToFavorite addBookToFavorite) {

        Optional<UserEntity> userEntity = userRepo.findUserEntityById(addBookToFavorite.getUserId());
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        Optional<BookEntity> bookEntity = bookRepo.findBookEntityByBookId(addBookToFavorite.getBookId());
        if (bookEntity.isEmpty()) {
            throw new BookNotExistsException("Nie znaleziono książki!");
        }

        for (BookEntity bookEntities : userEntity.get().getBooks()) {
            if (bookEntities.getBookId() == addBookToFavorite.getBookId()) {
                throw new BookIsAddedToFavoriteException("Książka jest już dodana do ulubionych!");
            }
        }

        userEntity.get().getBooks().add(bookEntity.get());
        userRepo.save(userEntity.get());

        return new StringInfo("Książka została dodana do ulubionych!");
    }

    public BookData getBook(Long id) {

        Optional<BookEntity> bookEntity = bookRepo.findBookEntityByBookId(id);
        if (bookEntity.isEmpty()) {
            throw new BookNotExistsException("Nie znaleziono książki!");
        }

        return BookData.builder()
                .bookId(bookEntity.get().getBookId())
                .title(bookEntity.get().getTitle())
                .author(bookEntity.get().getAuthor())
                .releaseDate(bookEntity.get().getReleaseDate())
                .description(bookEntity.get().getDescription())
                .addDate(bookEntity.get().getAddDate())
                .coverImage(convertImageToBytes(bookEntity.get().getCoverPath()))
                .categories(bookEntity.get().getCategories())
                .build();
    }

    public Resource downloadFile(Long id) {

        Optional<BookEntity> bookEntity = bookRepo.findBookEntityByBookId(id);
        if (bookEntity.isEmpty()) {
            throw new BookNotExistsException("Nie znaleziono książki!");
        }

        Path path = Paths.get(bookEntity.get().getBookPath());
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) { }

        return resource;
    }

    public List<BooksDataHome> getFavoriteBooks(Long id) {

        Optional<UserEntity> userEntity = userRepo.findUserEntityById(id);
        if(userEntity.isEmpty()) {
            throw new UserNotExistsException("Podany użytkownik nie istnieje!");
        }

        return userEntity.get().getBooks().stream().map(book -> BooksDataHome.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImage(convertImageToBytes(book.getCoverPath()))
                .addDate(book.getAddDate())
                .build()).collect(Collectors.toList());
    }

    public List<BooksDataSearch> getAllBooksSearch() {

        List<BookEntity> booksEntity = bookRepo.findAll();
        if (booksEntity.isEmpty()) {
            throw new BookNotExistsException("Lista książek jest pusta!");
        }

        return booksEntity.stream().map(book -> BooksDataSearch.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImage(convertImageToBytes(book.getCoverPath()))
                .build()).collect(Collectors.toList());
    }

    public List<BooksTitleAdd> getAllBooksTitle() {

        List<BookEntity> booksEntity = bookRepo.findAll();
        if (booksEntity.isEmpty()) {
            throw new BookNotExistsException("Lista książek jest pusta!");
        }

        return booksEntity.stream().map(book -> BooksTitleAdd.builder()
            .bookId(book.getBookId())
            .title(book.getTitle())
            .build()).collect(Collectors.toList());
    }

    public List<BooksDataHome> getAllBooksByTitle() {

        List<BookEntity> booksEntity = bookRepo.findAll();
        if (booksEntity.isEmpty()) {
            throw new BookNotExistsException("Lista książek jest pusta!");
        }

        List<BooksDataHome> booksDataHome = booksEntity.stream().map(book -> BooksDataHome.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImage(convertImageToBytes(book.getCoverPath()))
                .addDate(book.getAddDate())
                .build()).sorted(Comparator.comparing(BooksDataHome::getTitle).reversed()).collect(Collectors.toList());

        if (booksDataHome.size() <= 4) return booksDataHome;
        else {
            List<BooksDataHome> booksDataHomeCut = new ArrayList<>();
            for(int i = 0; i < 4; i++) booksDataHomeCut.add(booksDataHome.get(i));
            return booksDataHomeCut;
        }
    }

    public List<BooksDataHome> getAllBooksByAddDate() {

        List<BookEntity> booksEntity = bookRepo.findAll();
        if (booksEntity.isEmpty()) {
            throw new BookNotExistsException("Lista książek jest pusta!");
        }

        List<BooksDataHome> booksDataHome = booksEntity.stream().map(book -> BooksDataHome.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverImage(convertImageToBytes(book.getCoverPath()))
                .addDate(book.getAddDate())
                .build()).sorted(Comparator.comparing(BooksDataHome::getAddDate).reversed()).collect(Collectors.toList());

        if (booksDataHome.size() <= 4) return booksDataHome;
        else {
            List<BooksDataHome> booksDataHomeCut = new ArrayList<>();
            for(int i = 0; i < 4; i++) booksDataHomeCut.add(booksDataHome.get(i));
            return booksDataHomeCut;
        }
    }

    public byte[] convertImageToBytes(String coverPath) {

        Path path = Paths.get(coverPath);
        if (!Files.exists(path)) { }

        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(path);
        } catch(IOException e) { }

        return bytes;
    }
}
