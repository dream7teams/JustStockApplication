//package net.juststock.trading.service.impl;
//
//import net.juststock.trading.domain.common.InstrumentType;
//import net.juststock.trading.domain.common.SignalType;
//import net.juststock.trading.domain.market.UserSignalHistory;
//import net.juststock.trading.dto.AdviceResponseDTO;
//import net.juststock.trading.repository.UserSignalHistoryRepository;
//import net.juststock.trading.service.interfaces.AdminSignalMessageService;
//import net.juststock.trading.service.interfaces.SignalService;
//import org.springframework.stereotype.Service;
//
//import java.time.ZonedDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//@Service
//public class SignalServiceImpl implements SignalService {
//
//    private final AdminSignalMessageService adminMessageService;
//    private final UserSignalHistoryRepository historyRepository;
//
//    public SignalServiceImpl(AdminSignalMessageService adminMessageService,
//                             UserSignalHistoryRepository historyRepository) {
//        this.adminMessageService = adminMessageService;
//        this.historyRepository = historyRepository;
//    }
//
//    @Override
//    public List<String> availableSignalTypes(InstrumentType instrumentType) {
//        // For now, all instruments support CALL/PUT/FUTURE
//        return Arrays.asList(SignalType.CALL.name(), SignalType.PUT.name(), SignalType.FUTURE.name());
//    }
//
//    @Override
//    public AdviceResponseDTO getAdviceAfterPayment(Long userId, InstrumentType instrumentType, SignalType signalType) {
//        String advice = adminMessageService
//                .getOne(signalType, instrumentType)
//                .map(m -> m.getMessage())
//                .orElse("No advice configured. Please contact admin.");
//
//        // Save user-wise history
//        UserSignalHistory h = new UserSignalHistory();
//        h.setId(userId);
//        h.setInstrumentType(instrumentType);
//        h.setSignalType(signalType);
//        h.setAdviceMessage(advice);
//        h.setCreatedAt(ZonedDateTime.now());
//        historyRepository.save(h);
//
//        return new AdviceResponseDTO(userId, instrumentType, signalType, advice);
//    }
//}
