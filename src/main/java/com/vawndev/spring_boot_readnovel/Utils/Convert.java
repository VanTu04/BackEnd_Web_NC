package com.vawndev.spring_boot_readnovel.Utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Convert {
    public static String convertToImagePath(String cloudinaryUrl) {
        String[] parts = cloudinaryUrl.split("/");
        String fileName = parts[parts.length - 1];
        return "http://localhost:8080" + "/images/" + fileName;
    }

}
