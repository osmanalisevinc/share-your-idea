package com.share.controller.notificationcontroller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {
    private String id;
    private String type;
    private String notifierUserSurname;
    private String notifierUsername;
    private byte[] photo;
    private boolean isRead;
    private String createdAt;

    public NotificationResponse(com.share.model.Notifications notification) {
        this.id = notification.getId();
        this.type = notification.getType().label();
        this.notifierUserSurname = notification.getNotifier().getUserSurname();
        this.notifierUsername = notification.getNotifier().getUserName();
        this.photo = notification.getNotifier().getProfilePhoto();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt().toString();
    }
}
