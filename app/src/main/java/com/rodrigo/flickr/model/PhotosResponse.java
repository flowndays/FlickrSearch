package com.rodrigo.flickr.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class PhotosResponse {
    @SerializedName("photos")
    private final PhotosMeta photosMeta;

    @SerializedName("stat")
    private final String status;

    private PhotosResponse(PhotosMeta photosMeta, String status) {
        this.photosMeta = photosMeta;
        this.status = status;
    }

    private class PhotosMeta {
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

        private PhotosMeta(int page, int pages, int perpage, int total, List<Photo> list) {
            this.page = page;
            this.pages = pages;
            this.perpage = perpage;
            this.total = total;
            photoList = list;
        }
    }

    public boolean isSucceed() {
        return "ok".equals(status);
    }

    public boolean hasMore() {
        return isSucceed() && photosMeta != null && photosMeta.pages > photosMeta.page;
    }

    public int getPage() {
        assertPhotosNonNull();
        return photosMeta.page;
    }

    public int nextPage() {
        if (!hasMore()) {
            throw new IllegalStateException("no more pages");
        }
        return photosMeta.page + 1;
    }

    public boolean isFirstPage() {
        assertPhotosNonNull();
        return photosMeta.page == 1;
    }

    private void assertPhotosNonNull() {
        if (photosMeta == null) {
            throw new IllegalStateException("photos is null");
        }
    }

    @NonNull
    public List<Photo> getPhotos() {
        if (photosMeta == null || photosMeta.photoList == null) {
            return Collections.emptyList();
        } else {
            return photosMeta.photoList;
        }
    }
}
