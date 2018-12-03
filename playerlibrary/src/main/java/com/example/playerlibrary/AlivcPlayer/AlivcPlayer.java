package com.example.playerlibrary.AlivcPlayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.example.playerlibrary.R;
import com.example.playerlibrary.config.AppContextAttach;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.BundlePool;
import com.example.playerlibrary.event.EventKey;
import com.example.playerlibrary.event.OnErrorEventListener;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.player.BaseInternalPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Description: 阿里云播放器内核
 * Created by PangHaHa on 18-9-5.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class AlivcPlayer extends BaseInternalPlayer {


    private final String TAG = "AlivcPlayer";

    private  AliyunVodPlayer mAliyunVodPlayer;

    private int mTargetState;

    private int startSeekPos;

    private int mVideoWidth;
    private int mVideoHeight;
    private List<String> logStrs = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");

    private Context applicationContext = AppContextAttach.getApplicationContext();

    public AlivcPlayer(){

        init();
    }
//    提前　lib.init()时候初始化
//    static {
//        //查看log
//        VcPlayerLog.enableLog();
//        //初始化播放器
//        AliVcMediaPlayer.init(getApplicationContext());
//    }

    private void init(){
        mAliyunVodPlayer = createPlayer();
        applicationContext = AppContextAttach.getApplicationContext();
    }

    private AliyunVodPlayer createPlayer(){

        applicationContext = AppContextAttach.getApplicationContext();

        mAliyunVodPlayer = new AliyunVodPlayer(applicationContext);

        return mAliyunVodPlayer;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        if (dataSource!=null){
            openVideo(dataSource);
        }
    }

    private void openVideo(DataSource dataSource){
        try {
            if (mAliyunVodPlayer == null){
                mAliyunVodPlayer = new AliyunVodPlayer(applicationContext);
                mAliyunVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            }else {
                stop();
                reset();
                resetListener();
            }
            // REMOVED: mAudioSession
            mAliyunVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mAliyunVodPlayer.setOnPreparedListener(mPreparedListener);
            mAliyunVodPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mAliyunVodPlayer.setOnCompletionListener(mCompletionListener);
            mAliyunVodPlayer.setOnErrorListener(mErrorListener);
            mAliyunVodPlayer.setOnInfoListener(mInfoListener);
            mAliyunVodPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
            mAliyunVodPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mAliyunVodPlayer.setOnFirstFrameStartListener(onFirstFrameStartListener);
//            mAliyunVodPlayer.setOnTimeExpiredErrorListener(onTimeExpiredErrorListener);
//            mAliyunVodPlayer.setOnStoppedListner(onStoppedListener);
//            mAliyunVodPlayer.setOnUrlTimeExpiredListener(onUrlTimeExpiredListener);
//            mAliyunVodPlayer.setOnLoadingListener(onLoadingListener);
//            mAliyunVodPlayer.setOnAutoPlayListener(onAutoPlayListener);
//            mAliyunVodPlayer.setOnRePlayListener(onRePlayListener);
//            mAliyunVodPlayer.setOnPcmDataListener(onPcmDataListener);
            updateStatus(STATE_INITIALIZED);

            String url = dataSource.getData();

            if (url!=null){
                AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                alsb.setSource(url);
                alsb.setTitle(dataSource.getTitle());
                AliyunLocalSource localSource = alsb.build();
                mAliyunVodPlayer.prepareAsync(localSource);
            }else if(dataSource.aliyunLocalSource!=null){
                mAliyunVodPlayer.prepareAsync(dataSource.aliyunLocalSource);
            } else if (dataSource.aliyunVidSts!=null){
                mAliyunVodPlayer.prepareAsync(dataSource.aliyunVidSts);
            }else if (dataSource.aliyunVidSource!=null){
                mAliyunVodPlayer.prepareAsync(dataSource.aliyunVidSource);
            }else if (dataSource.aliyunPlayAuth!=null){
                mAliyunVodPlayer.prepareAsync(dataSource.aliyunPlayAuth);
            }


            Bundle bundle = BundlePool.obtain();
            bundle.putSerializable(EventKey.SERIALIZABLE_DATA,dataSource);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET,bundle);

        }catch (Exception e){
            e.printStackTrace();

            updateStatus(STATE_ERROR);
            mTargetState = STATE_ERROR;
            submitErrorEvent(OnErrorEventListener.ERROR_EVENT_COMMON,null);
        }

    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        try {
            if(mAliyunVodPlayer!=null){
                mAliyunVodPlayer.setDisplay(surfaceHolder);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_HOLDER_UPDATE, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            if(mAliyunVodPlayer!=null){
                mAliyunVodPlayer.setSurface(surface);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_UPDATE, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if(mAliyunVodPlayer!=null){
            mAliyunVodPlayer.setVolume((int) leftVolume);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if(mAliyunVodPlayer!=null){
            mAliyunVodPlayer.setPlaySpeed(speed);
        }
    }

    @Override
    public boolean isPlaying() {
        if(mAliyunVodPlayer!=null && getState()!= STATE_ERROR){
            return mAliyunVodPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        if(mAliyunVodPlayer!=null&& (getState()== STATE_PREPARED
                || getState()== STATE_STARTED
                || getState()== STATE_PAUSED
                || getState()== STATE_PLAYBACK_COMPLETE)){
            return (int) mAliyunVodPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if(mAliyunVodPlayer!=null
                && getState()!= STATE_ERROR
                && getState()!= STATE_INITIALIZED
                && getState()!= STATE_IDLE){
            return (int) mAliyunVodPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getAudioSessionId() {

        return 0;
    }

    @Override
    public int getVideoWidth() {
        if(mAliyunVodPlayer!=null){
            return mAliyunVodPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if(mAliyunVodPlayer!=null){
            return mAliyunVodPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public void start() {
        if (mAliyunVodPlayer!=null &&
                (getState()==STATE_PREPARED
                        || getState()==STATE_PAUSED
                        || getState()==STATE_PLAYBACK_COMPLETE
                        || getState()== STATE_INITIALIZED)){

            mAliyunVodPlayer.start();
            updateStatus(STATE_STARTED);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_START, null);
        }
        mTargetState = STATE_STARTED;
        Log.e(TAG,"start---");
    }

    @Override
    public void start(int msc) {
        if(msc > 0){
            startSeekPos = msc;
        }
        if(mAliyunVodPlayer!=null){
            start();
        }
    }

    @Override
    public void pause() {
        try {
            if (mAliyunVodPlayer!=null){
                mAliyunVodPlayer.pause();
                updateStatus(STATE_PAUSED);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public void resume() {
        try {
            if (mAliyunVodPlayer!=null){
                mAliyunVodPlayer.resume();
                updateStatus(STATE_STARTED);
                submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESUME, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        mTargetState = STATE_STARTED;
    }

    @Override
    public void seekTo(int msc) {
        if (mAliyunVodPlayer!=null && (getState()== STATE_PREPARED
                || getState()== STATE_STARTED
                || getState()== STATE_PAUSED
                || getState()== STATE_PLAYBACK_COMPLETE)){

            mAliyunVodPlayer.seekTo(msc);
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, msc);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO, bundle);
        }
    }

    @Override
    public void stop() {
        if (mAliyunVodPlayer!=null && (getState()== STATE_PREPARED
                || getState()== STATE_STARTED
                || getState()== STATE_PAUSED
                || getState()== STATE_PLAYBACK_COMPLETE)){

            mAliyunVodPlayer.stop();
            updateStatus(STATE_STOPPED);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STOP, null);
        }
        mTargetState = STATE_STOPPED;
    }

    @Override
    public void reset() {
        if(mAliyunVodPlayer!=null){
            mAliyunVodPlayer.reset();
            updateStatus(STATE_IDLE);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESET, null);
        }
        mTargetState = STATE_IDLE;
    }



    @Override
    public void destroy() {
        if(mAliyunVodPlayer!=null){
            updateStatus(STATE_END);
            resetListener();
            mAliyunVodPlayer.release();
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DESTROY, null);
        }
    }
    private final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    private long mBandWidth;
    IAliyunVodPlayer.OnInfoListener mInfoListener = new IAliyunVodPlayer.OnInfoListener() {
        @Override
        public void onInfo(int arg1, int arg2) {
            Log.e(TAG,"mInfoListener--"+arg1+"--"+arg2);
            switch (arg1) {
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.e(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.e(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                    startSeekPos = 0;
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START,null);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.e(TAG, "MEDIA_INFO_BUFFERING_START:" + arg2);
                    Bundle bundle = BundlePool.obtain();
                    bundle.putLong(EventKey.LONG_DATA, mBandWidth);
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START,bundle);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.e(TAG, "MEDIA_INFO_BUFFERING_END:" + arg2);
                    Bundle bundle1 = BundlePool.obtain();
                    bundle1.putLong(EventKey.LONG_DATA, mBandWidth);
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END,bundle1);
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.e(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BAD_INTERLEAVING,null);
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.e(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_NOT_SEEK_ABLE,null);
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.e(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_METADATA_UPDATE,null);
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.e(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_UNSUPPORTED_SUBTITLE,null);
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.e(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SUBTITLE_TIMED_OUT,null);
                    break;
                case MEDIA_INFO_NETWORK_BANDWIDTH:
                    Log.e(TAG,"band_width : " + arg2);
                    mBandWidth = arg2 * 1000;
                    break;
            }
        }
    };

    IAliyunVodPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IAliyunVodPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete() {
            Log.e(TAG,"EVENT_CODE_SEEK_COMPLETE");
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE,null);
        }
    };

    IAliyunVodPlayer.OnCompletionListener mCompletionListener = new IAliyunVodPlayer.OnCompletionListener() {
        @Override
        public void onCompletion() {
            updateStatus(STATE_PLAYBACK_COMPLETE);
            mTargetState = STATE_PLAYBACK_COMPLETE;
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE,null);
        }
    };

    IAliyunVodPlayer.OnErrorListener mErrorListener = new IAliyunVodPlayer.OnErrorListener() {
        @Override
        public void onError(int i, int i1, String s) {
            Log.e(TAG, "mErrorListener--"+format.format(new Date())+"--"+i+"--"+i1+"--"+s);

            updateStatus(STATE_ERROR);
            mTargetState = STATE_ERROR;
            /* If an error handler has been supplied, use it and finish. */
            Bundle bundle = BundlePool.obtain();
            submitErrorEvent(OnErrorEventListener.ERROR_EVENT_COMMON,bundle);
            mAliyunVodPlayer.replay();
        }
    };



    IAliyunVodPlayer.OnTimeExpiredErrorListener onTimeExpiredErrorListener = new IAliyunVodPlayer.OnTimeExpiredErrorListener() {
        @Override
        public void onTimeExpiredError() {
            mAliyunVodPlayer.replay();
        }
    };

    IAliyunVodPlayer.OnUrlTimeExpiredListener onUrlTimeExpiredListener = new IAliyunVodPlayer.OnUrlTimeExpiredListener() {
        @Override
        public void onUrlTimeExpired(String s, String s1) {

            Log.e(TAG, "onUrlTimeExpiredListener"+format.format(new Date())+"--"+s+"--"+s1);

            mAliyunVodPlayer.replay();
        }
    };
    IAliyunVodPlayer.OnStoppedListener onStoppedListener = new IAliyunVodPlayer.OnStoppedListener() {
        @Override
        public void onStopped() {
            mAliyunVodPlayer.replay();
        }
    };

    IAliyunVodPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IAliyunVodPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(int w, int h) {
            mVideoWidth = mAliyunVodPlayer.getVideoWidth();
            mVideoHeight = mAliyunVodPlayer.getVideoHeight();
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_ARG1, mVideoWidth);
            bundle.putInt(EventKey.INT_ARG2, mVideoHeight);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE,bundle);
            Log.e(TAG,w+"w--"+h+"h--");
        }
    };

    IAliyunVodPlayer.OnFirstFrameStartListener onFirstFrameStartListener = new IAliyunVodPlayer.OnFirstFrameStartListener() {
        @Override
        public void onFirstFrameStart() {
            Map<String, String> debugInfo = mAliyunVodPlayer.getAllDebugInfo();
            long createPts = 0;
            if (debugInfo.get("create_player") != null) {
                String time = debugInfo.get("create_player");
                createPts = (long) Double.parseDouble(time);
                logStrs.add(format.format(new Date(createPts)) + applicationContext.getString(R.string.alivc_log_player_create_success));
            }
            if (debugInfo.get("open-url") != null) {
                String time = debugInfo.get("open-url");
                long openPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(openPts)) + applicationContext.getString(R.string.alivc_log_open_url_success));
            }
            if (debugInfo.get("find-stream") != null) {
                String time = debugInfo.get("find-stream");
                long findPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(findPts)) + applicationContext.getString(R.string.alivc_log_request_stream_success));
            }
            if (debugInfo.get("open-stream") != null) {
                String time = debugInfo.get("open-stream");
                long openPts = (long) Double.parseDouble(time) + createPts;
                logStrs.add(format.format(new Date(openPts)) + applicationContext.getString(R.string.alivc_log_start_open_stream));
            }
            logStrs.add(format.format(new Date()) + applicationContext.getString(R.string.alivc_log_first_frame_played));

            for (String s:debugInfo.keySet()){
                logStrs.add(format.format(new Date())+"---"+debugInfo.get(s));
            }
            for (String log : logStrs) {
                Log.e(TAG, log);
            }
        }
    };

    IAliyunVodPlayer.OnPreparedListener mPreparedListener = new IAliyunVodPlayer.OnPreparedListener() {
        @Override
        public void onPrepared() {
            Log.e(TAG,"onPrepared...");
            updateStatus(STATE_PREPARED);
            mAliyunVodPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
            mVideoWidth = mAliyunVodPlayer.getVideoWidth();
            mVideoHeight = mAliyunVodPlayer.getVideoHeight();

            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_ARG1, mVideoWidth);
            bundle.putInt(EventKey.INT_ARG2, mVideoHeight);

            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED,bundle);

            int seekToPosition = startSeekPos;  // mSeekWhenPrepared may be changed after seekTo() call
            if (seekToPosition != 0) {
                mAliyunVodPlayer.seekTo(seekToPosition);
                startSeekPos = 0;
            }

            // We don't know the video size yet, but should start anyway.
            // The video size might be reported to us later.
            Log.e(TAG,"mTargetState = " + mTargetState);
            if (mTargetState == STATE_STARTED) {
                start();
            }else if(mTargetState == STATE_PAUSED){
                pause();
            }else if(mTargetState == STATE_STOPPED
                    || mTargetState == STATE_IDLE){
                reset();
            }

            logStrs.add(format.format(new Date()) + applicationContext.getString(R.string.alivc_log_prepare_success));
            for (String log : logStrs) {
                Log.e(TAG, log);
            }
        }
    };

    IAliyunVodPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IAliyunVodPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(int percent) {
            submitBufferingUpdate(percent, null);

            Log.e(TAG,"Buffer--"+percent);
        }
    };

    IAliyunVodPlayer.OnLoadingListener onLoadingListener = new IAliyunVodPlayer.OnLoadingListener() {
        @Override
        public void onLoadStart() {
            Log.e(TAG,"onLoadStart--");
        }

        @Override
        public void onLoadEnd() {
            Log.e(TAG,"onLoadEnd--");
        }

        @Override
        public void onLoadProgress(int i) {
            Log.e(TAG,"onLoadProgress--"+i);
        }
    };
    IAliyunVodPlayer.OnAutoPlayListener onAutoPlayListener = new IAliyunVodPlayer.OnAutoPlayListener() {
        @Override
        public void onAutoPlayStarted() {
            Log.e(TAG,"onAutoPlayStarted--");
        }
    };

    IAliyunVodPlayer.OnRePlayListener onRePlayListener = new IAliyunVodPlayer.OnRePlayListener() {
        @Override
        public void onReplaySuccess() {
            Log.e(TAG, "onReplaySuccess--");
            updateStatus(STATE_STARTED);
            submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_START, null);

            mTargetState = STATE_STARTED;
        }
    };

    IAliyunVodPlayer.OnPcmDataListener onPcmDataListener= new IAliyunVodPlayer.OnPcmDataListener() {
        @Override
        public void onPcmData(byte[] bytes, int i) {
            Log.e(TAG,"onPcmDataListener--"+bytes+"--"+i);
        }
    };



    private void resetListener(){
        if(mAliyunVodPlayer==null)
            return;
        mAliyunVodPlayer.setOnPreparedListener(null);
        mAliyunVodPlayer.setOnVideoSizeChangedListener(null);
        mAliyunVodPlayer.setOnCompletionListener(null);
        mAliyunVodPlayer.setOnErrorListener(null);
        mAliyunVodPlayer.setOnInfoListener(null);
        mAliyunVodPlayer.setOnBufferingUpdateListener(null);
    }
}
