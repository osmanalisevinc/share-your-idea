package com.share.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "UserFollower")
@Table(name = "user_follower")
@SQLDelete(sql = "UPDATE user_follower SET follow_status = false WHERE id = ?")
@Where(clause = "follow_status = true")
public class UserFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Takip edilen kullanıcı

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower; // Takip eden kullanıcı

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "follow_status", nullable = false)
    @ColumnDefault("true")
    private Boolean followStatus = true;
}
