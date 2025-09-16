package net.juststock.trading.dto;

import java.time.ZonedDateTime;

public class UserSignalHistoryResponseDTO {
    private Long id;
    private Long userId;
    private Long adminMessageId;
    private String instrumentType;
    private String message;
    private ZonedDateTime createdAt;

    public UserSignalHistoryResponseDTO(Long id,
                                        Long userId,
                                        Long adminMessageId,
                                        String instrumentType,
                                        String message,
                                        ZonedDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.adminMessageId = adminMessageId;
        this.instrumentType = instrumentType;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getAdminMessageId() { return adminMessageId; }
    public String getInstrumentType() { return instrumentType; }
    public String getMessage() { return message; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}
