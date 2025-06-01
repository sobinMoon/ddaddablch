const SimpleDonationPlatform = artifacts.require("SimpleDonationPlatform");

module.exports = function (deployer) {
  //플랫폼 수수료 1%로 초기화
  deployer.deploy(DonationPlatform, 100);
};
