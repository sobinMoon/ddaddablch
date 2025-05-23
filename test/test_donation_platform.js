const SimpleDonationPlatform = artifacts.require("SimpleDonationPlatform");

contract("SimpleDonationPlatform", (accounts) => {
  const owner = accounts[0]; //컨트랙트 배포자(소유자)
  const donor = accounts[1];
  const beneficiary = accounts[2];

  it("should set the creator as owner", async () => {
    const instance = await SimpleDonationPlatform.deployed(); //배포된 컨트랙트 인스턴스 가져오기
    const contractOwner = await instance.owner(); //컨트랙트의 owner()함수 호출해 소유자 확인인
    assert.equal(contractOwner, owner, "Owner is not set correctly");
  });

  it("should accept donations for beneficiaries", async () => {
    const instance = await SimpleDonationPlatform.deployed();
    const donationAmount = web3.utils.toWei("1", "ether"); //1eth=10^18wei

    await instance.donate(beneficiary, { from: donor, value: donationAmount });

    // 1% 수수료를 제외한 금액 계산(donationAmount의 99%)
    const expectedBeneficiaryAmount = web3.utils
      .toBN(donationAmount)
      .mul(web3.utils.toBN("99"))
      .div(web3.utils.toBN("100"));

    const beneficiaryBalance = await instance.getBeneficiaryBalance(
      beneficiary
    );
    assert.equal(
      beneficiaryBalance.toString(),
      expectedBeneficiaryAmount.toString(),
      "Beneficiary balance is incorrect"
    );
  });

  it("should allow beneficiaries to withdraw funds", async () => {
    const instance = await SimpleDonationPlatform.deployed();

    const initialBalance = await web3.eth.getBalance(beneficiary);
    const initialBalanceBN = web3.utils.toBN(initialBalance);

    // 수혜자로 출금 실행
    const tx = await instance.withdraw({ from: beneficiary });

    // 가스 비용 계산
    const gasUsed = web3.utils.toBN(tx.receipt.gasUsed);
    const txInfo = await web3.eth.getTransaction(tx.tx);
    const gasPrice = web3.utils.toBN(txInfo.gasPrice);
    const gasCost = gasUsed.mul(gasPrice);

    const finalBalance = await web3.eth.getBalance(beneficiary);
    const finalBalanceBN = web3.utils.toBN(finalBalance);

    // 기부금에서 가스비를 제외한 금액이 증가했는지 확인
    const beneficiaryAmount = await instance.getBeneficiaryBalance(beneficiary);
    assert.equal(
      beneficiaryAmount.toString(),
      "0",
      "Beneficiary balance should be 0 after withdrawal"
    );

    // 잔액 비교 (가스비 고려)
    const expectedBalance = initialBalanceBN
      .add(beneficiaryAmount)
      .sub(gasCost);
    assert(
      finalBalanceBN.gte(expectedBalance),
      "Beneficiary did not receive correct amount"
    );
  });

  it("should allow owner to withdraw platform fees", async () => {
    const instance = await SimpleDonationPlatform.deployed();
    const donationAmount = web3.utils.toWei("1", "ether");

    // 새로운 기부
    await instance.donate(beneficiary, { from: donor, value: donationAmount });

    // 플랫폼 수수료 (1%)
    const expectedFee = web3.utils
      .toBN(donationAmount)
      .mul(web3.utils.toBN("1"))
      .div(web3.utils.toBN("100"));

    const initialOwnerBalance = await web3.eth.getBalance(owner);

    // 수수료 출금
    await instance.withdrawPlatformFees({ from: owner });

    const finalOwnerBalance = await web3.eth.getBalance(owner);

    // 가스비 때문에 정확한 비교는 어렵지만, 출금액이 반영되었는지 확인
    assert(
      new web3.utils.BN(finalOwnerBalance).gt(
        new web3.utils.BN(initialOwnerBalance)
      ),
      "Owner balance did not increase after fee withdrawal"
    );
  });
});
