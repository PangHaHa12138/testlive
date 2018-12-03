package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;



/**
 * @author zhangzhiquan
 * @date 2018/3/27
 */

abstract public class ChatListView<T> extends FrameLayout implements View.OnClickListener {
    protected static final int CHAT_MSG = 0x01;
    protected static final int CHAT_MSG_LOCAL = 0x02;
    protected static final int CHAT_UPDATE = 0x03;

    /**
     * 当队列积攒过多时，如果超过这个阀值就更新UI
     */
    private static final int MESSAGE_DELAY_TIME = 150;
    /**
     * 是否使用本地回显
     */
    private boolean useLocal = false;
    private View mNewMessageTips;
    private TextView mNewMessageTextView;
    private RecyclerView mChatRecyclerView;
    private BaseChatListRecyclerViewAdapter mAdapter;
    protected OnCellClickListener mCellClickListener;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            T message = (T) msg.obj;
            switch (msg.what) {
                case CHAT_MSG:
                    updateChatData(message, false);
                    break;
                case CHAT_MSG_LOCAL:
                    updateChatData(message, true);
                    break;
                case CHAT_UPDATE:
                    mAdapter.notifyUpdateList();
                    //todo  hide function
//                    if (mAdapter.isSlideToBottom() || mAdapter.getNewCount() == 0) {
//                        hideNewMsgLayout();
//                    } else {
//                        //暂时屏蔽
//                        showNewMsgLayout();
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    public ChatListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout(context);
        mChatRecyclerView = getChatRecyclerView();
        mNewMessageTips = getNewMessageTips();
        mNewMessageTextView = getNewMessageTextView();
        mAdapter = createAdapter(context);
        mChatRecyclerView.setAdapter(mAdapter);
        mChatRecyclerView.setLayoutManager(getLayoutManager(context));
        mChatRecyclerView.addOnScrollListener(new OnScrollListener());
        mNewMessageTips.setOnClickListener(this);
        hideNewMsgLayout();
    }

    public BaseChatListRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }


    public View getView() {
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v == mNewMessageTips) {
            addCacheData();
            hideNewMsgLayout();
        }
    }

    public void refreshData() {
        if (mAdapter != null) {
            mAdapter.refreshData();
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    /**
     * 填充布局文件
     *
     * @param context
     * @return
     */
    abstract protected View inflateLayout(Context context);

    /**
     * 获取聊天区RecyclerView
     *
     * @return
     */
    abstract protected RecyclerView getChatRecyclerView();

    /**
     * 获取底部聊天提醒UI
     *
     * @return
     */
    abstract protected View getNewMessageTips();

    /**
     * 获取底部聊天提醒TextView
     *
     * @return
     */
    abstract protected TextView getNewMessageTextView();

    /**
     * 判断是否需要过滤掉本地回显的消息
     *
     * @param message
     * @return
     */
    protected boolean isLocalRepetition(T message) {
        return false;
    }

    /**
     * 创建适配器
     *
     * @param context
     * @return
     */
    abstract protected BaseChatListRecyclerViewAdapter createAdapter(Context context);

    private long lastTime = 0;

    public void addMessage(T message) {
        if (message == null) {
            return;
        }
        long current = SystemClock.uptimeMillis();
        if (current - lastTime < MESSAGE_DELAY_TIME) {
            current = lastTime + MESSAGE_DELAY_TIME;
        }
        lastTime = current;
        Message msg = mHandler.obtainMessage();
        msg.what = CHAT_MSG;
        msg.obj = message;
        mHandler.sendMessageAtTime(msg, current);

    }

    public void addMessageLocal(T message) {
        if (message == null) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = CHAT_MSG_LOCAL;
        msg.obj = message;
        mHandler.sendMessage(msg);
    }

    private long updateLastTime = 0;

    protected void updateChatData(T message, boolean isLocal) {
        if (message == null) {
            return;
        }
        if (!isLocal && isUseLocal() && isLocalRepetition(message)) {
            return;
        }
        if (mAdapter != null) {
            mAdapter.notifyAddItem(message);

            long currentTime = SystemClock.uptimeMillis();
            if (currentTime - updateLastTime < MESSAGE_DELAY_TIME) {
                currentTime = updateLastTime + MESSAGE_DELAY_TIME;
            }

//            if (mHandler.hasMessages(CHAT_UPDATE)) {
//                mHandler.removeMessages(CHAT_UPDATE);
//            }
            updateLastTime = currentTime;
            mHandler.sendEmptyMessageAtTime(CHAT_UPDATE, currentTime + MESSAGE_DELAY_TIME);
        }
    }

    private void hideNewMsgLayout() {
        if (mNewMessageTips != null && mNewMessageTips.getVisibility() == View.VISIBLE) {
            mNewMessageTips.setVisibility(View.GONE);
        }
    }

    protected void showNewMsgLayout() {
        if (mNewMessageTips != null && mNewMessageTips.getVisibility() == View.GONE) {
            mNewMessageTips.setVisibility(View.VISIBLE);
        }

        //更新新条数数量
        int count;
        if (mAdapter != null && mNewMessageTextView != null) {
            count = mAdapter.getNewCount();
            setNewMessageText(mNewMessageTextView, count);
        }
    }

    private void addCacheData() {
        if (mChatRecyclerView != null) {
            mChatRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.addCacheData();
                    }
                }
            });
        }
    }

    public void setLimitSize(String limitSize) {
        try {
            int limit = Integer.parseInt(limitSize);
            mAdapter.setLimit(limit);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }


    public void setOnCellClickListener(OnCellClickListener listener) {
        mCellClickListener = listener;
        mAdapter.setOnCellClickListener(mCellClickListener);
    }

    protected void setNewMessageText(TextView text, int count) {
        if (text != null) {
            text.setText(count + "条新消息");
        }
    }

    public void turnOnLocal() {
        this.useLocal = true;
    }

    public void turnOffLocal() {
        this.useLocal = false;
    }

    public boolean isUseLocal() {
        return useLocal;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        int nNewState = -2;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            nNewState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mAdapter != null && mAdapter.isSlideToBottom()) {
                hideNewMsgLayout();
                addCacheData();
            }
        }
    }
}
