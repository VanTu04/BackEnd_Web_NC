package com.vawndev.spring_boot_readnovel.Utils;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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

    // Hàm kiểm tra phần mở rộng file
    private static boolean isAllowedExtension(String extension, String pattern) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return extension.matches("(?i)jpg|jpeg|png|gif|bmp");
    }



    public static String getType(final RESOURCE_TYPE type) {
        if (!isValidType(type)) {
            throw new IllegalArgumentException("Invalid type. Only 'image' or 'raw are allowed.");
        }
        return type.name().toLowerCase();
    }

    private static boolean isValidType(RESOURCE_TYPE type) {
        return type == RESOURCE_TYPE.IMAGE || type == RESOURCE_TYPE.RAW ;
    }

}
