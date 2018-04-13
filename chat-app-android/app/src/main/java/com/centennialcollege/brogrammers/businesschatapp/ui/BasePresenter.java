package com.centennialcollege.brogrammers.businesschatapp.ui;

public interface BasePresenter<V> {

    void takeView(V v);

    void dropView();

}
