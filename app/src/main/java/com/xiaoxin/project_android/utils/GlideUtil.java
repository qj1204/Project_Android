package com.xiaoxin.project_android.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;

@GlideModule
public class GlideUtil {
    public static void load(Context context, String url, ImageView imageView, RequestOptions options) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.load_img)
                .error(R.drawable.ic_net_error)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .apply(options)
                .into(imageView);
    }

}