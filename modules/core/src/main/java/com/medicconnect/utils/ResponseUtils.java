package com.medicconnect.utils;

import java.util.HashMap;
import java.util.Map;

public final class ResponseUtils {

    private ResponseUtils() {}

    // ------------------- SUCCESS RESPONSES -------------------
    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = success(message);
        if (data != null) {
            response.put("data", data);
        }
        return response;
    }

    // ------------------- ERROR RESPONSES -------------------
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return response;
    }

    public static Map<String, Object> error(String message, Object details) {
        Map<String, Object> response = error(message);
        if (details != null) {
            response.put("details", details);
        }
        return response;
    }
}
