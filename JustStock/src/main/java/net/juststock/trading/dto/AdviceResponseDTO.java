package net.juststock.trading.dto;

import net.juststock.trading.domain.common.InstrumentType;

public class AdviceResponseDTO {
    private Long userId;
    private InstrumentType instrumentType;
    private String advice;

    public AdviceResponseDTO() {}

    public AdviceResponseDTO(Long userId, InstrumentType instrumentType, String advice) {
        this.userId = userId; this.instrumentType = instrumentType; this.advice = advice;
    }

    // getters/setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }
    public String getAdvice() { return advice; }
    public void setAdvice(String advice) { this.advice = advice; }
}
