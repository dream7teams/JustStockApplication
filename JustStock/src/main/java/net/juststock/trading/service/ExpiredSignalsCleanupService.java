package net.juststock.trading.service;

import net.juststock.trading.repository.AdminSignalHistoryRepository;
import net.juststock.trading.repository.AdminSignalMessageRepository;
import net.juststock.trading.repository.UserSignalHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ExpiredSignalsCleanupService {

    private final UserSignalHistoryRepository userHistoryRepo;
    private final AdminSignalHistoryRepository adminHistoryRepo;
    private final AdminSignalMessageRepository messageRepo;

    public ExpiredSignalsCleanupService(UserSignalHistoryRepository userHistoryRepo,
                                        AdminSignalHistoryRepository adminHistoryRepo,
                                        AdminSignalMessageRepository messageRepo) {
        this.userHistoryRepo = userHistoryRepo;
        this.adminHistoryRepo = adminHistoryRepo;
        this.messageRepo = messageRepo;
    }

    /** Deletes all rows where expiryDate < today (histories first, then messages). */
    @Transactional
    public int purgeExpiredMessages() {
        LocalDate today = LocalDate.now(); // server timezone; switch to ZoneId.of("Asia/Kolkata") if needed elsewhere

        int u = userHistoryRepo.deleteAllByAdminMessageExpiredBefore(today);
        int a = adminHistoryRepo.deleteAllByAdminMessageExpiredBefore(today);
        int m = messageRepo.deleteAllExpiredBefore(today);

        return u + a + m; // total rows deleted (for logs if you want)
    }
}
