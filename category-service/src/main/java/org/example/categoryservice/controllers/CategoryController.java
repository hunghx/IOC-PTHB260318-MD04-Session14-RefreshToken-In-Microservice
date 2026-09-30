package org.example.categoryservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestHeader("X-Auth-UserId") String userId) {
        System.out.println(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Create Category Successfully");
    }

}
