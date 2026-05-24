package com.example.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DriveLinkUtil {

    private static final Pattern DRIVE_ID_PATTERN = Pattern.compile("/d/([a-zA-Z0-9-_]+)|id=([a-zA-Z0-9-_]+)");

    public static String extractFileId(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn Google Drive không được để trống.");
        }
        Matcher matcher = DRIVE_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }
        throw new IllegalArgumentException("Đường dẫn Google Drive không hợp lệ hoặc không đúng định dạng chia sẻ.");
    }

    public static String getDirectDownloadUrl(String fileId) {
        return "https://docs.google.com/uc?export=download&id=" + fileId;
    }
}
