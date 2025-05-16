// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";
import "./Interfaces.sol";
import "./DonationStructs.sol";
import "./DonationUtils.sol";
import "./DonationAdmin.sol";
import "./DonationCore.sol";
import "./DonationQuery.sol";

/**
 * @title DonationPlatform
 * @dev 메인 기부 플랫폼 컨트랙트
 * 모든 모듈을 통합하여 완전한 기부 플랫폼 기능을 제공합니다.
 */
contract DonationPlatform is Ownable, DonationAdmin, DonationCore, DonationQuery {
    // 상태 변수
    uint256 public totalDonations; //총 기부 금액
    uint256 public projectCount;   //캠페인 개수
    uint256 public withdrawalRequestCount; //출금요청횟수
    
    // 저장소
    mapping(uint256 => DonationStructs.Project) public projects; //플젝번호로 플젝찾기
    mapping(address => DonationStructs.Donor) public donors; //donor주소로 donor 찾기
    mapping(uint256 => DonationStructs.Donation[]) public projectDonations; //각 project 별 donation개수
    mapping(uint256 => DonationStructs.WithdrawalRequest) public withdrawalRequests; // 인출 요청들 
    
    // 토큰 컨트랙트 주소
    IDonationToken public donationToken;
    IDonationCertificate public donationCertificate;
    
    // 이벤트 정의
    event ProjectCreated(uint256 indexed projectId, string name, address recipient, uint256 targetAmount, uint256 deadline);
    event DonationReceived(uint256 indexed projectId, address indexed donor, uint256 amount, bool isAnonymous);
    
    // 실제 인출 
    event FundsWithdrawn(uint256 indexed projectId, address recipient, uint256 amount);
    event ProjectStatusToggled(uint256 indexed projectId, bool isActive);
    event DonorNameSet(address indexed donor, string name);
    
    // 인출 요청 
    event WithdrawalRequested(uint256 indexed requestId, uint256 projectId, uint256 amount);
    event ProjectCancelled(uint256 indexed projectId);
    event ProjectDeadlineExtended(uint256 indexed projectId, uint256 newDeadline);
    event ProjectTargetIncreased(uint256 indexed projectId, uint256 newTarget);
    
    /**
     * @dev 컨트랙트 생성자
     */
     //상속받은 Ownable에서 owner 변수 설정
    constructor() DonationAdmin() {  //배포될 때 한 번 실행됨. 
        totalDonations = 0;
        projectCount = 0;
        withdrawalRequestCount = 0;
    }
    
    /**
     * @dev 토큰 컨트랙트 설정
     * @param _tokenAddress 토큰 컨트랙트 주소
     */
     //기부 토큰 컨트랙트의 주소를 설정하는 관리자용 함수, 외부에서만 호출 가능, 
    function setDonationToken(address _tokenAddress) external onlyOwner {
        donationToken = IDonationToken(_tokenAddress); 
        //실제 배포된 ERC20 토큰 컨트랙트 주소를 연결하는 역할
        //파라미터로 들어온 주소는 이미 배포된 ERC20토큰 컨트랙트 주소여야함. 
        //IDonationToken타입으로 형변환해서 donationToken에 저장 ->  donationToken.mint()처럼 외부 토큰 컨트랙트의 함수 호출 가능

    }
    //onlyOwner: Ownable에서 상속받은 제어자 (오직 owner만 호출 가능)
    
    /**
     * @dev NFT 컨트랙트 설정
     * @param _certAddress NFT 컨트랙트 주소
     */
    function setDonationCertificate(address _certAddress) external onlyOwner {
        donationCertificate = IDonationCertificate(_certAddress);
    }
    
    /**
     * @dev 기부자 이름 설정
     * @param _name 기부자 이름
     */
    function setDonorName(string memory _name) public {
        DonationStructs.Donor storage donor = donors[msg.sender]; //donor 구조체 가져오기
        donor.name = _name;
        donor.hasSetName = true; //이름은 optional이니까 
        
        emit DonorNameSet(msg.sender, _name);
    }
    
    /**
     * @dev 새 프로젝트 생성
     * @param _name 프로젝트 이름
     * @param _description 프로젝트 설명
     * @param _recipient 기금 수령자 주소
     * @param _targetAmount 목표 금액
     * @param _deadline 마감일
     * @return 생성된 프로젝트 ID
     */
    function createProject(
        string memory _name,
        string memory _description,
        address payable _recipient,
        uint256 _targetAmount,
        uint256 _deadline
    ) public onlyOwner returns (uint256) { //관리자만 호출 가능
        projectCount++;
        //플젝 생성하는 내부 함수 호출
        _createProject(projects, projectCount, _name, _description, _recipient, _targetAmount, _deadline);
        
        //이벤트 발생,블록체인에 기록 남김.ui나 블록탐색기에서 새 플젝 생성 이력 확인 가능
        emit ProjectCreated(projectCount, _name, _recipient, _targetAmount, _deadline);
        return projectCount;
    }
    
    /**
     * @dev 기부 기능
     * @param _projectId 기부할 프로젝트 ID
     * @param _isAnonymous 익명 기부 여부
     */
    function donate(uint256 _projectId, bool _isAnonymous) public payable {
        require(_projectId > 0 && _projectId <= projectCount,  unicode"존재하지 않는 projectId입니다.");
        require(projects[_projectId].isActive, unicode"현재 캠페인이 비활성상태입니다.");
        require(block.timestamp <= projects[_projectId].deadline,unicode"캠페인이 이미 만료료되었습니다.");
        require(msg.value > 0, unicode"기부금은 0eth보다 커야 합니다.");
        
        // 수수료 계산
        uint256 fee = (msg.value * platformFeePercent) / 100;
        uint256 donationAmount = msg.value - fee;
        
        // 기부 처리
        // DonationCore 컨트랙트 내부에 정의되어있음
        _processDonation(
            projects,
            donors,
            projectDonations,
            _projectId,
            _isAnonymous,
            msg.sender,
            donationAmount,
            platformFeeRecipient,
            fee
        );
        
        totalDonations += donationAmount; //총 기부금 누적
        

        //블록체인 로그에 남김 -> 이더스캔 / 프론트에서 조회 가능함.
        emit DonationReceived(_projectId, msg.sender, donationAmount, _isAnonymous);
        
        // // 리워드 토큰(ERC20) 발급함 (설정된 경우) 
        // if (address(donationToken) != address(0)) {
        //     //  1 ETH당 100 토큰 발급 -> 이거는 수정 가능함함
        //     uint256 tokenAmount = donationAmount * 100 / 1 ether;

        //     //앞에서 설정한 외부 ERC20 컨트랙트 호출출
        //     donationToken.mint(msg.sender, tokenAmount);
        // }
        
        // // NFT 인증서 발급 (금액이 0이더보다 많을 경우 & 인증서 발급용 nft컨트랙트가 설정된 경우)
        // if (donationAmount >= 0 ether && address(donationCertificate) != address(0)) {
            
        //     //nft 메타데이터 주소(url)을 동적으로 생성함
        //     //"https://donation-platform.example/certificate/3/1715600973"
        //     string memory tokenURI = string(abi.encodePacked(
        //         "https://donation-platform.example/certificate/", 
        //         DonationUtils.toString(_projectId),
        //         "/",
        //         DonationUtils.toString(block.timestamp)
        //     ));

        //     //기부자에게 NFT 한 장 발급, 메타데이터는 tokenURI로 링크되어있음.
        //     donationCertificate.mintCertificate(msg.sender, tokenURI);
        // }
    }
    
    /**
     * @dev 기부금 인출 요청
     * @param _projectId 프로젝트 ID
     * @param _amount 인출 금액
     */
    function requestWithdrawal(uint256 _projectId, uint256 _amount) public {
        require(_projectId > 0 && _projectId <= projectCount, unicode"projectID를 바르게 입력하세요.");
        require(msg.sender == projects[_projectId].recipient || msg.sender == owner(), unicode"인출 권한이 없습니다.");
        
        withdrawalRequestCount++;
        _requestWithdrawal(projects, withdrawalRequests, withdrawalRequestCount, _projectId, _amount);
        
        //누가 어떤 프로젝트에서 얼마를 요청했는지로그로 남겨서
        //프론트에서 대기 중인 요청 목록 표시하거나 이더스캔에서 확인 가능함.
        //실제 송금은 24시간 후에 가능함. 
        emit WithdrawalRequested(withdrawalRequestCount, _projectId, _amount);
    }
    
    /**
     * @dev 기부금 인출 실행
     * @param _requestId 인출 요청 ID
     */

     //기부금 인출 요청은 바로 실행되ㅣㅈ 않고
     //requestWithdrawal()함수는 쵸어만 기록하고, 실제 이더 받으려면 이 함수를 호출해야함. 
    function executeWithdrawal(uint256 _requestId) public {
        require(_requestId > 0 && _requestId <= withdrawalRequestCount, "Invalid request ID");
        
        (address recipient, uint256 amount) = _executeWithdrawal(withdrawalRequests, projects, _requestId);
        
        // 자금 전송
        payable(recipient).transfer(amount);
        
        emit FundsWithdrawn(withdrawalRequests[_requestId].projectId, recipient, amount);
    }
    
    /**
     * @dev 프로젝트 활성화/비활성화
     * @param _projectId 프로젝트 ID
     */
    function toggleProjectStatus(uint256 _projectId) public onlyOwner {
        _toggleProjectStatus(projects, _projectId, projectCount);
        emit ProjectStatusToggled(_projectId, projects[_projectId].isActive);
    }
    
    /**
     * @dev 프로젝트 마감일 연장
     * @param _projectId 프로젝트 ID
     * @param _newDeadline 새 마감일
     */
    function extendProjectDeadline(uint256 _projectId, uint256 _newDeadline) public onlyOwner {
        _extendProjectDeadline(projects, _projectId, _newDeadline, projectCount);
        emit ProjectDeadlineExtended(_projectId, _newDeadline);
    }
    
    /**
     * @dev 프로젝트 목표 금액 증가
     * @param _projectId 프로젝트 ID
     * @param _newTarget 새 목표 금액
     */
    function increaseProjectTarget(uint256 _projectId, uint256 _newTarget) public onlyOwner {
        _increaseProjectTarget(projects, _projectId, _newTarget, projectCount);
        emit ProjectTargetIncreased(_projectId, _newTarget);
    }
    
    /**
     * @dev 프로젝트 정보 조회
     * @param _projectId 프로젝트 ID
     * @return name 프로젝트 이름
     * @return description 프로젝트 설명
     * @return recipient 수혜자 주소
     * @return targetAmount 목표 금액
     * @return raisedAmount 현재까지 모금된 금액
     * @return isActive 프로젝트 활성화 여부
     * @return deadline 마감일 (타임스탬프)
     * @return createTime 생성일 (타임스탬프)
     * @return uniqueDonorCount 고유 기부자 수
     */
    function getProjectDetails(uint256 _projectId) public view returns (
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
        require(_projectId > 0 && _projectId <= projectCount, "Invalid project ID");
        return _getProjectDetails(projects, _projectId);
    }
    
    /**
     * @dev 프로젝트의 기부 내역 조회
     * @param _projectId 프로젝트 ID
     * @param _offset 시작 인덱스
     * @param _limit 조회할 항목 수
        * @return donors 기부자 주소 목록
        * @return amounts 기부 금액 목록
        * @return timestamps 기부 시간 목록
        * @return anonymousFlags 익명 기부 여부 목록
     */
    function getProjectDonations(uint256 _projectId, uint256 _offset, uint256 _limit) public view returns (
        address[] memory donors,
        uint256[] memory amounts,
        uint256[] memory timestamps,
        bool[] memory anonymousFlags
    ) {
        require(_projectId > 0 && _projectId <= projectCount, "Invalid project ID");
        return _getProjectDonations(projectDonations, _projectId, _offset, _limit);
    }
    
    /**
     * @dev 기부자 정보 조회
     * @param _donor 기부자 주소
     * @return name 기부자 이름
    * @return totalDonated 총 기부 금액
    * @return donatedProjects 기부자가 참여한 프로젝트 ID 목록
    * @return hasSetName 이름을 설정했는지 여부
     */
    function getDonorInfo(address _donor) public view returns (
        string memory name,
        uint256 totalDonated,
        uint256[] memory donatedProjects,
        bool hasSetName
    ) {
        return _getDonorInfo(donors, _donor);
    }
    
    /**
     * @dev 활성화된 프로젝트 목록 조회
     * @param _offset 시작 인덱스
     * @param _limit 조회할 항목 수
     * @return projectIds 프로젝트 ID 목록
     * @return names 프로젝트 이름 목록
     * @return targetAmounts 목표 금액 목록
     * @return raisedAmounts 현재까지 모금된 금액 목록
     * @return deadlines 프로젝트 마감일 목록
     */
    function getActiveProjects(uint256 _offset, uint256 _limit) public view returns (
        uint256[] memory projectIds,
        string[] memory names,
        uint256[] memory targetAmounts,
        uint256[] memory raisedAmounts,
        uint256[] memory deadlines
    ) {
        return _getActiveProjects(projects, projectCount, _offset, _limit);
    }
    
    /**
     * @dev 비상 상황 시 자금 회수
     */
    function emergencyWithdraw() public onlyOwner {
        _emergencyWithdraw(payable(owner()), address(this).balance);
    }
    
    /**
     * @dev 프로젝트 취소 및 환불
     * @param _projectId 프로젝트 ID
     */
    function cancelProjectAndRefund(uint256 _projectId) public onlyOwner {
        require(_projectId > 0 && _projectId <= projectCount, "Invalid project ID");
        _cancelProjectAndRefund(projects, projectDonations, _projectId);
        emit ProjectCancelled(_projectId);
    }
    
    /**
     * @dev ETH 직접 수신 가능
     */
    receive() external payable {
        // 플랫폼 주소로 직접 기부 (일반 기부가 아닌 경우)
        totalDonations += msg.value;
    }
}