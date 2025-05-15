const hre = require("hardhat");

async function main() {
  console.log("배포 시작...");

  // 배포 계정 정보
  const [deployer] = await ethers.getSigners();
  console.log("배포 계정:", deployer.address);
  console.log("계정 잔액:", (await deployer.getBalance()).toString());

  // DonationPlatform 배포
  console.log("DonationPlatform 컨트랙트 배포 중...");
  const DonationPlatform = await hre.ethers.getContractFactory(
    "DonationPlatform"
  );
  const donationPlatform = await DonationPlatform.deploy();
  await donationPlatform.deployed();
  console.log("DonationPlatform 배포 완료:", donationPlatform.address);

  // DonationToken 배포
  console.log("DonationToken 컨트랙트 배포 중...");
  const DonationToken = await hre.ethers.getContractFactory("DonationToken");
  const donationToken = await DonationToken.deploy(donationPlatform.address);
  await donationToken.deployed();
  console.log("DonationToken 배포 완료:", donationToken.address);

  // DonationCertificate 배포
  console.log("DonationCertificate 컨트랙트 배포 중...");
  const DonationCertificate = await hre.ethers.getContractFactory(
    "DonationCertificate"
  );
  const donationCertificate = await DonationCertificate.deploy(
    donationPlatform.address
  );
  await donationCertificate.deployed();
  console.log("DonationCertificate 배포 완료:", donationCertificate.address);

  // DonationPlatform 설정
  console.log("DonationPlatform 설정 중...");
  await donationPlatform.setDonationToken(donationToken.address);
  await donationPlatform.setDonationCertificate(donationCertificate.address);
  console.log("DonationPlatform 설정 완료");

  console.log("모든 컨트랙트 배포 및 설정 완료!");
  console.log({
    donationPlatform: donationPlatform.address,
    donationToken: donationToken.address,
    donationCertificate: donationCertificate.address,
  });
}

main()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
