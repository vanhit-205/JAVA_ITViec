package com.example.base;

import com.example.domain.dto.response.Meta;

import java.time.LocalDateTime;

public class BaseResponse<T> {
    public boolean success;
    public Meta meta;
    public T data;
    public Object error;
    public LocalDateTime timestamp;

    // SUCCESS
    public BaseResponse(T data, Meta meta) {
        this.success = true;
        this.data = data;
        this.meta = meta;
        this.timestamp = LocalDateTime.now();
    }

    // ERROR
    public BaseResponse(Meta meta, Object error) {
        this.success = false;
        this.meta = meta;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    // Factory method
    public static <T> BaseResponse<T> success(T data, int code, String path) {
        return new BaseResponse<>(data, new Meta(code, path));
    }

    public static <T> BaseResponse<T> error(int code, String path, Object error) {
        return new BaseResponse<>(new Meta(code, path), error);
    }
}
