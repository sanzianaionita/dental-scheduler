package com.example.dentalscheduler.security.jwt.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class TokenDetails {

    private Date expirationDate;
    private Date issuedAt;
    private String username;
    private String token;
}
