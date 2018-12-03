package com.example.pang.testlive.AlivcLiveRoom;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author zhangzhiquan
 * @date 2018/3/26
 */

abstract public class BaseChatListRecyclerViewAdapter<T,H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    protected static  int MAX_CHAT_LIST_LENGTH = 100;
    protected static  int MAX_CHAT_CACHE_LENGTH = 100;

    protected RecyclerView mRecyclerView;
    protected OnCellClickListener<T> mCellClickListener;
    private List<T> mLiveCommentItem = new ArrayList<>();
    private List<T> mCacheList = new ArrayList<>();
    private List<T> mChatMessage = new ArrayList<>();


    public void setLimit(int limit){
        synchronized (this){
            MAX_CHAT_LIST_LENGTH = MAX_CHAT_CACHE_LENGTH = limit;
        }
    }

    public void setOnCellClickListener(OnCellClickListener listener){
        mCellClickListener = listener;
    }
    protected List<T> getLiveCommentItem(){
        return mLiveCommentItem;
    }
    public void notifyAddItem(T msgInfo) {
        synchronized (this) {
            mChatMessage.add(msgInfo);
        }
    }

    public void notifyUpdateList(){
        synchronized (this) {
//            if (isSlideToBottom()) {
                int start = mLiveCommentItem.size();
                mLiveCommentItem.addAll(mChatMessage);
                if(mLiveCommentItem.size() != 0){
                    notifyItemRangeInserted(start,mLiveCommentItem.size() - start);
                }
                if (mLiveCommentItem.size() > MAX_CHAT_CACHE_LENGTH) {
                    int offset = mLiveCommentItem.size() - MAX_CHAT_CACHE_LENGTH;
                    for (int i = 0; i < offset; i++) {
                        mLiveCommentItem.remove(0);
                        notifyRemoveItem(0);
                    }
                }
                scrollToEnd();
//            } else {
//                if (mCacheList.size() < MAX_CHAT_CACHE_LENGTH) {
//                    mCacheList.addAll(mChatMessage);
//                }
//            }
            mChatMessage.clear();
        }
    }

    public void addCacheData() {
        try {
            synchronized (this) {
                if (mRecyclerView != null) {
                    if (mCacheList.size() == 0) {
                        return;
                    }
                    int len = mCacheList.size();
                    for (int i = 0; i < len; i++) {
                        T item = mCacheList.get(i);
                        if (mLiveCommentItem.size() > getMaxChatListLength()) {
                            mLiveCommentItem.remove(0);
                        }
                        mLiveCommentItem.add(item);
                    }
                    mCacheList.clear();
                    notifyDataSetChanged();
                    scrollToEnd();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取聊天列表最大长度，子类可以复写
     * @return
     */
    protected int getMaxChatListLength(){
        return MAX_CHAT_LIST_LENGTH;
    }

    /**
     * 获取缓存列表最大长度，子类可以复写
     * @return
     */
    protected int getMaxChatCacheLength(){
        return MAX_CHAT_CACHE_LENGTH;
    }

    public void refreshData() {
        try {
            synchronized (this) {
                notifyDataSetChanged();
                scrollToEnd();
            }
        } catch (Exception e) {

        }
    }

    public void clearData() {
        synchronized (this){
            mLiveCommentItem.clear();
            notifyDataSetChanged();
        }
    }

    public void scrollToEnd() {
        try {
            synchronized (this) {
                if (mRecyclerView != null) {
                    mRecyclerView.smoothScrollToPosition(mLiveCommentItem.size());
                }
            }
        } catch (Exception e) {

        }
    }

    public int getNewCount() {
        if (mCacheList != null) {
            return mCacheList.size();
        }
        return 1;
    }

    private void notifyRemoveItem(int pos) {
        if (isSlideToBottom()) {
            notifyItemRemoved(pos);
        }
    }

    public boolean isSlideToBottom() {
        if (mRecyclerView == null){
            return false;
        }
        if (mRecyclerView.computeVerticalScrollExtent() + mRecyclerView.computeVerticalScrollOffset()
                >= mRecyclerView.computeVerticalScrollRange()){
            return true;
        }
        return false;
    }

    public int getChatMessageSize() {
        return mChatMessage == null ? 0 : mChatMessage.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        synchronized (this) {
            if (mLiveCommentItem == null){
                return 0;
            }
            return mLiveCommentItem.size();
        }
    }
}
