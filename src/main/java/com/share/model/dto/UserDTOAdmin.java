package com.share.model.dto;

import com.share.model.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class UserDTOAdmin {

    private final String username;
    private final String userSurname;
    private final String email;
    private final List<String> roles;
    private final LocalDateTime createdDate;
    private final String phoneNumber;
    private final List<UserTransactionResponse> userTransactions;

    public UserDTOAdmin(User user, List<UserTransactionResponse> userTransactionResponses) {
        this.username = user.getUserName();
        this.userSurname = user.getUserSurname();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(role -> role.getName().getLabel()).toList();
        this.createdDate = user.getCreatedDate();
        this.phoneNumber = user.getPhone();
        this.userTransactions = userTransactionResponses;
    }
}

