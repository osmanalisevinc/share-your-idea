package com.share.repository;


import com.share.model.PasswordResetCode;
import com.share.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetCode,Long> {

    Optional<PasswordResetCode> findByUserAndCode(User user, String code);
}