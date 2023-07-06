package com.sparta.board.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ResultResponseDto {
    private String message;
    private String statusCode;

    public ResultResponseDto(String msg, String httpStatus) {
        this.message = msg;
        this.statusCode = httpStatus;
    }
}
