package org.example.controller;

import org.example.dto.ProtectedDataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ProtectedController {
    
    @GetMapping("/protected-data")
    public ResponseEntity<ProtectedDataResponse> getProtectedData() {
        // JWT 인증이 완료된 상태에서만 접근 가능
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        return ResponseEntity.ok(ProtectedDataResponse.builder()
                .message("This is protected data")
                .user(authentication.getName())
                .timestamp(LocalDateTime.now())
                .data("Secret information only for authenticated users")
                .build());
    }
}