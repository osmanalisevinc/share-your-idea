package com.share.repository;

import com.share.model.User;
import com.share.model.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, String> {
    List<UserTransaction> findByUserId(String id);

    List<UserTransaction> findByUserIdIn(List<String> userIds);

}
