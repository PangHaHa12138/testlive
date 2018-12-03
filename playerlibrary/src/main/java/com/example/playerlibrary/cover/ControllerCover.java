package com.example.playerlibrary.cover;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.example.playerlibrary.R;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.entity.DataSource;
import com.example.playerlibrary.event.BundlePool;
import com.example.playerlibrary.event.EventKey;
import com.example.playerlibrary.event.OnPlayerEventListener;
import com.example.playerlibrary.log.PLog;
import com.example.playerlibrary.player.IPlayer;
import com.example.playerlibrary.player.OnTimerUpdateListener;
import com.example.playerlibrary.receiver.BaseCover;
import com.example.playerlibrary.receiver.IReceiverGroup;
import com.example.playerlibrary.touch.OnTouchGestureListener;
import com.example.playerlibrary.utils.SharedPreferencesUtils;
import com.example.playerlibrary.utils.TimeUtil;


/**
 * Created by Taurus on 2018/4/15.
 */

public class ControllerCover extends BaseCover implements OnTimerUpdateListener, OnTouchGestureListener {

    private final int MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101;

    View mTopContainer;
    View mBottomContainer;
    ImageView mBackIcon;
    TextView mTopTitle;
    ImageView mStateIcon;
    TextView mCurrTime;
    TextView mTotalTime;
    ImageView mSwitchScreen;
    ImageView mBtnvolume;
    SeekBar mSeekBar;
    LinearLayout mRestartRoot;
    TextView mRryClose;
    TextView mRryOpen;

    private int mBufferPercentage;

    private int mSeekProgress = -1;

