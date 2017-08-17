package com.rva.mrb.vivify.View.Search;

import com.rva.mrb.vivify.Model.Data.Album;
import com.rva.mrb.vivify.Model.Data.Artist;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Playlist;
import com.rva.mrb.vivify.Model.Data.PlaylistPager;
import com.rva.mrb.vivify.Model.Data.Search;
import com.rva.mrb.vivify.Model.Data.Track;
import com.rva.mrb.vivify.Model.Service.RealmService;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenterImpl implements SearchPresenter{

    private final RealmService mRealmService;
    private SearchView mSearchView = new SearchView.EmptyNewSearchView();

    public SearchPresenterImpl(RealmService realmService) { mRealmService = realmService; }


    @Override
    public void setView(SearchView view) {
        mSearchView = view;
    }

    @Override
    public void clearView() {
        mSearchView = new SearchView.EmptyNewSearchView();
    }

    @Override
    public void closeRealm() {
        mRealmService.closeRealm();
    }

    @Override
    public List<MediaType> setupMediaList(Search results) {
        List<MediaType> mediaTypeList = new ArrayList<>();
        for (Track t : results.getTracks().getItems())
            mediaTypeList.add(new MediaType(t));
        for (Album a : results.getAlbums().getItems())
            mediaTypeList.add(new MediaType(a));
        for (Playlist a : results.getPlaylists().getItems())
            mediaTypeList.add(new MediaType(a));
//        for (Artist a : results.getArtists().getItems())
//            mediaTypeList.add(new MediaType(a));
        return mediaTypeList;
    }

    @Override
    public List<MediaType> setupMediaList(PlaylistPager results){
        List<MediaType> mediaTypeList = new ArrayList<>();
        for (Playlist p : results.getItems()){
            mediaTypeList.add(new MediaType(p));
        }
        return mediaTypeList;
    }
}
