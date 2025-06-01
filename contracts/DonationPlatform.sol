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
    string public baseImageURI="https://your-domain.com/nft-images/"; //NFT 이미지 기본 URI

    using Counters for Counters.Counter;
    using Strings for uint256;

    //카운터
    Counters.Counter private _tokenIdCounter;

    //카테고리 정보 구조체 
    struct CategoryInfo{
        string name; //카테고리 이름
        string description; //카테고리 설명
        string imageFolder; //이미지 폴더명 
        bool isActive; //카테고리 활성화 상태 
        uint256 totalRaised; //해당 카테고리 총 모금액 
        uint256 donationCount; //해당 카테고리 총 기부 횟수

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
        CHILDREN, //아동청소년
        ELDERLY, //노인
        ENVIRONMENT,//환경
        ANIMAL, //동물물
        MEDICAL, //장애인
        SOCIETY //사회 
    }

    struct NFTMetadata {
        address beneficiary; //수혜자 주소
        uint256 donationAmount; //기부 금액 
        uint256 timestamp; //기부 시점 
        //string message; //기부 메시지
        DonationCategory category; //기부 카테고리 
    }

    //NFT등급 -> 필요하면 구현하기



    //수혜자별 받은 기부금 기록
    mapping(address=> uint256) public beneficiaryBalances;
    //수혜자별 기부 내역
    mapping(address=> Donation[]) public beneficiaryDonations;
    //NFT 메타데이터 
    mapping(uint256=> NFTMetadata) public nftMetadata;
    mapping(address=> uint256[]) public donorNFTs;
    //기부자의 총 기부금액 
    mapping(address => uint256) public donorTotalDonations;
    // 기부자별 카테고리별 기부금액 -> 필요한가?
    //mapping(address => mapping(DonationCategory => uint256)) public donorCategoryDonations; 
    // 기부 카테고리별 카테고리 정보 
    mapping(DonationCategory => CategoryInfo) public categories;
    // 카테고리별 NFT 목록
    mapping(DonationCategory => uint256[]) public categoryNFTs; 



    //이벤트
    event DonationReceived(address indexed donor, address indexed beneficiary,
    uint256 amount,DonationCategory category);
    event FundsWithdrawn(address indexed beneficiary, uint256 amount);
    event PlatformFeeWithdrawn(address indexed owner,uint256 amount);
    event NFTMinted(uint256 indexed tokenId, address indexed donor,address indexed beneficiary,
     uint256 amount,DonationCategory category);
    //event CategoryUpdated(DonationCategory category, string name, string description);

    //생성자
       constructor(uint256 _initialFee) ERC721("DonationCertificate", "DCERT") {
        owner = msg.sender;
        platformFee = _initialFee;
        _initializeCategories(); // 카테고리 초기화 함수 
    }

    //소유자만 접근 가능한 modifier
    modifier onlyOwner(){
        require(msg.sender==owner, unicode"owner만 이 함수 호출 가능합니다");
        _;
    }

    // 카테고리 초기화
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


    //기부하기 - 특정 수혜자에게 직접 기부
    function donate(address _beneficiary,
                DonationCategory _category) public payable
                 returns (bool success, uint256 nftTokenId){
        require(msg.value>0,unicode"기부금은 0eth를 넘어야 합니다.");
        require(_beneficiary !=address(0),unicode"유효하지 않은 주소입니다.");
        require(categories[_category].isActive,unicode"활성화되지 않은 카테고리입니다.");

        //수수료 계산 (100분의 1% 단위)
        uint256 fee=(msg.value * platformFee) /10000;
        uint256 donationAmount = msg.value-fee; //수수료 뺀 값 계산 

        //수혜자의 잔액 증가 
        beneficiaryBalances[_beneficiary]+=donationAmount;

        //기부자의 총 기부금액 및 카테고리별 기부금액 업데이트 
        donorTotalDonations[msg.sender] += msg.value;

         // 카테고리 통계 업데이트
        categories[_category].totalRaised += msg.value;
        categories[_category].donationCount++;

        uint256 tokenId=0;

        // NFT 발행 조건 확인
        if (msg.value >= minDonationForNFT) {
            tokenId = _mintCategoryNFT(msg.sender, _beneficiary, donationAmount, _category);
        }

        //기부 내역 추가 
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
    }

    //카테고리별 NFT 발행
    function _mintCategoryNFT( //msg.sender, _beneficiary, donationAmount, _category
        address _donor,
        address _beneficiary, 
        uint256 _amount,
        DonationCategory _category
    ) internal returns (uint256){ //생성된 NFT의 토큰 ID를 반환
        uint256 tokenId=_tokenIdCounter.current();
        _tokenIdCounter.increment(); //각 NFT가 고유한 ID를 가지게함. 

            /**
            NFTTier tier = determineCategoryNFTTier(
            amount,
            donorTotalDonations[_donor],
            donorCategoryDonations[_donor][_category]
             ); */
        
        // NFT 발행
        //_safeMint는 OpenZeppelin의 안전한 NFT발행함수, 블록체인에 NFT 소유권 기록하기 위해 필요요
        _safeMint(_donor,tokenId);

         // 메타데이터 저장 -> NFT의 상세 정보를 컨트랙트에 저장해서 나중에 NFT정보 조회하기 위함. 
        nftMetadata[tokenId] = NFTMetadata({
            beneficiary: _beneficiary,
            donationAmount: _amount,
            timestamp: block.timestamp,
            category: _category
        });

        //NFT의 메타데이터 URI를 생성하고 설정 
        string memory MetaDataURI = _generateCategoryTokenURI(
            tokenId, _beneficiary, _amount, _category);
        _setTokenURI(tokenId, MetaDataURI);
        
         // 기부자의 NFT 목록에 추가 -> 기부자가 자신이 받은 NFF 목록 조회하기 위해 
        donorNFTs[_donor].push(tokenId);
        
        // 카테고리별 NFT 목록에 추가
        categoryNFTs[_category].push(tokenId);

        //NFT발행 이벤트를 블록체인에 기록 -> 프론트엔드에서 실시간으로 nft 발행 감지지
        emit NFTMinted(tokenId, _donor, _beneficiary, _amount, _category);

        return tokenId;
    }


    // 카테고리별 메타데이터 생성
    function _generateCategoryTokenURI(
        uint256 _tokenId,
        address _beneficiary,
        uint256 _amount,
        DonationCategory _category
    ) internal view returns (string memory) {
        
        // 카테고리별 이미지 URL 생성
        string memory imageUrl = string(abi.encodePacked(
            baseImageURI,
            categories[_category].imageFolder,
            "/",
            ".png"
        ));
        
        // JSON 메타데이터 생성
        string memory json = string(abi.encodePacked(
            '{"name": "', categories[_category].name, unicode' 기부 인증서 #', _tokenId.toString(), '",',
            '"image": "', imageUrl, '",',
            '"external_url": "https://your-donation-platform.com/nft/', _tokenId.toString(), '",',
            '"attributes": [',
                unicode'{"trait_type": "카테고리", "value": "', categories[_category].name, '"},',
                unicode'{"trait_type": "수혜자", "value": "', Strings.toHexString(uint160(_beneficiary), 20), '"},',
                unicode'{"trait_type": "기부금액 (ETH)", "value": ', (_amount / 1e18).toString(), '},',
                unicode'{"trait_type": "기부금액 (Wei)", "value": "', _amount.toString(), '"},',
                unicode'{"trait_type": "기부일시", "value": ', block.timestamp.toString(), '},',
              
            ']}'
        ));
        
        // Base64 인코딩
        string memory base64Json = Base64.encode(bytes(json));
        
        return string(abi.encodePacked("data:application/json;base64,", base64Json));
    }


    // 수혜자가 기부금 인출하기
    function withdraw() public {
        address beneficiary = msg.sender;
        uint256 amount = beneficiaryBalances[beneficiary];
        
        require(amount > 0, unicode"인출할 기부금이 없습니다.");
        
        // 잔액 초기화 (재진입 공격 방지)
        beneficiaryBalances[beneficiary] = 0;
        
        // 자금 전송
        payable(beneficiary).transfer(amount);
        
        emit FundsWithdrawn(beneficiary, amount);
    }

    // 운영자가 수수료 인출하기
    function withdrawPlatformFees() public onlyOwner {
        uint256 platformBalance = address(this).balance - getTotalBeneficiaryBalances();
        require(platformBalance > 0, unicode"플랫폼 수수료가 없습니다.");
        
        payable(owner).transfer(platformBalance);
        
        emit PlatformFeeWithdrawn(owner, platformBalance);
    }

    //수수료 변경 (onlyOwner)
    function setPlatformFee(uint256 _newFee) public onlyOwner{
        require(_newFee<=1000,unicode"수수료는 최대 5%까지만 설정 가능합니다."); //최대 5%
        platformFee=_newFee;
    }

    //모든 수혜자의 총 잔액 계산 - 내부용
    function getTotalBeneficiaryBalances() private view returns (uint256){
        return address(this).balance-(totalDonations*platformFee/10000);

    }

    // 특정 수혜자의 잔액 확인
    function getBeneficiaryBalance(address _beneficiary) public view returns (uint256) {
        return beneficiaryBalances[_beneficiary];
    }

    //특정 수혜자의 기부 내역 개수 확인
    function getDonationCount(address _beneficiary) public view returns (uint256){
         return beneficiaryDonations[_beneficiary].length;
    }

    // 특정 수혜자의 특정 기부 내역 확인
    function getDonationDetails(address _beneficiary, uint256 _index) public view returns (
        address donor,
        uint256 amount,
        uint256 timestamp
    ) {
        //유효한 인덱스인지 확인하기
        require(_index < beneficiaryDonations[_beneficiary].length, "Invalid donation index");
        
        //해당 기부 내역 정보 가져오기
        Donation memory donation = beneficiaryDonations[_beneficiary][_index];

        //정보 반환하기기
        return (donation.donor, donation.amount, donation.timestamp);
    }


    // 컨트랙트 총 잔액 확인
        function getContractBalance() public view returns (uint256) {
            return address(this).balance;
        }



    // 오버라이드 함수들
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