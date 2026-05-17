package com.share.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "User")
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Size(max = 30)
    private String userName;

    @NotBlank
    @Size(max = 30)
    private String userSurname;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String password;

    private String phone;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    private byte[] profilePhoto;

    private String photoPath;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;

    private Integer follower = 0; // takipçi sayısı

    private Integer following = 0;// takip edilen sayısı

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserLike> myLikedSharings = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserDislike> myDisLikedSharings = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserTransaction> transactions = new HashSet<>();

    public String getUserNameAndSurname() {
        return userName + " " + userSurname;
    }

    public void updateFollower(int delta) {
        this.follower += delta;
        if (this.follower < 0) {
            this.follower = 0; // Negatif takipçi sayısını önle
        }
    }

    public void updateFollowing(int delta) {
        this.follower += delta;
        if (this.follower < 0) {
            this.follower = 0; // Negatif takipçi sayısını önle
        }
    }
}
