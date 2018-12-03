package com.example.pang.testlive.AlivcLiveRoom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.pang.testlive.R;
import com.example.pang.testlive.VideoList.TestActivity;
import com.example.pang.testlive.VideoList.TestListActivity;
import com.example.pang.testlive.VideoList.VideoListActivity;
import com.example.pang.testlive.VideoList.WindowSwitchPlayActivity;
import com.example.playerlibrary.AlivcLiveRoom.AliyunVodPlayerView;
import com.example.playerlibrary.AlivcLiveRoom.DensityUtil;
import com.example.playerlibrary.AlivcLiveRoom.GestureDialogManager;
import com.example.playerlibrary.AlivcLiveRoom.GestureView;
import com.example.playerlibrary.AlivcLiveRoom.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 整体RoomView类，通过这个类来添加各个分层的view，接收view点击的事件监听
 * 包含:commentView,likeview,controlview,
 *
 * @author Mulberry
 *         create on 2018/4/23.
 */

public class AlivcLiveRoomView extends FrameLayout {


    private static final String TAG = AlivcLiveRoomView.class.getSimpleName();

    private Context context;
    /**
     * 聊天列表界面
     */
    private AlivcChatListView commentListView;
    /**
     * 界面控制按钮界面
     */
    private AlivcControlView controlView;
    /**
     * 点赞动画界面
     */
    private AlivcLikeView likeView;
    /**
     * 输入框
     */
    private InputDialog alivcInputTextDialog;

    /**
     * 房间信息View:包含观众信息列表,点赞数
     */
    private AlivcRoomInfoView alivcRoomInfoView;

    private AlivcLiveUserInfo mArchorInfo;

    private AliyunVodPlayerView aliyunVodPlayerView;//播放器

    private ImageView back;//关闭

    //手势对话框控制
    private GestureDialogManager mGestureDialogManager;
    //手势操作view
    private GestureView mGestureView;

    private int currentVolume;
    private int currentScreenBrigtness;

    public interface OnBackClickListener{
        void onBack();
    }

    private OnBackClickListener onBackClickListener;

    public void setOnBackClickListener(OnBackClickListener b){
        this.onBackClickListener = b;
    }
    /**
     * Constructor.
     *
     * @param context the context
     */
    public AlivcLiveRoomView(Context context) {
        super(context);
        init(context);
    }

    /**
     * Constructor.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public AlivcLiveRoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * Constructor.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public AlivcLiveRoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public void init(Context context) {
        this.context = context;
        aliyunVodPlayerView = new AliyunVodPlayerView(context);
        aliyunVodPlayerView.initVideoView(true);

        FrameLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = ScreenUtils.getWidth(context) * 9/16;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = DensityUtil.dp2px(context,100);

        addView(aliyunVodPlayerView,params);

        initCommentView();//聊天列表
        initControlView();//输入框
        initLikeView();//点赞
        initAlivcRoomInfoView();//头部直播间信息
        getRoomInfo();//假数据
        initBackView(context);//关闭
        //初始化手势view
        initGestureView();
        //初始化手势对话框控制
        initGestureDialogManager();
//        //重力感应
//        ScreenRotateUtil.getInstance(getContext()).setEffetSysSetting(true);
    }

    /**
     * 初始化手势view
     */
    private void initGestureView() {
        mGestureView = new GestureView(getContext());
        aliyunVodPlayerView.addSubView(mGestureView);

        //设置手势监听
        mGestureView.setOnGestureListener(new GestureView.GestureListener() {

            @Override
            public void onHorizontalDistance(float downX, float nowX) {

            }

            @Override
            public void onLeftVerticalDistance(float downY, float nowY) {
                //左侧上下滑动调节亮度
                int changePercent = (int) ((nowY - downY) * 100 / getHeight());

                if (mGestureDialogManager != null) {
                    mGestureDialogManager.showBrightnessDialog(AlivcLiveRoomView.this);
                    int brightness = mGestureDialogManager.updateBrightnessDialog(changePercent);
                    aliyunVodPlayerView.setCurrentScreenBrigtness(brightness);
                }
            }

            @Override
            public void onRightVerticalDistance(float downY, float nowY) {
                //右侧上下滑动调节音量
                int changePercent = (int) ((nowY - downY) * 100 / getHeight());
                int volume = aliyunVodPlayerView.getCurrentVolume();

                if (mGestureDialogManager != null) {
                    mGestureDialogManager.showVolumeDialog(AlivcLiveRoomView.this, volume);
                    int targetVolume = mGestureDialogManager.updateVolumeDialog(changePercent);
                    currentVolume = targetVolume;
                    aliyunVodPlayerView.setCurrentVolume(targetVolume);//通过返回值改变音量
                }
            }

            @Override
            public void onGestureEnd() {
                //手势结束。
                //seek需要在结束时操作。
                if (mGestureDialogManager != null) {
                    mGestureDialogManager.dismissBrightnessDialog();
                    mGestureDialogManager.dismissVolumeDialog();
                }
            }

            @Override
            public void onSingleTap() {
                //单击事件，显示控制栏
                hideOrShow();
            }

            @Override
            public void onDoubleTap() {

            }
        });
    }

