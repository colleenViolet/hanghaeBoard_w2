package com.sparta.hanghaeboard.repository;

import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.BoardLike;
import com.sparta.hanghaeboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    void deleteByBoardAndUser(Board board, User user);
    Optional<BoardLike> findByBoardAndUser(Board board, User user);
}