// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;


//표준 ERC20 기능 (전송, 잔액 확인 등등)
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

//소유자만 특정 함수 실행 가능(접근제어자 onlyOwner 제공됨)
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title DonationToken
 * @dev 기부자에게 보상으로 제공되는 ERC20 토큰
 */

//DotnationToken은 ERC20 토큰이고, 소유자가 제어 가능한 컨트랙트임. 
contract DonationToken is ERC20, Ownable {

    //토큰을 발행할 수 있는 권한이 있는 기부 플랫폼 컨트랙트 주소. 

     /*
     * @dev 컨트랙트 생성자
     * @param _donationPlatform 기부 플랫폼 컨트랙트 주소
     */
    address public donationPlatform;
    
    /**
     * @dev 컨트랙트 생성자
     * @param _donationPlatform 기부 플랫폼 컨트랙트 주소
     */
     //처음 기부시
    constructor(address _donationPlatform) ERC20("Donation Token", "DONATE") Ownable(msg.sender) {
        donationPlatform = _donationPlatform;
    }
    
    /**
     * @dev 토큰 발행 함수
     * @param to 토큰을 받을 주소
     * @param amount 발행할 토큰 양
     * 기부 플랫폼 컨트랙트만 호출할 수 있습니다.
     */
    function mint(address to, uint256 amount) external {
        require(msg.sender == donationPlatform, unicode"잘못된 주소입니다.");
        _mint(to, amount); //기부금 받고 토큰 주기
    }
    
    /**
     * @dev 기부 플랫폼 주소 변경 함수
     * @param _newPlatform 새로운 기부 플랫폼 주소
     * 컨트랙트 소유자만 호출할 수 있습니다.
     */
    function setDonationPlatform(address _newPlatform) external onlyOwner {
        require(_newPlatform != address(0), unicode"Invalid platform address");
        donationPlatform = _newPlatform;
    }
}