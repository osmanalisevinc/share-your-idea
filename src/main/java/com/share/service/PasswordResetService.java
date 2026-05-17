package com.share.service;


import com.share.model.PasswordResetCode;
import com.share.repository.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;

    @Transactional
    public void savePasswordResetToken(PasswordResetCode token){
        passwordResetRepository.save(token);
    }

}