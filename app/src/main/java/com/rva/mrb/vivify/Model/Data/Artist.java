package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class Artist {

//    public ExternalUrls externalUrls;
//    public Followers followers;
    public List<String> genres = null;
    public String href;
    public String id;
    public List<Image> images = null;
    public String name;
    public Integer popularity;
    public String type;
    public String uri;

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
//    public Map<String, String> external_urls;
//    public String href;
//    public String id;
//    public String name;
//    public String type;
//    public String uri;
//
//    public Map<String, String> getExternal_urls() {
//        return external_urls;
//    }
//
//    public void setExternal_urls(Map<String, String> external_urls) {
//        this.external_urls = external_urls;
//    }
//
//    public String getHref() {
//        return href;
//    }
//
//    public void setHref(String href) {
//        this.href = href;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public String getUri() {
//        return uri;
//    }
//
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
}
