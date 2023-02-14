package com.sparta.hanghaeboard.dto;
import lombok.Getter;

@Getter
public class StatusResponseDto {

    private int statusCode;
    private String statusMessage;

    public StatusResponseDto(int statusCode, String statusMessage){
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
