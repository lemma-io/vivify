package com.rva.mrb.vivify.View.Search;

import com.rva.mrb.vivify.Model.Service.RealmService;

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
}
