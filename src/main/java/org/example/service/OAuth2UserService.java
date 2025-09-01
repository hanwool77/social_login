package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("=== OAuth2UserService.loadUser() CALLED ===");
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            System.out.println("OAuth Provider: " + registrationId);
            System.out.println("User Attributes: " + oAuth2User.getAttributes());
            
            String email = extractEmail(oAuth2User, registrationId);
            String name = extractName(oAuth2User, registrationId);
            String providerId = extractProviderId(oAuth2User, registrationId);

            System.out.println("Extracted - Email: " + email + ", Name: " + name + ", ProviderId: " + providerId);

            if (email != null) {
                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            System.out.println("Creating new user for email: " + email);
                            return createUser(email, name, registrationId, providerId);
                        });
                
                System.out.println("User saved/found: " + user.getEmail());
            } else {
                System.err.println("Email is null for provider: " + registrationId);
            }

            return oAuth2User;
        } catch (Exception e) {
            System.err.println("OAuth2UserService error: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException("Failed to load user: " + e.getMessage());
        }
    }

    private String extractEmail(OAuth2User oAuth2User, String registrationId) {
        try {
            if ("google".equals(registrationId)) {
                return oAuth2User.getAttribute("email");
            } else if ("naver".equals(registrationId)) {
                Map<String, Object> response = oAuth2User.getAttribute("response");
                if (response != null) {
                    return (String) response.get("email");
                }
            } else if ("kakao".equals(registrationId)) {
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                if (kakaoAccount != null) {
                    return (String) kakaoAccount.get("email");
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting email for " + registrationId + ": " + e.getMessage());
        }
        return null;
    }

    private String extractName(OAuth2User oAuth2User, String registrationId) {
        try {
            if ("google".equals(registrationId)) {
                return oAuth2User.getAttribute("name");
            } else if ("naver".equals(registrationId)) {
                Map<String, Object> response = oAuth2User.getAttribute("response");
                if (response != null) {
                    String name = (String) response.get("name");
                    if (name == null) {
                        name = (String) response.get("nickname");
                    }
                    return name;
                }
            } else if ("kakao".equals(registrationId)) {
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                if (kakaoAccount != null) {
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null) {
                        return (String) profile.get("nickname");
                    }
                }
                // fallback to properties.nickname
                Map<String, Object> properties = oAuth2User.getAttribute("properties");
                if (properties != null) {
                    return (String) properties.get("nickname");
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting name for " + registrationId + ": " + e.getMessage());
        }
        return "Unknown";
    }

    private String extractProviderId(OAuth2User oAuth2User, String registrationId) {
        try {
            if ("google".equals(registrationId)) {
                return oAuth2User.getAttribute("sub");
            } else if ("naver".equals(registrationId)) {
                Map<String, Object> response = oAuth2User.getAttribute("response");
                if (response != null) {
                    return (String) response.get("id");
                }
            } else if ("kakao".equals(registrationId)) {
                Long id = oAuth2User.getAttribute("id");
                return id != null ? id.toString() : null;
            }
        } catch (Exception e) {
            System.err.println("Error extracting providerId for " + registrationId + ": " + e.getMessage());
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