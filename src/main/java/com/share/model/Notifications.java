package com.share.model;

import com.share.model.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "notifications")
@Table(name = "notifications")
@SQLDelete(sql = "UPDATE notifications SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.EAGER)
    private User owner; // Bildirimi alan kullanıcı

    @ManyToOne(fetch = FetchType.EAGER)
    private User notifier; // Bildirimi tetikleyen kullanıcı

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private boolean isRead = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;
}
