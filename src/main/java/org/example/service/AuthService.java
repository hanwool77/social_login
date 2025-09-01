package org.example.service;

import org.example.dto.TokenValidationResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public TokenValidationResponse validateToken(String token, String email) {
        if (token == null || email == null) {
            return new TokenValidationResponse(false, "Token and email are required");
        }

        try {
            boolean isValid = jwtUtil.validateToken(token, email);
            
            if (isValid) {
                String emailFromToken = jwtUtil.getEmailFromToken(token);
                return new TokenValidationResponse(true, emailFromToken, jwtUtil.getExpirationDateFromToken(token));
            } else {
                return new TokenValidationResponse(false);
            }
        } catch (Exception e) {
            return new TokenValidationResponse(false, e.getMessage());
        }
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}