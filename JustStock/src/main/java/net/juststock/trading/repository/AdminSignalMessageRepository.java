package net.juststock.trading.repository;

import net.juststock.trading.domain.admin.AdminSignalMessage;
import net.juststock.trading.domain.common.InstrumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AdminSignalMessageRepository extends JpaRepository<AdminSignalMessage, Long> {
	
	  @Modifying
	    @Transactional
	    @Query("delete from AdminSignalMessage m where m.expiryDate is not null and m.expiryDate < :today")
	    int deleteAllExpiredBefore(@Param("today") LocalDate today);
	
    List<AdminSignalMessage> findByInstrumentType(InstrumentType instrumentType);
    Optional<AdminSignalMessage> findTopByInstrumentTypeOrderByIdDesc(InstrumentType instrumentType);
    List<AdminSignalMessage> findByCreatedBy_IdOrderByCreatedAtDesc(Long createdById);
}
