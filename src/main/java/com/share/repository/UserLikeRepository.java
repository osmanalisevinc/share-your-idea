package com.share.repository;

import com.share.model.User;
import com.share.model.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLikeRepository extends JpaRepository<UserLike, String> {
    Integer countBySharingId(String sharingId);

    List<UserLike> findByUserId(String id);

    @Query("SELECT u.sharing.id FROM UserLike u WHERE u.user = :user")
    List<String> findUserIdsByUser(@Param("user") User user);

    Optional<UserLike> findByUserIdAndSharingId(String id, String sharingId);

    boolean existsByUserIdAndSharingId(String id, String sharingId);
}
