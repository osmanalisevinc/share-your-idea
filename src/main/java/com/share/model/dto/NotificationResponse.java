package com.share.model.dto;

import com.share.model.Notifications;
import com.share.model.enums.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {
    private final String id;
    private final String userId;
    private final boolean isRead = true;
    private final LocalDateTime time;
    private final NotificationType type;

    public NotificationResponse(Notifications notification) {
        this.id = notification.getId();
        this.userId = notification.getNotifier().getId();
        this.time = notification.getCreatedAt();
        this.type = notification.getType();
    }
}
