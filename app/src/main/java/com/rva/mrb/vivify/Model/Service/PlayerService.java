package com.rva.mrb.vivify.Model.Service;

import android.content.Context;
import android.util.Log;

import com.rva.mrb.vivify.Model.Data.Alarm;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Track;
import com.rva.mrb.vivify.Spotify.SpotifyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Bao on 8/22/17.
 */

public class PlayerService {

    private Alarm mAlarm;
    private Context mContext;
    private SpotifyService mSpotifyService;
    private int mediaType;
    private String trackId;
    private String artist;
    private List<Track> tracks;

    public PlayerService(Context context, SpotifyService spotifyService, Alarm alarm) {
        this.mContext = context;
        this.mSpotifyService = spotifyService;
        this.mAlarm = alarm;
        this.mediaType = alarm.getMediaType();
        this.trackId = alarm.getTrackId();
        this.artist = alarm.getArtistName();
        this.tracks = new ArrayList<>();
    }

    public Completable getTracks(){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                switch (mediaType) {
                    case MediaType.ALBUM_TYPE:
                        mSpotifyService.getAlbumTracks(trackId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
                                    Log.d("PlayerService", "getting response");
                                    tracks = response.getItems();
                                    Collections.shuffle(tracks);
                                    e.onComplete();
                                });
                        break;
                    case MediaType.PLAYLIST_TYPE:
                        Log.d("PlayerService", artist + " " + trackId);
                        mSpotifyService.getPlaylistTracks(artist, trackId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {
//                                    Log.d("PlayerService", response.getItems().get(0).getAddedAt().toString());
                                    for(int i = 0; i < response.getItems().size(); i++) {
                                        tracks.add(response.getItems().get(i).getTrack());
                                    }
                                    Collections.shuffle(tracks);
                                    e.onComplete();
                                });
                        break;
                    case MediaType.TRACK_TYPE:
                        Track track = new Track();
                        track.setUri("spotify:track:" + trackId);
                        tracks.add(track);
                        e.onComplete();
                        break;
                    case MediaType.DEFAULT_TYPE:
                        e.onComplete();
                        break;
                }

            }
        });
    }

    public List<Track> returnTracks(){
        return tracks;
    }
}
