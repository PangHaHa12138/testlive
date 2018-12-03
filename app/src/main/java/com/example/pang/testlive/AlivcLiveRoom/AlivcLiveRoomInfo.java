package com.example.pang.testlive.AlivcLiveRoom;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mulberry
 *         create on 2018/5/9.
 */

public class AlivcLiveRoomInfo implements Serializable {

    private String room_id;
    private String room_title;
    private String room_screen_shot;
    private int room_viewer_count;
    private String streamer_id;
    private String streamer_name;
    private String play_flv;
    private String play_hls;
    private String play_rtmp;
    private List<AlivcLiveUserInfo> room_user_list;

    public List<AlivcLiveUserInfo> getRoom_user_list() {
        return room_user_list;
    }

    public void setRoom_user_list(List<AlivcLiveUserInfo> room_user_list) {
        this.room_user_list = room_user_list;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_title() {
        return room_title;
    }

    public void setRoom_title(String room_title) {
        this.room_title = room_title;
    }

    public String getRoom_screen_shot() {
        return room_screen_shot;
    }

    public void setRoom_screen_shot(String room_screen_shot) {
        this.room_screen_shot = room_screen_shot;
    }

    public int getRoom_viewer_count() {
        return room_viewer_count;
    }

    public void setRoom_viewer_count(int room_viewer_count) {
        this.room_viewer_count = room_viewer_count;
    }

    public String getStreamer_id() {
        return streamer_id;
    }

    public void setStreamer_id(String streamer_id) {
        this.streamer_id = streamer_id;
    }

    public String getStreamer_name() {
        return streamer_name;
    }

    public void setStreamer_name(String streamer_name) {
        this.streamer_name = streamer_name;
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
        return "AlivcLiveRoomInfo{" +
                "room_id='" + room_id + '\'' +
                ", room_title='" + room_title + '\'' +
                ", room_screen_shot='" + room_screen_shot + '\'' +
                ", room_viewer_count=" + room_viewer_count +
                ", streamer_id='" + streamer_id + '\'' +
                ", streamer_name='" + streamer_name + '\'' +
                ", play_flv='" + play_flv + '\'' +
                ", play_hls='" + play_hls + '\'' +
                ", play_rtmp='" + play_rtmp + '\'' +
                '}';
    }
}
