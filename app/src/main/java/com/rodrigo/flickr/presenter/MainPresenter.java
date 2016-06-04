package com.rodrigo.flickr.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.rodrigo.flickr.MyApplication;
import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.model.PhotosResponse;
import com.rodrigo.flickr.model.SearchService;
import com.rodrigo.flickr.view.MainMvpView;

import java.util.Collections;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Non-UI Fragment serving as a Presenter in MVP model.
 */
public class MainPresenter extends Fragment {

    public static String TAG = "MainPresenter";

    private MainMvpView mainMvpView;
    private Subscription subscription;
    private boolean isLoading = false;

    /*
     * Keeps photos for all requests of specific keyword, for restoring Adapter items on rotation.
     * If the keyword is changed, it should be set null.
     */
    private AllPhotosResponse allPhotosResponse;

    private PhotosResponse lastPageResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainMvpView = (MainMvpView) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mainMvpView = null;
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    public List<Photo> getAllPhotoList() {
        return allPhotosResponse == null
               ? Collections.emptyList()
               : allPhotosResponse.getAllPhotoList();
    }

    public void reset() {
        allPhotosResponse = null;
        lastPageResponse = null;
    }

    public void searchPhotos(String keyword) {
        String key = keyword.trim();
        if (key.isEmpty() || isLoading) {
            return;
        }
        if (allPhotosResponse != null && !keyword.equals(allPhotosResponse.getKeyword())) {
            allPhotosResponse = null;
        }

        mainMvpView.showProgressIndicator();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        isLoading = true;
        MyApplication application = MyApplication.get(getContext());
        SearchService searchService = application.getSearchService();
        subscription = searchService.searchPhotos(key, getPageIndex())
                .doOnNext(response -> {
                    if (!response.isSucceed()) {
                        throw new RuntimeException("search failed for unknown error");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(application.defaultSubscribeScheduler())
                .subscribe(new Subscriber<PhotosResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted(), current page:" + lastPageResponse.getPage()
                                + " for keyword:" + keyword);
                        List<Photo> photos = lastPageResponse.getPhotos();
                        if (!photos.isEmpty()) {
                            if (lastPageResponse.isFirstPage()) {
                                mainMvpView.setPhotos(photos);
                            } else {
                                mainMvpView.appendPhotos(photos);
                            }
                        } else {
                            mainMvpView.showMessage(R.string.no_result_found);
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error searching photoes: ", error);
                        mainMvpView.showMessage(R.string.error_searching_photoes);
                        isLoading = false;
                    }

                    @Override
                    public void onNext(PhotosResponse newResponse) {
                        if (allPhotosResponse == null) {
                            allPhotosResponse = new AllPhotosResponse(keyword);
                        }
                        lastPageResponse = newResponse;
                        allPhotosResponse.addPhotos(newResponse.getPhotos());
                    }
                });
    }

    private int getPageIndex() {
        return lastPageResponse == null
               ? 1
               : lastPageResponse.nextPage();
    }

}
