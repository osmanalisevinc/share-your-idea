package com.share.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity(name = "Sharing")
@Table(name = "sharing")
@Getter
@Setter
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE sharing SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Sharing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 1000)
    @NotBlank
    private String shared;

    @ManyToOne(fetch = FetchType.EAGER)
    private User sharedUser;

    private Integer likes = 0;

    private Integer disLikes = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    private boolean hasReached100Likes = false;

    private boolean hasReached100Dislikes = false;

    private LocalDateTime expireStart;

    @OneToMany(mappedBy = "sharing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    private Integer qouta;

    public void plusLikes() {
        updateLikes(1);
    }

    public void plusDisLikes() {
        updateDisLikes(1);
    }

    public void minusLikes() {
        updateLikes(-1);
    }

    public void minusDisLikes() {
        updateDisLikes(-1);
    }

    private void updateLikes(int change) {
        this.likes = Math.max(0, this.likes + change); // Negatif olmaması için
        setHasReached100Likes(this.likes > (this.getQouta() - 1));
        if (Objects.equals(this.likes, this.getQouta())) {
            setExpireStart(LocalDateTime.now());
        }
    }

    private void updateDisLikes(int change) {
        this.disLikes = Math.max(0, this.disLikes + change); // Negatif olmaması için
        setHasReached100Dislikes(this.disLikes > (this.getQouta() - 1));
        if (Objects.equals(this.disLikes, this.getQouta())) {
            setExpireStart(LocalDateTime.now());
        }
    }
}
