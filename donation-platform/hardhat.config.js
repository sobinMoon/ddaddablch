require("@nomiclabs/hardhat-waffle"); //waffle이라는 테스트도구를 hardhat에 추가-> 컨트랙트 테스트하는데 도움됨.
require("dotenv").config();

/**
 * @type import('hardhat/config').HardhatUserConfig
 */
module.exports = {
  solidity: {
    version: "0.8.20",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200,
      },
    },
  },
  networks: {
    // 로컬 개발용 네트워크
    hardhat: {},
    // 가나슈 네트워크 추가
    ganache: {
      url: "http://127.0.0.1:7545", //Ganache GUI 기본 포트
      //또는 "http://127.0.0.1:8545" Ganache CLI 기본 포트
      accounts: process.env.PRIVATE_KEY ? [`0x${process.env.PRIVATE_KEY}`] : [],
    },
    // 테스트넷 설정
    sepolia: {
      url: process.env.INFURA_PROJECT_ID,
      accounts: process.env.PRIVATE_KEY ? [`0x${process.env.PRIVATE_KEY}`] : [],
    },
    // 메인넷 설정 (실제 배포 시)
    mainnet: {
      url: process.env.INFURA_PROJECT_ID.replace("sepolia", "mainnet"), // sepolia를 mainnet으로 변경
      accounts: process.env.PRIVATE_KEY ? [`0x${process.env.PRIVATE_KEY}`] : [],
    },
  },
  paths: {
    sources: "./contracts",
    tests: "./test",
    cache: "./cache",
    artifacts: "./artifacts",
  },
};
