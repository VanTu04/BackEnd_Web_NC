package com.vawndev.spring_boot_readnovel.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptionUtil {
    private final TextEncryptor textEncryptor;

    public AesEncryptionUtil(@Value("${encryption.aes.password}") String password,
                             @Value("${encryption.aes.salt}") String salt) {
        this.textEncryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String plainText) {
        return textEncryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }
}
