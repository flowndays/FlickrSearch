package com.rodrigo.flickr.view;

import com.rodrigo.flickr.model.Photo;

import java.util.List;

public interface MainMvpView {

    void appendPhotos(List<Photo> images);

    void setPhotos(List<Photo> images);

    void showMessage(int stringId);

    void showProgressIndicator();
}
