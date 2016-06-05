package com.rodrigo.flickr.presenter;

import com.rodrigo.flickr.BuildConfig;
import com.rodrigo.flickr.MyApplication;
import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.PhotosResponse;
import com.rodrigo.flickr.model.SearchService;
import com.rodrigo.flickr.util.MockModel;
import com.rodrigo.flickr.view.MainActivity;

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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MainPresenterTest {
    private static final String KEYWORD = "kittens";
    MainPresenter mainPresenter;
    MainActivity mainMvpView;
    SearchService searchService;

    @Before
    public void setUp() {
        MyApplication application = (MyApplication) RuntimeEnvironment.application;
        searchService = mock(SearchService.class);
        application.setSearchService(searchService);

        application.setDefaultSubscribeScheduler(Schedulers.immediate());
        mainPresenter = spy(new MainPresenter());
        mainMvpView = mock(MainActivity.class);
        when(mainPresenter.getContext()).thenReturn(application);
        when(mainPresenter.getActivity()).thenReturn(mainMvpView);
        mainPresenter.onAttach(application);
    }

    @Test
    public void searchPhotosShouldAppendPhotos_withResults() {
        PhotosResponse photosResponse = MockModel.mockSearchResult(10, 1);
        when(searchService.searchPhotos(KEYWORD, 1))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(KEYWORD);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView, never()).appendPhotos(photosResponse.getPhotos());
        verify(mainMvpView).setPhotos(photosResponse.getPhotos());
    }

    @Test
    public void searchPhotosShouldShowMessage_withNoResult() {
        PhotosResponse photosResponse = MockModel.mockEmptyResult();
        when(searchService.searchPhotos(KEYWORD, 1))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(KEYWORD);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView, never()).appendPhotos(photosResponse.getPhotos());
        verify(mainMvpView).showMessage(R.string.no_result_found);
    }

    @Test
    public void searchPhotosShouldShowErrorMessage_withError() {
        PhotosResponse photosResponse = MockModel.mockErrorResult();
        when(searchService.searchPhotos(KEYWORD, 1))
                .thenReturn(Observable.just(photosResponse));

        mainPresenter.searchPhotos(KEYWORD);
        verify(mainMvpView).showProgressIndicator();
        verify(mainMvpView, never()).appendPhotos(photosResponse.getPhotos());
        verify(mainMvpView).showMessage(R.string.error_searching_photos);
    }

}
