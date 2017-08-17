package com.rva.mrb.vivify.View.Search;


import com.rva.mrb.vivify.BasePresenter;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.PlaylistPager;
import com.rva.mrb.vivify.Model.Data.Search;

import java.util.List;

public interface SearchPresenter extends BasePresenter<SearchView> {
    List<MediaType> setupMediaList(Search results);
    List<MediaType> setupMediaList(PlaylistPager results);
}
