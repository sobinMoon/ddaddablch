// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/access/Ownable.sol";

/**
 * @title DonationCertificate
 * @dev 기부자에게 제공되는 기부 인증서 NFT
 * 기부 플랫폼에서만 발행 가능합니다.
 */
contract DonationCertificate is ERC721URIStorage, Ownable {
    uint256 private _tokenIds;
    address public donationPlatform;
    
    /**
     * @dev 컨트랙트 생성자
     * @param _donationPlatform 기부 플랫폼 컨트랙트 주소
     */
    constructor(address _donationPlatform) ERC721("Donation Certificate", "DCERT") Ownable(msg.sender) {
        donationPlatform = _donationPlatform;
    }
    
    /**
     * @dev 인증서 NFT 발행 함수
     * @param donor 인증서를 받을 기부자 주소
     * @param tokenURI 인증서 메타데이터 URI
     * @return 발행된 토큰 ID
     * 기부 플랫폼 컨트랙트만 호출할 수 있습니다.
     */
    function mintCertificate(address donor, string memory tokenURI) external returns (uint256) {
        require(msg.sender == donationPlatform, "Only donation platform can mint");
        
        _tokenIds++;
        uint256 newItemId = _tokenIds;
        _mint(donor, newItemId);
        _setTokenURI(newItemId, tokenURI);
        
        return newItemId;
    }
    
    /**
     * @dev 기부 플랫폼 주소 변경 함수
     * @param _newPlatform 새로운 기부 플랫폼 주소
     * 컨트랙트 소유자만 호출할 수 있습니다.
     */
    function setDonationPlatform(address _newPlatform) external onlyOwner {
        require(_newPlatform != address(0), "Invalid platform address");
        donationPlatform = _newPlatform;
    }
}