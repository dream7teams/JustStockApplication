package net.juststock.trading.controller;

import net.juststock.trading.domain.admin.AdminProfile;
import net.juststock.trading.domain.admin.AdminSignalHistory;
import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.market.UserSignalHistory;
import net.juststock.trading.domain.user.UserProfile;
import net.juststock.trading.dto.AdminSignalMessageDTO;
import net.juststock.trading.dto.AdminSignalMessageResponseDTO;
import net.juststock.trading.repository.AdminProfileRepository;
import net.juststock.trading.repository.AdminSignalHistoryRepository;
import net.juststock.trading.repository.AdminSignalMessageRepository;
import net.juststock.trading.repository.UserProfileRepository;
import net.juststock.trading.repository.UserSignalHistoryRepository;
import net.juststock.trading.service.interfaces.AdminSignalMessageService;
import net.juststock.trading.service.interfaces.NotificationService;
import net.juststock.trading.service.interfaces.UserProfileService;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adminmessage/messages")
public class AdminSignalMessageController {

    private final AdminSignalMessageService adminService;
    private final UserProfileService profileService;
    private final NotificationService notificationService;
    private final UserSignalHistoryRepository historyRepository;
    private final AdminSignalMessageRepository adminMessageRepository;
    private final AdminSignalHistoryRepository adminHistoryRepository;
    private final UserProfileRepository userProfileRepository;
    private final AdminProfileRepository adminProfileRepository;

    public AdminSignalMessageController(
            AdminSignalMessageService adminService,
            UserProfileService profileService,
            NotificationService notificationService,
            UserSignalHistoryRepository historyRepository,
            AdminSignalMessageRepository adminMessageRepository,
            AdminSignalHistoryRepository adminHistoryRepository,
            UserProfileRepository userProfileRepository,
            AdminProfileRepository adminProfileRepository
    ) {
        this.adminService = adminService;
        this.notificationService = notificationService;
        this.profileService = profileService;
        this.historyRepository = historyRepository;
        this.adminMessageRepository = adminMessageRepository;
        this.adminHistoryRepository = adminHistoryRepository;
        this.userProfileRepository = userProfileRepository;
        this.adminProfileRepository = adminProfileRepository;
    }


