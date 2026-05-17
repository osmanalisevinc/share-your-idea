package com.share.controller.userfollowercontroller;

import com.share.controller.usercontroller.user.response.UserFollowerResponse;
import com.share.model.UserFollower;
import com.share.service.UserFollowerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user-follower")
@RequiredArgsConstructor
public class UserFollowerController {
    private final UserFollowerService userFollowerService;

    @PostMapping("/follow/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void followUser(
            @PathVariable String userId
    ) {
        userFollowerService.followUser(userId);
    }

    @PostMapping("/unfollow/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public void unFollowUser(
            @PathVariable String userId
    ) {
        userFollowerService.unfollowUser(userId);
    }

    @GetMapping("/my-followers")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserFollowerResponse>> getMyFollowers() {

        List<UserFollower> followers = userFollowerService.getMyFollowers();
        List<UserFollowerResponse> response = followers.stream()
                .map(UserFollowerResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }

}
