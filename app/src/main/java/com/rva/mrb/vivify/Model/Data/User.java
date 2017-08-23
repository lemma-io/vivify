package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User {
    private String birthdate;
    private String country;
    private String display_name;
    private String email;
    private ExternalUrls external_urls;
    private Followers followers;
    private String href;
    private String id;
    private List<Image> images = new ArrayList<Image>();
    private String product;
    private String type;
    private String uri;

    public String getDisplayName() {
        return display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public ExternalUrls getExternalUrls() {
        return external_urls;
    }

    public void setExternalUrls(ExternalUrls external_urls) {
        this.external_urls = external_urls;
    }

    public Followers getFollowers() {
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.followers = followers;
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

    public String getBirthdate() { return birthdate; }

    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getProduct() { return product; }

    public void setProduct(String product) { this.product = product; }

    //    public Map<String, Object> getAdditionalProperties() {
//        return additionalProperties;
//    }
//
//    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
//        this.additionalProperties = additionalProperties;
//    }
//
//    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public class Image {

        private Object height;
        private String url;
        private Object width;

//        public Map<String, Object> getAdditionalProperties() {
//            return additionalProperties;
//        }
//
//        public void setAdditionalProperties(Map<String, Object> additionalProperties) {
//            this.additionalProperties = additionalProperties;
//        }

        public Object getHeight() {
            return height;
        }

        public void setHeight(Object height) {
            this.height = height;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Object getWidth() {
            return width;
        }

        public void setWidth(Object width) {
            this.width = width;
        }

//        private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    }
    public class Followers {

        private Object href;
        private Integer total;

        public Object getHref() {
            return href;
        }

        public void setHref(Object href) {
            this.href = href;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
//
//        public Map<String, Object> getAdditionalProperties() {
//            return additionalProperties;
//        }
//
//        public void setAdditionalProperties(Map<String, Object> additionalProperties) {
//            this.additionalProperties = additionalProperties;
//        }
//
//        private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    }

    public class ExternalUrls {

        private String spotify;

//        public Map<String, Object> getAdditionalProperties() {
//            return additionalProperties;
//        }
//
//        public void setAdditionalProperties(Map<String, Object> additionalProperties) {
//            this.additionalProperties = additionalProperties;
//        }

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }

//        private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    }
}
