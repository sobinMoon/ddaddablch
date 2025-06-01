// const DonationCertificateNFT = artifacts.require("DonationCertificationNFT");

// //스마트컨트랙트 artifacts(ABI,Bytecode) 가져오기
// module.exports = function (deployer, network, accounts) {
//   deployer
//     .deploy(DontationCertificateNFT)
//     .then((instance) => {
//       console.log(
//         "DonationCertificateNFT 컨트랙트가 성공적으로 배포되었습니다."
//       );
//       //console.log("컨트랙트 주소:", DonationCertificateNFT.address); //비동기 전에 찍혀 undefined가능
//       console.log("정확한 주소:", instance.address);
//       //컨트랙트 주소는 DonationCertificateNFT.deployed().then(instance=>instance.address)
//       //Truffle에서 .deployed()를 호출해야 실제 인스턴스

//       // Ganache 환경에서 테스트를 위한 초기 설정
//       if (network === "development") {
//         return DonationCertificateNFT.deployed().then((instance) => {
//           console.log("개발 환경 설정 완료");
//           console.log("Owner 주소:", accounts[0]);
//           console.log("test기부자 주소", accouns[1]);
//           console.log("test수혜자 주소", accounts[2]);
//           return instance;
//         });
//       }
//     })
//     .catch((error) => {
//       console.error("배포 중 오류 발생", error);
//     });
// };