 @PostMapping
 @Transactional
 public ResponseEntity<?> createMessage(@RequestBody AdminSignalMessageDTO dto) {

     // Log incoming data
     System.out.println(
         (dto.getInstrument() == null ? "" : dto.getInstrument()) + " " +
         (dto.getMessage() == null ? "" : dto.getMessage())     + "\t" +
         (dto.getExpiryDate() == null ? "" : dto.getExpiryDate()) +
         " email=" + (dto.getEmail() == null ? "" : dto.getEmail())
     );

     // ---- Validate required fields ----
     if (dto.getEmail() == null || dto.getEmail().isBlank()) {
         return ResponseEntity.badRequest().body(Map.of("error", "Admin email is required"));
     }
     if (dto.getInstrument() == null || dto.getInstrument().isBlank()) {
         return ResponseEntity.badRequest().body(Map.of("error", "Instrument is required"));
     }
     if (dto.getMessage() == null || dto.getMessage().isBlank()) {
         return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be blank"));
     }

     // ---- Load admin and reattach as managed reference ----
     var adminOpt = adminProfileRepository.findByEmailIgnoreCase(dto.getEmail());
     if (adminOpt.isEmpty()) {
         return ResponseEntity.status(403).body(Map.of(
             "error", "Forbidden: Admin email not registered -> " + dto.getEmail()
         ));
     }
     Long adminId = adminOpt.get().getId();
     // Very important: use a managed reference (avoids uninitialized proxy issues)
     AdminProfile adminRef = adminProfileRepository.getReferenceById(adminId);

     // ---- Build and save AdminSignalMessage ----
     AdminSignalMessage message = new AdminSignalMessage();
     message.setEmail(dto.getEmail());                // also persist the raw email string
     message.setCreatedBy(adminRef);                  // managed reference
     message.setCreatedAt(ZonedDateTime.now());
     message.setMessage(dto.getMessage().trim());

     try {
         message.setInstrumentType(InstrumentType.parse(dto.getInstrument())); // supports NIFTY50
     } catch (IllegalArgumentException e) {
         return ResponseEntity.badRequest().body(Map.of(
             "error", "Invalid instrument type. Use: NIFTY/NIFTY50, BANKNIFTY, SENSEX, STOCK, COMMODITY"
         ));
     }

     if (dto.getExpiryDate() != null && !dto.getExpiryDate().isBlank()) {
         try {
             var fmt = DateTimeFormatter.ofPattern("dd/MM/uuuu");
             LocalDate expiry = LocalDate.parse(dto.getExpiryDate().trim(), fmt);
             message.setExpiryDate(expiry);
         } catch (Exception ex) {
             return ResponseEntity.badRequest().body(Map.of(
                 "error", "Invalid expiryDate format. Use dd/MM/yyyy (e.g., 12/12/2025)"
             ));
         }
     }

     AdminSignalMessage saved = adminService.save(message);

     // ---- Notify all users & append per-user history ----
     List<UserProfile> users = profileService.getAllUsers();
     int recipientCount = 0;

     for (UserProfile user : users) {
         // Send notification
         notificationService.sendNotification(user, saved.getInstrumentType(), saved.getMessage());

         // IMPORTANT: use a managed reference for the user association
         Long uid = user.getId();
         UserProfile userRef = userProfileRepository.getReferenceById(uid);

         UserSignalHistory userHistory = new UserSignalHistory();
         userHistory.setUserProfile(userRef);                 // managed reference
         userHistory.setAdminMessage(saved);
         userHistory.setInstrumentType(saved.getInstrumentType());
         userHistory.setMessage(saved.getMessage());
         userHistory.setCreatedAt(ZonedDateTime.now());
         historyRepository.save(userHistory);

         recipientCount++;
     }

     // ---- Save admin-side history (use adminRef) ----
     AdminSignalHistory adminHistory = new AdminSignalHistory();
     adminHistory.setAdmin(adminRef);                         // managed reference
     adminHistory.setAdminMessage(saved);
     adminHistory.setInstrumentType(saved.getInstrumentType());
     adminHistory.setMessage(saved.getMessage());
     adminHistory.setRecipientCount(recipientCount);
     adminHistory.setCreatedAt(ZonedDateTime.now());
     adminHistoryRepository.save(adminHistory);


     AdminSignalMessageResponseDTO response = new AdminSignalMessageResponseDTO(
    	        new AdminSignalMessageResponseDTO.CreatedByDTO(
    	                saved.getCreatedBy().getEmail(),
    	                saved.getCreatedBy().getContactNumber()
    	        ),
    	        saved.getInstrumentType().name(),
    	        saved.getMessage(),
    	        saved.getExpiryDate(),
    	        saved.getCreatedAt()
    	);

    	return ResponseEntity.status(201).body(response);

 
 }


    // 🔹 Get all messages for a given instrument type
    @GetMapping("/instrument/{instrumentType}")
    public ResponseEntity<List<AdminSignalMessage>> getByInstrument(@PathVariable InstrumentType instrumentType) {
        return ResponseEntity.ok(adminService.getByInstrument(instrumentType));
    }

    // 🔹 Get user-wise signal history
    @GetMapping("/history/user/{userId}")
    public ResponseEntity<List<UserSignalHistory>> getHistoryByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(historyRepository.findByUserProfile_IdOrderByCreatedAtDesc(userId));
    }

    // 🔹 Get history for a specific admin message
    @GetMapping("/history/message/{messageId}")
    public ResponseEntity<List<UserSignalHistory>> getHistoryByAdminMessage(@PathVariable Long messageId) {
        return ResponseEntity.ok(historyRepository.findByAdminMessage_IdOrderByCreatedAtDesc(messageId));
    }

    // 🔹 Get messages created by a specific admin
    @GetMapping("/admin/{adminUserId}")
    public ResponseEntity<List<AdminSignalMessage>> getMessagesByAdmin(@PathVariable Long adminUserId) {
        return ResponseEntity.ok(adminMessageRepository.findByCreatedBy_IdOrderByCreatedAtDesc(adminUserId));
    }
}
