package com.donation.ddb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class ImageStore {

    // Spring에서 설정값 주입받기
    @Value("${app.upload.path}")
    private String configUploadPath;

    @Value("${app.upload.max-file-size}")
    private long configMaxFileSize;

    // Static 변수들 (PostConstruct에서 초기화)
    private static String uploadBasePath;
    private static long maxFileSize;

    // 허용된 이미지 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    );

    // 허용된 MIME 타입
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    /**
     * Spring 빈 초기화 후 static 변수에 설정값 할당
     */
    @PostConstruct
    public void init() {
        uploadBasePath = this.configUploadPath;
        maxFileSize = this.configMaxFileSize;

        log.info("=== ImageStore 초기화 완료 ===");
        log.info("업로드 기본 경로: {}", uploadBasePath);
        log.info("최대 파일 크기: {}MB", maxFileSize / 1024 / 1024);
        log.info("허용된 확장자: {}", ALLOWED_EXTENSIONS);
        log.info("=========================");
    }

    /**
     * 이미지 파일 저장 (Static 메서드)
     * @param image 업로드된 이미지 파일
     * @param subDirectory 하위 디렉토리 (예: "users/1/")
     * @return 저장된 파일의 상대 경로
     */
    public static String storeImage(MultipartFile image, String subDirectory) throws IOException {
        // 1. 초기화 확인
        checkInitialization();

        // 2. 파일 검증
        validateImageFile(image);

        // 3. 파일명 생성 (UUID + 원본 확장자)
        String originalFilename = image.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 4. 경로 정규화 (Windows/Linux 호환)
        String normalizedSubDir = normalizeDirectory(subDirectory);

        // 5. 저장 경로 생성
        Path uploadDir = Paths.get(uploadBasePath, normalizedSubDir);
        Path filePath = uploadDir.resolve(fileName);

        // 6. 디렉토리 생성
        Files.createDirectories(uploadDir);

        // 7. 파일 저장
        Files.write(filePath, image.getBytes());

        // 8. 상대 경로 반환 (DB 저장용)
        String relativePath = normalizedSubDir + fileName;

        log.info("이미지 저장 완료: 파일명={}, 경로={}, 크기={}bytes",
                fileName, relativePath, image.getSize());

        return relativePath;
    }

    /**
     * 이미지 파일 삭제 (Static 메서드)
     * @param relativePath 상대 경로
     */
    public static void deleteImage(String relativePath) {
        // 초기화 확인
        checkInitialization();

        if (relativePath == null || relativePath.trim().isEmpty()) {
            log.warn("삭제할 이미지 경로가 없습니다.");
            return;
        }

        // 기본 이미지는 삭제하지 않음
        if (relativePath.contains("default_profile.png")) {
            log.info("기본 프로필 이미지는 삭제하지 않습니다: {}", relativePath);
            return;
        }

        try {
            Path filePath = Paths.get(uploadBasePath, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("이미지 삭제 완료: {}", relativePath);
            } else {
                log.warn("삭제하려는 파일이 존재하지 않습니다: {}", relativePath);
            }

        } catch (IOException e) {
            log.error("이미지 삭제 실패: {}, 오류: {}", relativePath, e.getMessage(), e);
            // 삭제 실패해도 예외를 던지지 않음 (로그만 남김)
        }
    }

    /**
     * 파일의 절대 경로 반환 (Static 메서드)
     * @param relativePath 상대 경로
     * @return 절대 경로
     */
    public static String getAbsolutePath(String relativePath) {
        checkInitialization();

        if (relativePath == null || relativePath.trim().isEmpty()) {
            return null;
        }
        return Paths.get(uploadBasePath, relativePath).toString();
    }

    /**
     * 파일 존재 여부 확인 (Static 메서드)
     * @param relativePath 상대 경로
     * @return 존재하면 true
     */
    public static boolean exists(String relativePath) {
        checkInitialization();

        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(uploadBasePath, relativePath);
        return Files.exists(filePath);
    }

    // === Private Static 헬퍼 메서드들 ===

    /**
     * 초기화 확인
     */
    private static void checkInitialization() {
        if (uploadBasePath == null || maxFileSize == 0) {
            throw new IllegalStateException("ImageStore가 아직 초기화되지 않았습니다. Spring 컨텍스트가 로드되었는지 확인하세요.");
        }
    }

    /**
     * 이미지 파일 검증
     */
    private static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        // 파일 크기 검증
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("파일 크기가 너무 큽니다. 최대 %dMB까지 업로드 가능합니다.",
                            maxFileSize / 1024 / 1024));
        }

        // MIME 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "지원하지 않는 파일 형식입니다. 허용된 형식: jpg, png, gif, bmp, webp");
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                    "지원하지 않는 파일 확장자입니다. 허용된 확장자: jpg, jpeg, png, gif, bmp, webp");
        }

        // 파일명 보안 검증 (경로 조작 방지)
        if (originalFilename.contains("..") || originalFilename.contains("/") ||
                originalFilename.contains("\\")) {
            throw new IllegalArgumentException("안전하지 않은 파일명입니다.");
        }
    }

    /**
     * 디렉토리 경로 정규화
     */
    private static String normalizeDirectory(String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            return "";
        }

        // Windows 백슬래시를 슬래시로 변경
        String normalized = directory.replace("\\", "/");

        // 시작 슬래시 제거
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        // 끝에 슬래시 추가 (없다면)
        if (!normalized.endsWith("/") && !normalized.isEmpty()) {
            normalized += "/";
        }

        // 연속된 슬래시 제거
        normalized = normalized.replaceAll("/+", "/");

        return normalized;
    }

    /**
     * 파일 확장자 추출
     */
    private static String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDotIndex).toLowerCase();
    }
}