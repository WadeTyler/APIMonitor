package net.tylerwade.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class SampleController {

    @GetMapping("/test")
    public String test() {
        return "Test called!";
    }



    @PostMapping("/test2")
    public String test2() {
        return "Test2 called!";
    }

    @GetMapping("/test3")
    public ResponseEntity<?> test3() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Test 3 called, but it's bad...");
    }


}
