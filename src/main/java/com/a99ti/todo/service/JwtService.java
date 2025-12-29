package com.a99ti.todo.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {

    String extractUsername(String Token);
    boolean isTokenValid(String token, UserDetails userDetails);
    String generateToken(Map<String, Object> claims, UserDetails userDetails);
}
