package com.rodrigo.flickr.model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface SearchService {

    String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";

    @GET("services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&format=json&nojsoncallback=1")
    Observable<PhotosResponse> searchPhotoes(@Query("text") String keyword);

    class Factory {
        public static SearchService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.flickr.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(SearchService.class);
        }
    }
}
