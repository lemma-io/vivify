package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;

import java.util.List;

/**
 * Offset-based paging object is a container for a set Track objects.
 * Contains a key called items whose value is an array
 * of the requested Track objects
 */
@Parcel
public class TracksPager {

    public String href;         // Link to Web API returning full request response
    public List<Track> items;   // The requested data, Tracks in this case
    public int limit;           // The maximum number of objects in the response
    public String next;         // URL to the next page of items
    public int offset;          // Offset of items returned
    public String previous;     // URL to previous page of items
    public int total;           // Total number of items available

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<Track> getItems() {
        return items;
    }

    public void setItems(List<Track> items) {
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
