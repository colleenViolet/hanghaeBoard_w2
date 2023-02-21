package com.sparta.hanghaeboard.dto;

import com.sparta.hanghaeboard.entity.Board;
import com.sparta.hanghaeboard.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int likeCount;

    private List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

    @Builder
    public BoardResponseDto(Board board){
        this.id = board.getId();
        this.title = board.getTitle();
        this.username = board.getUser().getUsername();
        this.content = board.getContents();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.likeCount = board.getLikes().size();
        for (Comment comment: board.getComments()){
            this.commentResponseDtos.add(CommentResponseDto.from(comment));
        }
    }
}
