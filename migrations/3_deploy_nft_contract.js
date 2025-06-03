// // migrations/3_deploy_nft_contracts.js
// // GitHub URL 사용 버전

// const DonationPlatform = artifacts.require("DonationPlatform");

// module.exports = async function (deployer) {
//   try {
//     console.log("🚀 DonationPlatform 컨트랙트 배포 시작...");

//     // 1단계: 컨트랙트 배포 (플랫폼 수수료 1% = 100)
//     await deployer.deploy(DonationPlatform, 100);
//     const contract = await DonationPlatform.deployed();

//     console.log(`✅ 컨트랙트 배포 완료!`);
//     console.log(`📍 주소: ${contract.address}`);

//     // 2단계: GitHub Raw URL들 (실제 URL로 교체하세요!)
//     const imageUrls = {
//       CHILDREN:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_children.png?raw=true",
//       ELDERLY:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_elderly.png?raw=true",
//       ENVIRONMENT:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_environment.png?raw=true",
//       ANIMAL:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_animal.png?raw=true",
//       MEDICAL:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_medical.png?raw=true",
//       SOCIETY:
//         "https://github.com/2213607/donation-nft-images/blob/main/donation_nft_society.png?raw=true",
//     };

//     // 3단계: 이미지 URL 설정
//     console.log("🔧 이미지 URL 설정 중...");

//     // DonationCategory enum 값들 (0, 1, 2, 3, 4, 5)
//     await contract.setCategoryImageUrl(0, imageUrls.CHILDREN); // CHILDREN
//     await contract.setCategoryImageUrl(1, imageUrls.ELDERLY); // ELDERLY
//     await contract.setCategoryImageUrl(2, imageUrls.ENVIRONMENT); // ENVIRONMENT
//     await contract.setCategoryImageUrl(3, imageUrls.ANIMAL); // ANIMAL
//     await contract.setCategoryImageUrl(4, imageUrls.MEDICAL); // MEDICAL
//     await contract.setCategoryImageUrl(5, imageUrls.SOCIETY); // SOCIETY

//     console.log("✅ 이미지 URL 설정 완료!");

//     // 4단계: 배포 정보 저장
//     const deploymentInfo = {
//       network: deployer.network,
//       contractAddress: contract.address,
//       deployedAt: new Date().toISOString(),
//       platformFee: 100,
//       imageUrls: imageUrls,
//     };

//     console.log("\n🎉 배포 완료!");
//     console.log("=".repeat(50));
//     console.log(`📍 컨트랙트 주소: ${contract.address}`);
//     console.log(`🌐 네트워크: ${deployer.network}`);
//     console.log(`🖼️  이미지 호스팅: GitHub Raw URLs`);

//     // 테스트용 기부 예시
//     console.log("\n🧪 테스트용 기부 명령어:");
//     console.log(`truffle console --network ${deployer.network}`);
//     console.log(`contract = await DonationPlatform.deployed()`);
//     console.log(
//       `await contract.donate("0x수혜자주소", 0, { value: web3.utils.toWei("0.01", "ether") })`
//     );
//   } catch (error) {
//     console.error("💥 배포 실패:", error);
//     throw error;
//   }
// };
