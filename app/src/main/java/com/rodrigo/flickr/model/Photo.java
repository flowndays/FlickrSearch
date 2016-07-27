package com.rodrigo.flickr.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable{
//    public static final String

    @SerializedName("id")
    private final String id;
    @SerializedName("secret")
    private final String secret;
    @SerializedName("server")
    private final String server;
    @SerializedName("farm")
    private final int farm;
    @SerializedName("title")
    private final String title;

    Photo(String id, String secret, String server, int farm, String title) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getServer() {
        return server;
    }

    public int getFarm() {
        return farm;
    }

    public String getTitle() {
        return title;
    }
}
