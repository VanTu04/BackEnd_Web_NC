package com.vawndev.spring_boot_readnovel.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.util.*;

@Component
public class SecurityUtils {
    private static final Dotenv dotenv = Dotenv.load();
    public String GenerateSignature(Map<String, String> params) {
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        StringBuilder dataToSign = new StringBuilder();
        for (String key : sortedKeys) {
            dataToSign.append(key).append("=").append(params.get(key)).append("&");
        }
        dataToSign.deleteCharAt(dataToSign.length() - 1); // Xóa dấu '&' cuối cùng

        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(dotenv.get("API_SECRET").getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] hash = sha256Hmac.doFinal(dataToSign.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }




}
