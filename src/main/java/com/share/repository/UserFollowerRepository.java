package com.share.repository;

import com.share.model.User;
import com.share.model.UserFollower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFollowerRepository extends JpaRepository<UserFollower, String> {

    boolean existsByUserAndFollower(User userToFollow, User currentUser);

    Optional<UserFollower> findByUserAndFollower(User currentUser, User follower);

    List<UserFollower> findAllByUser(User currentUser);

    List<UserFollower> findByFollower(User user);

    List<UserFollower> findByUserAndFollowStatusTrue(User user);

    List<UserFollower> findByFollowerAndFollowStatusTrue(User follower);

    boolean existsByUserAndFollowerAndFollowStatusTrue(User user, User follower);
}
