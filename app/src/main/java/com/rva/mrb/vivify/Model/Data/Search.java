package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;


@Parcel
public class Search {

    public TracksPager tracks;
    public AlbumPager albums;
    public PlaylistPager playlists;
    public ArtistPager artists;

    public TracksPager getTracks() {
        return tracks;
    }

    public void setTracks(TracksPager tracks) {
        this.tracks = tracks;
    }

    public AlbumPager getAlbums() {
        return albums;
    }

    public void setAlbums(AlbumPager albums) {
        this.albums = albums;
    }

    public PlaylistPager getPlaylists() {
        return playlists;
    }

    public void setPlaylists(PlaylistPager playlists) {
        this.playlists = playlists;
    }

    public ArtistPager getArtists() {
        return artists;
    }

    public void setArtists(ArtistPager artists) {
        this.artists = artists;
    }
}
