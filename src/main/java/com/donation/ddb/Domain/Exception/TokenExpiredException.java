package com.donation.ddb.Domain.Exception;

public class TokenExpiredException extends RuntimeException {
    //기본 생성자
    public TokenExpiredException(){
        super("인증 번호가 만료되었습니다. ");
    }

    //메시지 받는 생성자
    public TokenExpiredException(String message){
        super(message);
    }

    //원인 예외를 포함하는 생성자
    public TokenExpiredException(String message, Throwable e){
        super(message,e);
    }

}
