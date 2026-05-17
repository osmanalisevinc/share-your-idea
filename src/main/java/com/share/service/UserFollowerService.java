package com.share.service;

import com.share.model.User;
import com.share.model.UserFollower;
import com.share.repository.UserFollowerRepository;
import com.share.repository.UserRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFollowerService {
    private final UserFollowerRepository userFollowerRepository;
    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public void followUser(String userId) {
        var currentUser = authService.getCurrentUser();
        var userToFollow = userService.getUserById(userId);

        // Check if the user is trying to follow themselves
        if (currentUser.getId().equals(userToFollow.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }

        // Check if the current user is already following the target user
        boolean alreadyFollowing = userFollowerRepository.existsByUserAndFollower(userToFollow, currentUser);

        if (alreadyFollowing) {
            throw new IllegalStateException("You are already following this user.");
        }

        var userFollower = new UserFollower();
        userFollower.setUser(userToFollow);     // takip edilen kullanıcı
        userFollower.setFollower(currentUser);  // takip eden kullanıcı
        userFollower.setCreatedAt(LocalDateTime.now());

        userFollowerRepository.save(userFollower);

        userToFollow.updateFollower(1);
        userRepository.save(userToFollow);

        currentUser.updateFollowing(1);
        userRepository.save(currentUser);

        notificationService.sendFollowNotification(userToFollow, currentUser);

    }

    @Transactional
    public void unfollowUser(String userId) {
        var currentUser = authService.getCurrentUser();
        var follower = userService.getUserById(userId);

        var userFollower = userFollowerRepository.findByUserAndFollower(follower, currentUser)
                .orElseThrow(() -> new IllegalStateException("You are not following this user."));

        userFollowerRepository.delete(userFollower);
        follower.updateFollower(-1);
        userRepository.save(follower);

        currentUser.updateFollowing(-1);
        userRepository.save(currentUser);
    }

    public List<UserFollower> getMyFollowers() {
        var currentUser = authService.getCurrentUser();
        return userFollowerRepository.findAllByUser(currentUser);
    }

    public List<String> getFollowedByMe(User user) {
        return userFollowerRepository.findByFollower(user)
                .stream()
                .map(UserFollower::getUser)
                .map(User::getId)
                .toList();
    }
}
