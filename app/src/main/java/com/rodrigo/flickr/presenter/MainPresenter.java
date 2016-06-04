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

import java.util.ArrayList;
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
    private List<Photo> allPhotoList = new ArrayList<>();
    private PhotosResponse responseOfCurrentPage;
    private boolean isLoading = false;

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
    public void onDetach() {
        this.mainMvpView = null;
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDetach();
    }

    public List<Photo> getAllPhotoList() {
        return allPhotoList;
    }

    public void resetPage() {
        responseOfCurrentPage = null;
    }

    public void searchPhotos(String keyword) {
        String key = keyword.trim();
        if (key.isEmpty() || isLoading) {
            return;
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
                        Log.i(TAG, "Search result: " + responseOfCurrentPage);
                        List<Photo> photos = responseOfCurrentPage.getPhotos();
                        if (!photos.isEmpty()) {
                            allPhotoList.addAll(photos);
                            if (responseOfCurrentPage.isFirstPage()) {
                                mainMvpView.setPhotos(photos);
                            } else {
                                mainMvpView.appendPhotos(photos);
                            }
                        } else {
                            allPhotoList.clear();
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
                        responseOfCurrentPage = newResponse;
                    }
                });
    }

    private int getPageIndex() {
        return responseOfCurrentPage == null ? 0 : responseOfCurrentPage.nextPage();
    }

}
