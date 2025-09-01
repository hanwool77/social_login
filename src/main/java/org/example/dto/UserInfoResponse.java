package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserInfoResponse {
    private String email;
    private String name;
    private String provider;
    private LocalDateTime createdAt;
}