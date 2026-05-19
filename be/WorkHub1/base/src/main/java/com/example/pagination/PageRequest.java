package com.example.pagination;

/**
 * Pagination request parameters holder
 */
public class PageRequest {

    private int page = 1;
    private int size = 10;
    private String sortBy = "createdAt";
    private String direction = "DESC";
    private String keyword;
    private String filter;

    public PageRequest() {}

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(1, page); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = Math.min(Math.max(1, size), 100); }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public int getOffset() {
        return (page - 1) * size;
    }

    public boolean isAscending() {
        return "ASC".equalsIgnoreCase(direction);
    }
}