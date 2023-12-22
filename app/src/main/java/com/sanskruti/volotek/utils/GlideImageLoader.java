package com.sanskruti.volotek.utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class GlideImageLoader {
    public ProgressBar mProgressBar;
    private ImageView mImageView;

    public GlideImageLoader(ImageView imageView, ProgressBar progressBar) {
        this.mImageView = imageView;
        this.mProgressBar = progressBar;
    }

    public void load(final String str, RequestOptions requestOptions) {
        if (str != null && requestOptions != null) {
            onConnecting();
            ProgressAppGlideModule.expect(str, new ProgressAppGlideModule.UIonProgressListener() {
                public float getGranualityPercentage() {
                    return 1.0f;
                }

                public void onProgress(long j, long j2) {
                    if (GlideImageLoader.this.mProgressBar != null) {
                        GlideImageLoader.this.mProgressBar.setProgress((int) ((j * 100) / j2));
                    }
                }
            });
            Glide.with(this.mImageView.getContext()).load(str).transition((TransitionOptions) DrawableTransitionOptions.withCrossFade()).apply(requestOptions).listener(new RequestListener<Drawable>() {
                public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target<Drawable> target, boolean z) {
                    ProgressAppGlideModule.forget(str);
                    GlideImageLoader.this.onFinished();
                    return false;
                }

                public boolean onResourceReady(Drawable drawable, Object obj, Target<Drawable> target, DataSource dataSource, boolean z) {
                    ProgressAppGlideModule.forget(str);
                    GlideImageLoader.this.onFinished();
                    return false;
                }
            }).into(this.mImageView);
        }
    }

    private void onConnecting() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }


    public void onFinished() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar != null && this.mImageView != null) {
            progressBar.setVisibility(View.GONE);
            this.mImageView.setVisibility(View.VISIBLE);
        }
    }
}
