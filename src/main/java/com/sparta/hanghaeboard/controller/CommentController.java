package com.sparta.hanghaeboard.controller;

import com.sparta.hanghaeboard.dto.CommentRequestDto;
import com.sparta.hanghaeboard.dto.CommentResponseDto;
import com.sparta.hanghaeboard.dto.StatusResponseDto;
import com.sparta.hanghaeboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/{id}")
    public StatusResponseDto<CommentResponseDto> createComment(@PathVariable Long id, String content, HttpServletRequest request){
        return commentService.createComment(id, content, request);
    }

    @PutMapping ("/comment/{id}")
    public StatusResponseDto<CommentResponseDto> updateComment(@PathVariable Long id, CommentRequestDto commentRequestDto, HttpServletRequest request){
        return commentService.updateComment(id, commentRequestDto, request);
    }

    @DeleteMapping("/comment/{id}")
    public StatusResponseDto<String> deleteComment(@PathVariable Long id, HttpServletRequest request){
        return commentService.deleteComment(id, request);
    }

}
