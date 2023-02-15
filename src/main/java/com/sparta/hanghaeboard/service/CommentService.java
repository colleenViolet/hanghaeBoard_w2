package com.sparta.hanghaeboard.service;

import com.google.gson.Gson;
import com.sparta.hanghaeboard.dto.CommentRequestDto;
import com.sparta.hanghaeboard.dto.CommentResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.Comment;
import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.entity.UserRoleEnum;
import com.sparta.hanghaeboard.jwt.JwtUtil;
import com.sparta.hanghaeboard.repository.BoardRepository;
import com.sparta.hanghaeboard.repository.CommentRepository;
import com.sparta.hanghaeboard.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Gson gson;

    public StatusResponseDto<CommentResponseDto> createComment(Long id, String content, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
                Board board = boardRepository.findById(id).orElseThrow(() -> new NullPointerException("등록되지 않은 게시글입니다."));
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다. "));
                Comment comment = new Comment(user, board, content);
                commentRepository.save(comment);
                return StatusResponseDto.success(new CommentResponseDto(comment));
            }
        }
        throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
    }

    @Transactional
    public StatusResponseDto<CommentResponseDto> updateComment(Long id, CommentRequestDto commentRequestDto, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(()-> new IllegalArgumentException("사용자 정보를 찾을 수 없다. "));
                Comment comment= commentRepository.findById(id).orElseThrow(()-> new NullPointerException("해당 댓글을 찾을 수 없습니다."));

                if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(comment.getUser().getUsername())){
                    comment.update(commentRequestDto);
                    return StatusResponseDto.success(new CommentResponseDto(comment));
                }else {
                    throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
                }
            }
        }
      throw new IllegalArgumentException("해당 토큰이 유효하지 않습니다.");
    }

    public StatusResponseDto<String> deleteComment(Long id, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        if (token != null){
            if (jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(()-> new IllegalArgumentException("사용자 정보를 찾을 수 없다. "));
                Comment comment= commentRepository.findById(id).orElseThrow(()-> new NullPointerException("해당 댓글을 찾을 수 없습니다."));
                if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(comment.getUser().getUsername())){
                    commentRepository.deleteById(id);
                    return StatusResponseDto.success("delete success!");
                }else {
                    throw new IllegalArgumentException("작성자만 수정이 가능합니다.");
                }
            }
        }
        throw  new IllegalArgumentException("토큰이 유효하지 않습니다.");
    }

}
