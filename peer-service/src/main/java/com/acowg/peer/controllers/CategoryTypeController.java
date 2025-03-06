package com.acowg.peer.controllers;

import com.acowg.peer.dto.CategoryTypesResponse;
import com.acowg.peer.mappers.CategoryTypeMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/category-types")
public class CategoryTypeController {

    private final CategoryTypeMapper categoryTypeMapper;

    public CategoryTypeController(CategoryTypeMapper categoryTypeMapper) {
        this.categoryTypeMapper = categoryTypeMapper;
    }

    @GetMapping
    public ResponseEntity<CategoryTypesResponse> getAllCategoryTypes() {
        return ResponseEntity.ok(categoryTypeMapper.toCategoryTypesResponse());
    }
}
