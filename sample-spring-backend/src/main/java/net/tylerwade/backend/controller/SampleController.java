package net.tylerwade.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
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

}
