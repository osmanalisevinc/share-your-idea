package com.share.model.dto;


import com.share.model.User;
import lombok.Getter;

@Getter
public class UserDTO {

    private final String username;
    private final String userSurname;
    private final String email;
    private final String photoPath;
    private final Integer follower;
    private final Integer following;
    private final byte[] photo;

    public UserDTO(User user) {
        this.username = user.getUserName();
        this.userSurname = user.getUserSurname();
        this.email = user.getEmail();
        this.photoPath = user.getPhotoPath();
        this.follower = user.getFollower();
        this.following = user.getFollowing();
        this.photo = user.getProfilePhoto();
    }
}
