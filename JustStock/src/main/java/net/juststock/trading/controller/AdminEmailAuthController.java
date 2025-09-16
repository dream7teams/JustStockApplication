package net.juststock.trading.controller;

import net.juststock.trading.domain.admin.AdminProfile;
import net.juststock.trading.repository.AdminProfileRepository;
import net.juststock.trading.service.ExpiredSignalsCleanupService;
import net.juststock.trading.service.JwtService;
import net.juststock.trading.dto.AdminLoginReq;
import net.juststock.trading.dto.AdminRegisterReq;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminEmailAuthController {

    private final AdminProfileRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final ExpiredSignalsCleanupService cleanupService;


    public AdminEmailAuthController(AdminProfileRepository adminRepo,
                                    PasswordEncoder passwordEncoder,
                                    AuthenticationManager authManager,
                                    ExpiredSignalsCleanupService cleanupService,
                                    JwtService jwtService) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.cleanupService = cleanupService;

    }

    // ✅ Admin registration
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AdminRegisterReq req) {
        cleanupService.purgeExpiredMessages();
        String email = req.email().toLowerCase();
        String contact = req.contactNumber();
        String password = req.password();

        if (adminRepo.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already registered"));
        }
        if (adminRepo.findByContactNumber(contact).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Contact already registered"));
        }

        AdminProfile admin = new AdminProfile();
        admin.setEmail(email);
        admin.setContactNumber(contact);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setCreatedAt(ZonedDateTime.now());

        adminRepo.save(admin);

        return ResponseEntity.status(201).body(Map.of("message", "Admin registered successfully"));
    }

    // ✅ Admin login
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AdminLoginReq req) {
        cleanupService.purgeExpiredMessages();
        String email = req.email().toLowerCase();
        String password = req.password();

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            AdminProfile admin = adminRepo.findByEmail(email).orElseThrow();

            // ✅ Generate JWT using the service
            String token = jwtService.generateToken(admin);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "adminId", admin.getId(),
                    "email", admin.getEmail()
            ));

        } catch (Exception ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
