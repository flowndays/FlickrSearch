package com.rodrigo.flickr.presenter;

import com.rodrigo.flickr.BuildConfig;
import com.rodrigo.flickr.MyApplication;
import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.PhotosResponse;
import com.rodrigo.flickr.model.SearchService;
import com.rodrigo.flickr.util.MockModel;
import com.rodrigo.flickr.view.MainMvpView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainPresenterTest {
    MainPresenter mainPresenter;
    MainMvpView mainMvpView;
    SearchService searchService;

    @Before
    public void setUp() {
        MyApplication application = (MyApplication) RuntimeEnvironment.application;
        searchService = mock(SearchService.class);
        application.setSearchService(searchService);

        application.setDefaultSubscribeScheduler(Schedulers.immediate());
        mainPresenter = new MainPresenter();
        mainMvpView = mock(MainMvpView.class);
        when(mainMvpView.getContext()).thenReturn(application);
        mainPresenter.attachView(mainMvpView);
    }

    @After
    public void tearDown() {
        mainPresenter.detachView();
    }

    @Test
    public void searchPhotosShouldAppendPhotos_withResults() {
        String keyword = "kittens";
        PhotosResponse photosResponse = MockModel.mockSearchResult(10);
        when(searchService.searchPhotoes(keyword))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(keyword);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView).appendPhotos(photosResponse.getPhotos());
    }

    @Test
    public void searchPhotosShouldShowMessage_withNoResult() {
        String keyword = "kittens";
        PhotosResponse photosResponse = MockModel.mockEmptyResult();
        when(searchService.searchPhotoes(keyword))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(keyword);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView, never()).appendPhotos(photosResponse.getPhotos());
        verify(mainMvpView).showMessage(R.string.no_result_found);
    }
    @Test
    public void searchPhotosShouldShowErrorMessage_withError() {
        String keyword = "kittens";
        PhotosResponse photosResponse = MockModel.mockErrorResult();
        when(searchService.searchPhotoes(keyword))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(keyword);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView, never()).appendPhotos(photosResponse.getPhotos());
        verify(mainMvpView).showMessage(R.string.error_searching_photoes);
    }

}
