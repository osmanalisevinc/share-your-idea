package com.share.controller.commentcontroller.response;

import com.share.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentResponse {

    private final String id;
    private final String sharedId;
    private final String shared;
    private final Integer likes ;
    private final Integer disLikes ;
    private final LocalDateTime sharedDate;
    private final String commentedUser;
    private final String comment;
    private final LocalDateTime commentedDate;
    private final String sharingUser;


    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.sharedId = comment.getSharing().getId();
        this.shared = comment.getSharing().getShared();
        this.likes = comment.getSharing().getLikes();
        this.disLikes = comment.getSharing().getDisLikes();
        this.sharedDate = comment.getSharing().getCreatedDate();
        this.commentedUser = comment.getCommentedUser().getUserNameAndSurname();
        this.comment =comment.getComment() ;
        this.commentedDate = comment.getCreatedAt();
        this.sharingUser= comment.getSharing().getSharedUser().getUserNameAndSurname();
    }


}
