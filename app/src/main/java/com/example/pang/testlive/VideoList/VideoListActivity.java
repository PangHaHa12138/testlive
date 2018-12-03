package com.example.pang.testlive.VideoList;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.pang.testlive.R;
import com.example.playerlibrary.assist.AssistPlayer;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.assist.ReceiverGroupManager;
import com.example.playerlibrary.cover.GestureCover;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.player.IPlayer;
import com.example.playerlibrary.provider.VideoBean;
import com.example.playerlibrary.receiver.OnReceiverEventListener;
import com.example.playerlibrary.receiver.ReceiverGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Description:
 * Created by PangHaHa on 18-8-29.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class VideoListActivity extends AppCompatActivity implements VideoListAdapter.OnListListener,
        OnReceiverEventListener, OnPlayerEventListener {

    private List<VideoBean> mItems = new ArrayList<>();
    private VideoListAdapter mAdapter;

    private RecyclerView mRecycler;
    private FrameLayout mContainer;

    private boolean toDetail;
    private boolean isLandScape;

    private ReceiverGroup mReceiverGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_videolist);

        mRecycler = findViewById(R.id.recycler);
        mContainer = findViewById(R.id.listPlayContainer);
        mRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AssistPlayer.get().addOnReceiverEventListener(this);
        AssistPlayer.get().addOnPlayerEventListener(this);

        mReceiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);

        mItems.addAll(DataUtils.getVideoList());
        mAdapter = new VideoListAdapter(getApplicationContext(), mRecycler, mItems);
        mAdapter.setOnListListener(VideoListActivity.this);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int first = manager.findFirstVisibleItemPosition();
                    int pos = manager.findFirstCompletelyVisibleItemPosition();
                    if (pos != mAdapter.getPlayPosition()) {
                        View view = mRecycler.getChildAt(pos - first);
                        RelativeLayout relativeLayout = view.findViewById(R.id.album_layout);
                        relativeLayout.performClick();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE;
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            attachFullScreen();
        }else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            attachList();
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

    @Override
    public void onBackPressed() {
        if(isLandScape){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    private void attachFullScreen(){
        mReceiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new GestureCover(this));
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        AssistPlayer.get().play(mContainer,null);
    }

    @Override
    public void onTitleClick(VideoBean item, int position) {
        toDetail = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        toDetail = false;
        mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        AssistPlayer.get().setReceiverGroup(mReceiverGroup);
        if(isLandScape){
            attachFullScreen();
        }else{
            attachList();
        }
        int state = AssistPlayer.get().getState();
        if(state!= IPlayer.STATE_PLAYBACK_COMPLETE){
            AssistPlayer.get().resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!toDetail){
            AssistPlayer.get().pause();
        }
    }

    private void attachList() {
        if(mAdapter!=null){
            mReceiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            mAdapter.getListPlayLogic().attachPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                onBackPressed();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                setRequestedOrientation(isLandScape?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                AssistPlayer.get().stop();
                break;
        }
    }
}
