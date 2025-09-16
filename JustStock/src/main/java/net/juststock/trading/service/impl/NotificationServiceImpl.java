package net.juststock.trading.service.impl;

import net.juststock.trading.domain.common.InstrumentType;
import net.juststock.trading.domain.notification.Notification;
import net.juststock.trading.domain.user.UserProfile;
import net.juststock.trading.dto.NotificationDTO;
import net.juststock.trading.repository.NotificationRepository;
import net.juststock.trading.service.interfaces.NotificationService;
import net.juststock.trading.service.messaging.NotificationBroadcaster;
import net.juststock.trading.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationBroadcaster broadcaster;
    private final UserProfileRepository userProfileRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationBroadcaster broadcaster,
                                   UserProfileRepository userProfileRepository) {
        this.notificationRepository = notificationRepository;
        this.broadcaster = broadcaster;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public void sendNotification(UserProfile user,
                                 InstrumentType instrumentType,
                                 String message) {
        Notification notification = new Notification();
        // use a managed reference to avoid detached entity issues
        notification.setUserProfile(userProfileRepository.getReferenceById(user.getId()));
        notification.setInstrumentType(instrumentType);
        notification.setMessage(message);
        notification.setCreatedAt(ZonedDateTime.now());
        Notification saved = notificationRepository.save(notification);

        // Low-latency push to frontend subscribers
        try {
            broadcaster.pushToUser(user.getId(), NotificationDTO.fromEntity(saved));
        } catch (Exception ignored) {}

        // Additional channels (email/SMS/push) can be integrated here.
    }
}
