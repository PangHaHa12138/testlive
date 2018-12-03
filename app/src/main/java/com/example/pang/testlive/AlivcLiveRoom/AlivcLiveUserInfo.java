package com.example.pang.testlive.AlivcLiveRoom;

import java.io.Serializable;

/**
 * @author Mulberry
 *         create on 2018/5/9.
 */

public class AlivcLiveUserInfo implements Serializable {

    private String user_id;
    private String nick_name;
    private String avatar;
    private String room_id;
    private String push_url;
    private String play_flv;
    private String play_hls;
    private String play_rtmp;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getRoomId() {
        return room_id;
    }

    public void setRoomId(String roomId) {
        this.room_id = roomId;
    }

    public String getNickName() {
        return nick_name;
    }

    public void setNickName(String nickName) {
        this.nick_name = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPush_url() {
        return push_url;
    }

    public void setPush_url(String push_url) {
        this.push_url = push_url;
    }

    public String getPlay_flv() {
        return play_flv;
    }

    public void setPlay_flv(String play_flv) {
        this.play_flv = play_flv;
    }

    public String getPlay_hls() {
        return play_hls;
    }

    public void setPlay_hls(String play_hls) {
        this.play_hls = play_hls;
    }

    public String getPlay_rtmp() {
        return play_rtmp;
    }

    public void setPlay_rtmp(String play_rtmp) {
        this.play_rtmp = play_rtmp;
    }

    @Override
    public String toString() {
        return "AlivcLiveUserInfo{" +
                "userId='" + user_id + '\'' +
                ", nickName='" + nick_name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", room_id='" + room_id + '\'' +
                ", push_url='" + push_url + '\'' +
                ", play_flv='" + play_flv + '\'' +
                ", play_hls='" + play_hls + '\'' +
                ", play_rtmp='" + play_rtmp + '\'' +
                '}';
    }
}
