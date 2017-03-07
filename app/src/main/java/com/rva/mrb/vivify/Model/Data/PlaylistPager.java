package com.rva.mrb.vivify.Model.Data;


import org.parceler.Parcel;

import java.util.List;

@Parcel
public class PlaylistPager {

    public String href;
    public List<Playlist> items = null;
    public int limit;
    public String next;
    public int offset;
    public String previous;
    public int total;
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Playlist> getItems() {
        return items;
    }

    public void setItems(List<Playlist> items) {
        this.items = items;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
