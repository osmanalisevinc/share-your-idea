package com.share.service;


import com.share.constants.Constants;
import com.share.controller.usercontroller.user.request.UpdateUserRequest;
import com.share.exception.BadRequestException;
import com.share.model.Comment;
import com.share.model.Sharing;
import com.share.model.User;
import com.share.model.UserDislike;
import com.share.model.UserLike;
import com.share.model.UserTransaction;
import com.share.model.dto.UserDTO;
import com.share.model.dto.UserDTOAdmin;
import com.share.model.dto.UserTransactionResponse;
import com.share.model.enums.AWSDirectory;
import com.share.repository.CommentRepository;
import com.share.repository.RoleRepository;
import com.share.repository.SharingRepository;
import com.share.repository.UserDislikeRepository;
import com.share.repository.UserLikeRepository;
import com.share.repository.UserRepository;
import com.share.repository.UserTransactionRepository;
import com.share.security.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final RoleRepository roleRepository;
    private final UserTransactionRepository userTransactionRepository;
    private final SharingRepository sharingRepository;
    private final CommentRepository commentRepository;
    private final UserLikeRepository userLikeRepository;
    private final UserDislikeRepository userDislikeRepository;
    private final S3StorageService s3StorageService;

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(Constants.USER_NOT_FOUND));
    }

    public List<UserDTOAdmin> getAllUsers() {
        List<User> users = userRepository.findAllUsersWithRolesAndTransactions();

        return users.stream()
                .map(user -> new UserDTOAdmin(
                        user,
                        user.getTransactions().stream()
                                .map(UserTransactionResponse::new)
                                .toList()
                ))
                .toList();
    }

    public Page<UserDTOAdmin> getUserPage(String search, Pageable pageable) {
        Page<User> userPage = userRepository.findAllAsPage(search, pageable);

        // Tüm kullanıcıların ID'lerini topla
        List<String> userIds = userPage.getContent().stream()
                .map(User::getId)
                .toList();

        // Bu kullanıcılara ait transaction'ları tek seferde getir
        List<UserTransaction> userTransactions = userTransactionRepository.findByUserIdIn(userIds);

        // UserTransaction'ları user ID'ye göre grupla
        Map<String, List<UserTransaction>> transactionsByUserId = userTransactions.stream()
                .collect(Collectors.groupingBy(ut -> ut.getUser().getId()));

        // UserDTOAdmin sayfası oluştur
        List<UserDTOAdmin> userDTOAdmins = userPage.getContent().stream()
                .map(user -> {
                    List<UserTransaction> userTrans = transactionsByUserId.getOrDefault(user.getId(), Collections.emptyList());
                    List<UserTransactionResponse> transactionResponses = userTrans.stream()
                            .map(UserTransactionResponse::new)
                            .toList();
                    return new UserDTOAdmin(user, transactionResponses);
                })
                .toList();

        return new PageImpl<>(userDTOAdmins, pageable, userPage.getTotalElements());
    }

    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(Constants.USER_NOT_FOUND));

        // Kullanıcının paylaşımlarını ve yorumlarını sil
        deleteUserRelatedData(user);

        userRepository.delete(user);
    }

    @Transactional
    public void deleteUserRelatedData(User user) {
        // Kullanıcının paylaşımlarını sil (cascade ile yorumlar da silinecek)
        List<Sharing> userSharings = sharingRepository.findBySharedUser(user);
        sharingRepository.deleteAll(userSharings);

        // Kullanıcının yorumlarını sil (eğer paylaşım silinmemişse)
        List<Comment> userComments = commentRepository.findByCommentedUser(user);
        commentRepository.deleteAll(userComments);

        // Kullanıcının like/dislike'larını sil
        List<UserLike> userLikes = userLikeRepository.findByUserId(user.getId());
        userLikeRepository.deleteAll(userLikes);

        List<UserDislike> userDislikes = userDislikeRepository.findByUserId(user.getId());
        userDislikeRepository.deleteAll(userDislikes);
    }

    @Transactional
    public void deleteMyAccount() {
        User user = authService.getCurrentUser();

        // Kullanıcının paylaşımlarını ve yorumlarını sil
        deleteUserRelatedData(user);

        userRepository.delete(user);
    }

    public UserDTO getUser() {
        User user = authService.getCurrentUser();

        return new UserDTO(user);
    }

    @Transactional
    public User updateUser(UpdateUserRequest updateUserRequest, MultipartFile photo) throws IOException {
        User user = authService.getCurrentUser();

        boolean existsEmail = userRepository.existsByEmail(updateUserRequest.getEmail());

        if (!user.getEmail().equals(updateUserRequest.getEmail()) && existsEmail) {
            throw new BadRequestException(String.format(Constants.EMAIL_ALREADY_EXISTS, updateUserRequest.getEmail()));
        }

        user.setUserName(authService.userNameSaveFormat(updateUserRequest.getUserName()));
        user.setUserSurname(authService.userNameSaveFormat(updateUserRequest.getUserSurname()));
        user.setEmail(updateUserRequest.getEmail());
        if (photo != null && !photo.isEmpty()) {
            user.setPhotoPath(s3StorageService.uploadFile(photo, AWSDirectory.USERS));
        }

        return userRepository.save(user);
    }


}
