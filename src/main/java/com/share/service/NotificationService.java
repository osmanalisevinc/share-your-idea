package com.share.service;

import com.share.controller.notificationcontroller.response.NotificationResponse;
import com.share.model.Notifications;
import com.share.model.User;
import com.share.model.UserFollower;
import com.share.model.enums.NotificationType;
import com.share.repository.NotificationRepository;
import com.share.repository.UserFollowerRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final AuthService authService;
    private final UserFollowerRepository userFollowerRepository;

    @Transactional
    public void sendSharingNotificationToFollowers(User sharingUser) {
        User currentUser = authService.getCurrentUser();

        // Sadece paylaşım yapan kullanıcının takipçilerine bildirim gönder
        if (sharingUser.getId().equals(currentUser.getId())) {
            List<UserFollower> followers = userFollowerRepository.findByUserAndFollowStatusTrue(sharingUser);

            for (UserFollower follower : followers) {
                createNotification(follower.getFollower(), currentUser, NotificationType.SHARE);
            }
        }
    }

    /**
     * Beğeni işlemi için bildirim gönderir
     */
    @Transactional
    public void sendLikeNotification(User sharingOwner, User liker) {
        if (!sharingOwner.getId().equals(liker.getId())) {
            createNotification(sharingOwner, liker, NotificationType.LIKE);
        }
    }

    /**
     * Beğenmeme işlemi için bildirim gönderir
     */
    @Transactional
    public void sendDislikeNotification(User sharingOwner, User disliker) {
        if (!sharingOwner.getId().equals(disliker.getId())) {
            createNotification(sharingOwner, disliker, NotificationType.DISLIKE);
        }
    }

    /**
     * Yorum işlemi için bildirim gönderir
     */
    @Transactional
    public void sendCommentNotification(User sharingOwner, User commenter) {
        if (!sharingOwner.getId().equals(commenter.getId())) {
            createNotification(sharingOwner, commenter, NotificationType.COMMENT);
        }
    }

    /**
     * Takip işlemi için bildirim gönderir
     */
    @Transactional
    public void sendFollowNotification(User followedUser, User follower) {
        if (!followedUser.getId().equals(follower.getId())) {
            createNotification(followedUser, follower, NotificationType.FOLLOW_REQUEST);
        }
    }

    /**
     * Genel bildirim oluşturma metodu
     */
    @Transactional
    public void createNotification(User owner, User notifier, NotificationType notificationType) {
        // Kendi kendine bildirim göndermeyi engelle
        if (!owner.getId().equals(notifier.getId())) {
            var notification = new Notifications();
            notification.setOwner(owner);
            notification.setNotifier(notifier);
            notification.setType(notificationType);
            notificationRepository.save(notification);
        }
    }

    /**
     * Toplu bildirim gönderme (admin veya sistem bildirimleri için)
     */
    @Transactional
    public void sendBulkNotification(List<User> users, User notifier, NotificationType notificationType) {
        for (User user : users) {
            createNotification(user, notifier, notificationType);
        }
    }

    @Transactional
    public void createNotifications(User sharedUser, NotificationType notificationType) {
        User currentUser = authService.getCurrentUser();

        if (!sharedUser.getId().equals(currentUser.getId())) {
            var notification = new Notifications();
            notification.setOwner(sharedUser);
            notification.setNotifier(currentUser);
            notification.setType(notificationType);
            notificationRepository.save(notification);
        }
    }

    public List<NotificationResponse> getUserNotifications() {
        User currentUser = authService.getCurrentUser();
        return notificationRepository.findByOwnerOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(NotificationResponse::new)
                .toList();
    }
}
