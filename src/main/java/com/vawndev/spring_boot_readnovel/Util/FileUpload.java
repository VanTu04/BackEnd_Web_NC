package com.vawndev.spring_boot_readnovel.Util;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUpload {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final String FILE_NAME_FORMAT = "%s_%s";  // Updated to match usage

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new RuntimeException("Max file size is 10MB");
        }

        final String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("File name is null");
        }

        // Optionally, you can check the file extension if needed.
        final String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(fileName, pattern)) {
            throw new RuntimeException("Only jpg, jpeg, png, gif, bmp files are allowed");
        }
    }

    public static String getFileName(final String name) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        final String date = LocalDateTime.now().format(formatter);
        return String.format(FILE_NAME_FORMAT, name, date);
    }
}
