package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuli on 2018/1/14.
 */

public class Util {
    /**
     * 按照中文占2个字符 英文占1个字符规则 获取字符串占位符
     *
     * @param source
     * @return
     */
    public static int getSpaceCount(String source) {
        int count = 0;
        count = source.length() + getSpaceChineseCount(source);
        return count;
    }

    /**
     * 获取中文个数
     *
     * @param source
     * @return
     */
    public static int getSpaceChineseCount(String source) {
        String regEx = "[\\u4e00-\\u9fa5]";
        int count = 0;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(source);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }

    public static boolean isNetworkAvailable(Context mActivity){
        Context context = mActivity.getApplicationContext();

        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        if(connectivity == null){
            return false;
        }else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if(info != null ){
                for(int i=0; i<info.length; i++){
                    if(info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        return width;
    }

    public static int getScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;
        return height;
    }

    private static Toast mToast;
    public static void showToast(Context context, String msg){
        if(mToast != null){
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context.getApplicationContext(),msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
