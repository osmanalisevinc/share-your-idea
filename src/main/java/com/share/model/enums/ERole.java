package com.share.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ERole {
    ROLE_USER("User"),
    ROLE_ADMIN("Admin");

    private final String label;

}
