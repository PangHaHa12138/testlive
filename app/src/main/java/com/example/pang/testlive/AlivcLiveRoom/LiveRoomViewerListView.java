package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.example.pang.testlive.R;

import java.util.List;

/**
 * Created by Akira on 2018/5/30.
 */

public class LiveRoomViewerListView extends FrameLayout {

    protected static final int ADD_USER = 0x01;
    protected static final int REMOVE_USER = 0x02;
    protected static final int USER_UPDATE = 0x03;

    /**
     * 当队列积攒过多时，如果超过这个阀值就更新UI
     */
    private static final int MESSAGE_DELAY_TIME = 150;

    private Context mContext;

    private RecyclerView mRecyclerView;

    private RoomViewerAdapter mAdapter;

    private OnItemClickListener mOnItemClickListener;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            AlivcLiveUserInfo user = (AlivcLiveUserInfo) msg.obj;
            switch (msg.what) {
                case ADD_USER:
                    updateUser(user, true);
                    break;
                case REMOVE_USER:
                    updateUser(user, false);
                    break;
                default:
                    break;
            }
        }
    };

    private long updateLastTime = 0;

    private void updateUser(final AlivcLiveUserInfo user, final boolean isAdd) {
        if (null == user) {
            return;
        }

        if (mAdapter != null) {
            long currentTime = SystemClock.uptimeMillis();
            if (currentTime - updateLastTime < MESSAGE_DELAY_TIME) {
                currentTime = updateLastTime + MESSAGE_DELAY_TIME;
            }

            updateLastTime = currentTime;

            mHandler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    if (isAdd) {
                        mAdapter.addUser(user);
                        mRecyclerView.smoothScrollToPosition(0);
                    } else {
                        mAdapter.removeUser(user);
                    }
                }
            }, currentTime + MESSAGE_DELAY_TIME);
        }

    }

    public LiveRoomViewerListView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public LiveRoomViewerListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public LiveRoomViewerListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        mAdapter.setItemClickListener(mOnItemClickListener);
    }

    public void setViewCountUpdateListener(IViewerCountListener listener) {
        mAdapter.setViewCountUpdateListener(listener);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_viewer_list, this);

        mRecyclerView = findViewById(R.id.viewer_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RoomViewerAdapter(mContext, mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
    }

    public void setData(List<AlivcLiveUserInfo> data) {
        mAdapter.addData(data);
    }

    private long lastAddTime = 0;

    public void addUser(AlivcLiveUserInfo user) {

        long current = SystemClock.uptimeMillis();
        if (current - lastAddTime < MESSAGE_DELAY_TIME) {
            current = lastAddTime + MESSAGE_DELAY_TIME;
        }
        lastAddTime = current;

        Message msg = mHandler.obtainMessage();
        msg.what = ADD_USER;
        msg.obj = user;
        mHandler.sendMessageAtTime(msg, current);
    }

    public boolean containUser(AlivcLiveUserInfo user) {
        boolean hasUser = false;
        if (mAdapter != null && mAdapter.getData().size() > 0) {
            for (AlivcLiveUserInfo userInfo : mAdapter.getData()) {
                if (user.getUserId().equals(userInfo.getUserId())) {
                    hasUser = true;
                }
            }
        }
        return hasUser;
    }

    private long lastRemoveTime = 0;

    public void removeUser(AlivcLiveUserInfo user) {
        if (mAdapter.getData().size() == 0) {
            return;
        }


        long current = SystemClock.uptimeMillis();
        if (current - lastRemoveTime < MESSAGE_DELAY_TIME) {
            current = lastRemoveTime + MESSAGE_DELAY_TIME;
        }
        lastRemoveTime = current;

        Message msg = mHandler.obtainMessage();
        msg.what = REMOVE_USER;
        msg.obj = user;
        mHandler.sendMessageAtTime(msg, current);

    }

    public AlivcLiveUserInfo getUser(int position) {
        return mAdapter.getUser(position);
    }
}
