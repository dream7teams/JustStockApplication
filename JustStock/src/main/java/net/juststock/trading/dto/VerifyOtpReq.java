package net.juststock.trading.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpReq(
        @NotBlank @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$") String phone,
        @NotBlank @Pattern(regexp = "^\\d{4,6}$") String code
) {}

