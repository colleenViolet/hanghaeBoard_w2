package com.sparta.hanghaeboard.controller;

import com.sparta.hanghaeboard.dto.BoardRequestDto;
import com.sparta.hanghaeboard.dto.BoardResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BoardController {

    private BoardService boardService;

    //전체 게시글확인
    @GetMapping("/boards")
    public ResponseEntity<List<BoardResponseDto>> getBoards(){
        return boardService.findBoards();
    }

    // 게시글 생성하기
    @PostMapping("/boards")
    public ResponseEntity createdBoard(@RequestBody BoardRequestDto boardRequestDto, HttpServletRequest request){
        return boardService.createBoard(boardRequestDto, request);
    }

    //선택 게시글 확인
    @GetMapping("/board/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long id){
        return boardService.findBoard(id);
    }

    //선택게시글 수정
    @PutMapping("/board/{id}")
    public ResponseEntity<Object> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto requestDto, HttpServletRequest req){
        return boardService.updateBoard(id, requestDto,req);
    }

    //선택게시글 삭제
    @DeleteMapping("/board/{id}")
    public ResponseEntity<StatusResponseDto> deleteBoard(@PathVariable Long id, HttpServletRequest req){
        return boardService.removeBoard(id,req);
    }
}

