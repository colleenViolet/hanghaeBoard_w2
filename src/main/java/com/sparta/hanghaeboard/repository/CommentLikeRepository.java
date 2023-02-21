package com.sparta.hanghaeboard.repository;

import com.sparta.hanghaeboard.entity.Comment;
import com.sparta.hanghaeboard.entity.CommentLike;
import com.sparta.hanghaeboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
