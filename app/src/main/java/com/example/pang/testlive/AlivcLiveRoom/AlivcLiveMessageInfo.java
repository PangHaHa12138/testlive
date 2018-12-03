package com.example.pang.testlive.AlivcLiveRoom;

import java.io.Serializable;

/**
 * @author Mulberry
 *         create on 2018/5/10.
 */

public class AlivcLiveMessageInfo implements Serializable {

    public enum AlivcMsgType{

        /**
         * 加入房间
         */
        ALIVC_MESSAGE_TYPE_LOGIN(0),

        /**
         * 允许发言
         */
        ALIVC_MESSAGE_TYPE_ALLOWSENDMSG(1),

        /**
         * 禁止发言
         */
        ALIVC_MESSAGE_TYPE_FORBIDSENDMSG(2),

        /**
         * 允许所有用户发言
         */
        ALIVC_MESSAGE_TYPE_ALLOWALLSENDMSG(3),

        /**
         * 禁止所有用户发言
         */
        ALIVC_MESSAGE_TYPE_FORBIDALLSENDMSG(4),

        /**
         * 踢人消息
         */
        ALIVC_MESSAGE_TYPE_KICKOUT(5),

        /**
         * 普通消息
         */
        ALIVC_MESSAGE_TYPE_CHAT(6),

        /**
         * 礼物消息
         */
        ALIVC_MESSAGE_TYPE_SENDGIFT(7),

        /**
         * 点赞消息
         */
        ALIVC_MESSAGE_TYPE_LIKE(8),

        /**
         * 离开房间
         */
        ALIVC_MESSAGE_TYPE_LOGOUT_ROOM(9),

        /**
         * 上麦通知
         */
        ALIVC_MESSAGE_UP_MIC(10);

        private int msgType;

        AlivcMsgType(int msgType) {
            this.msgType = msgType;
        }

        public int getMsgType() {
            return msgType;
        }
    }


    private String userId;
    private String sendName;

    private String avatar;
    private String dataContent;
    private int  type;

    public boolean isKickout() {
        return isKickout;
    }

    public void setKickout(boolean kickout) {
        isKickout = kickout;
    }

    private boolean isKickout = false;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getDataContent() {
        return dataContent;
    }

    public void setDataContent(String dataContent) {
        this.dataContent = dataContent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
