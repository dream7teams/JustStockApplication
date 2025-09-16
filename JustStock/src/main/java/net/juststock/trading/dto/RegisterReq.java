package net.juststock.trading.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterReq(
        @NotBlank String phone,
        @NotBlank String code,
        @NotBlank String fullName
) {}

