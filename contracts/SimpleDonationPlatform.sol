// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract SimpleDonationPlatform {
    //상태 변수
    address public owner;
    uint256 public platformFee; //플랫폼 수수료 (100분의 1 퍼센트 단위,100=1%)
    uint256 public totalDonations;

    //기부금 기록을 위한 구조체
    struct Donation{
        address donor;
        uint256 amount;
        uint256 timestamp;
    }

    //수혜자별 받은 기부금 기록
    mapping(address=> uint256) public beneficiaryBalances;

    //수혜자별 기부 내역
    mapping(address=> Donation[]) public beneficiaryDonations;

    //이벤트
    event DonationReceived(address indexed donor, address indexed beneficiary,
    uint256 amount);
    event FundsWithdrawn(address indexed beneficiary, uint256 amount);
    event PlatformFeeWithdrawn(address indexed owner,uint256 amount);

    //생성자
    constructor(uint256 _initialFee){
        owner=msg.sender;
        platformFee=_initialFee; //기본 수수료 설정
    }

    //소유자만 접근 가능한 modifier
    modifier onlyOwner(){
        require(msg.sender==owner, unicode"owner만 이 함수 호출 가능합니다");
        _;
    }

    //기부하기 - 특정 수혜자에게 직접 기부
    function donate(address _beneficiary) public payable{
        require(msg.value>0,unicode"기부금은 0eth를 넘어야 합니다.");
        require(_beneficiary !=address(0),unicode"유효하지 않은 주소입니다.");

        //수수료 계산 (100분의 1% 단위)
        uint256 fee=(msg.value * platformFee) /10000;
        uint256 donationAmount = msg.value-fee;

        //수혜자의 잔액 증가 
        beneficiaryBalances[_beneficiary]+=donationAmount;

        //기부 내역 추가 
        beneficiaryDonations[_beneficiary].push(
            Donation({
                donor: msg.sender,
                amount: donationAmount,
                timestamp: block.timestamp
            })

        );

        totalDonations+=msg.value;
        emit DonationReceived(msg.sender,_beneficiary,donationAmount);
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

}