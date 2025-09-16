package net.juststock.trading.service.interfaces;

import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.common.InstrumentType;

import java.util.List;
import java.util.Optional;

public interface AdminSignalMessageService {
    AdminSignalMessage save(AdminSignalMessage msg);
    List<AdminSignalMessage> getByInstrument(InstrumentType instrumentType);
    Optional<AdminSignalMessage> getLatestByInstrument(InstrumentType instrumentType);
}
