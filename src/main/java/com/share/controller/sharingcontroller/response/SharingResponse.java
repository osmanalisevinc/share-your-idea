package com.share.controller.sharingcontroller.response;

import com.share.controller.commentcontroller.response.CommentResponse;
import com.share.model.Sharing;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class SharingResponse {
    private final String id;
    private final String userId;
    private final String userName;
    private final String userSurname;
    private final String shared;
    private final LocalDateTime createdDate;
    private final Integer likes;
    private final Integer disLikes;
    private final Boolean isLiked;
    private final Boolean isdisliked;
    private final boolean hasReached100Likes;
    private final boolean hasReached100Dislikes;
    private final LocalDateTime expireStart;
    private final List<CommentResponse> commentResponse;
    private final Boolean isFollowing;
    private final Boolean userVote;
    private final byte[] photo;

    public SharingResponse(Sharing sharing, List<String> likedIds, List<String> dislikedIds, List<String> followingIds) {
        this.id = sharing.getId();
        this.userId = sharing.getSharedUser().getId();
        this.userName = sharing.getSharedUser().getUserName();
        this.userSurname = sharing.getSharedUser().getUserSurname();
        this.shared = sharing.getShared();
        this.createdDate = sharing.getCreatedDate();
        this.likes = sharing.getLikes();
        this.disLikes = sharing.getDisLikes();
        this.isLiked = likedIds.contains(id);
        this.isdisliked = dislikedIds.contains(id);
        this.hasReached100Likes = sharing.isHasReached100Likes();
        this.hasReached100Dislikes = sharing.isHasReached100Dislikes();
        this.expireStart = sharing.getExpireStart();
        this.commentResponse = sharing.getComments().stream().map(CommentResponse::new).toList();
        this.isFollowing = followingIds.contains(sharing.getSharedUser().getId());
        if (likedIds.contains(id)) {
            this.userVote = true;
        } else if (dislikedIds.contains(id)) {
            this.userVote = false;
        } else {
            this.userVote = null;
        }
        this.photo = sharing.getSharedUser().getProfilePhoto();
    }

    public SharingResponse(Sharing sharing, Boolean like) {
        this.id = sharing.getId();
        this.userId = sharing.getSharedUser().getId();
        this.userName = sharing.getSharedUser().getUserName();
        this.userSurname = sharing.getSharedUser().getUserSurname();
        this.shared = sharing.getShared();
        this.createdDate = sharing.getCreatedDate();
        this.likes = sharing.getLikes();
        this.disLikes = sharing.getDisLikes();
        this.isLiked = like;
        this.isdisliked = !like;
        this.hasReached100Likes = sharing.isHasReached100Likes();
        this.hasReached100Dislikes = sharing.isHasReached100Dislikes();
        this.expireStart = sharing.getExpireStart();
        this.commentResponse = sharing.getComments().stream().map(CommentResponse::new).toList();
        this.isFollowing = null;
        if (this.isLiked) {
            this.userVote = true;
        } else if (this.isdisliked) {
            this.userVote = false;
        } else {
            this.userVote = null;
        }
        this.photo = sharing.getSharedUser().getProfilePhoto();
    }

    public SharingResponse(Sharing sharing) {
        this.id = sharing.getId();
        this.userId = sharing.getSharedUser().getId();
        this.userName = sharing.getSharedUser().getUserName();
        this.userSurname = sharing.getSharedUser().getUserSurname();
        this.shared = sharing.getShared();
        this.createdDate = sharing.getCreatedDate();
        this.likes = sharing.getLikes();
        this.disLikes = sharing.getDisLikes();
        this.isLiked = null;
        this.isdisliked = null;
        this.hasReached100Likes = sharing.isHasReached100Likes();
        this.hasReached100Dislikes = sharing.isHasReached100Dislikes();
        this.expireStart = sharing.getExpireStart();
        this.commentResponse = sharing.getComments().stream().map(CommentResponse::new).toList();
        this.isFollowing = null;
        this.userVote = null;
        this.photo = sharing.getSharedUser().getProfilePhoto();
    }
}
