package com.share.model;

import com.share.model.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@Entity(name = "UserTransaction")
@Table(name = "user_transaction")
@SQLDelete(sql = "UPDATE user_like SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class UserTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private TransactionType transactionType;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("false")
    private Boolean isDeleted = false;

}
