package com.rodrigo.flickr.presenter;

public interface Presenter<V> {

    void attachView(V view);

    void detachView();

}
