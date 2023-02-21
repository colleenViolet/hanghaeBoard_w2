package com.sparta.hanghaeboard.service;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.BoardLike;
import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.entity.UserRoleEnum;
import com.sparta.hanghaeboard.jwt.JwtUtil;
import com.sparta.hanghaeboard.repository.BoardLikeRepository;
import com.sparta.hanghaeboard.repository.BoardRepository;
import com.sparta.hanghaeboard.repository.CommentRepository;
import com.sparta.hanghaeboard.repository.UserRepository;
import com.sparta.hanghaeboard.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;

    //게시글 올리기
    public StatusResponseDto<BoardResponseDto> createBoard(BoardRequestDto boardRequestDto, UserDetailsImpl userDetails) {
        Board board = new Board(boardRequestDto, userDetails.getUser());
        boardRepository.save(board);
        return StatusResponseDto.success(new BoardResponseDto(board));
    }

    //전체 게시글 조회
//    @Transactional(readOnly = true)
    public StatusResponseDto<List<BoardResponseDto>> findBoards() {
        List<Board> list = boardRepository.findAll();
        List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
        for (Board board : list) {
            boardResponseDtos.add(new BoardResponseDto(board));
        }
        return StatusResponseDto.success(boardResponseDtos);
    }

    //선탹 게시글 조회
//    @Transactional(readOnly = true)
    public StatusResponseDto<BoardResponseDto> findBoard(Long id) {
        BoardResponseDto boardResponseDto = new BoardResponseDto(boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Can not find board")));
        return StatusResponseDto.success(boardResponseDto);
    }

    //선택 게시글 수정
    @Transactional
    public StatusResponseDto<BoardResponseDto> updateBoard(Long id, BoardRequestDto boardrequestDto, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("It doesn't exist this board"));
        if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(board.getUser().getUsername())) {
            board.update(boardrequestDto);
            return StatusResponseDto.success(new BoardResponseDto(board));
        } else {
            throw new IllegalArgumentException("작성자만 수정가능합니다.");
        }
    }


    //선택 게시글 삭제
    public StatusResponseDto<String> removeBoard(Long id, UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not exist"));
        if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(board.getUser().getUsername())) {
            boardRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("작성자만 삭제가 가능합니다.");
        }
        return StatusResponseDto.success("delete success");
    }

    public StatusResponseDto<String> likeBoard(Long id, UserDetailsImpl userDetails){
        Board board = boardRepository.findById(id).orElseThrow(
                ()-> new NullPointerException("존재하지 않는 게시글")
        );
        Optional<BoardLike> optionalBoardLike = boardLikeRepository.findByBoardAndUser(board, userDetails.getUser());
        if (optionalBoardLike.isPresent()){
            boardLikeRepository.deleteById(optionalBoardLike.get().getId());
            return StatusResponseDto.success("게시글 좋아요 취소");
        }

        boardLikeRepository.save(new BoardLike(board, userDetails.getUser()));
        return StatusResponseDto.success("게시글 좋아요 성공");
    }
}
