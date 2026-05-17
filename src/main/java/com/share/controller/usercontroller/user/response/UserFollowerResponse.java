package com.share.controller.usercontroller.user.response;

import com.share.model.UserFollower;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserFollowerResponse {
    private final String userName;
    private final String userSurname;

    public UserFollowerResponse(UserFollower userFollower) {
        this.userName = userFollower.getFollower().getUserName();
        this.userSurname = userFollower.getFollower().getUserSurname();
    }
}
