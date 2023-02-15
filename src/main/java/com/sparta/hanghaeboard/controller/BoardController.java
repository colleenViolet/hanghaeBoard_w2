package com.sparta.hanghaeboard.controller;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.service.BoardService;
import lombok.RequiredArgsConstructor;
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
    public StatusResponseDto<BoardResponseDto> createdBoard(@RequestBody BoardRequestDto boardRequestDto, HttpServletRequest request){
        return boardService.createBoard(boardRequestDto, request);
    }

    //선택 게시글 확인
    @GetMapping("/board/{id}")
    public StatusResponseDto<BoardResponseDto> getBoard(@PathVariable Long id){
        return boardService.findBoard(id);
    }

    //선택게시글 수정
    @PutMapping("/board/{id}")
    public StatusResponseDto<BoardResponseDto> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto requestDto, HttpServletRequest req){
        return boardService.updateBoard(id, requestDto,req);
    }

    //선택게시글 삭제
    @DeleteMapping("/board/{id}")
    public StatusResponseDto<String> deleteBoard(@PathVariable Long id, HttpServletRequest req){
        return boardService.removeBoard(id,req);
    }
}

