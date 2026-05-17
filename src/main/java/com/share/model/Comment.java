package com.share.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "comment")
@Table(name = "comment")
@SQLDelete(sql = "UPDATE comment SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sharing_id")  // foreign key kolonunun adını belirt
    private Sharing sharing;

    @ManyToOne(fetch = FetchType.EAGER)
    private User commentedUser;

    @Column(length = 1000)
    @NotBlank
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;
}
