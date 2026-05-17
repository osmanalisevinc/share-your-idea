package com.share.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "UserDisLike")
@Table(name = "user_dislike")
@SQLDelete(sql = "UPDATE user_dislike SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class UserDislike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sharing_id")
    private Sharing sharing;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;
}


