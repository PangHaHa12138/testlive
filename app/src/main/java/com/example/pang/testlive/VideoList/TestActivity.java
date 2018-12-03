package com.example.pang.testlive.VideoList;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pang.testlive.R;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.assist.OnVideoViewEventHandler;
import com.example.playerlibrary.assist.ReceiverGroupManager;
import com.example.playerlibrary.config.PlayerConfig;
import com.example.playerlibrary.cover.ControllerCover;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.provider.MonitorDataProvider;
import com.example.playerlibrary.receiver.IReceiver;
import com.example.playerlibrary.receiver.ReceiverGroup;
import com.example.playerlibrary.render.AspectRatio;
import com.example.playerlibrary.render.IRender;
import com.example.playerlibrary.widget.BaseVideoView;

/**
 * Description:
 * Created by PangHaHa on 18-9-6.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class TestActivity extends AppCompatActivity implements OnPlayerEventListener {

    private BaseVideoView mVideoView;

    private int margin;

    private boolean permissionSuccess;

    private int typeIndex;
    private ReceiverGroup mReceiverGroup;
    private boolean isLandscape;

    private long mDataSourceId;

    private boolean userPause;
    TextView testbut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.test_video_activity);

        margin = PUtil.dip2px(this,2);

        mVideoView = findViewById(R.id.videoView);
        testbut = findViewById(R.id.testbut);
        initPlay();
    }

    private void initPlay(){
        updateVideo(false);

        mVideoView.setOnPlayerEventListener(this);
        mVideoView.setEventHandler(mOnEventAssistHandler);
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this, null);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true);
        mVideoView.setReceiverGroup(mReceiverGroup);

        //设置数据提供者 MonitorDataProvider
        MonitorDataProvider dataProvider = new MonitorDataProvider();
        dataProvider.setTestData(DataUtils.getVideoList());
        mVideoView.setDataProvider(dataProvider);
        mVideoView.setDataSource(generatorDataSource(mDataSourceId));
        mVideoView.start();

        // If you want to start play at a specified time,
        // please set this method.
        //mVideoView.start(30*1000);
        testbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = mVideoView.getCurrentPosition();
                int d = mVideoView.getDuration();
                Log.e("getCurrentPosition---",i+"");
                Log.e("getDuration---",d+"");
            }
        });

    }

    private DataSource generatorDataSource(long id){
        DataSource dataSource = new DataSource();
        dataSource.setId(id);
        return dataSource;
    }

    public void setRenderSurfaceView(View view){
        mVideoView.setRenderType(IRender.RENDER_TYPE_SURFACE_VIEW);
    }

    public void setRenderTextureView(View view){
        mVideoView.setRenderType(IRender.RENDER_TYPE_TEXTURE_VIEW);
    }

    public void onStyleSetRoundRect(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mVideoView.setRoundRectShape(PUtil.dip2px(this,25));
        }else{
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStyleSetOvalRect(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mVideoView.setOvalRectShape();
        }else{
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShapeStyleReset(View view){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mVideoView.clearShapeStyle();
        }else{
            Toast.makeText(this, "not support", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAspect16_9(View view){
        mVideoView.setAspectRatio(AspectRatio.AspectRatio_16_9);
    }

    public void onAspect4_3(View view){
        mVideoView.setAspectRatio(AspectRatio.AspectRatio_4_3);
    }

    public void onAspectFill(View view){
        mVideoView.setAspectRatio(AspectRatio.AspectRatio_FILL_PARENT);
    }

    public void onAspectFit(View view){
        mVideoView.setAspectRatio(AspectRatio.AspectRatio_FIT_PARENT);
    }

    public void onAspectOrigin(View view){
        mVideoView.setAspectRatio(AspectRatio.AspectRatio_ORIGIN);
    }

    public void onDecoderChangeMediaPlayer(View view){
        int curr = mVideoView.getCurrentPosition();
        if(mVideoView.switchDecoder(PlayerConfig.DEFAULT_PLAN_ID)){
            replay(curr);
        }
    }


    private void replay(int msc){
        mVideoView.setDataSource(generatorDataSource(mDataSourceId));
        mVideoView.start(msc);
    }

    public void removeControllerCover(View view){
        mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER);
        Toast.makeText(this, "已移除", Toast.LENGTH_SHORT).show();
    }

    public void addControllerCover(View view){
        IReceiver receiver = mReceiverGroup.getReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER);
        if(receiver==null){
            mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, new ControllerCover(this,true));
            Toast.makeText(this, "已添加", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mVideoView.isInPlaybackState()){
            mVideoView.pause();
        }else{
            mVideoView.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mVideoView.isInPlaybackState()){
            if(!userPause)
                mVideoView.resume();
        }else{
            mVideoView.rePlay(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    @Override
    public void onBackPressed() {
        if(isLandscape){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            isLandscape = true;
            updateVideo(true);
        }else{
            isLandscape = false;
            updateVideo(false);
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    }

    private OnVideoViewEventHandler mOnEventAssistHandler = new OnVideoViewEventHandler(){
        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode){
                case DataInter.Event.CODE_REQUEST_PAUSE:
                    userPause = true;
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    if(isLandscape){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }else{
                        finish();
                    }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_NEXT:
                    mDataSourceId++;
                    mVideoView.setDataSource(generatorDataSource(mDataSourceId));
                    mVideoView.start();
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    setRequestedOrientation(isLandscape ?
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
            }
        }
    };

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:

                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:

                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_RESUME:
                userPause = false;
                break;
        }
    }

    private void updateVideo(boolean landscape){
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mVideoView.getLayoutParams();
        if(landscape){
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
        }else{
            layoutParams.width = PUtil.getScreenW(this) - (margin*2);
            layoutParams.height = layoutParams.width * 9/16;
            layoutParams.setMargins(margin, margin, margin, margin);
        }
        mVideoView.setLayoutParams(layoutParams);
    }
}
