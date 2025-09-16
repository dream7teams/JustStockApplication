package net.juststock.trading.domain.admin;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

import net.juststock.trading.domain.common.InstrumentType;

@Entity
@Table(
    name = "admin_signal_history",
    indexes = {
        @Index(name = "idx_admin_signal_history_created_at", columnList = "createdAt"),
        @Index(name = "idx_admin_signal_history_instrument", columnList = "instrumentType")
    }
)
public class AdminSignalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** If you aren't wiring Authentication yet, keep this nullable=true to avoid constraint errors. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true) // was false; make true if you’re not setting it
    private AdminProfile admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_message_id", nullable = false)
    private AdminSignalMessage adminMessage;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private InstrumentType instrumentType;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private int recipientCount;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    /* ---------- lifecycle ---------- */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = ZonedDateTime.now();
    }

    /* ---------- getters & setters ---------- */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AdminProfile getAdmin() { return admin; }
    public void setAdmin(AdminProfile admin) { this.admin = admin; }

    public AdminSignalMessage getAdminMessage() { return adminMessage; }
    public void setAdminMessage(AdminSignalMessage adminMessage) { this.adminMessage = adminMessage; }

    public InstrumentType getInstrumentType() { return instrumentType; }
    public void setInstrumentType(InstrumentType instrumentType) { this.instrumentType = instrumentType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRecipientCount() { return recipientCount; }
    public void setRecipientCount(int recipientCount) { this.recipientCount = recipientCount; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
