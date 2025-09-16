package net.juststock.trading.domain.admin;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import net.juststock.trading.domain.common.InstrumentType;

@Entity
@Table(name = "admin_signal_message", indexes = {
		@Index(name = "idx_admin_signal_message_created_at", columnList = "createdAt"),
		@Index(name = "idx_admin_signal_message_instrument", columnList = "instrumentType") })
public class AdminSignalMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	/** Required: who created this message */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_admin_id", nullable = false)
	private AdminProfile createdBy;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private InstrumentType instrumentType;

	@Column(length = 1000, nullable = false)
	private String message;

	/** Optional: expiry for the advisory (parsed from DTO if present) */
	@Column
	private LocalDate expiryDate;

	@Column(nullable = false)
	private ZonedDateTime createdAt;

	/* ---------- lifecycle ---------- */
	@PrePersist
	protected void onCreate() {
		if (createdAt == null)
			createdAt = ZonedDateTime.now();
	}

	/* ---------- getters & setters ---------- */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AdminProfile getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AdminProfile createdBy) {
		this.createdBy = createdBy;
	} // <-- add this

	public InstrumentType getInstrumentType() {
		return instrumentType;
	}

	public void setInstrumentType(InstrumentType instrumentType) {
		this.instrumentType = instrumentType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
