package com.share.repository;

import com.share.model.Notifications;
import com.share.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notifications, String> {
    List<Notifications> findByOwnerOrderByCreatedAtDesc(User currentUser);
}
