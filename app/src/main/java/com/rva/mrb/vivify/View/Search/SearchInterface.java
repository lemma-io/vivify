
package com.rva.mrb.vivify.View.Search;

import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Search;
import com.rva.mrb.vivify.Model.Data.SimpleTrack;
import com.rva.mrb.vivify.Model.Data.Track;

/**
 * Created by Bao on 8/19/16.
 */
public interface SearchInterface {

//    public void onTrackSelected(SimpleTrack.Item track);
//    void onTrackSelected(Track track);

    void onMediaSelected(MediaType mediaType);
}