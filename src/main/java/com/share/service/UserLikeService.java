package com.share.service;

import com.share.exception.BadRequestException;
import com.share.model.Sharing;
import com.share.model.User;
import com.share.model.UserLike;
import com.share.model.enums.NotificationType;
import com.share.repository.SharingRepository;
import com.share.repository.UserLikeRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLikeService {
    private final UserLikeRepository userLikeRepository;
    private final SharingRepository sharingRepository;
    private final AuthService authService;
    private final SharingService sharingService;
    private final NotificationService notificationService;

    @Transactional
    public Integer likeSharing(String sharingId) {
        User currentUser = authService.getCurrentUser();
        Sharing sharing = sharingRepository.findById(sharingId).orElseThrow(() -> new RuntimeException("Sharing not found"));
        boolean exists = userLikeRepository.existsByUserIdAndSharingId(currentUser.getId(), sharingId);
        if (exists) {
            throw new BadRequestException("Already liked");
        }
        UserLike userLike = new UserLike();
        userLike.setSharing(sharing);
        userLike.setUser(authService.getCurrentUser());
        userLikeRepository.save(userLike);

        sharingService.likeSharing(sharingId);

        notificationService.sendLikeNotification(sharing.getSharedUser(), currentUser);

        return userLikeRepository.countBySharingId(sharingId);
    }

    public List<Sharing> getMyLikeSharings() {
        User currentUser = authService.getCurrentUser();
        List<UserLike> userLikes = userLikeRepository.findByUserId(currentUser.getId());

        return userLikes.stream().map(UserLike::getSharing).toList();
    }

    @Transactional
    public Integer cancelLikeSharing(String sharingId) {
        Sharing sharing = sharingRepository.findById(sharingId).orElseThrow(() -> new RuntimeException("Sharing not found"));
        User currentUser = authService.getCurrentUser();
        UserLike userLike = userLikeRepository.findByUserIdAndSharingId(currentUser.getId(), sharingId)
                .orElseThrow(() -> new RuntimeException("Like not found"));
        userLikeRepository.delete(userLike);
        sharing.minusLikes();
        return sharingRepository.save(sharing).getLikes();
    }
}
