package net.juststock.trading.dto;

import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.notification.Notification;

import java.time.ZonedDateTime;

public record NotificationDTO(
        Long id,
        InstrumentType instrumentType,
        String message,
        boolean readStatus,
        ZonedDateTime createdAt
) {
    public static NotificationDTO fromEntity(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getInstrumentType(),
                n.getMessage(),
                n.isReadStatus(),
                n.getCreatedAt()
        );
    }
}
