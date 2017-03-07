package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;

@Parcel
public class Image {

    public Integer width;
    public Integer height;
    public String url;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
