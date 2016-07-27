package com.rodrigo.flickr.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO = "Photo";

    public static void start(Context context, Photo photo) {
        Intent intent = new Intent(context, PhotoDetailActivity.class);
        intent.putExtra(EXTRA_PHOTO, photo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail_activity);
        Photo photo = (Photo) getIntent().getSerializableExtra(EXTRA_PHOTO);
        ImageView imageView = (ImageView) findViewById(R.id.photo_image_view);
        Picasso.with(this).load(PhotoAdapter.createPhotoUrl(photo)).into(imageView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
