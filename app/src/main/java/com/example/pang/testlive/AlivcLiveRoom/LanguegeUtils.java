package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;

import java.util.Locale;

/**
 * Created by Akira on 2018/6/19.
 */

public class LanguegeUtils {

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }
}
