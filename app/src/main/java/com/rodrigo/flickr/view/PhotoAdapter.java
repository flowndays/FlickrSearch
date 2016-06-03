package com.rodrigo.flickr.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> photoList = new ArrayList<>();

    public void addPhotos(List<Photo> photos) {
        photoList.addAll(photos);
        notifyDataSetChanged();
    }


    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        final PhotoViewHolder viewHolder = new PhotoViewHolder(itemView);
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        Context context = holder.imageView.getContext();
        Picasso.with(context).load(createPhotoUrl(photo)).into(holder.imageView);
    }

    private String createPhotoUrl(Photo photo) {
        return String.format("http://farm%s.static.flickr.com/%s/%s_%s.jpg", photo.getFarm(),
                photo.getServer(), photo.getId(), photo.getSecret());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.photo_image_view);
        }
    }
}
