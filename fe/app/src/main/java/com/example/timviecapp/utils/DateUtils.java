package com.example.timviecapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    /**
     * Chuyển đổi chuỗi ngày ISO 8601 (từ API) sang định dạng dd/MM/yyyy hiển thị cho người dùng.
     * Ví dụ: "2026-05-17T12:00:00.000Z" -> "17/05/2026"
     */
    public static String formatIsoDate(String isoDateStr) {
        if (isoDateStr == null || isoDateStr.isEmpty()) {
            return "N/A";
        }
        try {
            // Parser cho định dạng ISO có phần mili giây
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date;
            try {
                date = parser.parse(isoDateStr);
            } catch (Exception e) {
                // Parser dự phòng cho định dạng ISO không có phần mili giây
                parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                date = parser.parse(isoDateStr);
            }
            
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            // Dự phòng nếu lỗi phân tích, thử trích xuất 10 ký tự đầu (yyyy-MM-dd)
            try {
                if (isoDateStr.length() >= 10) {
                    String sub = isoDateStr.substring(0, 10);
                    String[] parts = sub.split("-");
                    if (parts.length == 3) {
                        return parts[2] + "/" + parts[1] + "/" + parts[0];
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return isoDateStr;
        }
    }
}
