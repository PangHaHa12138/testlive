package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pang.testlive.R;

/**
 * @author zhangzhiquan
 * @date 2018/3/26
 */

public class AILPChatListRecyclerViewAdapter extends BaseChatListRecyclerViewAdapter<AlivcLiveMessageInfo, AILPChatListRecyclerViewAdapter.ChatViewHolder> implements View.OnClickListener {
    private Context mContext;


    public AILPChatListRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        final ChatViewHolder holder;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alivc_comment_list_item, parent, false);
        holder = new ChatViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        try {
            synchronized (this) {
                final AlivcLiveMessageInfo bean = getLiveCommentItem().get(position);
                SpannableString spanString = null;
                if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_CHAT.getMsgType()) {//设置名称颜色

                    spanString = new SpannableString(bean.getSendName() + ":  " + bean.getDataContent().toString());
                    spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.alivc_color_send_name)),
                            0, String.valueOf(bean.getSendName()).length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.tvComment.setText(spanString);
                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_LIKE.getMsgType()) {
                    // TODO: 2018/5/4

                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_ALLOWALLSENDMSG.getMsgType()) {
                    //允许所有用户发言
                    spanString = new SpannableString(mContext.getResources().getString(R.string.alivc_message_type_allow_all_user));
                    holder.tvComment.setText(spanString);
                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_ALLOWSENDMSG.getMsgType()) {
                    //允许用户发言
//                    if (bean.getUserId().equals(AlivcLiveUserManager.getInstance().getUserInfo(mContext).getUserId())) {
//                        spanString = new SpannableString(AlivcLiveUserManager.getInstance().getUserInfo(mContext).getNickName() + ":  " + mContext.getResources().getString(R.string.alivc_message_type_allow_user));
//                    }

//                    AlivcHttpManager.getInstance().getUserDetail(bean.getUserId(), new HttpEngine.OnResponseCallback<HttpResponse.User>() {
//                        @Override
//                        public void onResponse(boolean result, @Nullable String retmsg, @Nullable HttpResponse.User data) {
//                            if (result) {
//                                if (data != null && data.data != null) {
//                                    String nickName;
//                                    if (!TextUtils.isEmpty(data.data.getNickName())) {
//                                        nickName = data.data.getNickName();
//                                    } else {
//                                        nickName = data.data.getUserId();
//                                    }
//                                    SpannableString spanString = new SpannableString(nickName + mContext.getResources().getString(R.string.alivc_message_type_allow_user));
//                                    holder.tvComment.setText(spanString);
//                                }
//                            }
//                        }
//                    });
//                } else if (bean.getType() == AlivcMsgType.ALIVC_MESSAGE_TYPE_FORBIDALLSENDMSG.getMsgType()) {
//                    //所有用户被禁言
//                    spanString = new SpannableString(mContext.getResources().getString(R.string.alivc_message_type_forbid_all_user));
//                    holder.tvComment.setText(spanString);
//
//                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_FORBIDSENDMSG.getMsgType()) {
//                    AlivcHttpManager.getInstance().getUserDetail(bean.getUserId(), new HttpEngine.OnResponseCallback<HttpResponse.User>() {
//                        @Override
//                        public void onResponse(boolean result, @Nullable String retmsg, @Nullable HttpResponse.User data) {
//                            if (result) {
//                                if (data != null && data.data != null) {
//                                    String nickName;
//                                    if (!TextUtils.isEmpty(data.data.getNickName())) {
//                                        nickName = data.data.getNickName();
//                                    } else {
//                                        nickName = data.data.getUserId();
//                                    }
//                                    SpannableString spanString = new SpannableString(nickName + mContext.getResources().getString(R.string.alivc_message_type_forbid_user_other));
//                                    spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.alivc_color_send_name)), 0, nickName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                    spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.chat_newmsg_tip_color)), nickName.length(), spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                                    holder.tvComment.setText(spanString);
//                                }
//                            }
//                        }
//                    });

                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_LOGIN.getMsgType()) {
                    //加入房间
                    fetchKickOutInfo(bean.getUserId(), false);
                    spanString = new SpannableString(bean.getSendName() + mContext.getResources().getString(R.string.alivc_message_type_join_room));
                    holder.tvComment.setText(spanString);
                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_KICKOUT.getMsgType()) {
                    //踢出用户
                    bean.setKickout(true);
                    fetchKickOutInfo(bean.getUserId(), true);
                    spanString = new SpannableString(bean.getSendName() + mContext.getResources().getString(R.string.alivc_message_type_kick_out));
                    spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.alivc_color_send_name)), 0, bean.getSendName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.chat_newmsg_tip_color)), bean.getSendName().length(), spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tvComment.setText(spanString);
                } else if (bean.getType() == AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_LOGOUT_ROOM.getMsgType()) {
                    //离开房间
                    spanString = new SpannableString(bean.getSendName() + mContext.getResources().getString(R.string.alivc_message_type_leave_room));
                    holder.tvComment.setText(spanString);
                }
//                else if(bean.getType() == AlivcConstants.ALIVC_PUSHER_EVENT_RTMP_RECONNECT_START) {
//                    spanString = new SpannableString(bean.getDataContent());
//                    holder.tvComment.setText(spanString);
//                }
//                else if(bean.getType() == AlivcConstants.ALIVC_PUSHER_EVENT_RTMP_RECONNECT_SUCCESS) {
//                    spanString = new SpannableString(bean.getDataContent());
//                    holder.tvComment.setText(spanString);
//                }
//
//                if (bean.getType() == AlivcMsgType.ALIVC_MESSAGE_TYPE_CHAT.getMsgType() ||
//                        bean.getType() == AlivcMsgType.ALIVC_MESSAGE_TYPE_KICKOUT.getMsgType() ||
//                        bean.getType() == AlivcMsgType.ALIVC_MESSAGE_TYPE_LOGIN.getMsgType() ||
//                        bean.getType() == AlivcMsgType.ALIVC_MESSAGE_TYPE_LOGOUT_ROOM.getMsgType()) {
                    holder.tvComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bean.setKickout(checkKickout(bean.getUserId()));
                            mCellClickListener.onCellClick(bean);
                        }
                    });
//                } else {
//                    mCellClickListener.onCellClick(null);
//                }
            }
        } catch (Exception err) {
        }

    }

    private void fetchKickOutInfo(String userId, boolean b) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        for (int i = 0; i < getLiveCommentItem().size(); i++) {
            if (userId.equals(getLiveCommentItem().get(i).getUserId())) {
                getLiveCommentItem().get(i).setKickout(b);
            }
        }
    }

    private boolean checkKickout(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return false;
        }
        for (int i = 0; i < getLiveCommentItem().size(); i++) {
            if (userId.equals(getLiveCommentItem().get(i).getUserId())) {
                if (getLiveCommentItem().get(i).isKickout()) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public int getItemViewType(int position) {
        AlivcLiveMessageInfo bean = getLiveCommentItem().get(position);
        return bean.getType();
    }

    @Override
    public void onClick(View v) {

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;

        public ChatViewHolder(View itemView) {
            super(itemView);
            tvComment = (TextView) itemView.findViewById(R.id.tv_comment);
        }
    }

}
