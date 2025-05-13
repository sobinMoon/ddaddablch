// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";

contract DonationPlatform is Ownable {
    //address public _owner; //이 컨트랙트 배포자
    uint256 public totalDonations; //이 컨트랙트로 기부된 금액 최종 

    // 기부 프로젝트(수혜자) 구조체
    struct Project {
        address payable recipient;
        string name;
        string description;
        uint256 targetAmount;
        uint256 raisedAmount;
        bool isActive;
        uint256 deadline;
    }

    // 기부 내역 구조체
    struct Donation {
        address donor;
        uint256 projectId;
        uint256 amount;
        uint256 timestamp;
    }

    // 기부자 구조체
    struct Donor {
        string name; // 선택적
        uint256 totalDonated;
        uint256[] donatedProjects;
    }

    // 상태 변수들
    // 저장소 이름 
    mapping(uint256 => Project) public projects;
    mapping(address => Donor) public donors;
    mapping(uint256 => Donation[]) public projectDonations; //이 프로젝트에 대한 기부 내역 

    uint256 public projectCount;

    // 이벤트 정의
    // 블록체인에 기록 남기기 위한 로그 시스템
    
    event ProjectCreated(uint256 projectId, string name, address recipient, uint256 targetAmount);
    event DonationReceived(uint256 projectId, address donor, uint256 amount);
    event FundsWithdrawn(uint256 projectId, address recipient, uint256 amount);

    // 컨트랙트 소유자만 접근 가능한 함수 제어
    // modifier : 함수 실행 전에 특정 조건을 검사하거나 로직 추가하는 문법
    //오직 owner만 호출할 수 있는 함수에 붙이는 제한자.
    //modifier onlyOwner() {
    //    require(msg.sender == owner, unicode"contract owner만 호출할 수 있는 함수입니다.");
    //    _;
    //}

    // 컨트랙트 초기화
    constructor() Ownable(msg.sender) {
        //owner = msg.sender;
        totalDonations = 0;

        projectCount = 0;
        
    }

    //계약 만든 사람만 호출 가능하고, 새로 만들어진 프로젝트의 id를 반환함. 
    //새 프로젝트를 블록체인에 등록 
    function createProject(string memory _name,
        string memory _description,
        address payable _recipient,
        uint256 _targetAmount,
        uint256 _deadline)
         public onlyOwner returns (uint256) {
            require(_targetAmount > 0, unicode"목표 기부 금액이 잘못 입력되었습니다.");
            require(_deadline > block.timestamp, unicode"마감기간이 잘못 입력되었습니다.");

            projectCount++;
            projects[projectCount] = Project({
            recipient: _recipient,
            name: _name,
            description: _description,
            targetAmount: _targetAmount,
            raisedAmount: 0,
            isActive: true,
            deadline: _deadline
        });

        emit ProjectCreated(projectCount, _name, _recipient, _targetAmount);
        return projectCount;
         }


        //기부 기능
        function donate(uint256 _projectId) public payable{
            require(_projectId > 0 && _projectId <= projectCount, unicode"projectId를 확인해주세요.");
            require(projects[_projectId].isActive, unicode"해당 Project는 진행이 끝난 캠페인입니다.");
            require(block.timestamp <= projects[_projectId].deadline, unicode"해당 project은 마감이 끝난 캠페인입니다. ");
            require(msg.value > 0, unicode"기부는 0eth이상이어야 합니다.");

            Project storage project=projects[_projectId];
            project.raisedAmount +=msg.value;

            //기부자 정보 업데이트
            Donor storage donor=donors[msg.sender];
            donor.totalDonated+=msg.value; //msg.value = 현재 트랜잭션과 함게 전송된 이더양
            donor.donatedProjects.push(_projectId);


            //기부 내역 기록 
            projectDonations[_projectId].push(Donation({
                donor: msg.sender,
                projectId: _projectId,
                amount: msg.value,
                timestamp: block.timestamp
            }));

            totalDonations +=msg.value;

            emit DonationReceived(_projectId, msg.sender,msg.value);

            //목표금액 달성 시 자동 종료하기-> 추가해야되면 나중에 추가
            

        
        }

        //기부금 인출(캠페인 수혜자 or 플랫폼 소유자만 가능)
        function withdrawFunds(uint256 _projectId) public{
            Project storage project=projects[_projectId];
            require(msg.sender == project.recipient || msg.sender == owner(), unicode"수혜자/관리자가 아닙니다.");
            require(project.raisedAmount>0,unicode"인출할 금액이 없습니다.");

            uint256 amount=project.raisedAmount;
            project.raisedAmount=0;

            //자금 전송
            project.recipient.transfer(amount);

            emit FundsWithdrawn(_projectId, project.recipient, amount);

        }


        //프로젝트 활성화/비활성화
        function toggleProjectStatus(uint256 _projectId) public onlyOwner{
            require(_projectId > 0 && _projectId <= projectCount, unicode"잘못된 projectId 값입니다.");
            projects[_projectId].isActive=!projects[_projectId].isActive;

        }


        //프로젝트 정보 조회 
        // 프로젝트 정보 조회
    function getProjectDetails(uint256 _projectId) public view returns (
        string memory name,
        string memory description,
        address recipient,
        uint256 targetAmount,
        uint256 raisedAmount,
        bool isActive,
        uint256 deadline
    ) {
        Project storage project = projects[_projectId];
        return (
            project.name,
            project.description,
            project.recipient,
            project.targetAmount,
            project.raisedAmount,
            project.isActive,
            project.deadline
        );
    }

         // 기부자의 기부 내역 조회
    function getDonorInfo(address _donor) public view returns (
        uint256 totalDonated,
        uint256[] memory donatedProjects
    ) {
        Donor storage donor = donors[_donor];
        return (donor.totalDonated, donor.donatedProjects);
    }

    // 비상 상황 시 자금 회수 (보안 목적, 선택적)
    function emergencyWithdraw() public onlyOwner {
        payable(owner()).transfer(address(this).balance);
    }

    function cancelProjectAndRefund(uint256 _projectId) public onlyOwner{
        Project storage project=projects[_projectId];
        require(project.isActive,unicode"프로젝트가 종료됩니다");

        project.isActive=false;

        //모든 기부 내역 순회하며 환불하기 
        Donation[] storage donations = projectDonations[_projectId];
    for (uint i = 0; i < donations.length; i++) {
        Donation storage donation = donations[i];
        payable(donation.donor).transfer(donation.amount);
    }

    }

}

