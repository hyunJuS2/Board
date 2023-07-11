package com.sparta.board.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResultResponseDto {
    private final String Message;
    private final int statusCode;

    public ResultResponseDto(String Message , int statusCode) {
        this.Message = Message;
        this.statusCode = statusCode;
    }
}
