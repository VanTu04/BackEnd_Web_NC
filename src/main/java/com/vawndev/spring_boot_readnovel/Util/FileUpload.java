package com.vawndev.spring_boot_readnovel.Util;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
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

    public static void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new RuntimeException("Max file size is 10MB");
        }

        final String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("File name is null");
        }

        final String extension = FilenameUtils.getExtension(fileName);
        if (!isAllowedExtension(extension, pattern)) {
            throw new RuntimeException("Only jpg, jpeg, png, gif, bmp files are allowed");
        }
    }

    private static boolean isAllowedExtension(final String extension, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(extension);
        return matcher.matches();
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
