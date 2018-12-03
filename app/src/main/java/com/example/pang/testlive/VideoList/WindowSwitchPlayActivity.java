package com.example.pang.testlive.VideoList;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.pang.testlive.R;
import com.example.playerlibrary.assist.AssistPlay;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.assist.OnAssistPlayEventHandler;
import com.example.playerlibrary.assist.ReceiverGroupManager;
import com.example.playerlibrary.assist.RelationAssist;
import com.example.playerlibrary.cover.CloseCover;
import com.example.playerlibrary.cover.GestureCover;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.receiver.ReceiverGroup;
import com.example.playerlibrary.window.FloatWindow;
import com.example.playerlibrary.window.FloatWindowParams;


public class WindowSwitchPlayActivity extends AppCompatActivity {

    private int mVideoContainerH;
    private FrameLayout mVideoContainer;
    private Button mBtnSwitchPlay;

    private RelationAssist mAssist;
    private ReceiverGroup mReceiverGroup;

    private FloatWindow mFloatWindow;

    private boolean isLandScape;
    private FrameLayout mWindowVideoContainer;

    private final int VIEW_INTENT_FULL_SCREEN = 1;
    private final int WINDOW_INTENT_FULL_SCREEN = 2;

    private int mWhoIntentFullScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_window_switch_play);

        mBtnSwitchPlay = findViewById(R.id.btn_switch_play);
        mVideoContainer = findViewById(R.id.videoContainer);

        mVideoContainer.post(new Runnable() {
            @Override
            public void run() {
                mVideoContainerH = mVideoContainer.getHeight();
            }
        });

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int width = (int) (widthPixels * 0.8f);

        int type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//8.0+
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            type =  WindowManager.LayoutParams.TYPE_PHONE;
        }

        mWindowVideoContainer = new FrameLayout(this);
        mFloatWindow = new FloatWindow(this, mWindowVideoContainer,
                                        new FloatWindowParams()
                                                .setWindowType(type)
                                                .setX(100)
                                                .setY(400)
                                                .setWidth(width)
                                                .setHeight(width*9/16));
        mFloatWindow.setBackgroundColor(Color.BLACK);

        mAssist = new RelationAssist(this);
        mAssist.getSuperContainer().setBackgroundColor(Color.BLACK);
        mAssist.setEventAssistHandler(eventHandler);

        mReceiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mAssist.setReceiverGroup(mReceiverGroup);

        changeMode(false);

        DataSource dataSource = new DataSource();
        dataSource.setData("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
        dataSource.setTitle("cctv1");

        mAssist.setDataSource(dataSource);
        mAssist.attachContainer(mVideoContainer);
        mAssist.play();

    }

    private void changeMode(boolean window){
        if(window){
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER, new CloseCover(this));
        }else{
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_CLOSE_COVER);
            mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new GestureCover(this));
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, !window);
    }

    private OnAssistPlayEventHandler eventHandler = new OnAssistPlayEventHandler(){
        @Override
        public void onAssistHandle(AssistPlay assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode){
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    onBackPressed();
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mAssist.stop();
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    if(isLandScape){
                        quitFullScreen();
                    }else{
                        mWhoIntentFullScreen =
                                mFloatWindow.isWindowShow()?
                                        WINDOW_INTENT_FULL_SCREEN:
                                        VIEW_INTENT_FULL_SCREEN;
                        enterFullScreen();
                    }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_CLOSE:
                    normalPlay();
                    break;
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        ViewGroup.LayoutParams params = mVideoContainer.getLayoutParams();
        if(newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE){
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = mVideoContainerH;
        }
        mVideoContainer.setLayoutParams(params);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

    private void enterFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(mWhoIntentFullScreen==WINDOW_INTENT_FULL_SCREEN){
            normalPlay();
        }
    }

    private void quitFullScreen(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(mWhoIntentFullScreen==WINDOW_INTENT_FULL_SCREEN){
            windowPlay();
        }
    }

    private void normalPlay(){
        mBtnSwitchPlay.setText("窗口播放");
        changeMode(false);
        mAssist.attachContainer(mVideoContainer);
        closeWindow();
    }

    public void switchWindowPlay(View view){
        if(mFloatWindow.isWindowShow()){
            normalPlay();
        }else{
            if(WindowPermissionCheck.checkPermission(this)){
                windowPlay();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WindowPermissionCheck.onActivityResult(this, requestCode, resultCode, data, null);
    }

    private void windowPlay() {
        if(!mFloatWindow.isWindowShow()){
            mBtnSwitchPlay.setText("页面播放");
            changeMode(true);
            mFloatWindow.setElevationShadow(20);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                mFloatWindow.setRoundRectShape(50);
            mFloatWindow.show();
            mAssist.attachContainer(mWindowVideoContainer);
        }
    }

    private void closeWindow(){
        if(mFloatWindow.isWindowShow()){
            mFloatWindow.close();
        }
    }

    @Override
    public void onBackPressed() {
        if(isLandScape){
            quitFullScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAssist.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAssist.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // closeWindow();
       // mAssist.destroy();
    }
}
