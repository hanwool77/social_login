package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String email;
    private Date expiration;
    private String message;
    private String error;

    public TokenValidationResponse() {}

    public TokenValidationResponse(boolean valid) {
        this.valid = valid;
    }

    public TokenValidationResponse(boolean valid, String email, Date expiration) {
        this.valid = valid;
        this.email = email;
        this.expiration = expiration;
    }

    public TokenValidationResponse(boolean valid, String error) {
        this.valid = valid;
        this.error = error;
    }

    public TokenValidationResponse(boolean valid, String email, Date expiration, String message, String error) {
        this.valid = valid;
        this.email = email;
        this.expiration = expiration;
        this.message = message;
        this.error = error;
    }
}