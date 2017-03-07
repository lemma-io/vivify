package com.rva.mrb.vivify;

/**
 * Created by Bao on 6/24/16.
 */
public interface BasePresenter<T> {
    void setView(T view);
    void clearView();
    void closeRealm();
}