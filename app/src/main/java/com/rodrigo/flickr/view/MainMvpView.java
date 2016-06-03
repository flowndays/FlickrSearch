package com.rodrigo.flickr.view;

import com.rodrigo.flickr.model.Photo;

import java.util.List;

public interface MainMvpView extends MvpView {

    void appendPhotos(List<Photo> images);

    void showMessage(int stringId);

    void showProgressIndicator();
}
