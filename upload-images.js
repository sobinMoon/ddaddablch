// // upload-images.js
// // Truffle 프로젝트용 NFT 이미지 업로드 스크립트

// import { NFTStorage, File } from "nft.storage";
// import fs from "fs";
// import path from "path";

// // NFT.Storage API 키 (여기에 실제 키를 넣으세요)
// const API_KEY = "64e8f400.cbca30fb22b7426082343dc272610459";
// const client = new NFTStorage({ token: API_KEY });

// // 카테고리별 이미지 파일 매핑
// const categoryImages = {
//   CHILDREN: "donation_nft_children.png",
//   ELDERLY: "donation_nft_elderly.png",
//   ENVIRONMENT: "donation_nft_environment.png",
//   ANIMAL: "donation_nft_animal.png",
//   MEDICAL: "donation_nft_medical.png",
//   SOCIETY: "donation_nft_society.png",
// };

// async function uploadImage(category, fileName) {
//   try {
//     const imagePath = path.join("./nft-images/", fileName);

//     if (!fs.existsSync(imagePath)) {
//       console.log(`❌ 파일 없음: ${imagePath}`);
//       return null;
//     }

//     console.log(`📤 업로드 중: ${category}`);

//     const imageData = fs.readFileSync(imagePath);
//     const extension = path.extname(fileName).toLowerCase();
//     const mimeType =
//       extension === ".jpg" || extension === ".jpeg"
//         ? "image/jpeg"
//         : "image/png";

//     const imageFile = new File([imageData], fileName, { type: mimeType });
//     const cid = await client.storeBlob(imageFile);

//     console.log(`✅ ${category}: ${cid}`);
//     console.log(`   🔗 https://nftstorage.link/ipfs/${cid}`);

//     return cid;
//   } catch (error) {
//     console.error(`❌ ${category} 업로드 실패:`, error.message);
//     return null;
//   }
// }

// async function main() {
//   console.log("🚀 NFT 이미지 업로드 시작!\n");

//   const uploadedHashes = {};

//   for (const [category, fileName] of Object.entries(categoryImages)) {
//     const cid = await uploadImage(category, fileName);
//     if (cid) {
//       uploadedHashes[category] = cid;
//     }

//     // 1초 대기
//     await new Promise((resolve) => setTimeout(resolve, 1000));
//   }

//   console.log("\n📊 업로드 결과:");
//   console.log("================");

//   Object.entries(categoryImages).forEach(([category, fileName]) => {
//     if (uploadedHashes[category]) {
//       console.log(`✅ ${category}: ${uploadedHashes[category]}`);
//     } else {
//       console.log(`❌ ${category}: 실패`);
//     }
//   });

//   // 결과를 파일로 저장
//   const result = {
//     timestamp: new Date().toISOString(),
//     hashes: uploadedHashes,
//     contractCode: {
//       children: uploadedHashes.CHILDREN || "",
//       elderly: uploadedHashes.ELDERLY || "",
//       environment: uploadedHashes.ENVIRONMENT || "",
//       animal: uploadedHashes.ANIMAL || "",
//       medical: uploadedHashes.MEDICAL || "",
//       society: uploadedHashes.SOCIETY || "",
//     },
//   };

//   fs.writeFileSync("ipfs-hashes.json", JSON.stringify(result, null, 2));
//   console.log("\n💾 결과가 ipfs-hashes.json에 저장되었습니다.");

//   console.log("\n🔧 Truffle migration에서 사용할 코드:");
//   console.log("=======================================");
//   console.log("// migrations/3_deploy_nft_contracts.js에 추가할 내용:");
//   console.log(`const contract = await DonationPlatform.deployed()`);
//   console.log(`await contract.setAllCategoryImageHashes(`);
//   console.log(`  "${uploadedHashes.CHILDREN || ""}", // CHILDREN`);
//   console.log(`  "${uploadedHashes.ELDERLY || ""}", // ELDERLY`);
//   console.log(`  "${uploadedHashes.ENVIRONMENT || ""}", // ENVIRONMENT`);
//   console.log(`  "${uploadedHashes.ANIMAL || ""}", // ANIMAL`);
//   console.log(`  "${uploadedHashes.MEDICAL || ""}", // MEDICAL`);
//   console.log(`  "${uploadedHashes.SOCIETY || ""}" // SOCIETY`);
//   console.log(`)`);
// }

// main().catch(console.error);
