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
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> photoList = new ArrayList<>();
    private int fixedWidth;
    private int fixedHeight;

    public void setFixedSizeInPixels(int fixedWidth, int fixedHeight) {
        this.fixedWidth = fixedWidth;
        this.fixedHeight = fixedHeight;
    }

    public void setPhotos(List<Photo> photos) {
        photoList.clear();
        photoList.addAll(photos);
        notifyDataSetChanged();
    }

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
        RequestCreator requestCreator = Picasso.with(context).load(createPhotoUrl(photo));
        if (fixedWidth > 0 && fixedHeight > 0) {
            requestCreator.resize(fixedWidth, fixedHeight);
        }
        requestCreator.error(R.drawable.flickr);
        requestCreator.into(holder.imageView);
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
