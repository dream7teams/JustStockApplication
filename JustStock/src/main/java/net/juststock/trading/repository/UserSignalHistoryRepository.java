package net.juststock.trading.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.market.UserSignalHistory;

public interface UserSignalHistoryRepository extends JpaRepository<UserSignalHistory, Long> {
	@Modifying
	@Transactional
	@Query("delete from UserSignalHistory h where h.adminMessage.expiryDate is not null and h.adminMessage.expiryDate < :today")
	int deleteAllByAdminMessageExpiredBefore(@Param("today") LocalDate today);

	List<UserSignalHistory> findByUserProfile_IdOrderByCreatedAtDesc(Long userId);

	List<UserSignalHistory> findByInstrumentTypeOrderByCreatedAtDesc(InstrumentType instrumentType);

	List<UserSignalHistory> findByAdminMessage_IdOrderByCreatedAtDesc(Long adminMessageId);
}
