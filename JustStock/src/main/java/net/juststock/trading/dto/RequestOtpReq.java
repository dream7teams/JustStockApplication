package net.juststock.trading.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RequestOtpReq(
        @NotBlank
        @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Invalid phone number format") // E.164 with optional +
        String phone) {
}
