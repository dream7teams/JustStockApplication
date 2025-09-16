package net.juststock.trading.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminLoginReq(
        @NotBlank @Email String email,
        @NotBlank String password
) {}

