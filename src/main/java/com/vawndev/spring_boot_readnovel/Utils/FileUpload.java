package com.vawndev.spring_boot_readnovel.Utils;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import java.util.*;

@UtilityClass
public class FileUpload {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final String API_SECRET = Dotenv.configure().ignoreIfMissing().load().get("API_SECRET");

    public static void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        final String fileName = file.getOriginalFilename();
        final String extension = FilenameUtils.getExtension(fileName);
        final String contentType = file.getContentType();

        // Log thông tin file
        System.out.println("Checking file: " + fileName +
                ", size: " + size + " bytes, extension: " + extension +
                ", content type: " + contentType);

        // Kiểm tra dung lượng file
        if (size > MAX_FILE_SIZE) {
            throw new RuntimeException("Max file size is 10MB");
        }

        // Kiểm tra tên file không null
        if (fileName == null) {
            throw new RuntimeException("File name is null");
        }

        // Kiểm tra phần mở rộng hợp lệ
        if (!isAllowedExtension(extension, pattern)) {
            throw new RuntimeException("Only jpg, jpeg, png, gif, bmp files are allowed");
        }

        // Kiểm tra MIME Type
        if (contentType == null || !contentType.matches("image/(jpg|jpeg|png|gif|bmp)")) {
            throw new RuntimeException("Invalid file type: " + contentType);
        }
    }

    private static boolean isAllowedExtension(String extension, String pattern) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return extension.matches("(?i)jpg|jpeg|png");
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return ""; // Avoid NullPointerException error
        }

        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return ""; // no extension
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    public static String getType(final RESOURCE_TYPE type) {
        if (!isValidType(type)) {
            throw new IllegalArgumentException("Invalid type. Only 'image' or 'document' are allowed.");
        }
        return type.name().toLowerCase();
    }

    public static void validFormatFile(String fileName) {
        Set<String> validExtensions = Set.of("jpg", "png", "jpeg", "docx");
        String extension = getFileExtension(fileName);
        if (!validExtensions.contains(extension)) {
            throw new IllegalArgumentException("Invalid file type: " + extension +
                    ". Only jpg, png, jpeg, docx are allowed.");
        }
    }

    public static void validFormatImageCover(String fileName) {
        Set<String> validExtensions = Set.of("jpg", "png", "jpeg");
        String extension = getFileExtension(fileName);
        if (!validExtensions.contains(extension)) {
            throw new IllegalArgumentException("Invalid file type: " + extension +
                    ". Only jpg, png, jpeg are allowed.");
        }
    }

    private static boolean isValidType(RESOURCE_TYPE type) {
        return type == RESOURCE_TYPE.IMAGE || type == RESOURCE_TYPE.RAW;
    }

    public static String extractPublicId(String url) {
        String regex = ".*/v\\d+/(.+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(url);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
        return matcher.group(1);
    }

}
