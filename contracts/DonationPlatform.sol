// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/utils/Counters.sol";
import "@openzeppelin/contracts/utils/Strings.sol";
import "@openzeppelin/contracts/utils/Base64.sol";

contract DonationPlatform is ERC721, ERC721URIStorage {
    //상태 변수
    address public owner;
    uint256 public platformFee; //플랫폼 수수료 (100분의 1 퍼센트 단위,100=1%)
    uint256 public totalDonations;

    // NFT 관련 변수
    uint256 public minDonationForNFT=0.00000000001 ether; //일단 작게 설정함.  
    // string public baseImageURI="https://your-domain.com/nft-images/"; //NFT 이미지 기본 URI

    using Counters for Counters.Counter;
    using Strings for uint256;

    //카운터
    Counters.Counter private _tokenIdCounter;

    //카테고리 정보 구조체 
    struct CategoryInfo{
        string name; 
        string description; 
        string imageFolder; 
        bool isActive; 
        uint256 totalRaised; 
        uint256 donationCount; 
    }

    //기부금 기록을 위한 구조체
    struct Donation{
        address donor;
        uint256 amount;
        uint256 timestamp;
        uint256 nftTokenId;
        DonationCategory category;
    }

    enum DonationCategory {
        CHILDREN, 
        ELDERLY, 
        ENVIRONMENT,
        ANIMAL, 
        MEDICAL, 
        SOCIETY 
    }

    struct NFTMetadata {
        address beneficiary; 
        uint256 donationAmount;
        uint256 timestamp; 
        DonationCategory category; 
    }
    mapping(address=> uint256) public beneficiaryBalances;
    mapping(address=> Donation[]) public beneficiaryDonations;
    mapping(uint256=> NFTMetadata) public nftMetadata;
    mapping(address=> uint256[]) public donorNFTs;
    mapping(address => uint256) public donorTotalDonations;

    mapping(DonationCategory => CategoryInfo) public categories;

    mapping(DonationCategory => uint256[]) public categoryNFTs; 
    mapping(DonationCategory => string) public categoryImageUrls;   
    function _initializeImageUrls() internal{
        categoryImageUrls[DonationCategory.CHILDREN] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_children.png?raw=true";
            categoryImageUrls[DonationCategory.ELDERLY] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_elderly.png?raw=true";
            categoryImageUrls[DonationCategory.ENVIRONMENT] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_environment.png?raw=true";
            categoryImageUrls[DonationCategory.ANIMAL] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_animal.png?raw=true";
            categoryImageUrls[DonationCategory.MEDICAL] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_medical.png?raw=true";
            categoryImageUrls[DonationCategory.SOCIETY] = "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_society.png?raw=true";
    }

    event DonationReceived(address indexed donor, address indexed beneficiary,
    uint256 amount,DonationCategory category);
    event FundsWithdrawn(address indexed beneficiary, uint256 amount);
    event PlatformFeeWithdrawn(address indexed owner,uint256 amount);
    event NFTMinted(uint256 indexed tokenId, address indexed donor,address indexed beneficiary,
     uint256 amount,DonationCategory category);
    event CategoryImageUpdated(DonationCategory category, string imageUrl);

    //생성자
       constructor(uint256 _initialFee) ERC721("DonationCertificate", "DCERT") {
        owner = msg.sender;
        platformFee = _initialFee;
        _initializeCategories(); 
        _initializeImageUrls(); 
    }

    modifier onlyOwner(){
        require(msg.sender==owner, unicode"owner만 이 함수 호출 가능합니다");
        _;
    }

    function _initializeCategories() internal {
        categories[DonationCategory.CHILDREN] = CategoryInfo({
            name: unicode"아동청소년",
            description: unicode"아동 보호 및 복지 향상",
            imageFolder: "education",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });
        
        categories[DonationCategory.MEDICAL] = CategoryInfo({
            name: unicode"장애인",
            description: unicode"의료비 지원 및 의료 환경 개선",
            imageFolder: "medical",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });
        
        categories[DonationCategory.ENVIRONMENT] = CategoryInfo({
            name: unicode"환경",
            description: unicode"환경 보호 및 기후변화 대응",
            imageFolder: "environment",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });
        
        categories[DonationCategory.SOCIETY] = CategoryInfo({
            name: unicode"사회",
            description: unicode"자연재해 피해 복구 및 구호 및 기타",
            imageFolder: "disaster",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });
        
        categories[DonationCategory.ANIMAL] = CategoryInfo({
            name: unicode"동물보호",
            description: unicode"유기동물 보호 및 동물복지",
            imageFolder: "animal",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });
  
        
        categories[DonationCategory.ELDERLY] = CategoryInfo({
            name: unicode"노인복지",
            description: unicode"노인 돌봄 및 복지 서비스",
            imageFolder: "elderly",
            isActive: true,
            totalRaised: 0,
            donationCount: 0
        });

    }
    function setCategoryImageUrl(
        DonationCategory _category, 
        string memory _imageUrl
    ) public onlyOwner {
        require(categories[_category].isActive, unicode"활성화되지 않은 카테고리입니다.");
        categoryImageUrls[_category] = _imageUrl;
        emit CategoryImageUpdated(_category, _imageUrl);
    }
    function donate(address _beneficiary,
                DonationCategory _category) public payable
                 returns (bool success, uint256 nftTokenId){
        require(msg.value>0,unicode"기부금은 0eth를 넘어야 합니다.");
        require(_beneficiary !=address(0),unicode"유효하지 않은 주소입니다.");
        require(categories[_category].isActive,unicode"활성화되지 않은 카테고리입니다.");

        uint256 fee=(msg.value * platformFee) /10000;
        uint256 donationAmount = msg.value-fee; 

        beneficiaryBalances[_beneficiary]+=donationAmount;

        donorTotalDonations[msg.sender] += msg.value;
        categories[_category].totalRaised += msg.value;
        categories[_category].donationCount++;

        uint256 tokenId=0;

        if (msg.value >= minDonationForNFT) {
            tokenId = _mintCategoryNFT(msg.sender, _beneficiary, donationAmount, _category);
        }

        beneficiaryDonations[_beneficiary].push(
            Donation({
                donor: msg.sender,
                amount: donationAmount,
                timestamp: block.timestamp,
                nftTokenId: tokenId,
                category:_category
            })
        );

        totalDonations+=msg.value;
        emit DonationReceived(msg.sender,_beneficiary,donationAmount,_category);
        return (true, tokenId);
    }
    function _mintCategoryNFT(
    address _donor,
    address _beneficiary, 
    uint256 _amount,
    DonationCategory _category
) internal returns (uint256){
    uint256 tokenId=_tokenIdCounter.current();
    _tokenIdCounter.increment();
    
    // ✅ 카테고리가 비활성화되면 SOCIETY(기본값)으로 변경
    DonationCategory finalCategory = categories[_category].isActive ? _category : DonationCategory.SOCIETY;
    
    _safeMint(_donor,tokenId);
    
    nftMetadata[tokenId] = NFTMetadata({
        beneficiary: _beneficiary,
        donationAmount: _amount,
        timestamp: block.timestamp,
        category: finalCategory  // ✅ 최종 카테고리 사용
    });
    
    string memory MetaDataURI = _generateCategoryTokenURI(
        tokenId, _beneficiary, _amount, finalCategory);  // ✅ 최종 카테고리 사용
    _setTokenURI(tokenId, MetaDataURI);
    
    donorNFTs[_donor].push(tokenId);
    categoryNFTs[finalCategory].push(tokenId);  // ✅ 최종 카테고리에 추가
    
    emit NFTMinted(tokenId, _donor, _beneficiary, _amount, finalCategory);

    return tokenId;
}
    function _generateCategoryTokenURI(
        uint256 _tokenId,
        address _beneficiary,
        uint256 _amount,
        DonationCategory _category
    ) internal view returns (string memory) {
       
        string memory imageUrl = categoryImageUrls[_category];

        if (bytes(imageUrl).length == 0) {
            imageUrl = "https://via.placeholder.com/400x400.png?text=Donation+NFT";
        }

        string memory json = string(abi.encodePacked(
            '{"name": "', categories[_category].name, unicode' 기부 인증서 #', _tokenId.toString(), '",',
            '"description": "', categories[_category].description, unicode'에 대한 소중한 기부에 감사드립니다.",',
            '"image": "', imageUrl, '",',
            '"external_url": "https://your-donation-platform.com/nft/', _tokenId.toString(), '",',
            '"attributes": [',
                unicode'{"trait_type": "카테고리", "value": "', categories[_category].name, '"},',
                unicode'{"trait_type": "수혜자", "value": "', Strings.toHexString(uint160(_beneficiary), 20), '"},',
                unicode'{"trait_type": "기부금액 (ETH)", "value": ', (_amount / 1e18).toString(), '},',
                unicode'{"trait_type": "기부금액 (Wei)", "value": "', _amount.toString(), '"},',
                unicode'{"trait_type": "기부일시", "value": ', block.timestamp.toString(), '}',
            ']}'
        ));
        string memory base64Json = Base64.encode(bytes(json));
        return string(abi.encodePacked("data:application/json;base64,", base64Json));
    }
    function getCategoryImageUrl(DonationCategory _category) public view returns (string memory) {
        return categoryImageUrls[_category];
    }
    function withdraw() public {
        address beneficiary = msg.sender;
        uint256 amount = beneficiaryBalances[beneficiary];
        
        require(amount > 0, unicode"인출할 기부금이 없습니다.");
        beneficiaryBalances[beneficiary] = 0;
        payable(beneficiary).transfer(amount);
        emit FundsWithdrawn(beneficiary, amount);
    }
    function withdrawPlatformFees() public onlyOwner {
        uint256 platformBalance = address(this).balance - getTotalBeneficiaryBalances();
        require(platformBalance > 0, unicode"플랫폼 수수료가 없습니다.");
        
        payable(owner).transfer(platformBalance);
        
        emit PlatformFeeWithdrawn(owner, platformBalance);
    }
    function setPlatformFee(uint256 _newFee) public onlyOwner{
        require(_newFee<=1000,unicode"수수료는 최대 5%까지만 설정 가능합니다."); //최대 5%
        platformFee=_newFee;
    }
    function getTotalBeneficiaryBalances() private view returns (uint256){
        return address(this).balance-(totalDonations*platformFee/10000);

    }
    function getBeneficiaryBalance(address _beneficiary) public view returns (uint256) {
        return beneficiaryBalances[_beneficiary];
    }
    function getDonationCount(address _beneficiary) public view returns (uint256){
         return beneficiaryDonations[_beneficiary].length;
    }
    function getDonationDetails(address _beneficiary, uint256 _index) public view returns (
        address donor,
        uint256 amount,
        uint256 timestamp
    ) {
        
        require(_index < beneficiaryDonations[_beneficiary].length, "Invalid donation index");
        Donation memory donation = beneficiaryDonations[_beneficiary][_index];
        return (donation.donor, donation.amount, donation.timestamp);
    }
        function getContractBalance() public view returns (uint256) {
            return address(this).balance;
        }
    function _burn(uint256 tokenId) internal override(ERC721, ERC721URIStorage) {
        super._burn(tokenId);
    }
    function tokenURI(uint256 tokenId) public view override(ERC721, ERC721URIStorage) returns (string memory) {
        return super.tokenURI(tokenId);
    }
    function supportsInterface(bytes4 interfaceId) public view override(ERC721, ERC721URIStorage) returns (bool) {
        return super.supportsInterface(interfaceId);
    }

}