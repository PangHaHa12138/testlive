package com.example.pang.testlive.AlivcLiveRoom;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Akira on 2018/5/29.
 */

public class ImageUtils {

    //加载普通图片
    public static void loadImage(Context context, String url, ImageView imageView) {
        if (context != null) {
            if (context instanceof Activity && ((Activity) context).isDestroyed()) {
                return;
            }
            url = filterUrl(url);
            Glide.with(context).load(url).crossFade().centerCrop().into(imageView);
        }
    }


    //加载圆形
    public static void loadCircleImage(Context context, String url, ImageView imageView) {
        if (context != null) {
            if (context instanceof Activity && ((Activity) context).isDestroyed()) {
                return;
            }
            url = filterUrl(url);
            Glide.with(context).load(url).crossFade().centerCrop().transform(new GlideCircleTransform(context)).into(imageView);
        }
    }

    //加载圆角
    public static void loadRoundImage(Context context, String url, int round, ImageView imageView) {
        if (context != null) {
            if (context instanceof Activity && ((Activity) context).isDestroyed()) {
                return;
            }
            url = filterUrl(url);
            Glide.with(context).load(url).crossFade().centerCrop().transform(new GlideRoundTransform(context, round)).into(imageView);
        }
    }

    private static String filterUrl(String url) {
        if (url!=null&& url.contains("http:") ||url!=null&& url.contains("https:")) {
            return url;
        } else {
            return "";
        }
    }
}
