package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProtectedDataResponse {
    private String message;
    private String user;
    private LocalDateTime timestamp;
    private String data;
}