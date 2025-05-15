// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;


//기부 토큰 인터페이스
interface IDonationToken{
  function mint(address to, uint256 amount) external;
}

//기부 nft 인터페이스
interface IDonationCertificate{
  function mintCertificate(address donor, string memory tokenURI)
   external returns (uint256);
}

//기부 플랫폼 인터페이스
interface IDonationPlatform{
    function donate(uint256 projectId, bool isAnonymous) external payable;
    function getProjectDetails(uint projectId) external view returns (
        string memory name,
        string memory description,
        address recipient,
        uint256 targetAmount,
        uint256 raisedAmount,
        bool isActive,
        uint256 deadline,
        uint256 createTime,
        uint256 uniqueDonorCount
    );


}

