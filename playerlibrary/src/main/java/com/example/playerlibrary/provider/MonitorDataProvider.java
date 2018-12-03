package com.example.playerlibrary.provider;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.BundlePool;
import com.example.playerlibrary.event.EventKey;

import java.util.List;

/**
 * Created by Taurus on 2018/4/15.
 */

public class MonitorDataProvider extends BaseDataProvider {

    private DataSource mDataSource;

    private List<VideoBean> mVideos;

    public void setTestData(List<VideoBean> list){
        mVideos = list;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    public void handleSourceData(DataSource sourceData) {
        this.mDataSource = sourceData;
        onProviderDataStart();
        mHandler.removeCallbacks(mLoadDataRunnable);
        mHandler.postDelayed(mLoadDataRunnable, 2000);
    }

    private Runnable mLoadDataRunnable = new Runnable() {
        @Override
        public void run() {
            long id = mDataSource.getId();
            int index = (int) (id%mVideos.size());
            VideoBean bean = mVideos.get(index);
            mDataSource.setData(bean.getPath());
            mDataSource.setTitle(bean.getDisplayName());
            Bundle bundle = BundlePool.obtain();
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA, mDataSource);
            onProviderMediaDataSuccess(bundle);
        }
    };

    @Override
    public void cancel() {
        mHandler.removeCallbacks(mLoadDataRunnable);
    }

    @Override
    public void destroy() {
        cancel();
    }
}
