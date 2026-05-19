package com.example.pagination;

/**
 * Pagination metadata
 */
public class PagingMeta {

    public long totalElements;
    public int totalPages;
    public int currentPage;
    public int pageSize;
    public int pageNumber; // 0-based
    public String sortBy;
    public String direction;
    public boolean hasNext;
    public boolean hasPrevious;

    public PagingMeta() {}

    public PagingMeta(long totalElements, int totalPages, int currentPage, int pageSize,
                     String sortBy, String direction) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.pageNumber = currentPage - 1;
        this.sortBy = sortBy;
        this.direction = direction;
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }

    // Getters for JSON serialization
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public int getPageNumber() { return pageNumber; }
    public String getSortBy() { return sortBy; }
    public String getDirection() { return direction; }
    public boolean isHasNext() { return hasNext; }
    public boolean isHasPrevious() { return hasPrevious; }
}