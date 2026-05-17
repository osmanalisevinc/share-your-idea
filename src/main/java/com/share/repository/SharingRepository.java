package com.share.repository;

import com.share.model.Sharing;
import com.share.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface SharingRepository extends JpaRepository<Sharing, String> {
    List<Sharing> findBySharedUser(User user);

    List<Sharing> findBySharedUserIdIn(Collection<String> sharedUserIds);

    @EntityGraph(attributePaths = {"comments"})
    List<Sharing> findAll();

    @Modifying
    @Query("UPDATE Sharing s SET s.qouta = :quota")
    void updateQuota(Integer quota);

    @Modifying
    int deleteByExpireStartBefore(LocalDateTime threshold);
}
