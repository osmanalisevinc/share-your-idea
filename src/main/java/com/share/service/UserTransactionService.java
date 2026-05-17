package com.share.service;

import com.share.model.User;
import com.share.model.UserTransaction;
import com.share.model.enums.TransactionType;
import com.share.repository.UserTransactionRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserTransactionService {
    private final UserTransactionRepository userTransactionRepository;
    private final AuthService authService;


    @Transactional
    public UserTransaction login() {
        User user = authService.getCurrentUser();
        UserTransaction userTransaction = new UserTransaction();
        userTransaction.setUser(user);
        userTransaction.setTime(LocalDateTime.now());
        userTransaction.setTransactionType(TransactionType.LOGIN);
        return userTransactionRepository.save(userTransaction);
    }

    @Transactional
    public UserTransaction logout() {
        User user = authService.getCurrentUser();
        UserTransaction userTransaction = new UserTransaction();
        userTransaction.setUser(user);
        userTransaction.setTime(LocalDateTime.now());
        userTransaction.setTransactionType(TransactionType.LOGOUT);
        return userTransactionRepository.save(userTransaction);
    }


}
