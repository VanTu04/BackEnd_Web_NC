package com.vawndev.spring_boot_readnovel.Utils.Help;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getValueFromJson(String json, String key) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            return jsonNode.has(key) ? jsonNode.get(key).asText() : null;
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }

    public static String sanitizeJsonString(String json) {
        if (json == null || json.isEmpty()) return json;

        if (json.startsWith("\"") && json.endsWith("\"")) {
            json = json.substring(1, json.length() - 1);
        }
        return json.replace("\\n", "").replace("\\", "").trim();
    }

    // Hàm parse JSON thành object
    public static <T> T parseJson(String json, Class<T> clazz) {
        try {
            json = sanitizeJsonString(json); // Chuẩn hóa JSON trước khi parse
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON format", e);
        }
    }
}
