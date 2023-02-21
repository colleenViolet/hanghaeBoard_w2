package com.sparta.hanghaeboard.controller;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.security.UserDetailsImpl;
import com.sparta.hanghaeboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    //전체 게시글확인
    @GetMapping("/boards")
    public StatusResponseDto<List<BoardResponseDto>> getBoards(){
        return boardService.findBoards();
    }

    // 게시글 생성하기
    @PostMapping("/boards")
    public StatusResponseDto<BoardResponseDto> createdBoard(@RequestBody BoardRequestDto boardRequestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.createBoard(boardRequestDto, userDetails);
    }

    //선택 게시글 확인
    @GetMapping("/board/{id}")
    public StatusResponseDto<BoardResponseDto> getBoard(@PathVariable Long id){
        return boardService.findBoard(id);
    }

    //선택게시글 수정
    @PutMapping("/board/{id}")
    public StatusResponseDto<BoardResponseDto> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto requestDto,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.updateBoard(id, requestDto,userDetails);
    }

    //선택게시글 삭제
    @DeleteMapping("/board/{id}")
    public StatusResponseDto<String> deleteBoard(@PathVariable Long id,  @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.removeBoard(id,userDetails);
    }

    //선택 게시글 좋아요 구현
    @PostMapping("/board/like/{id}")
    public StatusResponseDto<String> likeBoard(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.likeBoard(id, userDetails);
    }
}

