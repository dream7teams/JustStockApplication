package net.juststock.trading.service.messaging;

import net.juststock.trading.dto.NotificationDTO;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationBroadcaster {

    private final Map<Long, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        // 0L = no timeout; client should reconnect if needed
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> remove(userId, emitter));
        emitter.onTimeout(() -> remove(userId, emitter));
        emitter.onError(e -> remove(userId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("init")
                    .data("connected")
                    .id(String.valueOf(System.currentTimeMillis()))
                    .reconnectTime(5000));
        } catch (IOException ignored) {}
        return emitter;
    }

    public void pushToUser(Long userId, NotificationDTO dto) {
        List<SseEmitter> list = subscribers.get(userId);
        if (list == null || list.isEmpty()) return;
        for (SseEmitter emitter : list) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(dto, MediaType.APPLICATION_JSON)
                        .id(String.valueOf(dto.id())));
            } catch (IOException e) {
                remove(userId, emitter);
            }
        }
    }

    private void remove(Long userId, SseEmitter emitter) {
        List<SseEmitter> list = subscribers.get(userId);
        if (list != null) {
            list.remove(emitter);
            if (list.isEmpty()) subscribers.remove(userId);
        }
    }
}

