package com.example.bookmanagement.controller;

import com.example.bookmanagement.model.Book;
import com.example.bookmanagement.model.BookCategory;
import com.example.bookmanagement.model.User;
import com.example.bookmanagement.service.BookCategoryService;
import com.example.bookmanagement.service.BookService;
import com.example.bookmanagement.util.FileUploadUtil;
import com.example.bookmanagement.validator.BookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;

    private final BookCategoryService bookCategoryService;

    private final BookValidator bookValidator;

    public BookController(BookCategoryService bookCategoryService, BookService bookService, BookValidator bookValidator) {
        this.bookCategoryService = bookCategoryService;
        this.bookService = bookService;
        this.bookValidator = bookValidator;
    }

    @GetMapping({ "/", "/books"})
    public String books(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    @GetMapping("/books/new")
    public String createBookForm(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);
        model.addAttribute("mode", "create");

        model.addAttribute("categories", bookCategoryService.findAll());
        return "create_book";

    }

    @PostMapping("/books")
    public String saveBook(@ModelAttribute("book") Book book, Model model, BindingResult bindingResult,
                           @RequestParam(value = "image") MultipartFile image) throws IOException {
        bookValidator.validate(book, bindingResult);

        model.addAttribute("categories", bookCategoryService.findAll());
        model.addAttribute("mode", "create");

        if (bindingResult.hasErrors()) {
            return "create_book";
        }

        String fileName = null;
        if(image.getOriginalFilename() != null) {
            fileName = StringUtils.cleanPath(image.getOriginalFilename());
            book.setPhotos(fileName);
        }

        Book savedBook = bookService.saveBook(book);
        String uploadDir = "book-photos/" + savedBook.getId();

        if(fileName != null) {
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }

        return "redirect:/";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("categories", bookCategoryService.findAll());
        model.addAttribute("mode", "edit");
        return "create_book";
    }

    @GetMapping("/books/view/{id}")
    public String viewBookForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("categories", bookCategoryService.findAll());
        model.addAttribute("mode", "view");
        return "create_book";
    }

    @GetMapping("/books/{id}")
    public String deleteStudent(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return "redirect:/";
    }

    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable Long id,
                                @ModelAttribute("book") Book book,
                                BindingResult bindingResult,
                                Model model, @RequestParam(value = "image") MultipartFile image) throws IOException {
        bookValidator.validate(book, bindingResult);
        model.addAttribute("categories", bookCategoryService.findAll());
        model.addAttribute("mode", "edit");

        String fileName = null;
        if(image.getOriginalFilename() != null) {
            fileName = StringUtils.cleanPath(image.getOriginalFilename());
        }

        if (bindingResult.hasErrors()) {
            return "create_book";
        }

        Book existingBook = bookService.findById(id);
        existingBook.setId(id);
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setDescription(book.getDescription());
        existingBook.setCategory(book.getCategory());
        existingBook.setNumberOfPages(book.getNumberOfPages());
        existingBook.setPublishedDate(book.getPublishedDate());

        if(fileName != null && !fileName.isEmpty()) {
            existingBook.setPhotos(fileName);
        }
        bookService.updateBook(existingBook);
        String uploadDir = "book-photos/" + book.getId();

        if(fileName != null && !fileName.isEmpty()) {
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }

        return "redirect:/";
    }

}
