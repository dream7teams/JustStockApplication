package net.juststock.trading.dto;

import net.juststock.trading.domain.common.InstrumentType;

public class PaymentRequestDTO {
    private Long userId;
    private InstrumentType instrumentType;
    private Integer amount; // in INR

    // getters/setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}
