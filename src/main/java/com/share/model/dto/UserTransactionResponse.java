package com.share.model.dto;

import com.share.model.UserTransaction;
import com.share.model.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserTransactionResponse {
    private final LocalDateTime time;
    private final TransactionType transactionType;

    public UserTransactionResponse(UserTransaction userTransaction) {
        this.time = userTransaction.getTime();
        this.transactionType = userTransaction.getTransactionType();
    }
}
