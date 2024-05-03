package com.example.demo.exception.api;

import lombok.Getter;

@Getter
public enum ApiResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    REQUEST_ERROR(2000, "입력값을 확인해주세요."),
    EMPTY_JWT(2001, "JWT를 입력해주세요."),
    INVALID_JWT( 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(2003,"권한이 없는 유저의 접근입니다."),
    NONE_EXIST_USER( 2004, "존재하지 않는 사용자입니다."),
    NONE_EXIST_NICKNAME(2005, "존재하지 않는 닉네임입니다."),

    USERS_EMPTY_USER_ID(2006, "유저 아이디 값을 확인해주세요."),
    INVALID_MEMBER_ID(2007, "멤버 아이디와 이메일이 일치하지 않습니다."),
    PASSWORD_CANNOT_BE_NULL(2008, "비밀번호를 입력해주세요."),

    POST_USERS_EMPTY_EMAIL(2009, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(2010, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(2011,"중복된 이메일입니다."),
    LOG_OUT_USER(2012,"이미 로그아웃된 유저입니다."),
    CANNOT_UPDATE_NICKNAME(2013, "동일한 닉네임으로 변경할 수 없습니다."),

    /**
     * 3000 : Response 오류
     */
    RESPONSE_ERROR(3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(4001, "서버와의 연결에 실패하였습니다."),

    /**
     *   5000 : Diary 관련 오류
     */
    NONE_EXIST_REVIEW(5000, "요청하신 일기는 존재하지 않습니다."),
    INVALID_DIARY_ID(5001, "유효하지 않은 입력입니다."),
    EXCEEDED_CONTENT_LIMIT(5002, "본문이 글자수 제한을 초과하였습니다."),
    USER_WITHOUT_PERMISSION(5003, "본인의 일기에 대해서만 수정 및 삭제가 가능합니다."),

    /**
     *   6000 : 회원등록 관련 오류
     */
    DUPLICATED_NICKNAME(6000, "이미 존재하는 닉네임입니다."),
    KAKAO_ERROR(6001, "카카오 로그아웃에 실패했습니다."),
    ALREADY_LOGIN(6002, "이미 로그인된 유저입니다."),
    ALREADY_REGISTERED_USER(6003, "이미 가입된 유저입니다."),
    NICKNAME_CANNOT_BE_NULL(6004, "닉네임을 입력해주세요"),
    EMAIL_CANNOT_BE_NULL(6005, "이메일을 입력해주세요"),

    /**
     *   7000 : 토큰 관련 오류
     */
    EXPIRED_USER_JWT(7000,"만료된 JWT입니다."),
    REISSUE_TOKEN(7001, "토큰이 만료되었습니다. 다시 로그인해주세요."),
    FAILED_TO_UPDATE(7002, "토큰을 만료시키는 작업에 실패하였습니다."),
    FAILED_TO_REFRESH(7003, "토큰 재발급에 실패하였습니다.");


    private final int code;
    private final String message;

    private ApiResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

}