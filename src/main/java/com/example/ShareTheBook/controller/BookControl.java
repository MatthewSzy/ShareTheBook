package com.example.ShareTheBook.controller;

import com.example.ShareTheBook.dto.Book.*;
import com.example.ShareTheBook.dto.StringInfo;
import com.example.ShareTheBook.service.BookService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/books")
public class BookControl {

    private final BookService bookService;

    public BookControl(BookService bookService) { this.bookService = bookService; }

    @PostMapping("/addData")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StringInfo> addBookData(@RequestBody AddBookData addBookData) {

        StringInfo stringInfo = bookService.AddBookData(addBookData);
        return ResponseEntity.ok(stringInfo);
    }

    @PutMapping("/addFiles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StringInfo> addBookFile(@PathVariable(name = "id") Long id, @RequestParam("files") MultipartFile[] files) {

        StringInfo stringInfo = bookService.AddBookFile(id, files);
        return ResponseEntity.ok(stringInfo);
    }

    @PutMapping("/addBookToFavorite")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StringInfo> addBookToFavorite(@RequestBody AddBookToFavorite addBookToFavorite) {

        StringInfo stringInfo = bookService.AddBookToFavorite(addBookToFavorite);
        return ResponseEntity.ok(stringInfo);
    }

    @GetMapping("/getBook/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BookData> getBook(@PathVariable(name = "id") Long id) {

        BookData bookData = bookService.getBook(id);
        return ResponseEntity.ok(bookData);
    }

    @GetMapping("/getBookFile/{id}")
    @PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id") Long id) throws IOException {
        Resource file = bookService.downloadFile(id);
        Path path = file.getFile().toPath();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping("/getFavoriteBooks/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BooksDataHome>> getFavoriteBooks(@PathVariable(name = "id") Long id) {

        List<BooksDataHome> booksDataHome = bookService.getFavoriteBooks(id);
        return ResponseEntity.ok(booksDataHome);
    }

    @GetMapping("/getAllBooksSearch")
    public ResponseEntity<List<BooksDataSearch>> getAllBooksSearch() {

        List<BooksDataSearch> booksDataSearch = bookService.getAllBooksSearch();
        return ResponseEntity.ok(booksDataSearch);
    }

    @GetMapping("/getAllBooksTitle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BooksTitleAdd>> getAllBooksTitle() {

        List<BooksTitleAdd> booksTitleAdd = bookService.getAllBooksTitle();
        return ResponseEntity.ok(booksTitleAdd);
    }

    @GetMapping("/getAllBooksByTitle")
    public ResponseEntity<List<BooksDataHome>> getAllBooksByTitle() {

        List<BooksDataHome> booksDataHome = bookService.getAllBooksByTitle();
        return ResponseEntity.ok(booksDataHome);
    }

    @GetMapping("/getAllBooksByAddDate")
    public ResponseEntity<List<BooksDataHome>> getAllBooksByAddDate() {

        List<BooksDataHome> booksDataHome = bookService.getAllBooksByAddDate();
        return ResponseEntity.ok(booksDataHome);
    }
}
