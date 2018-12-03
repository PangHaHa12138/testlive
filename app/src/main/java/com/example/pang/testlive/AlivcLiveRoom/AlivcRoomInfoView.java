package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pang.testlive.R;

import java.lang.ref.WeakReference;

/**
 * @author Mulberry
 *         create on 2018/5/4.
 */
public class AlivcRoomInfoView extends RelativeLayout {

    private final String TAG = "AlivcRoomInfoView";

    private TextView mTvRoomId;
    private TextView mTvLikeCount;

    private ArchorAvatarView mArchorInfoView;
    private LiveRoomViewerListView mViewerListView;

    private OnItemClickListener mItemClickListener;
    private OnViewClickListener mOnArchorViewClickListener;
    private AlivcLiveUserInfo mArchorInfo;

    public AlivcRoomInfoView(Context context) {
        super(context);
        initView();
    }

    public AlivcRoomInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AlivcRoomInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.alivc_live_room_info_layout, this, true);
        mArchorInfoView = findViewById(R.id.ahrchor_info_view);
        mViewerListView = findViewById(R.id.viewer_info_listview);
        mTvRoomId = (TextView) findViewById(R.id.tv_room_id);
        mTvLikeCount = (TextView) findViewById(R.id.tv_like_count);

        mViewerListView.setViewCountUpdateListener(new IViewerCountListener() {
            @Override
            public void updateCount(int count) {
                updateUserCount(count);
            }
        });

    }


    public void setArchorInfo(AlivcLiveUserInfo userInfo) {
        if (userInfo == null) {
            Log.d(TAG, "archor info is null");
            return;
        }
        mArchorInfo = userInfo;
        mArchorInfoView.setData(userInfo.getNickName(), userInfo.getAvatar());
    }

    public void setViewerItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
        mViewerListView.setItemClickListener(mItemClickListener);
    }

    public void setArchorViewClickListener(OnViewClickListener listener) {
        mOnArchorViewClickListener = listener;
        mArchorInfoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnArchorViewClickListener.onClick();
            }
        });
    }

    public void setRoomInfo(AlivcLiveRoomInfo roomInfo) {
        if (roomInfo == null) {
            Log.d(TAG, "room info is null");
            return;
        }
        mTvRoomId.setText(String.format("ID:%s", roomInfo.getRoom_id()));
        mArchorInfoView.updateViewerCount(roomInfo.getRoom_viewer_count());

        if (roomInfo.getRoom_user_list() != null && roomInfo.getRoom_user_list().size() > 0) {
            mViewerListView.setData(roomInfo.getRoom_user_list());
        }

    }


    public void addUserInfo(AlivcLiveUserInfo alivcUserInfo) {
        if (alivcUserInfo == null) {
            return;
        }

        if (mViewerListView != null) {
            mViewerListView.addUser(alivcUserInfo);
        }
    }

    public void removeUserInfo(AlivcLiveUserInfo alivcUserInfo) {
        if (alivcUserInfo == null) {
            return;
        }

        if (mViewerListView != null) {
            mViewerListView.removeUser(alivcUserInfo);
        }
    }

    public void updateLikeCount(int likeCount) {
        String count = mTvLikeCount.getText().toString();
        if (!TextUtils.isEmpty(count)) {
            mTvLikeCount.setText((Integer.parseInt(count) + likeCount) + "");
        } else {
            mTvLikeCount.setText(likeCount + "");
        }
    }

    public void updateUserCount(int userCount) {
        mArchorInfoView.updateViewerCount((mArchorInfoView.getViewerCount() + userCount));
    }

    public AlivcLiveUserInfo getUserInfo(int postion) {
        return mViewerListView.getUser(postion);
    }

    private static final int UPDATE_LIKE_COUNT = 0;
    private static final int UPDATE_ROOM_INFP = 1;

    private static class UiHandler extends Handler {
        WeakReference<Context> contextWeakReference;

        public UiHandler(Looper looper, Context context) {
            super(looper);
            this.contextWeakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (contextWeakReference.get() != null) {
                if (msg.what == UPDATE_LIKE_COUNT) {
                } else if (msg.what == UPDATE_ROOM_INFP) {
                }
            }
        }
    }

}
