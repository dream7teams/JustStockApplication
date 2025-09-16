package net.juststock.trading.dto;

public class AdminSignalMessageDTO {
    private String email;       // <- from request
    private String instrument;
    private String message;
    private String expiryDate;  // dd/MM/yyyy

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getInstrument() { return instrument; }
    public void setInstrument(String instrument) { this.instrument = instrument; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}
