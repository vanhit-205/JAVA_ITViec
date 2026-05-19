package com.example.pagination;

/**
 * Generic pagination response wrapper
 */
public class PageResponse<T> {

    public PagingMeta meta;
    public java.util.List<T> items;

    public PageResponse() {}

    public PageResponse(PagingMeta meta, java.util.List<T> items) {
        this.meta = meta;
        this.items = items;
    }

    public static <T> PageResponse<T> of(PagingMeta meta, java.util.List<T> items) {
        return new PageResponse<>(meta, items);
    }
}