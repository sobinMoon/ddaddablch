package com.donation.ddb.Domain;


public enum WalletAuthStatus {
    NONE,//지갑 연결 안된 초기 상태
    PENDING, //연결 진행중 or 인증 대기
    CONNECTED,//지갑 연결 및 검증 완료
    REJECTED, //지갑 연결 거부됨.
    FAILED; //연결 or 인증 실패

    public boolean isActive(){
        return this==CONNECTED;
    }
}
