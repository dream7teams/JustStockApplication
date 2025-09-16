package net.juststock.trading.service.impl;

import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.repository.AdminSignalMessageRepository;
import net.juststock.trading.service.interfaces.AdminSignalMessageService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminSignalMessageServiceImpl implements AdminSignalMessageService {

    private final AdminSignalMessageRepository repo;

    public AdminSignalMessageServiceImpl(AdminSignalMessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public AdminSignalMessage save(AdminSignalMessage msg) {
        return repo.save(msg);
    }

    @Override
    public List<AdminSignalMessage> getByInstrument(InstrumentType instrumentType) {
        return repo.findByInstrumentType(instrumentType);
    }

    @Override
    public Optional<AdminSignalMessage> getLatestByInstrument(InstrumentType instrumentType) {
        return repo.findTopByInstrumentTypeOrderByIdDesc(instrumentType);
    }
}
