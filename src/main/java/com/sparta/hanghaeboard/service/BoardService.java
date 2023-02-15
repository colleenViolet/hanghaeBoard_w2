package com.sparta.hanghaeboard.service;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.jwt.JwtUtil;
import com.sparta.hanghaeboard.repository.BoardRepository;
import com.sparta.hanghaeboard.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //게시글 올리기
    public ResponseEntity<Object> createBoard(BoardRequestDto boardRequestDto, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 유효하지 않습니다."));
            }
            Optional<User> found = userRepository.findByUsername(claims.getSubject());
            if(found.isEmpty()){
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "해당 유저가 없습니다."));
            }
            Board board = Board.builder()
                    .user(found.get())
                    .requestDto(boardRequestDto)
                    .build();
            boardRepository.save(board);
            return ResponseEntity.ok(new BoardResponseDto(board));
        }else {
            return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 없습니다."));
        }
    }

    //전체 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<List<BoardResponseDto>> findBoards(){
        return ResponseEntity.ok(boardRepository.findAllByOrderByModifiedAtDesc()
                .stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList()));
    }

    //선탹 게시글 조회
    @Transactional(readOnly = true)
    public ResponseEntity<BoardResponseDto> findBoard(Long id){
        Optional<Board> findBoard = boardRepository.findById(id);
        if (findBoard.isEmpty()){
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }
        return ResponseEntity.ok(new BoardResponseDto(findBoard.get()));
    }

    //선택 게시글 수정
    public ResponseEntity<Object> updateBoard(Long id, BoardRequestDto requestDto, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token != null){
            if (jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 유효하지 않습니다."));
            }
            Optional<User> found = userRepository.findByUsername(claims.getSubject());
            if(found.isEmpty()){
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "해당 유저가 없습니다."));
            }
            Optional<Board> board = boardRepository.findByIdAndUserId(id, found.get().getId());
            if (board.isEmpty()){
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 게시물입니다."));
            }
            board.get().update(requestDto);
            return ResponseEntity.ok(new BoardResponseDto(board.get()));
        }else {
            return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 없습니다."));
        }
    }

    //선택 게시글 삭제
    public ResponseEntity<StatusResponseDto> removeBoard(Long id, HttpServletRequest req)  {
        String token = jwtUtil.resolveToken(req);
        Claims claims;

        if(token != null){
            if(jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
            }else{
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 유효하지 않습니다."));
            }
            Optional<User> found = userRepository.findByUsername(claims.getSubject());
            if(found.isEmpty()){
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "해당 유저가 없습니다."));
            }
            Optional<Board> findBoard = boardRepository.findByIdAndUserId(id, found.get().getId());
            if (findBoard.isEmpty()){
                return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 게시물입니다."));
            }
            boardRepository.delete(findBoard.get());
            return ResponseEntity.ok(new StatusResponseDto(HttpStatus.OK.value(), "삭제 완료"));
        }else {
            return ResponseEntity.badRequest().body(new StatusResponseDto(HttpStatus.BAD_REQUEST.value(), "토큰이 없습니다."));
        }
    }

}
