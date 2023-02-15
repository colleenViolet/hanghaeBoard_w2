package com.sparta.hanghaeboard.service;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.User;
import com.sparta.hanghaeboard.entity.UserRoleEnum;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //게시글 올리기
  public StatusResponseDto<BoardResponseDto> createBoard(BoardRequestDto boardRequestDto, HttpServletRequest request){
      String token = jwtUtil.resolveToken(request);
      Claims claims;
      if (token != null){
          if (jwtUtil.validateToken(token)){
              claims = jwtUtil.getUserInfoFromToken(token);
              User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(()-> new NullPointerException("Can not find user Information"));
              Board board = new Board(boardRequestDto, user);
              boardRepository.save(board);

              return StatusResponseDto.success(new BoardResponseDto(board));
          }
      }
      throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
  }

    //전체 게시글 조회
//    @Transactional(readOnly = true)
    public StatusResponseDto<List<BoardResponseDto>> findBoards(){
       List<Board> list = boardRepository.findAll();
       List<BoardResponseDto> boardResponseDtos = new ArrayList<>();
       for (Board board : list){
           boardResponseDtos.add(new BoardResponseDto(board));
       }
       return StatusResponseDto.success(boardResponseDtos);
    }

    //선탹 게시글 조회
//    @Transactional(readOnly = true)
    public StatusResponseDto<BoardResponseDto> findBoard(Long id){
       BoardResponseDto boardResponseDto = new BoardResponseDto(boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("Can not find board")));
        return StatusResponseDto.success(boardResponseDto);
    }

    //선택 게시글 수정
    @Transactional
    public StatusResponseDto<BoardResponseDto> updateBoard(Long id, BoardRequestDto boardrequestDto, HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        Claims claims;

        if(token != null){
            if (jwtUtil.validateToken(token)){
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(()-> new NullPointerException("Can not find User Info"));
                Board board = boardRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("It doesn't exist this board"));
                if (user.getRole()== UserRoleEnum.ADMIN|| user.getUsername().equals(board.getUser().getUsername())){
                    board.update(boardrequestDto);
                    return StatusResponseDto.success(new BoardResponseDto(board));
                }else {
                    throw  new IllegalArgumentException("작성자만 수정가능합니다.");
                }
            }
        }
        throw new IllegalArgumentException("토큰이 유효하지 않습니다");
    }

    //선택 게시글 삭제
    public StatusResponseDto<String> removeBoard(Long id, HttpServletRequest req) {
        String token = jwtUtil.resolveToken(req);
        Claims claims;

        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                claims = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(() -> new NullPointerException("Can not find User Info"));
                Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not exist"));
                if (user.getRole() == UserRoleEnum.ADMIN || user.getUsername().equals(board.getUser().getUsername())) {
                    boardRepository.deleteById(id);
                } else {
                    throw new IllegalArgumentException("작성자만 삭제가 가능합니다.");
                }
                return StatusResponseDto.success("delete success");
            }
        }
        throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
    }
}
