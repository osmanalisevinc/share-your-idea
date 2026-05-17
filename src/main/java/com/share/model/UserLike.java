package com.share.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.ColumnDefault;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "UserLike")
@Table(name = "user_like")
@SQLDelete(sql = "UPDATE user_like SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class UserLike {

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
