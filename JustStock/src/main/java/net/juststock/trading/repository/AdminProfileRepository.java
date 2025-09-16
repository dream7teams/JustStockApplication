package net.juststock.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import net.juststock.trading.domain.admin.AdminProfile;

import java.util.Optional;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {

    // Existence check
    boolean existsByContactNumber(String contactNumber);
    boolean existsByEmail(String email);

    // Finders
    Optional<AdminProfile> findByContactNumber(String contactNumber);
    Optional<AdminProfile> findByEmail(String email);
    Optional<AdminProfile> findByEmailIgnoreCase(String email); // <-- new
}
