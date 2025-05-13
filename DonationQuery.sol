// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./DonationStructs.sol";

/**
 * @title DonationQuery
 * @dev 기부 플랫폼의 데이터 조회 기능을 제공하는 컨트랙트
 * 프로젝트 정보, 기부 내역, 기부자 정보 등 조회 기능이 포함됩니다.
 */
contract DonationQuery {
    /**
     * @dev 프로젝트 정보 조회
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _getProjectDetails(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 _projectId
    ) internal view returns (
        string memory name,
        string memory description,
        address recipient,
        uint256 targetAmount,
        uint256 raisedAmount,
        bool isActive,
        uint256 deadline,
        uint256 createTime,
        uint256 uniqueDonorCount
    ) {
        DonationStructs.Project storage project = projects[_projectId];
        return (
            project.name,
            project.description,
            project.recipient,
            project.targetAmount,
            project.raisedAmount,
            project.isActive,
            project.deadline,
            project.createTime,
            project.uniqueDonorCount
        );
    }
    
    /**
     * @dev 프로젝트의 기부 내역 조회
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _getProjectDonations(
        mapping(uint256 => DonationStructs.Donation[]) storage projectDonations,
        uint256 _projectId,
        uint256 _offset,
        uint256 _limit
    ) internal view returns (
        address[] memory donors,
        uint256[] memory amounts,
        uint256[] memory timestamps,
        bool[] memory anonymousFlags
    ) {
        DonationStructs.Donation[] storage donations = projectDonations[_projectId];
        
        uint256 resultCount = _limit;
        if (_offset + _limit > donations.length) {
            resultCount = donations.length > _offset ? donations.length - _offset : 0;
        }
        
        donors = new address[](resultCount);
        amounts = new uint256[](resultCount);
        timestamps = new uint256[](resultCount);
        anonymousFlags = new bool[](resultCount);
        
        for (uint256 i = 0; i < resultCount; i++) {
            DonationStructs.Donation storage donation = donations[_offset + i];
            donors[i] = donation.donor;
            amounts[i] = donation.amount;
            timestamps[i] = donation.timestamp;
            anonymousFlags[i] = donation.isAnonymous;
        }
        
        return (donors, amounts, timestamps, anonymousFlags);
    }
    
    /**
     * @dev 기부자 정보 조회
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _getDonorInfo(
        mapping(address => DonationStructs.Donor) storage donors,
        address _donor
    ) internal view returns (
        string memory name,
        uint256 totalDonated,
        uint256[] memory donatedProjects,
        bool hasSetName
    ) {
        DonationStructs.Donor storage donor = donors[_donor];
        return (donor.name, donor.totalDonated, donor.donatedProjects, donor.hasSetName);
    }
    
    /**
     * @dev 활성화된 프로젝트 목록 조회
     * 상세 구현은 DonationPlatform 컨트랙트에서 이루어집니다.
     */
    function _getActiveProjects(
        mapping(uint256 => DonationStructs.Project) storage projects,
        uint256 projectCount,
        uint256 _offset,
        uint256 _limit
    ) internal view returns (
        uint256[] memory projectIds,
        string[] memory names,
        uint256[] memory targetAmounts,
        uint256[] memory raisedAmounts,
        uint256[] memory deadlines
    ) {
        // 먼저 활성 프로젝트 수 계산
        uint256 activeCount = 0;
        for (uint256 i = 1; i <= projectCount; i++) {
            if (projects[i].isActive) {
                activeCount++;
            }
        }
        
        uint256 resultCount = _limit;
        if (_offset >= activeCount) {
            resultCount = 0;
        } else if (_offset + _limit > activeCount) {
            resultCount = activeCount - _offset;
        }
        
        projectIds = new uint256[](resultCount);
        names = new string[](resultCount);
        targetAmounts = new uint256[](resultCount);
        raisedAmounts = new uint256[](resultCount);
        deadlines = new uint256[](resultCount);
        
        if (resultCount > 0) {
            uint256 activeIndex = 0;
            uint256 resultIndex = 0;
            
            for (uint256 i = 1; i <= projectCount && resultIndex < resultCount; i++) {
                if (projects[i].isActive) {
                    if (activeIndex >= _offset) {
                        projectIds[resultIndex] = i;
                        names[resultIndex] = projects[i].name;
                        targetAmounts[resultIndex] = projects[i].targetAmount;
                        raisedAmounts[resultIndex] = projects[i].raisedAmount;
                        deadlines[resultIndex] = projects[i].deadline;
                        resultIndex++;
                    }
                    activeIndex++;
                }
            }
        }
        
        return (projectIds, names, targetAmounts, raisedAmounts, deadlines);
    }
}