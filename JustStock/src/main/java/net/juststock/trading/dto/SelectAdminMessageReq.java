package net.juststock.trading.dto;

import jakarta.validation.constraints.NotNull;

/**
 * For public select API: either userId or phone must be supplied.
 */
public record SelectAdminMessageReq(
        @NotNull Long messageId,
        Long userId,
        String phone
) {}
