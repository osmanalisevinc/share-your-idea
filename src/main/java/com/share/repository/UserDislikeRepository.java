package com.share.repository;

import com.share.model.User;
import com.share.model.UserDislike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDislikeRepository extends JpaRepository<UserDislike, String> {
    int countBySharingId(String sharingId);

    List<UserDislike> findByUserId(String id);

    @Query("SELECT u.sharing.id FROM UserDisLike u WHERE u.user = :user")
    List<String> findUserIdsByUser(@Param("user") User user);

    Optional<UserDislike> findByUserIdAndSharingId(String id, String sharingId);

    boolean existsByUserIdAndSharingId(String id, String sharingId);
}
