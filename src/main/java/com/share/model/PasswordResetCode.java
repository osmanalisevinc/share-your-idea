package com.share.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "password_reset_code")
@Entity(name = "password_reset_code")
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    @ManyToOne
    @JoinColumn(nullable = false,
            name = "user_id")
    private User user;

    public PasswordResetCode(String code, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
        this.code = code;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}