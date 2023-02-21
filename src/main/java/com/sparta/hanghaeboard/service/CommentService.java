package com.sparta.hanghaeboard.service;

import com.google.gson.Gson;
import com.sparta.hanghaeboard.dto.CommentRequestDto;
import com.sparta.hanghaeboard.dto.CommentResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.*;
import com.sparta.hanghaeboard.jwt.JwtUtil;
import com.sparta.hanghaeboard.repository.BoardRepository;
import com.sparta.hanghaeboard.repository.CommentLikeRepository;
import com.sparta.hanghaeboard.repository.CommentRepository;
import com.sparta.hanghaeboard.repository.UserRepository;
import com.sparta.hanghaeboard.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Gson gson;
    private final CommentLikeRepository commentLikeRepository;

    public StatusResponseDto<CommentResponseDto> createComment(Long id, String content, UserDetailsImpl userDetails) {

        Board board = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("등록되지 않은 게시글입니다."));
        Comment comment = new Comment(userDetails.getUser(), board, content);
        commentRepository.save(comment);
        return StatusResponseDto.success(new CommentResponseDto(comment));
    }


    @Transactional
    public StatusResponseDto<CommentResponseDto> updateComment(Long id, CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NullPointerException("해당 댓글을 찾을 수 없습니다."));

        if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(comment.getUser().getUsername())) {
            comment.update(commentRequestDto);
            return StatusResponseDto.success(new CommentResponseDto(comment));
        } else {
            throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
        }
    }

    public StatusResponseDto<String> deleteComment(Long id, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new NullPointerException("댓글을 찾을 수 없음")
        );
        if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(comment.getUser().getUsername())) {
            commentRepository.deleteById(id);
            return StatusResponseDto.success("delete success!");
        } else {
            throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
        }
    }

    public StatusResponseDto<String> likeComment(Long id, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new NullPointerException("존재하지 않는 댓글"));
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByCommentAndUser(comment, userDetails.getUser());
        if (optionalCommentLike.isPresent()) { // 유저가 이미 좋아요를 눌렀을 때
            commentLikeRepository.deleteById(optionalCommentLike.get().getId());
            return StatusResponseDto.success("댓글 좋아요 취소");
        }
        commentLikeRepository.save(new CommentLike(comment, userDetails.getUser()));
        return StatusResponseDto.success("댓글 좋아요 성공");
    }
}
