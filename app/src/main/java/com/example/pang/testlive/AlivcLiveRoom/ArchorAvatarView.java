package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pang.testlive.R;

/**
 * Created by Akira on 2018/5/30.
 */

public class ArchorAvatarView extends FrameLayout {

    private final String TAG = "ArchorAvatarView";

    private Context mContext;

    private TextView mTvArchorName, mTvViewerCount;
    private ImageView mIvArchorAvatar;

    public ArchorAvatarView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public ArchorAvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public ArchorAvatarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.archor_avatar_info, this);
        mIvArchorAvatar = findViewById(R.id.archor_avatar);
        mTvArchorName = findViewById(R.id.archor_name);
        mTvViewerCount = findViewById(R.id.room_viwer_count);
    }

    public void setData(String name, String avatarUrl) {
        mTvArchorName.setText(name);
        ImageUtils.loadCircleImage(getContext(), avatarUrl, mIvArchorAvatar);
    }

    public void updateViewerCount(final int count) {
        HandleUtils.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                String num = NumberUtils.formatPeopleNum(getContext(), count);
                if (num.contains("-")) {
                    mTvViewerCount.setText("0");
                    LogUtils.d(TAG, "live room viewer count = " + 1);
                } else {
                    LogUtils.d(TAG, "live room viewer count = " + num);
                    mTvViewerCount.setText(num);
                }
            }
        });
    }

    public int getViewerCount() {
        String count = mTvViewerCount.getText().toString();
        if (!TextUtils.isEmpty(count)) {
            return Integer.parseInt(count);
        }
        return 0;
    }
}
