package com.rodrigo.flickr.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class PhotosResponse {
    @SerializedName("photos")
    private final Photos photos;

    @SerializedName("stat")
    private final String status;

    private PhotosResponse(Photos photphotoses, String status) {
        this.photos = photphotoses;
        this.status = status;
    }

    private class Photos {
        @SerializedName("page")
        final int page;
        @SerializedName("pages")
        final int pages;
        @SerializedName("perpage")
        final int perpage;
        @SerializedName("total")
        final int total;
        @SerializedName("photo")
        final List<Photo> photoList;

        private Photos(int page, int pages, int perpage, int total, List<Photo> list) {
            this.page = page;
            this.pages = pages;
            this.perpage = perpage;
            this.total = total;
            photoList = list;
        }
    }

    public boolean isSucceed() {
        return status.equals("ok");
    }

    public boolean hasMore() {
        return isSucceed() && photos.pages > photos.page;
    }

    public int nextPage() {
        if (!hasMore()) {
            throw new IllegalStateException("no more pages");
        }
        return photos.page + 1;
    }

    public boolean isFirstPage() {
        return photos.page == 1;
    }

    @NonNull
    public List<Photo> getPhotos() {
        return photos.photoList == null ? Collections.emptyList() : photos.photoList;
    }
}
