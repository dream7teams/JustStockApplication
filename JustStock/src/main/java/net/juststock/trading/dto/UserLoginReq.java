package net.juststock.trading.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserLoginReq(
        @NotBlank String fullName,
        @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$") String contactNumber
) {}

