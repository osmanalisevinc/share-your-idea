package com.share.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AWSDirectory {
    USERS("users/");

    public final String path;
}
