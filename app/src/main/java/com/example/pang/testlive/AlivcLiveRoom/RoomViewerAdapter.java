package com.example.pang.testlive.AlivcLiveRoom;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.pang.testlive.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Akira on 2018/5/30.
 */

public class RoomViewerAdapter extends RecyclerView.Adapter {

    private final int MAX_NUM = 30;

    private List<AlivcLiveUserInfo> mInfos = new ArrayList<>();

    private Context mContext;

    private OnItemClickListener mListener;
    private IViewerCountListener mViewerCountListener;

    public RoomViewerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public RoomViewerAdapter(Context mContext, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mListener = listener;
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void addData(List<AlivcLiveUserInfo> data) {
        mInfos.clear();
        mInfos.addAll(data);
        notifyDataSetChanged();
    }

    public void setViewCountUpdateListener(IViewerCountListener listener) {
        mViewerCountListener = listener;
    }

    public void addUser(AlivcLiveUserInfo user) {
        synchronized (this) {
            mViewerCountListener.updateCount(1);

            if (getData().size() >= MAX_NUM) {
                return;
            }
            String uid = user.getUserId();
            boolean hasRemove = false;
            Iterator<AlivcLiveUserInfo> iterator = mInfos.iterator();
            while (iterator.hasNext()) {
                AlivcLiveUserInfo userInfo = iterator.next();
                if (uid.equals(userInfo.getUserId())) {
                    iterator.remove();
                    hasRemove = true;
                }
            }
            mInfos.add(0, user);
            notifyItemRangeInserted(0, 1);
            if (!hasRemove) {
                notifyItemRangeChanged(0, mInfos.size());
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public void removeUser(AlivcLiveUserInfo user) {
        synchronized (this) {
            mViewerCountListener.updateCount(-1);
            if (getData().size() == 0) {
                return;
            }
            if (user != null) {
                String uid = user.getUserId();
                for (int i = 0; i < mInfos.size(); i++) {
                    if (uid.equals(mInfos.get(i).getUserId())) {
                        if (i < mInfos.size()) {
                            mInfos.remove(mInfos.get(i));
                            notifyItemRangeRemoved(i, 1);
                            notifyItemRangeChanged(i, mInfos.size() - 1);
                        }
                    }
                }
            }
        }
    }

    public AlivcLiveUserInfo getUser(int position) {
        if (position < mInfos.size()) {
            return mInfos.get(position);
        }
        return null;
    }

    public List<AlivcLiveUserInfo> getData() {
        return mInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_viewer_list, parent, false);
        ViewerInfoHolder holder = new ViewerInfoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mInfos != null && mInfos.size() > 0) {
            AlivcLiveUserInfo userInfo = mInfos.get(position);
            if (holder instanceof ViewerInfoHolder) {
                ViewerInfoHolder viewerHolder = (ViewerInfoHolder) holder;
                if (userInfo != null) {
                    ImageUtils.loadCircleImage(mContext, userInfo.getAvatar(), viewerHolder.imAvatar);

                    viewerHolder.imClick.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.onClick(position);
                            }
                        }
                    });
                }
            }

        }
    }

    @Override
    public int getItemCount() {
        return mInfos == null ? 0 : mInfos.size();
    }

    class ViewerInfoHolder extends RecyclerView.ViewHolder {
        ImageView imAvatar;
        ImageView imClick;

        public ViewerInfoHolder(View itemView) {
            super(itemView);
            imAvatar = itemView.findViewById(R.id.viewer_avatar);
            imClick = itemView.findViewById(R.id.click_view);
        }
    }
}
