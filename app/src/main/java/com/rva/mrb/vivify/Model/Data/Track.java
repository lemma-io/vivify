package com.rva.mrb.vivify.Model.Data;


import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class Track {

    public Album album;
    public List<Artist> artists;
    public List<String> availableMarkets;
    public int durationMs;
    public Map<String,String> externalURLs;
    public Boolean explicit;
    public String href;
    public String id;
    public String name;
    public String previewURL;
    public int trackNumber;
    public String type;
    public String uri;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public List<String> getAvailableMarkets() {
        return availableMarkets;
    }

    public void setAvailableMarkets(List<String> availableMarkets) {
        this.availableMarkets = availableMarkets;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public Map<String, String> getExternalURLs() {
        return externalURLs;
    }

    public void setExternalURLs(Map<String, String> externalURLs) {
        this.externalURLs = externalURLs;
    }

    public Boolean getExplicit() {
        return explicit;
    }

    public void setExplicit(Boolean explicit) {
        this.explicit = explicit;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
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
//    private String trackName;
//    private String artist;
//    private String trackId;

//    public String getTrackName() {
//        return trackName;
//    }
//
//    public void setTrackName(String trackName) {
//        this.trackName = trackName;
//    }
//
//    public String getArtist() {
//        return artist;
//    }
//
//    public void setArtist(String artist) {
//        this.artist = artist;
//    }
//
//    public String getTrackId() {
//        return trackId;
//    }
//
//    public void setTrackId(String trackId) {
//        this.trackId = trackId;
//    }
}
