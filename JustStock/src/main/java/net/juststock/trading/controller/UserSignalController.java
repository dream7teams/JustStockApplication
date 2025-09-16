package net.juststock.trading.controller;

import jakarta.validation.Valid;
import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.market.UserSignalHistory;
import net.juststock.trading.dto.SelectAdminMessageReq;
import net.juststock.trading.dto.UserSignalHistoryResponseDTO;
import net.juststock.trading.repository.AdminSignalMessageRepository;
import net.juststock.trading.repository.UserProfileRepository;
import net.juststock.trading.repository.UserSignalHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/messages")
public class UserSignalController {

    private final AdminSignalMessageRepository adminMessageRepository;
    private final UserSignalHistoryRepository historyRepository;
    private final UserProfileRepository userProfileRepository;

    public UserSignalController(AdminSignalMessageRepository adminMessageRepository,
                                UserSignalHistoryRepository historyRepository,
                                UserProfileRepository userProfileRepository) {
        this.adminMessageRepository = adminMessageRepository;
        this.historyRepository = historyRepository;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Public endpoint (no auth): user selects an admin message; store in history.
     * Body must include either userId or phone.
     */
    @PostMapping("/select")
    @Transactional
    public ResponseEntity<?> selectMessage(@Valid @RequestBody SelectAdminMessageReq req) {

        // 1) Load admin message
        AdminSignalMessage adminMsg = adminMessageRepository.findById(req.messageId()).orElse(null);
        if (adminMsg == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Admin message not found"));
        }

        // 2) Resolve user (by userId or phone)
        Long userId = req.userId();
        if (userId == null && (req.phone() == null || req.phone().isBlank())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Either userId or phone is required"
            ));
        }

        if (userId == null) {
            var userOpt = userProfileRepository.findByContactNumber(req.phone());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found for phone: " + req.phone()
                ));
            }
            userId = userOpt.get().getId();
        } else {
            if (!userProfileRepository.existsById(userId)) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found for id: " + userId
                ));
            }
        }

        // Managed JPA reference avoids uninitialized proxy errors
        var userRef = userProfileRepository.getReferenceById(userId);

        // 3) Create and save history
        UserSignalHistory h = new UserSignalHistory();
        h.setUserProfile(userRef); // expects UserProfile entity
        h.setAdminMessage(adminMsg);
        h.setInstrumentType(adminMsg.getInstrumentType());
        h.setMessage(adminMsg.getMessage());
        h.setCreatedAt(ZonedDateTime.now());

        UserSignalHistory saved = historyRepository.save(h);

        // 4) Map to DTO to avoid serializing lazy associations
        var resp = new UserSignalHistoryResponseDTO(
                saved.getId(),
                userId,
                adminMsg.getId(),
                saved.getInstrumentType().name(),
                saved.getMessage(),
                saved.getCreatedAt()
        );

        return ResponseEntity.status(201).body(resp);
    }

    /**
     * Public (or make it authenticated if you prefer):
     * Get a user's signal history by userId OR phone (one must be provided).
     * Returns DTOs to avoid lazy-loading issues.
     */
    @GetMapping("/history")
    public ResponseEntity<?> userHistory(@RequestParam(required = false) Long userId,
                                         @RequestParam(required = false) String phone) {

        if (userId == null && (phone == null || phone.isBlank())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Either userId or phone is required"
            ));
        }

        if (userId == null) {
            var userOpt = userProfileRepository.findByContactNumber(phone);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found for phone: " + phone
                ));
            }
            userId = userOpt.get().getId();
        } else if (!userProfileRepository.existsById(userId)) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found for id: " + userId
            ));
        }

        // make a final copy for the lambda
        final Long uid = userId;

        var items = historyRepository.findByUserProfile_IdOrderByCreatedAtDesc(uid);

        List<UserSignalHistoryResponseDTO> dto = items.stream()
                .map(h -> new UserSignalHistoryResponseDTO(
                        h.getId(),
                        uid,
                        h.getAdminMessage() != null ? h.getAdminMessage().getId() : null,
                        h.getInstrumentType() != null ? h.getInstrumentType().name() : null,
                        h.getMessage(),
                        h.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(dto);
    }

}
