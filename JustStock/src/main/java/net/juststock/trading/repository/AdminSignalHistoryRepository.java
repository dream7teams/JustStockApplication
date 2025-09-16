package net.juststock.trading.repository;

import net.juststock.trading.domain.admin.AdminSignalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface AdminSignalHistoryRepository extends JpaRepository<AdminSignalHistory, Long> {
	
	  @Modifying
	    @Transactional
	    @Query("delete from AdminSignalHistory h where h.adminMessage.expiryDate is not null and h.adminMessage.expiryDate < :today")
	    int deleteAllByAdminMessageExpiredBefore(@Param("today") LocalDate today);
	  
    List<AdminSignalHistory> findByAdmin_IdOrderByCreatedAtDesc(Long adminId);
}

