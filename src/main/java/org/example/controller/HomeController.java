package org.example.controller;

import org.example.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public ApiResponse home() {
        return new ApiResponse("OAuth Login Server is running", null, "1.0.0");
    }

    @GetMapping("/health")
    public ApiResponse health() {
        return new ApiResponse(null, "UP");
    }
}