package net.juststock.trading.dto;

public class PaymentResponseDTO {
    private String status; // SUCCESS/FAILED
    private String transactionId;
    private String message;

    public PaymentResponseDTO() {}
    public PaymentResponseDTO(String status, String transactionId, String message) {
        this.status = status; this.transactionId = transactionId; this.message = message;
    }

    // getters/setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
