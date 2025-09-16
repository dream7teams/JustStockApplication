package net.juststock.trading.domain.market;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.user.UserProfile;

@Entity
@Table(name = "user_signal_history")
public class UserSignalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_message_id", nullable = false)
    private AdminSignalMessage adminMessage;

    @Enumerated(EnumType.STRING)
    private InstrumentType instrumentType;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

    public AdminSignalMessage getAdminMessage() { return adminMessage; }
    public void setAdminMessage(AdminSignalMessage adminMessage) { this.adminMessage = adminMessage; }

    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
