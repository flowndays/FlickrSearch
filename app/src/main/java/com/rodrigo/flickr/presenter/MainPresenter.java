package com.rodrigo.flickr.presenter;

import android.util.Log;

import com.rodrigo.flickr.MyApplication;
import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.rodrigo.flickr.model.PhotosResponse;
import com.rodrigo.flickr.model.SearchService;
import com.rodrigo.flickr.view.MainMvpView;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainPresenter implements Presenter<MainMvpView> {

    public static String TAG = "MainPresenter";

    private MainMvpView mainMvpView;
    private Subscription subscription;
    private List<Photo> photoList;

    @Override
    public void attachView(MainMvpView view) {
        this.mainMvpView = view;
    }

    @Override
    public void detachView() {
        this.mainMvpView = null;
        if (subscription != null) subscription.unsubscribe();
    }

    public void searchPhotos(String keyword) {
        String key = keyword.trim();
        if (key.isEmpty()) {
            return;
        }

        mainMvpView.showProgressIndicator();
        if (subscription != null) subscription.unsubscribe();
        MyApplication application = MyApplication.get(mainMvpView.getContext());
        SearchService searchService = application.getSearchService();
        subscription = searchService.searchPhotos(key, 0)
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
                        Log.i(TAG, "Search result: " + photoList);
                        if (!photoList.isEmpty()) {
                            mainMvpView.appendPhotos(photoList);
                        } else {
                            mainMvpView.showMessage(R.string.no_result_found);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Log.e(TAG, "Error searching photoes: ", error);
                        mainMvpView.showMessage(R.string.error_searching_photoes);
                    }

                    @Override
                    public void onNext(PhotosResponse response) {
                        photoList = response.getPhotos();
                    }
                });
    }

}
