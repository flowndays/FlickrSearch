package com.rodrigo.flickr.presenter;

import android.util.Log;

import com.rodrigo.flickr.MyApplication;
import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.PhotosResponse;
import com.rodrigo.flickr.model.SearchService;
import com.rodrigo.flickr.view.MainMvpView;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainPresenter implements Presenter<MainMvpView> {

    public static String TAG = "MainPresenter";

    private MainMvpView mainMvpView;
    private Subscription subscription;
    private PhotosResponse responseOfCurrentPage;
    private boolean isLoading = false;

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
        if (key.isEmpty() || isLoading) {
            return;
        }

        mainMvpView.showProgressIndicator();
        if (subscription != null) {
            subscription.unsubscribe();
        }

        isLoading = true;
        MyApplication application = MyApplication.get(mainMvpView.getContext());
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
                        if (!responseOfCurrentPage.getPhotos().isEmpty()) {
                            mainMvpView.appendPhotos(responseOfCurrentPage.getPhotos());
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
                        responseOfCurrentPage = newResponse;
                    }
                });
    }

    private int getPageIndex() {
        return responseOfCurrentPage == null ? 0 : responseOfCurrentPage.nextPage();
    }

    public boolean isLoading() {
        return isLoading;
    }
}
