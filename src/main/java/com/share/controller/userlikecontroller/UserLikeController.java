package com.share.controller.userlikecontroller;

import com.share.controller.sharingcontroller.response.SharingResponse;
import com.share.model.Sharing;
import com.share.service.UserLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user-like")
@RequiredArgsConstructor
public class UserLikeController {
    private final UserLikeService userLikeService;

    @PutMapping("/like/{sharingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> likeSharing(@PathVariable String sharingId) {
        Integer response = userLikeService.likeSharing(sharingId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/like/cancel/{sharingId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Integer> cancelLikeSharing(@PathVariable String sharingId) {
        Integer response = userLikeService.cancelLikeSharing(sharingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("get-my-like")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<SharingResponse>> getMyLike() {
        List<Sharing> sharings = userLikeService.getMyLikeSharings();
        List<SharingResponse> response = sharings.stream()
                .map(sharing -> new SharingResponse(sharing, true))
                .toList();
        return ResponseEntity.ok(response);
    }
}
