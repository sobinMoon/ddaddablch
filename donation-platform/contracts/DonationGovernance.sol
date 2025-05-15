// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/governance/Governor.sol";
import "@openzeppelin/contracts/governance/extensions/GovernorCountingSimple.sol";
import "@openzeppelin/contracts/governance/extensions/GovernorVotes.sol";
import "@openzeppelin/contracts/governance/extensions/GovernorVotesQuorumFraction.sol";
import "@openzeppelin/contracts/governance/extensions/GovernorTimelockControl.sol";




/**
 * @title DonationGovernance
 * @dev 기부 플랫폼의 거버넌스 컨트랙트
 * 기부자들이 플랫폼 정책 변경에 투표할 수 있는 기능을 제공합니다.
 */
// contract DonationGovernance is 
//     Governor, 
//     GovernorCountingSimple, 
//     GovernorVotes, 
//     GovernorVotesQuorumFraction,
//     GovernorTimelockControl 
// {
//     /**
//      * @dev 컨트랙트 생성자
//      * @param _token 투표 토큰 컨트랙트(ERC20Votes 인터페이스 따르는 투표 토큰)
//      * @param _timelock 타임락 컨트랙트(제안이 실행되기까지 시간 지연 )
//      */
//     constructor(IVotes _token, TimelockController _timelock)
//         Governor("DonationGovernance")
//         GovernorVotes(_token)
//         GovernorVotesQuorumFraction(4) // 40% 쿼럼
//         GovernorTimelockControl(_timelock)
//     {}

//     /**
//      * @dev 투표 제안 후 투표 시작까지의 대기 블록 수
//      * @return 대기 블록 수
//      */
//     function votingDelay() public pure override returns (uint256) {
//         return 1; // 1 블록 (약 12초)
//     }

//     /**
//      * @dev 투표 기간 (블록 수)
//      * @return 투표 기간 블록 수
//      */
//     function votingPeriod() public pure override returns (uint256) {
//         return 45818; // 약 1주일 (12초 * 45818 ≈ 7일)
//     }

//     /**
//      * @dev 제안을 생성하기 위한 최소 토큰 보유량
//      * @return 최소 토큰 보유량
//      */
//     function proposalThreshold() public pure override returns (uint256) {
//         return 0; // 모든 토큰 보유자가 제안 가능
//     }

//     /**
//      * @dev 투표 제안이 처리되기 위한 최소 찬성 비율 (%)
//      * @return 최소 찬성 비율 (%)
//      */
//     function proposalSuccessThreshold() public pure returns (uint256) {
//         return 51; // 51% 이상 찬성 시 통과
//     }

//     // 필수 오버라이드 함수들
//     function propose(
//         address[] memory targets,
//         uint256[] memory values,
//         bytes[] memory calldatas,
//         string memory description
//     ) public override(Governor) returns (uint256) {
//         return super.propose(targets, values, calldatas, description);
//     }

//     function state(uint256 proposalId) public view override(Governor, GovernorTimelockControl) returns (ProposalState) {
//         return super.state(proposalId);
//     }

//     function _execute(
//         uint256 proposalId,
//         address[] memory targets,
//         uint256[] memory values,
//         bytes[] memory calldatas,
//         bytes32 descriptionHash
//     ) internal override(Governor, GovernorTimelockControl) {
//         super._execute(proposalId, targets, values, calldatas, descriptionHash);
//     }

//     function _cancel(
//         address[] memory targets,
//         uint256[] memory values,
//         bytes[] memory calldatas,
//         bytes32 descriptionHash
//     ) internal override(Governor, GovernorTimelockControl) returns (uint256) {
//         return super._cancel(targets, values, calldatas, descriptionHash);
//     }

//     function _executor() internal view override(Governor, GovernorTimelockControl) returns (address) {
//         return super._executor();
//     }

//     function supportsInterface(bytes4 interfaceId) public view override(Governor, GovernorTimelockControl) returns (bool) {
//         return super.supportsInterface(interfaceId);
//     }
// }


