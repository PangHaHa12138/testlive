package com.example.pang.testlive.VideoList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pang.testlive.R;
import com.example.playerlibrary.AlivcLiveRoom.DensityUtil;
import com.example.playerlibrary.AlivcLiveRoom.ScreenUtils;
import com.example.playerlibrary.provider.VideoBean;

import java.util.List;

/**
 * Description:
 * Created by PangHaHa on 18-8-29.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoItemHolder>{

    private Context mContext;
    private List<VideoBean> mItems;
    private OnListListener onListListener;
    private int mPlayPosition;
    private ListPlayLogic mListPlayLogic;

    private int mScreenUseW;

    public void setOnListListener(OnListListener onListListener) {
        this.onListListener = onListListener;
    }

    public interface OnListListener{
        void onTitleClick(VideoBean item, int position);
    }
    public ListPlayLogic getListPlayLogic(){
        return mListPlayLogic;
    }

    public VideoListAdapter(Context context, RecyclerView recyclerView, List<VideoBean> list){
        this.mContext = context;
        this.mItems = list;
        mScreenUseW = ScreenUtils.getWidth(context) - DensityUtil.dp2px(context, 6*2);
        mListPlayLogic = new ListPlayLogic(context, recyclerView, this);
        mPlayPosition=-1;
    }

    @NonNull
    @Override
    public VideoItemHolder onCreateViewHolder( ViewGroup viewGroup, int position) {
        return new VideoItemHolder(View.inflate(mContext, R.layout.item_video, null));
    }

    @Override
    public void onBindViewHolder(final VideoItemHolder holder, final int position) {
        ViewCompat.setElevation(holder.card, DensityUtil.dp2px(mContext, 3));
        updateWH(holder);
        final VideoBean item = getItem(position);
        Glide.with(mContext)
                .load(item.getCover())
                .into(holder.albumImage);
        holder.title.setText(item.getDisplayName());
        holder.layoutContainer.removeAllViews();
        holder.albumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayPosition = holder.getLayoutPosition();
                updatePlayPosition(position);
                mListPlayLogic.playPosition(position);

            }
        });
        if(onListListener!=null){
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePlayPosition(position);
                    onListListener.onTitleClick(item, position);
                }
            });
        }

    }

    public int getPlayPosition() {
        return mPlayPosition;
    }

    private void updateWH(VideoItemHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.layoutBox.getLayoutParams();
        layoutParams.width = mScreenUseW;
        layoutParams.height = mScreenUseW * 9/16;
        holder.layoutBox.setLayoutParams(layoutParams);
    }

    public void updatePlayPosition(int position){
        mListPlayLogic.updatePlayPosition(position);
    }

    public VideoBean getItem(int position){
        if(mItems==null)
            return null;
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        if(mItems==null)
            return 0;
        return mItems.size();
    }

    class VideoItemHolder extends RecyclerView.ViewHolder{

        View card;
        public FrameLayout layoutContainer;
        public RelativeLayout layoutBox;
        View albumLayout;
        ImageView albumImage;
        TextView title;

        public VideoItemHolder( View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
            layoutBox = itemView.findViewById(R.id.layBox);
            albumLayout = itemView.findViewById(R.id.album_layout);
            albumImage = itemView.findViewById(R.id.albumImage);
            title = itemView.findViewById(R.id.tv_title);

        }
    }
}
