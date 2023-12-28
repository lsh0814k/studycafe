package com.studycafe.modules.account.exception;


public class UnMatchedTokenException extends RuntimeException {
    public UnMatchedTokenException() {
        super("이메일 확인 링크가 정확하지 않습니다.");
    }
}
