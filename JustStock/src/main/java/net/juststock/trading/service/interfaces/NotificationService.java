package net.juststock.trading.service.interfaces;

import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.user.UserProfile;

public interface NotificationService {
    void sendNotification(UserProfile user, InstrumentType instrumentType, String message);
}
