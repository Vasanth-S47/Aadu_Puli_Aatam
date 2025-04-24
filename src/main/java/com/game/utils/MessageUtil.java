package com.game.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageUtil {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = MessageUtil.class.getClassLoader().getResourceAsStream("messages.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("messages.properties not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load messages.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key, "Message not found");
    }
}
