package net.juststock.trading.domain.user;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import net.juststock.trading.domain.market.UserSignalHistory;



@Table(name = "user_profile")
@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, unique = true, length = 15)
    private String contactNumber;
    
    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Token version used to invalidate JWTs on logout
    @Column(nullable = false)
    private int tokenVersion = 0;



//    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Payment> payments;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSignalHistory> signalHistories;

    // ✅ Add this constructor
    public UserProfile(Long id, String fullName, String contactNumber) {
        this.id = id;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
    }

    // Optional: constructor for just contactNumber
    public UserProfile(String contactNumber) {
        this.contactNumber = contactNumber;
    }
         
    // Default constructor (required by JPA)
    public UserProfile() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getTokenVersion() {
		return tokenVersion;
	}

	public void setTokenVersion(int tokenVersion) {
		this.tokenVersion = tokenVersion;
	}

	public List<UserSignalHistory> getSignalHistories() {
		return signalHistories;
	}

	public void setSignalHistories(List<UserSignalHistory> signalHistories) {
		this.signalHistories = signalHistories;
	}

	@Override
	public String toString() {
		return "UserProfile [id=" + id + ", fullName=" + fullName + ", contactNumber=" + contactNumber + ", createdAt="
				+ createdAt + ", tokenVersion=" + tokenVersion + ", signalHistories=" + signalHistories + "]";
	}

    
    
}
