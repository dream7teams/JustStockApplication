package net.juststock.trading.dto;


import java.time.LocalDate;
import java.time.ZonedDateTime;

public class AdminSignalMessageResponseDTO {
    private CreatedByDTO createdBy;
    private String instrumentType;
    private String message;
    private LocalDate expiryDate;
    private ZonedDateTime createdAt;

    // nested DTO for createdBy
    public static class CreatedByDTO {
        private String email;
        private String contactNumber;

        public CreatedByDTO(String email, String contactNumber) {
            this.email = email;
            this.contactNumber = contactNumber;
        }

        public String getEmail() { return email; }
        public String getContactNumber() { return contactNumber; }
    }

    public AdminSignalMessageResponseDTO(CreatedByDTO createdBy, String instrumentType,
                                         String message, LocalDate expiryDate, ZonedDateTime createdAt) {
        this.createdBy = createdBy;
        this.instrumentType = instrumentType;
        this.message = message;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public CreatedByDTO getCreatedBy() { return createdBy; }
    public String getInstrumentType() { return instrumentType; }
    public String getMessage() { return message; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
}
