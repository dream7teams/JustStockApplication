package net.juststock.trading.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdminRegisterReq(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$") String contactNumber,
        @NotBlank String password
) {}

