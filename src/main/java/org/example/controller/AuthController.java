package org.example.controller;

import org.example.dto.*;
import org.example.entity.User;
import org.example.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login/google")
    public ResponseEntity<LoginUrlResponse> googleLogin() {
        LoginUrlResponse response = new LoginUrlResponse("/oauth2/authorization/google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/naver")
    public ResponseEntity<LoginUrlResponse> naverLogin() {
        LoginUrlResponse response = new LoginUrlResponse("/oauth2/authorization/naver");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken() {
        // JWT 필터에서 이미 검증했으므로 SecurityContext에서 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            return ResponseEntity.ok(TokenValidationResponse.builder()
                    .valid(true)
                    .email(userDetails.getUsername())
                    .message("Token is valid")
                    .build());
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(TokenValidationResponse.builder()
                        .valid(false)
                        .error("Token is invalid")
                        .build());
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // DB에서 사용자 정보 조회
            User user = authService.findByEmail(userDetails.getUsername());
            
            return ResponseEntity.ok(UserInfoResponse.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .provider(user.getProvider())
                    .createdAt(user.getCreatedAt())
                    .build());
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout() {
        // JWT는 서버에서 상태를 관리하지 않으므로 클라이언트에서 토큰을 삭제하면 됨
        ApiResponse response = new ApiResponse("Logged out successfully");
        return ResponseEntity.ok(response);
    }
}