package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.pang.testlive.R;

/**
 * Created by zy on 2018/3/27.
 */

public class AlivcChatListView extends ChatListView<AlivcLiveMessageInfo> {
    private static final String TAG = "YKLChatListYoukuAdapter";

    public AlivcChatListView(@NonNull Context context) {
        this(context, null);
    }

    public AlivcChatListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AlivcChatListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View inflateLayout(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.ailp_chat_list, this);
    }

    @Override
    protected RecyclerView getChatRecyclerView() {
        return (RecyclerView) findViewById(R.id.portrait_chat_recyclerview);
    }

    @Override
    protected View getNewMessageTips() {
        return findViewById(R.id.portrait_chat_newmsg_tip);
    }

    @Override
    protected TextView getNewMessageTextView() {
        return (TextView) findViewById(R.id.portrait_newmsg_tip_text);
    }

    @Override
    protected BaseChatListRecyclerViewAdapter createAdapter(Context context) {
        return new AILPChatListRecyclerViewAdapter(context);
    }


}
