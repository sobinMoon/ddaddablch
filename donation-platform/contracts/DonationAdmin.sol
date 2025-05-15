// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";
import "./DonationStructs.sol";

/**
 * @title DonationAdmin
 * @dev 기부 플랫폼의 관리자 기능 컨트랙트
 * 프로젝트 생성, 상태 변경 등 관리자 권한이 필요한 기능들을 포함합니다.
 */
contract DonationAdmin is Ownable {
    // 플랫폼 수수료 관련 변수
    address public platformFeeRecipient;
    uint256 public platformFeePercent = 2; // 2% 기본 수수료
    
    // 컨트랙트 생성자
    constructor() Ownable(msg.sender) {
        platformFeeRecipient = msg.sender;
    }
    
    /**
     * @dev 플랫폼 수수료 설정
     * @param _feePercent 새로운 수수료 비율 (%)
     */
    function setPlatformFee(uint256 _feePercent) external onlyOwner {
        require(_feePercent <= 10, unicode"운영수수료는 10%를 넘길 수 없습니다.");
        platformFeePercent = _feePercent;
    }
    
    /**
     * @dev 수수료 수령자 변경
     * @param _recipient 새로운 수수료 수령자 주소
     */
    function setPlatformFeeRecipient(address _recipient) external onlyOwner {
        require(_recipient != address(0), unicode"주소는 0x0이 될 수 없습니다.");
        platformFeeRecipient = _recipient;
    }
    
    /**
     * @dev 새 프로젝트 생성 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _createProject(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 projectId,
        string memory _name,
        string memory _description,
        address payable _recipient,
        uint256 _targetAmount,
        uint256 _deadline
    ) internal {
        require(_targetAmount > 0, unicode"목표 기부 금액은 0eth이상입니다.");
        require(_deadline > block.timestamp, unicode"기부 마감일을 다시 확인해주세요.");
        require(_recipient != address(0), unicode"캠페인 주소는 0x0일 수 없습니다.");
        
        DonationStructs.Project storage newProject = projects[projectId];
        newProject.recipient = _recipient;
        newProject.name = _name;
        newProject.description = _description;
        newProject.targetAmount = _targetAmount;
        newProject.raisedAmount = 0;
        newProject.isActive = true;
        newProject.deadline = _deadline;
        newProject.createTime = block.timestamp;
        newProject.uniqueDonorCount = 0;
    }
    
    /**
     * @dev 프로젝트 활성화/비활성화 토글 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _toggleProjectStatus(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 _projectId,
        uint256 projectCount
    ) internal {
        require(_projectId > 0 && _projectId <= projectCount, unicode"프로젝트 id를 다시 입력해주세요.");
        projects[_projectId].isActive = !projects[_projectId].isActive;
    }
    
    /**
     * @dev 프로젝트 마감일 연장 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _extendProjectDeadline(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 _projectId,
        uint256 _newDeadline,
        uint256 projectCount
    ) internal {
        require(_projectId > 0 && _projectId <= projectCount, unicode"유효하지 않은 projectId입니다.");
        require(_newDeadline > block.timestamp, unicode"연장된 마감일 입력이 잘못되었습니다.");
        require(_newDeadline > projects[_projectId].deadline, unicode"연장된 마감일은 연장 전 마감일보다 길어야 합니다.");
        
        projects[_projectId].deadline = _newDeadline;
    }
    
    /**
     * @dev 프로젝트 목표 금액 증가 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _increaseProjectTarget(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 _projectId,
        uint256 _newTarget,
        uint256 projectCount
    ) internal {
        require(_projectId > 0 && _projectId <= projectCount, unicode"유효하지 않은 projectId입니다.");
        require(_newTarget > projects[_projectId].targetAmount, unicode"새로 설정할 목표 금액은 기존 금액보다 높아야 합니다.");
        
        projects[_projectId].targetAmount = _newTarget;
    }
    
    /**
     * @dev 비상 상황 시 자금 회수 기능
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _emergencyWithdraw(address payable _to, uint256 _amount) internal {
        _to.transfer(_amount);
        //컨트랙트에 남은 잔액을 _to 주소로 보내줌 
    }
}