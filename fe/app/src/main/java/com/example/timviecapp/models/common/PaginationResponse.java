package com.example.timviecapp.models.common;

import java.util.List;

public class PaginationResponse<T> {
    private MetaPage meta;
    private List<T> items;

    public MetaPage getMeta() {
        return meta;
    }

    public void setMeta(MetaPage meta) {
        this.meta = meta;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
