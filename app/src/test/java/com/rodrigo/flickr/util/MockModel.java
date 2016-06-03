package com.rodrigo.flickr.util;

import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.model.PhotosResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockModel {
    public static PhotosResponse mockEmptyResult() {
        PhotosResponse photosResponse = mock(PhotosResponse.class);
        when(photosResponse.isSucceed()).thenReturn(true);
        when(photosResponse.hasMore()).thenReturn(false);
        when(photosResponse.getPhotos()).thenReturn(Collections.emptyList());
        return photosResponse;
    }

    public static PhotosResponse mockErrorResult() {
        PhotosResponse photosResponse = mock(PhotosResponse.class);
        when(photosResponse.isSucceed()).thenReturn(false);
        when(photosResponse.getPhotos()).thenReturn(Collections.emptyList());
        return photosResponse;
    }

    public static PhotosResponse mockSearchResult(int number) {
        List<Photo> photos = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            photos.add(mock(Photo.class));
        }
        PhotosResponse photosResponse = mock(PhotosResponse.class);
        when(photosResponse.isSucceed()).thenReturn(true);
        when(photosResponse.getPhotos()).thenReturn(photos);
        return photosResponse;
    }

}
