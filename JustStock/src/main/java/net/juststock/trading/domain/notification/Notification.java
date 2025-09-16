package net.juststock.trading.domain.notification;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import net.juststock.trading.domain.user.UserProfile;
import net.juststock.trading.domain.common.InstrumentType;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to the user who will receive the notification
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private UserProfile userProfile;

    // Instrument for which the message is sent
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InstrumentType instrumentType;


    // The actual notification message/advice
    @Column(nullable = false, length = 1000)
    private String message;

    // Whether the user has read the notification
    @Column(nullable = false)
    private boolean readStatus = false;

    // Timestamp of when the notification was created
    @Column(nullable = false)
    private ZonedDateTime createdAt;

    // ===== Getters and Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserProfile getUserProfile() { return userProfile; }
    public void setUserProfile(UserProfile userProfile) { this.userProfile = userProfile; }

    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }

    // signal type removed

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isReadStatus() { return readStatus; }
    public void setReadStatus(boolean readStatus) { this.readStatus = readStatus; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
