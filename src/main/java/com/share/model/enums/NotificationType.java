package com.share.model.enums;

public enum NotificationType {
    FOLLOW_REQUEST,
    LIKE,
    DISLIKE,
    COMMENT,
    SHARE;

    public String label() {
        return switch (this) {
            case FOLLOW_REQUEST -> "Takip edildi";
            case LIKE -> "Beğendi";
            case DISLIKE -> "Beğenmedi";
            case COMMENT -> "Yorum yaptı";
            case SHARE -> "Paylaşımda bulundu";
        };
    }
}
