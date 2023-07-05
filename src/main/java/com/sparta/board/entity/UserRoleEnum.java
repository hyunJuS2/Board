package com.sparta.board.entity;

//사용자 권한을 주기 위한 enum class

public enum UserRoleEnum {

    USER(Authority.USER), //사용자 권한

    ADMIN(Authority.ADMIN); //관리자 권한

    private final String authority;

    UserRoleEnum(String authority) { //생성자
        this.authority = authority;
    }
    public String getAuthority() { //Getter
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
