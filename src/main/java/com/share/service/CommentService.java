package com.share.service;

import com.share.controller.commentcontroller.request.CommentSaveRequest;
import com.share.exception.BadRequestException;
import com.share.model.Comment;
import com.share.model.Sharing;
import com.share.model.User;
import com.share.model.enums.NotificationType;
import com.share.repository.CommentRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final SharingService sharingService;
    private final AuthService authService;
    private final NotificationService notificationService;

    @Transactional
    public Comment saveComment(String shareId, CommentSaveRequest request) {
        Sharing sharing = sharingService.getSharingById(shareId);
        User user = authService.getCurrentUser();

        Comment comment = new Comment();
        comment.setComment(request.getContent());
        comment.setSharing(sharing);
        comment.setCommentedUser(user);
        comment.setCreatedAt(LocalDateTime.now());

        notificationService.sendCommentNotification(sharing.getSharedUser(), user);

        return commentRepository.save(comment);
    }

    public Comment getCommentById(String id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Yorum bulunamadı"));
    }

    public List<Comment> getCommentsBySharingId(String sharingId) {
        Sharing sharing = sharingService.getSharingById(sharingId);
        return commentRepository.findAll().stream().filter(comment -> comment.getSharing().equals(sharing)).toList();
    }

    @Transactional
    public void deleteComment(String commentId) {
        Comment comment = getCommentById(commentId);
        User user = authService.getCurrentUser();
        if (!comment.getCommentedUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bu yorumu silme yetkiniz yok");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public Comment updateComment(String commentId, CommentSaveRequest request) {
        Comment comment = getCommentById(commentId);
        User user = authService.getCurrentUser();

        if (!comment.getCommentedUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bu yorumu güncelleme yetkiniz yok");
        }

        comment.setComment(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteCommentAdmin(String commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }
}
