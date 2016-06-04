package com.rodrigo.flickr.presenter;

import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.model.PhotosResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps all photos and the latest {@link PhotosResponse} for specific keyword
 */
public class AllPhotosResponse {
    private final String keyword;
    private final List<Photo> allPhotoList = new ArrayList<>();

    public AllPhotosResponse(String keyword) {this.keyword = keyword;}

    public String getKeyword() {
        return keyword;
    }

    public void addPhotos(List<Photo> photos) {
        allPhotoList.addAll(photos);
    }

    public List<Photo> getAllPhotoList() {
        return allPhotoList;
    }
}
