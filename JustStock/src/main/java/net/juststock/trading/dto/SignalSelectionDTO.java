package net.juststock.trading.dto;

import net.juststock.trading.domain.common.InstrumentType;

public class SignalSelectionDTO {
    private InstrumentType instrumentType; // NIFTY, BANKNIFTY, STOCK, COMMODITY, SENSEX

    // getter/setter
    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }
}