    /**
     * 初始化手势的控制类
     */
    private void initGestureDialogManager() {
        Context context = getContext();
        if (context instanceof Activity) {
            mGestureDialogManager = new GestureDialogManager((Activity) context);
        }
    }
    //全屏时显示或隐藏
    public void hideOrShow(){
        if (isScreenFull){
            if (commentListView.getVisibility() != VISIBLE) {
                back.setVisibility(VISIBLE);
                commentListView.setVisibility(VISIBLE);
                controlView.setVisibility(VISIBLE);
            } else {
                back.setVisibility(INVISIBLE);
                commentListView.setVisibility(INVISIBLE);
                controlView.setVisibility(INVISIBLE);
            }
        }

    }

    private void initBackView(Context context) {
        back = new ImageView(context);
        back.setImageResource(R.drawable.icon_close);
        FrameLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT|Gravity.TOP;
        params.topMargin = DensityUtil.dp2px(context,10);
        params.rightMargin = DensityUtil.dp2px(context,12);
        addView(back,params);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClickListener.onBack();
            }
        });
    }

    public AliyunVodPlayerView getPlayerView(){

        return aliyunVodPlayerView;
    }

    private void getRoomInfo() {
        mArchorInfo = new AlivcLiveUserInfo();
        AlivcLiveRoomInfo roomInfo = new AlivcLiveRoomInfo();

        mArchorInfo.setAvatar("https://www.sinaimg.cn/cj/licaishi/avatar/180/31481168873.jpg");
        mArchorInfo.setNickName("王大锤");
        mArchorInfo.setRoomId("666666666666");
        mArchorInfo.setUserId("8888888");

        roomInfo.setRoom_id("6666666666");
        roomInfo.setRoom_viewer_count(200);
        roomInfo.setStreamer_name("红红火火恍恍惚惚");
        roomInfo.setRoom_title("啦啦啦啦");
        roomInfo.setStreamer_id("9999");
        List<AlivcLiveUserInfo> room_user_list = new ArrayList<>();
        AlivcLiveUserInfo info = new AlivcLiveUserInfo();
        info.setUserId("000000000000");
        info.setRoomId("88888");
        info.setNickName("王大锤");
        info.setAvatar("https://www.sinaimg.cn/cj/licaishi/avatar/180/31481168873.jpg");
        for (int i = 0; i < 7; i++) {
            room_user_list.add(info);
        }
        roomInfo.setRoom_user_list(room_user_list);

        String streamerId = null;
        if (mArchorInfo != null) {
            LogUtils.d(TAG, "room detail mArchorInfo  = " + mArchorInfo.toString());
            alivcRoomInfoView.setArchorInfo(mArchorInfo);
            streamerId = mArchorInfo.getUserId();
        }
            LogUtils.d(TAG, "room detail roomInfo  = " + roomInfo.toString());
            if (roomInfo.getRoom_user_list() != null) {
                LogUtils.d(TAG, "room detail list = " + roomInfo.getRoom_user_list().size());
                for (int i = 0; i < roomInfo.getRoom_user_list().size(); i++) {
                    if (roomInfo.getRoom_user_list().get(i) != null
                            && roomInfo.getRoom_user_list().get(i).getUserId().equals(streamerId)) {
                        roomInfo.getRoom_user_list().remove(i);
                    }
                }
            }
            alivcRoomInfoView.setRoomInfo(roomInfo);

    }

    public void getLikeCount(final int count) {

        if (alivcRoomInfoView != null) {
            HandleUtils.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    alivcRoomInfoView.updateLikeCount(count);
                }
            });

        }
    }



    /**
     * addSubView
     * 添加子view到布局中
     *
     * @param view 子view
     */
    public void addSubView(View view) {
        //添加到布局中
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    /**
     * 界面控制View
     */
    private void initControlView() {
        controlView = new AlivcControlView(getContext().getApplicationContext(), false);
        controlView.showViewVisiblelyByRole();
        controlView.setOnControlClickListener(new AlivcControlView.OnControlClickListener() {
            @Override
            public void showMessageInPut() {

                alivcInputTextDialog = new InputDialog((Activity) getContext(), "1234567");
                alivcInputTextDialog.setmOnTextSendListener(new InputDialog.OnTextSendListener() {
                    @Override
                    public void onTextSend(String msg) {
                        //发送消息

                        //更新数据
                        onAddMsg(6,msg);

                    }
                });
                alivcInputTextDialog.show();
            }

            @Override
            public void onClickCamera() {

            }

            @Override
            public void onClickBeauty() {


            }

            @Override
            public void onClickFlashLight(boolean bool) {


            }

            @Override
            public void onClickMusicSelect(boolean bool) {
            }

            @Override
            public void onClickSilent(boolean bool) {
            }

            @Override
            public void onClickScreenRecord(boolean bool) {
            }

            @Override
            public void onClickleave() {
            }

            @Override
            public void onLike() {
                likeView.addPraiseWithCallback();
            }

            @Override
            public void onScreen() {
                Context context = getContext();
                if (context instanceof Activity){
                    toggleFullScreen(context);
                }

            }
        });
        controlView.setTag(true);
        addSubView(controlView);
    }
    //横竖屏切换
    public void toggleFullScreen(Context context){
        if (getScreenOrientation(context) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){//设为横屏
            isScreenFull = false;
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //如果是固定全屏，那么直接设置view的布局，宽高
            FrameLayout.LayoutParams params = (LayoutParams) aliyunVodPlayerView.getLayoutParams();
            params.height = ScreenUtils.getWidth(context) * 9/16;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = DensityUtil.dp2px(context,100);
            alivcRoomInfoView.setVisibility(VISIBLE);
        }else {
            isScreenFull = true;
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设为竖屏
            //如果是固定全屏，那么直接设置view的布局，宽高
            FrameLayout.LayoutParams params = (LayoutParams) aliyunVodPlayerView.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            alivcRoomInfoView.setVisibility(GONE);
        }
    }

    /**
     * 获取界面方向
     */
    public int getScreenOrientation(Context context) {
        int rotation = ((Activity)context).getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    //是否全屏
    private boolean isScreenFull = false;


    private void initAlivcRoomInfoView() {
        alivcRoomInfoView = new AlivcRoomInfoView(getContext());

        alivcRoomInfoView.setViewerItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
//                final AlivcLiveUserInfo userInfo = alivcRoomInfoView.getUserInfo(position);
//                if (userInfo != null) {
//                    showOperateDialog(userInfo, false);
//                }

                context.startActivity(new Intent(context, TestActivity.class));
            }
        });

        alivcRoomInfoView.setArchorViewClickListener(new OnViewClickListener() {
            @Override
            public void onClick() {
//                if (mArchorInfo != null) {
//                    showOperateDialog(mArchorInfo, false);
//                }
                context.startActivity(new Intent(context,TestListActivity.class));

            }
        });

        addSubView(alivcRoomInfoView);

    }

    private void initCommentView() {
        commentListView = new AlivcChatListView(getContext().getApplicationContext());
        commentListView.setOnCellClickListener(new OnCellClickListener<AlivcLiveMessageInfo>() {
            @Override
            public void onCellClick(AlivcLiveMessageInfo alivcLiveMessageInfo) {
//                final AlivcLiveUserInfo roomUserInfo = new AlivcLiveUserInfo();
//                roomUserInfo.setUserId(alivcLiveMessageInfo.getUserId());
//                roomUserInfo.setNickName(String.valueOf(alivcLiveMessageInfo.getSendName()));
//                roomUserInfo.setAvatar(String.valueOf(alivcLiveMessageInfo.getAvatar()));
//
//                showOperateDialog(roomUserInfo, alivcLiveMessageInfo.isKickout());

                context.startActivity(new Intent(context, WindowSwitchPlayActivity.class));

            }
        });
        addSubView(commentListView);
    }

    private void showOperateDialog(final AlivcLiveUserInfo userInfo, final boolean isKickout) {

        Toast.makeText(getContext(),"啦啦啦啦啦",Toast.LENGTH_SHORT).show();

    }





    public void onAddMsg(int event, String content) {
        AlivcLiveMessageInfo messageInfo = new AlivcLiveMessageInfo();
        messageInfo.setType(AlivcLiveMessageInfo.AlivcMsgType.ALIVC_MESSAGE_TYPE_CHAT.getMsgType());
        messageInfo.setDataContent(content);
        messageInfo.setAvatar("https://www.sinaimg.cn/cj/licaishi/avatar/180/31481168873.jpg");
        messageInfo.setSendName("王大锤");
        messageInfo.setUserId("6666666");
        commentListView.addMessageLocal(messageInfo);
    }

    /**
     * 界面控制View
     */

    private void initLikeView() {
        likeView = new AlivcLikeView(getContext().getApplicationContext());
        likeView.setOnLikeClickListener(new AlivcLikeView.OnLikeClickListener() {
            @Override
            public void onLike() {

                getLikeCount(1);

            }
        });
        addSubView(likeView);
        likeView.onStart();
    }

    public AlivcLikeView getLikeView(){

        return likeView;
    }


}