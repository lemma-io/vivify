package com.rva.mrb.vivify.Model.Data;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by Bao on 8/23/17.
 */

@Parcel
public class PlaylistTrack {
    public Owner addedBy;
    public Boolean isLocal;
    public Track track;



    public Owner getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(Owner addedBy) {
        this.addedBy = addedBy;
    }

    public Boolean isLocal() {
        return isLocal;
    }

    public void setLocal(Boolean local) {
        isLocal = local;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}
