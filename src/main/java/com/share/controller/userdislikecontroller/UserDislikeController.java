package com.share.controller.userdislikecontroller;

import com.share.controller.sharingcontroller.response.SharingResponse;
import com.share.model.Sharing;
import com.share.service.UserDislikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user-dislike")
@RequiredArgsConstructor
public class UserDislikeController {
    private final UserDislikeService userDislikeService;


    @PutMapping("/dislike/{sharingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> disLikeSharing(@PathVariable String sharingId) {
        int response = userDislikeService.disLikeSharing(sharingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/dislike/cancel/{sharingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> cancelDisLikeSharing(@PathVariable String sharingId) {
        int response = userDislikeService.cancelDisLikeSharing(sharingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-my-dislike")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getMyLike() {
        List<Sharing> sharings= userDislikeService.getMyDisLikeSharings();
        List<SharingResponse> response = sharings.stream()
                .map(sharing -> new SharingResponse(sharing, false)).toList();
        return ResponseEntity.ok(response);
    }
}
