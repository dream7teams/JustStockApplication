package net.juststock.trading.controller;

import jakarta.validation.Valid;
import net.juststock.trading.service.ExpiredSignalsCleanupService;
import net.juststock.trading.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.juststock.trading.domain.user.UserProfile;
import net.juststock.trading.dto.LoginResponse;
import net.juststock.trading.dto.UserProfileDTO;
import net.juststock.trading.dto.RequestOtpReq;
import net.juststock.trading.dto.VerifyOtpReq;
import net.juststock.trading.dto.RegisterReq;
import net.juststock.trading.repository.UserProfileRepository;
import net.juststock.trading.repository.OtpSessionRepository;
import net.juststock.trading.service.interfaces.OtpService;
import net.juststock.trading.util.PhoneNormalizer;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserProfileRepository users;
    private final JwtService jwt;
    private final OtpService otpService;
    private final OtpSessionRepository otpRepo;
    private final ExpiredSignalsCleanupService cleanupService;

    public UserAuthController(ExpiredSignalsCleanupService cleanupService,
                              UserProfileRepository users,
                              JwtService jwt,
                              OtpService otpService,
                              OtpSessionRepository otpRepo) {
        this.users = users;
        this.jwt = jwt;
        this.otpService = otpService;
        this.otpRepo = otpRepo;
        this.cleanupService = cleanupService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody RequestOtpReq req) {
        // purge expired messages on OTP send
        cleanupService.purgeExpiredMessages();

        otpService.requestOtp(req.phone());
        return ResponseEntity.ok(Map.of("message", "OTP generated"));
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend(@Valid @RequestBody RequestOtpReq req) {
        otpService.resendOtp(req.phone());
        return ResponseEntity.ok(Map.of("message", "OTP resent"));
    }

    // ✅ Verify OTP and issue only access token
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyOtpReq req) {

        // purge expired messages on login
        cleanupService.purgeExpiredMessages();

        boolean ok = otpService.verifyOtp(req.phone(), req.code());
        if (!ok) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired OTP"));
        }

        var optUser = users.findByContactNumber(req.phone());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not registered", "requiresRegistration", true));
        }

        UserProfile user = optUser.get();
        int ver = user.getTokenVersion();

        String access = jwt.createAccessToken(
                user.getContactNumber(),
                Map.of(
                        "uid", user.getId(),
                        "ver", ver,
                        "name", user.getFullName(),
                        "phone", user.getContactNumber()
                )
        );

        // Cleanup OTP sessions (both raw & normalized)
        cleanupOtp(req.phone());

        return ResponseEntity.ok(
                new LoginResponse(
                        access,
                        null, // no refresh token
                        new UserProfileDTO(user.getId(), user.getFullName(), user.getContactNumber())
                )
        );
    }

    // ✅ Logout bumps tokenVersion to invalidate existing access tokens
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // purge expired messages on logout as well
        cleanupService.purgeExpiredMessages();

        try {
            String phone = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String access = authHeader.substring(7);
                var jws = jwt.parse(access);
                phone = jws.getBody().getSubject();
            }
            if (phone == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Missing token"));
            }

            var user = users.findByContactNumber(phone).orElse(null);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }

            user.setTokenVersion(user.getTokenVersion() + 1);
            users.save(user);

            cleanupOtp(phone);
            return ResponseEntity.ok(Map.of("message", "Logged out"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "Logout failed"));
        }
    }

    // ✅ Register new user, return only access token
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req) {

        // purge expired messages on registration too
        cleanupService.purgeExpiredMessages();

        String normalizedPhone = PhoneNormalizer.normalizeE164Loose(req.phone());
        if (users.existsByContactNumber(normalizedPhone)) {
            return ResponseEntity.status(409).body(Map.of("error", "Contact already registered"));
        }

        boolean ok = otpService.verifyOtp(req.phone(), req.code());
        if (!ok) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired OTP"));
        }

        UserProfile user = users.findByContactNumber(req.phone()).orElseGet(() -> {
            UserProfile u = new UserProfile();
            u.setContactNumber(req.phone());
            u.setFullName(req.fullName());
            return users.save(u);
        });

        int ver = user.getTokenVersion();

        String access = jwt.createAccessToken(
                user.getContactNumber(),
                Map.of(
                        "uid", user.getId(),
                        "ver", ver,
                        "name", user.getFullName(),
                        "phone", user.getContactNumber()
                )
        );

        cleanupOtp(req.phone());

        return ResponseEntity.ok(
                new LoginResponse(
                        access,
                        null, // no refresh token
                        new UserProfileDTO(user.getId(), user.getFullName(), user.getContactNumber())
                )
        );
    }

    // 🔹 helper for cleanup
    private void cleanupOtp(String raw) {
        String normalized = PhoneNormalizer.normalizeE164Loose(raw);
        try {
            otpRepo.deleteByMobileNumber(raw);
            if (normalized != null && !normalized.equals(raw)) {
                otpRepo.deleteByMobileNumber(normalized);
            }
        } catch (Exception ignored) {
        }
    }
}
