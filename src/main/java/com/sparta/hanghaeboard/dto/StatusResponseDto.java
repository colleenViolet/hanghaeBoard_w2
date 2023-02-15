package com.sparta.hanghaeboard.dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StatusResponseDto<T> {

    private int statusCode;
    private T result;

    public StatusResponseDto(int statusCode, T result){
        this.statusCode = statusCode;
        this.result = result;
    }

    public static <T> StatusResponseDto<T> success(T result){
        return new StatusResponseDto<>(200, result);
    }

    public static <T> StatusResponseDto<T> fail(int statusCode, T result){
        return new StatusResponseDto<>(statusCode, result);
    }
}
