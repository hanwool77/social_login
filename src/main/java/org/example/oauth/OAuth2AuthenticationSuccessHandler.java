package org.example.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorizedRedirectUri:http://localhost:3000/auth/callback}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException {
        
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            
            String email = extractEmail(oAuth2User);
            String name = extractName(oAuth2User);
            
            System.out.println("OAuth Success - Email: " + email + ", Name: " + name);
            
            if (email == null) {
                System.err.println("Email is null, redirecting to failure");
                getRedirectStrategy().sendRedirect(request, response, 
                    UriComponentsBuilder.fromUriString(redirectUri)
                        .queryParam("error", "true")
                        .queryParam("message", "Email not found")
                        .build().encode().toUriString());
                return;
            }
            
            // 사용자 저장 또는 조회
            saveOrUpdateUser(oAuth2User, email, name);
            
            String token = jwtUtil.generateToken(email);
            
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", token)
                    .queryParam("email", email)
                    .queryParam("name", name != null ? name : "Unknown")
                    .build().encode().toUriString();

            System.out.println("Redirecting to: " + targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            System.err.println("OAuth Success Handler error: " + e.getMessage());
            e.printStackTrace();
            getRedirectStrategy().sendRedirect(request, response, 
                UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("error", "true")
                    .queryParam("message", "Authentication processing failed")
                    .build().encode().toUriString());
        }
    }

    private String extractEmail(OAuth2User oAuth2User) {
        // Google의 경우
        if (oAuth2User.getAttributes().containsKey("email")) {
            return (String) oAuth2User.getAttributes().get("email");
        }
        
        // Naver의 경우
        Object response = oAuth2User.getAttributes().get("response");
        if (response != null && response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> naverResponse = (java.util.Map<String, Object>) response;
            return (String) naverResponse.get("email");
        }
        
        // 카카오의 경우
        Object kakaoAccount = oAuth2User.getAttributes().get("kakao_account");
        if (kakaoAccount != null && kakaoAccount instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
            return (String) account.get("email");
        }
        
        return null;
    }

    private String extractName(OAuth2User oAuth2User) {
        // Google의 경우
        if (oAuth2User.getAttributes().containsKey("name")) {
            return (String) oAuth2User.getAttributes().get("name");
        }
        
        // Naver의 경우
        Object response = oAuth2User.getAttributes().get("response");
        if (response != null && response instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> naverResponse = (java.util.Map<String, Object>) response;
            return (String) naverResponse.get("name");
        }
        
        // 카카오의 경우 - kakao_account.profile.nickname 시도
        Object kakaoAccount = oAuth2User.getAttributes().get("kakao_account");
        if (kakaoAccount != null && kakaoAccount instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> account = (java.util.Map<String, Object>) kakaoAccount;
            Object profile = account.get("profile");
            if (profile != null && profile instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> profileMap = (java.util.Map<String, Object>) profile;
                return (String) profileMap.get("nickname");
            }
        }
        
        // 카카오의 경우 - properties.nickname 시도
        Object properties = oAuth2User.getAttributes().get("properties");
        if (properties != null && properties instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> propertiesMap = (java.util.Map<String, Object>) properties;
            return (String) propertiesMap.get("nickname");
        }
        
        return null;
    }

    private void saveOrUpdateUser(OAuth2User oAuth2User, String email, String name) {
        try {
            String provider = determineProvider(oAuth2User);
            String providerId = extractProviderId(oAuth2User, provider);
            
            System.out.println("Saving user - Provider: " + provider + ", Email: " + email + ", Name: " + name);
            
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        System.out.println("Creating new user for email: " + email);
                        return createUser(email, name, provider, providerId);
                    });
            
            System.out.println("User saved/found: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String determineProvider(OAuth2User oAuth2User) {
        // Google의 경우 iss 클레임으로 확인
        Object iss = oAuth2User.getAttributes().get("iss");
        if (iss != null && iss.toString().contains("accounts.google.com")) {
            return "google";
        }
        
        // Naver의 경우 response 구조로 확인
        if (oAuth2User.getAttributes().containsKey("response")) {
            return "naver";
        }
        
        // 카카오의 경우 kakao_account 구조로 확인
        if (oAuth2User.getAttributes().containsKey("kakao_account")) {
            return "kakao";
        }
        
        return "unknown";
    }

    private String extractProviderId(OAuth2User oAuth2User, String provider) {
        if ("google".equals(provider)) {
            return oAuth2User.getAttribute("sub");
        } else if ("naver".equals(provider)) {
            Map<String, Object> response = oAuth2User.getAttribute("response");
            return response != null ? (String) response.get("id") : null;
        } else if ("kakao".equals(provider)) {
            Long id = oAuth2User.getAttribute("id");
            return id != null ? id.toString() : null;
        }
        return null;
    }

    private User createUser(String email, String name, String provider, String providerId) {
        User user = new User();
        user.setEmail(email);
        user.setName(name != null ? name : "Unknown");
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
}