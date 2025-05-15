// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./ReentrancyGuard.sol";
import "./DonationStructs.sol";

/**
 * @title DonationCore
 * @dev 기부 플랫폼의 핵심 기능 컨트랙트
 * 기부 처리, 자금 인출 등 핵심 금융 로직이 포함됩니다.
 */
contract DonationCore is ReentrancyGuard {
    // 타임락 관련 상수
    // 기부금 인출 요청 후 24시간 후에만 실행 가능한 타임락 설정 -> 재진입 공격 방지 
    uint256 public constant WITHDRAWAL_TIMELOCK = 1 days; // 24시간 대기
    
    
    /**
     * @dev 기부 처리 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _processDonation(
        mapping(uint256 => DonationStructs.Project) storage projects,
        mapping(address => DonationStructs.Donor) storage donors,
        mapping(uint256 => DonationStructs.Donation[]) storage projectDonations,
        uint256 _projectId,
        bool _isAnonymous,
        address _donor,
        uint256 _donationAmount,
        address _feeRecipient,
        uint256 _fee
    ) internal nonReentrant {
        // 수수료 전송
        if (_fee > 0) {
            payable(_feeRecipient).transfer(_fee);
        }
        
        DonationStructs.Project storage project = projects[_projectId];
        project.raisedAmount += _donationAmount;
        
        // 기부자 정보 업데이트
        DonationStructs.Donor storage donor = donors[_donor];
        donor.totalDonated += _donationAmount;
        
        // 중복 없이 기부한 프로젝트 추가
        // 기부한 적이 없는 프로젝트의 경우 플젝 id추가 donorExist등록, uniqueDonerCount증가
        if (!project.donorExists[_donor]) {
            donor.donatedProjects.push(_projectId);
            project.donorExists[_donor] = true;
            project.uniqueDonorCount++;
        }
        
        // 기부 내역 기록
        projectDonations[_projectId].push(DonationStructs.Donation({
            donor: _donor,
            projectId: _projectId,
            amount: _donationAmount,
            timestamp: block.timestamp,
            isAnonymous: _isAnonymous
        }));
        
        // 목표금액 달성 시 자동 종료
        if (project.raisedAmount >= project.targetAmount) {
            project.isActive = false;
        }
    }
    
    /**
     * @dev 기부금 인출 요청 처리
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
     //nonReentrant사용 이유
     //수수료송금,환불 등 이더 송금하는 로직 포함되어 있으므로
     
    function _requestWithdrawal(
        mapping(uint256 => DonationStructs.Project) storage projects,
        mapping(uint256 => DonationStructs.WithdrawalRequest) storage withdrawalRequests,
        uint256 _requestId,
        uint256 _projectId,
        uint256 _amount
    ) internal nonReentrant {
        DonationStructs.Project storage project = projects[_projectId];
        require(_amount > 0 && _amount <= project.raisedAmount, "Invalid amount");
        
        withdrawalRequests[_requestId] = DonationStructs.WithdrawalRequest({
            projectId: _projectId,
            amount: _amount,
            requestTime: block.timestamp,
            executed: false
        });
    }
    
    /**
     * @dev 기부금 인출 실행
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _executeWithdrawal(
        mapping(uint256 => DonationStructs.WithdrawalRequest) storage withdrawalRequests,
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 _requestId
    ) internal nonReentrant returns (address recipient, uint256 amount) {
        DonationStructs.WithdrawalRequest storage request = withdrawalRequests[_requestId];
        require(!request.executed, "Request already executed");
        require(block.timestamp >= request.requestTime + WITHDRAWAL_TIMELOCK, "Timelock period not passed");
        
        DonationStructs.Project storage project = projects[request.projectId];
        require(request.amount <= project.raisedAmount, "Insufficient funds");
        
        request.executed = true;
        project.raisedAmount -= request.amount;
        
        return (project.recipient, request.amount);
    }
    
    /**
     * @dev 프로젝트 취소 및 환불 처리
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _cancelProjectAndRefund(
        mapping(uint256 => DonationStructs.Project) storage projects,
        mapping(uint256 => DonationStructs.Donation[]) storage projectDonations,
        uint256 _projectId
    ) internal nonReentrant {
        DonationStructs.Project storage project = projects[_projectId];
        require(project.isActive, "Project is not active");
        
        project.isActive = false;
        
        // 모든 기부 내역 순회하며 환불
        DonationStructs.Donation[] storage donations = projectDonations[_projectId];
        for (uint256 i = 0; i < donations.length; i++) {
            DonationStructs.Donation storage donation = donations[i];
            payable(donation.donor).transfer(donation.amount);
        }
        
        // 프로젝트 잔액 초기화
        project.raisedAmount = 0;
    }
}