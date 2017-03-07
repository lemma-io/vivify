package com.rva.mrb.vivify.View.Wake;

import com.rva.mrb.vivify.Model.Service.RealmService;

/**
 * Created by rigo on 8/6/16.
 */
public class WakePresenterImpl implements WakePresenter {

    private final RealmService mRealmService;
    private WakeView mWakeView = new WakeView.EmptypAlarmView();

    public WakePresenterImpl(RealmService realmService) {
        mRealmService = realmService;
    }
    @Override
    public void setView(WakeView view) {
        mWakeView = view;
    }

    @Override
    public void clearView() {
        mWakeView = new WakeView.EmptypAlarmView();
    }

    @Override
    public void closeRealm() {
        mRealmService.closeRealm();
    }
}
