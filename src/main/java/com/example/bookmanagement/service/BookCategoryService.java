package com.example.bookmanagement.service;

import com.example.bookmanagement.model.BookCategory;
import com.example.bookmanagement.repository.BookCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;

    public BookCategoryService(BookCategoryRepository bookCategoryRepository) {
        this.bookCategoryRepository = bookCategoryRepository;
    }

    public List<BookCategory> findAll() {
        return bookCategoryRepository.findAll();
    }

    public void createIfNotExist(BookCategory bookCategory) {
        if(bookCategoryRepository.findById(bookCategory.getId()).isPresent()) {
            return;
        }
        bookCategoryRepository.save(bookCategory);
    }

}
