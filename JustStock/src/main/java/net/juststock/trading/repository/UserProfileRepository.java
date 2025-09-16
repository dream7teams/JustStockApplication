package net.juststock.trading.repository;

import net.juststock.trading.domain.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByContactNumber(String contactNumber);
    boolean existsByContactNumber(String contactNumber);
    boolean existsById(Long id); // (inherited; shown here for clarity)
}
