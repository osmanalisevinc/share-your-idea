package com.share.service;

import com.share.exception.BadRequestException;
import com.share.model.Sharing;
import com.share.model.User;
import com.share.model.UserDislike;
import com.share.model.enums.NotificationType;
import com.share.repository.SharingRepository;
import com.share.repository.UserDislikeRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDislikeService {
    private final UserDislikeRepository userDislikeRepository;
    private final SharingRepository sharingRepository;
    private final AuthService authService;
    private final SharingService sharingService;
    private final NotificationService notificationService;


    @Transactional
    public int disLikeSharing(String sharingId) {
        User currentUser = authService.getCurrentUser();
        Sharing sharing = sharingRepository.findById(sharingId).orElseThrow(() -> new RuntimeException("Sharing not found"));
        boolean exists = userDislikeRepository.existsByUserIdAndSharingId(currentUser.getId(), sharingId);
        if (exists) {
            throw new BadRequestException("Already disliked");
        }
        UserDislike userDislike = new UserDislike();
        userDislike.setSharing(sharing);
        userDislike.setUser(currentUser);
        userDislikeRepository.save(userDislike);

        sharingService.disLikeSharing(sharingId);

        notificationService.sendDislikeNotification(sharing.getSharedUser(), currentUser);

        return userDislikeRepository.countBySharingId(sharingId);
    }

    public List<Sharing> getMyDisLikeSharings() {
        User currentUser = authService.getCurrentUser();
        List<UserDislike> userDislikes = userDislikeRepository.findByUserId(currentUser.getId());

        return userDislikes.stream().map(UserDislike::getSharing).toList();
    }

    @Transactional
    public int cancelDisLikeSharing(String sharingId) {
        Sharing sharing = sharingRepository.findById(sharingId).orElseThrow(() -> new RuntimeException("Sharing not found"));
        User currentUser = authService.getCurrentUser();
        UserDislike userDislike = userDislikeRepository.findByUserIdAndSharingId(currentUser.getId(), sharingId)
                .orElseThrow(() -> new RuntimeException("Dislike not found"));
        userDislikeRepository.delete(userDislike);
        sharing.minusDisLikes();
        return sharingRepository.save(sharing).getDisLikes();
    }
}
