package com.donation.ddb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@Slf4j
public class ImageStore {
    public static String storeImage(MultipartFile image, String uploadsDir) throws IOException {
        // 파일 이름 생성
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + image.getOriginalFilename();
        // 실제 파일이 저장될 경로
        String filePath = "C:\\DDADDABLCH" + uploadsDir + fileName;
        // DB에 저장할 경로 문자열

        Path path = Paths.get(filePath); // Path 객체 생성
        Files.createDirectories(path.getParent()); // 디렉토리 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장

        return filePath;
    }

//    public static void deleteImage(String imagePath) {
//        if (imagePath == null || imagePath.trim().isEmpty()) {
//            log.warn("삭제할 이미지 경로가 없습니다.");
//            return;
//        }
//
//        try {
//            Path filePath = Paths.get(FILE_STORE_PATH + imagePath);
//
//            if (Files.exists(filePath)) {
//                Files.delete(filePath);
//                log.info("이미지 삭제 완료: {}", imagePath);
//            } else {
//                log.warn("삭제하려는 파일이 존재하지 않습니다: {}", imagePath);
//            }
//
//        } catch (IOException e) {
//            log.error("이미지 삭제 실패: {}, 오류: {}", imagePath, e.getMessage(), e);
//            // 삭제 실패해도 예외를 던지지 않음 (로그만 남김)
//        }
//    }
}
