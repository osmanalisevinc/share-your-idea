package com.share.service;

import com.share.controller.sharingcontroller.request.CreateSharingRequest;
import com.share.controller.sharingcontroller.response.SharingResponse;
import com.share.exception.BadRequestException;
import com.share.model.Sharing;
import com.share.model.User;
import com.share.model.enums.NotificationType;
import com.share.repository.SharingRepository;
import com.share.repository.UserDislikeRepository;
import com.share.repository.UserLikeRepository;
import com.share.security.services.AuthService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SharingService {
    private final SharingRepository sharingRepository;
    private final AuthService authService;
    private final UserFollowerService userFollowerService;
    private final NotificationService notificationService;
    private final UserLikeRepository userLikeRepository;
    private final UserDislikeRepository userDislikeRepository;
    private final UserService userService;

    @Transactional
    public void createSharing(CreateSharingRequest createSharingRequest) {
        User user = authService.getCurrentUser();

        Sharing sharing = new Sharing();
        sharing.setSharedUser(user);
        sharing.setShared(createSharingRequest.getShare());
        sharing.setCreatedDate(LocalDateTime.now());
        sharing.setQouta(100);

        sharingRepository.save(sharing);

        notificationService.sendSharingNotificationToFollowers(user);
    }

    public List<SharingResponse> getAllSharing() {
        User currentUser = authService.getCurrentUser();

        // Bu metotların "beğenilen paylaşım id'lerini" döndüğünü varsayıyorum
        List<String> myLikedSharings = userLikeRepository.findUserIdsByUser(currentUser);
        List<String> myDislikedSharings = userDislikeRepository.findUserIdsByUser(currentUser);
        List<String> followingUserIds = userFollowerService.getFollowedByMe(currentUser);

        return sharingRepository.findAll()
                .stream()
                .map(sharing -> new SharingResponse(sharing, myLikedSharings, myDislikedSharings, followingUserIds))
                .toList();
    }


    @PutMapping
    public Integer disLikeSharing(String id) {
        Sharing sharing = getSharingById(id);
        sharing.plusDisLikes();
        return sharingRepository.save(sharing).getDisLikes();
    }

    @PutMapping
    public Integer likeSharing(String id) {
        Sharing sharing = getSharingById(id);
        sharing.plusLikes();
        return sharingRepository.save(sharing).getLikes();
    }

    public Sharing getSharingById(String id) {
        return sharingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Paylaşım bulunamadı"));
    }

    @Transactional
    public void deleteSharing(String id) {
        Sharing sharing = getSharingById(id);
        User user = authService.getCurrentUser();
        if (!sharing.getSharedUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bu paylaşımı silme yetkiniz yok");
        }
        sharingRepository.delete(sharing);
    }

    public List<SharingResponse> getMyShares() {
        User user = authService.getCurrentUser();

        return sharingRepository.findBySharedUser(user).stream().map(SharingResponse::new).toList();
    }

    public List<SharingResponse> getMyFollowingShares() {
        User user = authService.getCurrentUser();
        List<String> followingUserIds = userFollowerService.getFollowedByMe(user);
        List<String> myDislikedSharings = userDislikeRepository.findUserIdsByUser(user);
        List<String> myLikedSharings = userLikeRepository.findUserIdsByUser(user);

        return sharingRepository.findBySharedUserIdIn((followingUserIds)).stream()
                .map(sharing -> new SharingResponse(sharing, myLikedSharings, myDislikedSharings, followingUserIds))
                .toList();
    }

    @Transactional
    public void startExpire(String sharingId) {
        Sharing sharing = getSharingById(sharingId);
        sharing.setExpireStart(LocalDateTime.now());

        sharingRepository.save(sharing);
    }

    @Transactional
    public int updateSharingLikeAdmin(String sharingId, Integer likes) {
        Sharing sharing = getSharingById(sharingId);
        sharing.setLikes(likes);
        return sharingRepository.save(sharing).getLikes();
    }

    @Transactional
    public int updateSharingDislikeAdmin(String sharingId, Integer dislikes) {
        Sharing sharing = getSharingById(sharingId);
        sharing.setDisLikes(dislikes);
        return sharingRepository.save(sharing).getDisLikes();
    }

    public List<SharingResponse> getUserShares(String userId) {
        User user = userService.getUserById(userId);

        // Bu metotların "beğenilen paylaşım id'lerini" döndüğünü varsayıyorum
        List<String> myLikedSharings = userLikeRepository.findUserIdsByUser(user);
        List<String> myDislikedSharings = userDislikeRepository.findUserIdsByUser(user);
        List<String> followingUserIds = userFollowerService.getFollowedByMe(user);

        return sharingRepository.findBySharedUser(user)
                .stream()
                .map(sharing -> new SharingResponse(sharing, myLikedSharings, myDislikedSharings, followingUserIds))
                .toList();
    }

    @Transactional
    public void deleteSharingExpire(String id) {
        Sharing sharing = getSharingById(id);

        if (sharing.getExpireStart().plusDays(1).isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Bu paylaşım süre dolmadığı içn silinemez");
        }
        sharingRepository.delete(sharing);
    }

    @Transactional
    public void updateSharingQuota(Integer quota) {
        if (quota < 0) {
            throw new BadRequestException("Kota değeri 0 dan büyük olmalıdır");
        }
        sharingRepository.updateQuota(quota);
    }
}
