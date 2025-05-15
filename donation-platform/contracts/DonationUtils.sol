// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title DonationUtils
 * @dev 기부 플랫폼에서 사용하는 유틸리티 함수들을 모아놓은 라이브러리
 */
library DonationUtils {
    /**
     * @dev uint를 string으로 변환하는 함수
     * @param value 변환할 uint 값
     * @return 변환된 string 값
     */
    function toString(uint256 value) internal pure returns (string memory) {
        if (value == 0) {
            return "0";
        }
        
        uint256 temp = value;
        uint256 digits;
        
        while (temp != 0) {
            digits++;
            temp /= 10;
        }
        
        bytes memory buffer = new bytes(digits);
        while (value != 0) {
            digits -= 1;
            buffer[digits] = bytes1(uint8(48 + uint256(value % 10)));
            value /= 10;
        }
        
        return string(buffer);
    }
    
    /**
     * @dev 이더를 Wei 단위로 변환하는 함수
     * @param amount 변환할 이더 양 (예: 1.5)
     * @return 변환된 Wei 값
     */
    function toWei(uint256 amount) internal pure returns (uint256) {
        return amount * 1 ether;
    }
    
    /**
     * @dev Wei를 이더 단위로 변환하는 함수
     * @param amount 변환할 Wei 양
     * @return 변환된 이더 값
     */
    function fromWei(uint256 amount) internal pure returns (uint256) {
        return amount / 1 ether;
    }
    
    /**
     * @dev 두 문자열이 같은지 비교하는 함수
     * @param a 첫 번째 문자열
     * @param b 두 번째 문자열
     * @return 문자열이 같으면 true, 다르면 false
     */
    function stringsEqual(string memory a, string memory b) internal pure returns (bool) {
        return keccak256(abi.encodePacked(a)) == keccak256(abi.encodePacked(b));
    }
    
    /**
     * @dev 타임스탬프를 Date 문자열로 변환하는 함수
     * @param timestamp Unix 타임스탬프
     * @return 날짜 문자열 (예: '20250513')
     */
    function timestampToDateString(uint256 timestamp) internal pure returns (string memory) {
        // 이 함수는 실제로는 복잡한 계산이 필요하지만, 
        // 여기서는 간단한 구현만 제공합니다.
        // 실제 구현에서는 더 정교한 날짜 계산이 필요할 수 있습니다.
        return toString(timestamp);
    }
}