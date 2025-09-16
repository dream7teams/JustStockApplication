package net.juststock.trading.dto;

public record LoginResponse(String accessToken, String refreshToken, UserProfileDTO user) {}

