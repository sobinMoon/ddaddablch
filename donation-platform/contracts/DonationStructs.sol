// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

// 기부 플랫폼에서 사용하는 모든 데이터 구조 정의
library DonationStructs {
    
    // 기부 프로젝트(수혜자) 구조체
    struct Project {
        address payable recipient;
        string name;
        string description;
        uint256 targetAmount;
        uint256 raisedAmount;
        bool isActive;
        uint256 deadline;
        uint256 createTime;
        mapping(address => bool) donorExists;
        uint256 uniqueDonorCount;
    }

    // 기부 내역 구조체
    struct Donation {
        address donor;
        uint256 projectId;
        uint256 amount;
        uint256 timestamp;
        bool isAnonymous;
    }

    // 기부자 구조체
    struct Donor {
        string name;
        uint256 totalDonated;
        uint256[] donatedProjects;
        bool hasSetName;
    }

    // 인출 요청 구조체
    struct WithdrawalRequest {
        uint256 projectId;
        uint256 amount;
        uint256 requestTime;
        bool executed;
    }
}