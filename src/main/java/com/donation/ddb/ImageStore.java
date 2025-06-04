package com.donation.ddb;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
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
}
