// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract ReentrancyGuard {
    // 재진입 방지를 위한 상태 변수
    bool private locked;

    modifier nonReentrant() {
        require(!locked, unicode"ReentrancyGuard: 재진입 시도가 감지되었습니다");
        locked = true;
        _;
        locked = false;
    }
}