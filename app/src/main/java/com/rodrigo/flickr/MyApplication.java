package com.rodrigo.flickr;

import android.app.Application;
import android.content.Context;

import com.rodrigo.flickr.model.SearchService;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class MyApplication extends Application {

    private SearchService searchService;
    private Scheduler defaultSubscribeScheduler;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    public SearchService getSearchService() {
        if (searchService == null) {
            searchService = SearchService.Factory.create();
        }
        return searchService;
    }

    //For setting mocks during testing
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    //Used to change scheduler from tests
    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }
}