    private boolean mTimerUpdateProgressEnable = true;
    private int PlayerState = 111;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_DELAY_HIDDEN_CONTROLLER:
                    PLog.d(getTag().toString(), "msg_delay_hidden...");
                    setControllerState(false);
                    break;
            }
        }
    };

    private boolean mGestureEnable = true;

    private String mTimeFormat;

    private boolean mControllerTopEnable;
    private ObjectAnimator mBottomAnimator;
    private ObjectAnimator mTopAnimator;
    private Context context;
    private AudioManager audioManager;
    private boolean isCloseVolume = false;//音量是否关闭
    private boolean showVol = false;//显示一键静音
    private boolean isNeedRestart =false;//显示一键播放

    private interface OnRestartListener{
        void restart();
    }

    public void setOnRestartListener(OnRestartListener listener){
        this.onRestartListener = listener;
    }

    private OnRestartListener onRestartListener;

    public ControllerCover(Context context,boolean isShowVol) {
        super(context);
        this.context = context;
        showVol = isShowVol;
        isNeedRestart = isShowVol;
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);

        mTopContainer = findViewById(R.id.cover_player_controller_top_container);
        mBottomContainer = findViewById(R.id.cover_player_controller_bottom_container);
        mBackIcon = findViewById(R.id.cover_player_controller_image_view_back_icon);
        mTopTitle = findViewById(R.id.cover_player_controller_text_view_video_title);
        mStateIcon = findViewById(R.id.cover_player_controller_image_view_play_state);
        mCurrTime = findViewById(R.id.cover_player_controller_text_view_curr_time);
        mTotalTime = findViewById(R.id.cover_player_controller_text_view_total_time);
        mSwitchScreen = findViewById(R.id.cover_player_controller_image_view_switch_screen);
        mSeekBar = findViewById(R.id.cover_player_controller_seek_bar);
        mBtnvolume = findViewById(R.id.cover_player_controller_image_view_volume);
        mRryOpen = findViewById(R.id.text_open_again);
        mRryClose = findViewById(R.id.text_close);
        mRestartRoot = findViewById(R.id.lcs_course_schedule);

        initVolume();
        setVolumeBtnShow(showVol);

        mRryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRestartRoot.setVisibility(View.GONE);
            }
        });

        mRryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestartListener.restart();
            }
        });

        mBtnvolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVolume();
            }
        });

        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_BACK, null);
            }
        });

        mSwitchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN, null);
            }
        });
        mStateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (PlayerState == OnPlayerEventListener.PLAYER_EVENT_ON_START
//                        ||PlayerState ==OnPlayerEventListener.PLAYER_EVENT_ON_RESUME ) {
//                    requestPause(null);
//                    mStateIcon.setSelected(true);
//                } else if (PlayerState == OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE
//                        || PlayerState == OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED
//                        || PlayerState == OnPlayerEventListener.PLAYER_EVENT_ON_STOP) {
//                    requestResume(null);
//                    mStateIcon.setSelected(false);
//                }

                boolean selected = mStateIcon.isSelected();
                if(selected){
                    requestResume(null);
                }else{
                    requestPause(null);
                }
                mStateIcon.setSelected(!selected);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        getGroupValue().registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener);

    }

    public void setVolumeBtnShow(boolean show){
        if (show){
            mBtnvolume.setVisibility(View.VISIBLE);
        }else {
            mBtnvolume.setVisibility(View.GONE);
        }
    }

    public void initVolume() {
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
            mBtnvolume.setImageResource(R.drawable.ic_volume_up_white);
            isCloseVolume = true;
        } else {
            mBtnvolume.setImageResource(R.drawable.ic_volume_off_white);
            isCloseVolume = false;
        }
    }

    private void setVolume(){

        if (isCloseVolume) {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            SharedPreferencesUtils.setParam(getContext(), "volume", currentVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 1);
            mBtnvolume.setImageResource(R.drawable.ic_volume_off_white);
            isCloseVolume = false;
        } else {
            int currentVolume = (int) SharedPreferencesUtils.getParam(getContext(), "volume", 0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 1);
            mBtnvolume.setImageResource(R.drawable.ic_volume_up_white);
            isCloseVolume = true;
        }
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        DataSource dataSource = getGroupValue().get(DataInter.Key.KEY_DATA_SOURCE);
        setTitle(dataSource);

        boolean topEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
        mControllerTopEnable = topEnable;
        if(!topEnable){
            setTopContainerState(false);
        }

        boolean screenSwitchEnable = getGroupValue().getBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, true);
        setScreenSwitchEnable(screenSwitchEnable);
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        mTopContainer.setVisibility(View.GONE);
        mBottomContainer.setVisibility(View.GONE);
        mStateIcon.setVisibility(View.GONE);
        removeDelayHiddenMessage();
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();

        cancelTopAnimation();
        cancelBottomAnimation();

        getGroupValue().unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener);
        removeDelayHiddenMessage();
        mHandler.removeCallbacks(mSeekEventRunnable);


    }

    private IReceiverGroup.OnGroupValueUpdateListener mOnGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    DataInter.Key.KEY_COMPLETE_SHOW,
                    DataInter.Key.KEY_TIMER_UPDATE_ENABLE,
                    DataInter.Key.KEY_DATA_SOURCE,
                    DataInter.Key.KEY_IS_LANDSCAPE,
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE};
        }

        @Override
        public void onValueUpdate(String key, Object value) {
            if(key.equals(DataInter.Key.KEY_COMPLETE_SHOW)){
                boolean show = (boolean) value;
                if(show){
                    setControllerState(false);
                }
                setGestureEnable(!show);
            }else if(key.equals(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE)){
                mControllerTopEnable = (boolean) value;
                if(!mControllerTopEnable){
                    setTopContainerState(false);
                }
            }else if(key.equals(DataInter.Key.KEY_IS_LANDSCAPE)){
                setSwitchScreenIcon((Boolean) value);
            }else if(key.equals(DataInter.Key.KEY_TIMER_UPDATE_ENABLE)){
                mTimerUpdateProgressEnable = (boolean) value;
            }else if(key.equals(DataInter.Key.KEY_DATA_SOURCE)){
                DataSource dataSource = (DataSource) value;
                setTitle(dataSource);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
                updateUI(progress, seekBar.getMax());
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendSeekEvent(seekBar.getProgress());
        }
    };

    private void sendSeekEvent(int progress){
        mTimerUpdateProgressEnable = false;
        mSeekProgress = progress;
        mHandler.removeCallbacks(mSeekEventRunnable);
        mHandler.postDelayed(mSeekEventRunnable, 300);
    }

    private Runnable mSeekEventRunnable = new Runnable() {
        @Override
        public void run() {
            if(mSeekProgress < 0)
                return;
            Bundle bundle = BundlePool.obtain();
            bundle.putInt(EventKey.INT_DATA, mSeekProgress);
            requestSeek(bundle);
        }
    };

    private void setTitle(DataSource dataSource){
        if(dataSource!=null){
            String title = dataSource.getTitle();
            if(!TextUtils.isEmpty(title)){
                setTitle(title);
                return;
            }
            String data = dataSource.getData();
            if(!TextUtils.isEmpty(data)){
                setTitle(data);
            }
        }
    }

    private void setTitle(String text){
        mTopTitle.setText(text);
    }

    private void setSwitchScreenIcon(boolean isFullScreen){
        mSwitchScreen.setImageResource(isFullScreen?R.drawable.icon_exit_full_screen:R.drawable.icon_full_screen);
    }

    private void setScreenSwitchEnable(boolean screenSwitchEnable) {
        mSwitchScreen.setVisibility(screenSwitchEnable?View.VISIBLE:View.GONE);
    }

    private void setGestureEnable(boolean gestureEnable) {
        this.mGestureEnable = gestureEnable;
    }

    private void cancelTopAnimation(){
        if(mTopAnimator!=null){
            mTopAnimator.cancel();
            mTopAnimator.removeAllListeners();
            mTopAnimator.removeAllUpdateListeners();
        }
    }

    private void setTopContainerState(final boolean state){
        if(mControllerTopEnable){
            mTopContainer.clearAnimation();
            cancelTopAnimation();
            mTopAnimator = ObjectAnimator.ofFloat(mTopContainer,
                            "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
            mTopAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    if(state){
                        mTopContainer.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if(!state){
                        mTopContainer.setVisibility(View.GONE);
                    }
                }
            });
            mTopAnimator.start();
        }else{
            mTopContainer.setVisibility(View.GONE);
        }
    }

    private void cancelBottomAnimation(){
        if(mBottomAnimator!=null){
            mBottomAnimator.cancel();
            mBottomAnimator.removeAllListeners();
            mBottomAnimator.removeAllUpdateListeners();
        }
    }

    private void setBottomContainerState(final boolean state){
        mBottomContainer.clearAnimation();
        cancelBottomAnimation();
        mBottomAnimator = ObjectAnimator.ofFloat(mBottomContainer,
                "alpha", state ? 0 : 1, state ? 1 : 0).setDuration(300);
        mBottomAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if(state){
                    mBottomContainer.setVisibility(View.VISIBLE);
                    mStateIcon.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!state){
                    mBottomContainer.setVisibility(View.GONE);
                    mStateIcon.setVisibility(View.GONE);
                }
            }
        });
        mBottomAnimator.start();
        if(state){
            PLog.d(getTag().toString(), "requestNotifyTimer...");
            requestNotifyTimer();
        }else{
            PLog.d(getTag().toString(), "requestStopTimer...");
            requestStopTimer();
        }
    }

    private void setControllerState(boolean state){
        if(state){
            sendDelayHiddenMessage();
        }else{
            removeDelayHiddenMessage();
        }
        setTopContainerState(state);
        setBottomContainerState(state);
    }

    private boolean isControllerShow(){
        return mBottomContainer.getVisibility()==View.VISIBLE;
    }

    private void toggleController(){
        if(isControllerShow()){
            setControllerState(false);
        }else{
            setControllerState(true);
        }
    }

    private void sendDelayHiddenMessage(){
        removeDelayHiddenMessage();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000);
    }

    private void removeDelayHiddenMessage(){
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER);
    }

    private void setCurrTime(int curr){
        mCurrTime.setText(TimeUtil.getTime(mTimeFormat, curr));
    }

    private void setTotalTime(int duration){
        mTotalTime.setText(TimeUtil.getTime(mTimeFormat, duration));
    }

    private void setSeekProgress(int curr, int duration){
        mSeekBar.setMax(duration);
        mSeekBar.setProgress(curr);
        float secondProgress = mBufferPercentage * 1.0f/100 * duration;
        setSecondProgress((int) secondProgress);
    }

    private void setSecondProgress(int secondProgress){
        mSeekBar.setSecondaryProgress(secondProgress);
    }

    @Override
    public void onTimerUpdate(int curr, int duration, int bufferPercentage) {
        if(!mTimerUpdateProgressEnable)
            return;
        if(mTimeFormat==null){
            mTimeFormat = TimeUtil.getFormat(duration);
        }
        mBufferPercentage = bufferPercentage;
        updateUI(curr, duration);
    }

    private void updateUI(int curr, int duration) {
        setSeekProgress(curr, duration);
        setCurrTime(curr);
        setTotalTime(duration);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

//        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_START){
//            PlayerState = eventCode;
//        }else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED){
//            PlayerState = eventCode;
//        }else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE){
//            PlayerState = eventCode;
//        }else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_RESUME){
//            PlayerState = eventCode;
//        }else if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_STOP){
//            PlayerState = eventCode;
//        }
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                mBufferPercentage = 0;
                mTimeFormat = null;
                updateUI(0, 0);
                DataSource data = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                getGroupValue().putObject(DataInter.Key.KEY_DATA_SOURCE, data);
                setTitle(data);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE:
                int status = bundle.getInt(EventKey.INT_DATA);
                if(status== IPlayer.STATE_PAUSED){
                    mStateIcon.setSelected(true);
                }else if(status==IPlayer.STATE_STARTED){
                    mStateIcon.setSelected(false);
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE:
                mTimerUpdateProgressEnable = true;
                break;
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK:
                if(bundle!=null){
                    int curr = bundle.getInt(EventKey.INT_ARG1);
                    int duration = bundle.getInt(EventKey.INT_ARG2);
                    updateUI(curr, duration);
                }
                break;
        }
        return null;
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_controller_cover, null);
    }

    @Override
    public int getCoverLevel() {
        return levelLow(1);
    }

    @Override
    public void onSingleTapUp(MotionEvent event) {
        if(!mGestureEnable)
            return;
        toggleController();
    }

    @Override
    public void onDoubleTap(MotionEvent event) {
    }

    @Override
    public void onDown(MotionEvent event) {
    }

    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(!mGestureEnable)
            return;
    }

    @Override
    public void onEndGesture() {
    }
}
