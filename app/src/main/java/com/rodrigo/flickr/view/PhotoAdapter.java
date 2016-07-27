package com.rodrigo.flickr.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.rodrigo.flickr.R;
import com.rodrigo.flickr.model.Photo;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> photoList = new ArrayList<>();
    private static final int ANIMATION_DURATION = 300;
    private static final Interpolator mInterpolator = new LinearInterpolator();

    private int fixedWidth;
    private int fixedHeight;

    private int lastPosition = -1;
    private LinkedList<AnimatorSet> animationQueue = new LinkedList<>();
    private int columnCount = 3;
    private PhotoClickAction clickAction;

    public interface PhotoClickAction {
        void onPhotoClicked(Photo photo);
    }

    private View.OnClickListener itemClickListener = view -> {
        Photo photo = (Photo) view.getTag();
        clickAction.onPhotoClicked(photo);
    };

    public PhotoAdapter(@NonNull PhotoClickAction clickAction, int columnCount) {
        this.columnCount = columnCount;
        this.clickAction = clickAction;
    }

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

        viewHolder.itemView.setOnClickListener(itemClickListener);
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

        showAnimation(holder, holder.getAdapterPosition());

        holder.itemView.setTag(photo);
    }


    public static String createPhotoUrl(Photo photo) {
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

    private void showAnimation(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > lastPosition) {
            showAnimationsInQueue(getBottomAnimators(viewHolder.itemView));
            lastPosition = position;
        } else if (position < lastPosition) {
            showAnimationsInQueue(getTopAnimators(viewHolder.itemView));
            lastPosition = position;
        }
    }

    private AnimatorSet getBottomAnimators(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f), ObjectAnimator.ofFloat(view, "translationY", view.getMeasuredHeight(), 0));
        return animatorSet;
    }

    private AnimatorSet getTopAnimators(final View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(view, "alpha", 0f, 1f), ObjectAnimator.ofFloat(view, "translationY", -view.getMeasuredHeight(), 0));
        return animatorSet;
    }

    /**
     * Only show animations of the last {@link this#columnCount} items. Former animations will be
     * ended.
     */
    private void showAnimationsInQueue(AnimatorSet animators) {
        if (animationQueue.size() >= columnCount) {
            animationQueue.removeFirst().end();
        }
        animationQueue.addLast(animators);
        animators.setDuration(ANIMATION_DURATION).start();
        animators.setInterpolator(mInterpolator);
    }
}
