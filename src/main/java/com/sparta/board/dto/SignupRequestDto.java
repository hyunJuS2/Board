package com.sparta.board.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

    @Pattern(regexp = "^[a-z0-9]{4,10}$", message = "소문자, 숫자 포함 4-10자리를 입력했는지 확인하세요.")
    private String username;


    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*\\])(?=.]{8,15}$",
            message = "대소문자, 숫자, 특수문자(!@#$%^&*)가 최소 하나씩 들어가는 8-15 자리를 입력했는지 확인하세요")
    private String password;
    private boolean admin = false;
    private String adminToken = "";
}
