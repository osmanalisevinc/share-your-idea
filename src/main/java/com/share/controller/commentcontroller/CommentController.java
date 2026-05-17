package com.share.controller.commentcontroller;

import com.share.controller.commentcontroller.request.CommentSaveRequest;
import com.share.controller.commentcontroller.response.CommentResponse;
import com.share.model.Comment;
import com.share.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/user/{shareId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String shareId,
            @RequestBody CommentSaveRequest request
    ) {
       Comment comment= commentService.saveComment(shareId, request);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @PutMapping("/user/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable String commentId,
            @RequestBody CommentSaveRequest request
    ) {
        Comment comment = commentService.updateComment(commentId, request);

        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @GetMapping("/user/{shareId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<CommentResponse> getCommentById(@PathVariable String shareId) {
        return commentService.getCommentsBySharingId(shareId).stream().map(CommentResponse::new).toList();
    }

    @DeleteMapping("/user/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return "Comment deleted successfully";
    }



}
