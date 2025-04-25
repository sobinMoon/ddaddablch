package com.donation.ddb.Domain;


public enum WalletAuthStatus {
    NONE,
    PENDING, //연결 진행중 or 인증 대기
    CONNECTED,
    DISCONNECTED,
    FAILED //연결 or 인증 실패
}
