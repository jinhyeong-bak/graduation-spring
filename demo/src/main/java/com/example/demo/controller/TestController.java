package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    @GetMapping("/auth/tokenFilterTest")
    public ResponseEntity tokenFilterTest() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
