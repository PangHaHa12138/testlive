package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pang.testlive.R;


/**
 * 相机控制View类:控制摄像头的切换，闪光灯的切换等
 *
 * @author Mulberry
 * create on 2018/4/19.
 */

public class AlivcControlView extends RelativeLayout implements OnClickListener {

    private ImageView btnMessageInput;
    private ImageView btnFlash;
    private ImageView btnSwitchCamera;
    private ImageView btnBeauty;
    private ImageView btnAudioControl;
    private ImageView btnSilent;
    private ImageView btnLike;
    private ImageView btn_screen;
    private TextView btChat;
    private boolean isback;
    private View line;

    public AlivcControlView(Context context, boolean isback) {
        super(context);
        initView(isback);
    }

    public AlivcControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AlivcControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){
        initView(false);
    }

    private void initView(boolean isback) {
        View controlView = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.alivc_control_bar_view_layout,
                this, true);
        btnMessageInput = (ImageView) controlView.findViewById(R.id.btn_message_input);
        btChat = (TextView) controlView.findViewById(R.id.tv_chat);
        btnFlash = (ImageView) controlView.findViewById(R.id.flash_btn);
        btnSwitchCamera = (ImageView) controlView.findViewById(R.id.switch_camera);
        btnBeauty = (ImageView) controlView.findViewById(R.id.beauty_btn);
        btnAudioControl = (ImageView) controlView.findViewById(R.id.btn_audio_control);
        btnSilent = (ImageView) controlView.findViewById(R.id.btn_silent);
        btnLike = (ImageView) controlView.findViewById(R.id.btn_like);
        btn_screen = (ImageView) controlView.findViewById(R.id.btn_screen);
        line =  controlView.findViewById(R.id.line);

        btnMessageInput.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnSwitchCamera.setOnClickListener(this);
        btnBeauty.setOnClickListener(this);
        btnAudioControl.setOnClickListener(this);
        btnSilent.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btChat.setOnClickListener(this);
        btn_screen.setOnClickListener(this);

        if (isback) {
            btnFlash.setActivated(false);
            btnFlash.setImageResource(R.drawable.icon_flash_off);
            btnFlash.setAlpha(1f);
            btnFlash.setClickable(true);
            btnSwitchCamera.setActivated(true);
        } else {
            btnFlash.setActivated(false);
            btnFlash.setImageResource(R.drawable.icon_flash_off);
            btnFlash.setAlpha(0.7f);
            btnFlash.setClickable(false);
            btnSwitchCamera.setActivated(false);
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_message_input || i == R.id.tv_chat) {
            onControlClickListener.showMessageInPut();
        } else if (i == R.id.flash_btn) {
            btnFlash.setActivated(!btnFlash.isActivated());
            btnFlash.setImageResource(btnFlash.isActivated() ? R.drawable.icon_flash : R.drawable.icon_flash_off);
            onControlClickListener.onClickFlashLight(v.isActivated());
        } else if (i == R.id.switch_camera) {
            btnSwitchCamera.setActivated(!btnSwitchCamera.isActivated());
            onControlClickListener.onClickCamera();
            btnFlash.setImageResource(R.drawable.icon_flash_off);
            if (!btnSwitchCamera.isActivated()) {//前
                btnFlash.setActivated(false);
                btnFlash.setAlpha(0.7f);
                btnFlash.setClickable(false);
                onControlClickListener.onClickFlashLight(false);
            } else {
                btnFlash.setAlpha(1f);
                btnFlash.setClickable(true);
            }


        } else if (i == R.id.beauty_btn) {

            onControlClickListener.onClickBeauty();
        } else if (i == R.id.btn_audio_control) {
            onControlClickListener.onClickMusicSelect(v.isActivated());
        } else if (i == R.id.btn_silent) {
            if (btnSilent.isActivated()) {
                btnSilent.setActivated(false);
                btnSilent.setImageResource(R.drawable.icon_mic);
            } else {
                btnSilent.setActivated(true);
                btnSilent.setImageResource(R.drawable.icon_mic_off);
            }
            onControlClickListener.onClickSilent(v.isActivated());
        } else if (i == R.id.btn_like) {
            onControlClickListener.onLike();
        }else if (i == R.id.btn_screen){
            if (btn_screen.isActivated()) {
                btn_screen.setActivated(false);
                btn_screen.setImageResource(R.drawable.course_fullscrence_icon_defa);
                btChat.setVisibility(VISIBLE);
                btnLike.setVisibility(VISIBLE);
                line.setVisibility(VISIBLE);
            } else {
                btn_screen.setActivated(true);
                btnLike.setVisibility(GONE);
                btChat.setVisibility(INVISIBLE);
                line.setVisibility(GONE);
                btn_screen.setImageResource(R.drawable.course_fullscrence_icon_hen);
            }
            onControlClickListener.onScreen();
        }
    }

    public void showViewVisiblelyByRole() {

            btnFlash.setVisibility(View.GONE);
            btnSwitchCamera.setVisibility(View.GONE);
            btnBeauty.setVisibility(View.GONE);
            btnAudioControl.setVisibility(View.GONE);
            btnSilent.setVisibility(View.GONE);
            btnMessageInput.setVisibility(View.GONE);
            btnLike.setVisibility(View.VISIBLE);
            btChat.setVisibility(VISIBLE);



    }

    private OnControlClickListener onControlClickListener;

    public void setOnControlClickListener(
            OnControlClickListener onControlClickListener) {
        this.onControlClickListener = onControlClickListener;
    }

    public interface OnControlClickListener {

        /**
         * 评论
         */
        void showMessageInPut();

        /**
         * 切换摄像头
         */
        void onClickCamera();

        /**
         * 美颜开关
         */
        void onClickBeauty();


        /**
         * 闪光灯
         *
         * @param bool 参数待细化
         */
        void onClickFlashLight(boolean bool);

        /**
         * 音乐选择按钮
         *
         * @param bool
         */
        void onClickMusicSelect(boolean bool);

        /**
         * 静音
         *
         * @param bool
         */
        void onClickSilent(boolean bool);


        /**
         * 录屏
         *
         * @param bool
         */
        void onClickScreenRecord(boolean bool);

        /**
         * 退出
         */
        void onClickleave();

        /**
         * 喜欢
         */
        void onLike();
        /**
         * 横竖屏
         */
        void onScreen();
    }
}

