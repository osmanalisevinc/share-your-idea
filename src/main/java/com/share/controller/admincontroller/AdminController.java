package com.share.controller.admincontroller;

import com.share.service.CommentService;
import com.share.service.SharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final SharingService sharingService;
    private final CommentService commentService;


    @PutMapping("update-like/{sharingId}")
    public ResponseEntity<Integer> updateLikes(
            @PathVariable String sharingId,
            @RequestParam Integer likes) {

        int like = sharingService.updateSharingLikeAdmin(sharingId, likes);

        return ResponseEntity.ok(like);
    }

    @PutMapping("update-dislike/{sharingId}")
    public ResponseEntity<Integer> updateDislikes(
            @PathVariable String sharingId,
            @RequestParam Integer dislikes) {

        int like = sharingService.updateSharingDislikeAdmin(sharingId, dislikes);

        return ResponseEntity.ok(like);
    }

    @DeleteMapping("/user/admin/{commentId}")
    public String deleteCommentAdmin(@PathVariable String commentId) {
        commentService.deleteCommentAdmin(commentId);
        return "Comment deleted successfully";
    }

    @PutMapping("/admin/qouta")
    public ResponseEntity<String> updateQouta(@RequestParam Integer qouta) {
        sharingService.updateSharingQuota(qouta);
        return ResponseEntity.ok("Qouta updated successfully");
    }
}
