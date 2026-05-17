package com.share.repository;

import com.share.model.Comment;
import com.share.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByCommentedUser(User user);
}
