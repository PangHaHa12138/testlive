package com.example.pang.testlive;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.example.pang.testlive.AlivcLiveRoom.AlivcLiveRoomView;
import com.example.playerlibrary.AlivcLiveRoom.AliyunScreenMode;
import com.example.playerlibrary.AlivcLiveRoom.AliyunVodPlayerView;
import com.example.playerlibrary.AlivcLiveRoom.DensityUtil;
import com.example.playerlibrary.AlivcLiveRoom.ErrorInfo;
import com.example.playerlibrary.AlivcLiveRoom.PlayParameter;
import com.example.playerlibrary.AlivcLiveRoom.ScreenUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *  使用阿里云直播sdk demo
 *
 *  支持直播,点播 悬浮窗小窗口无缝衔接切换(一般拉流格式 rtmp m3u8等)
 *  全局视频小窗口 权限判断,高斯模糊背景(可以加深颜色)
 *  飘心效果，还有列表单例播放视频
 *  视频支持手势,进度,亮度,声音
 *
 * */

public class MainActivity extends AppCompatActivity {

    private AliyunVodPlayerView mAliyunVodPlayerView = null;
    private AlivcLiveRoomView mAlivcChatRoomView;
    private List<String> logStrs = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
    private String DEFAULT_URL = "rtmp://live1.sinalcs.com/lcsdemo/stream2?auth_key=1534534145-0-0-b5fa7f8a2cc594efb8fbd312f2029bfe";
    private String DEFAULT_URL2 = "http://player.alicdn.com/video/aliyunmedia.mp4";
    private String DEFAULT_URL3 = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
    private ErrorInfo currentError = ErrorInfo.Normal;
    private String TAG = "AliyunVodPlayer";
    private FrameLayout root_layout;//设置 高斯模糊
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAliRoomView();

        initAliyunPlayerView();

        initTaskTime();//测试倒计时

    }

    private void initTaskTime() {
        countDownTimer = new CountDownTimer(1000 * 60 * 60 * 24, 500) {
            @Override
            public void onTick(long millTime) {
                mAlivcChatRoomView.getLikeView().addPraise(1);
            }

            @Override
            public void onFinish() {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        };
        countDownTimer.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        ScreenRotateUtil.getInstance(this).stop();
        mAliyunVodPlayerView.onPause();
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        ScreenRotateUtil.getInstance(this).stop();
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        mAliyunVodPlayerView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ScreenRotateUtil.getInstance(this).start(this);
        mAliyunVodPlayerView.onResume();
        if (countDownTimer!=null){
            countDownTimer.start();
        }
    }

    private void initAliRoomView() {

        mAlivcChatRoomView = (AlivcLiveRoomView) findViewById(R.id.alivc_chat_room_view);
        root_layout = (FrameLayout) findViewById(R.id.root_layout);
        mAliyunVodPlayerView = mAlivcChatRoomView.getPlayerView();

    }


    private void initAliyunPlayerView() {
        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        PlayParameter.PLAY_PARAM_URL = DEFAULT_URL3;
//        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
//        mAliyunVodPlayerView.setPlayingCache(false, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);
        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Blue);
//        mAliyunVodPlayerView.setCirclePlay(true);//循环
        mAliyunVodPlayerView.setAutoPlay(true);
        mAliyunVodPlayerView.setControlBarCanShow(false);
        mAliyunVodPlayerView.setTitleBarCanShow(false);
        mAliyunVodPlayerView.setCoverResource(R.drawable.cover_fm);
        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        mAliyunVodPlayerView.setNetConnectedListener(new MyNetConnectedListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new MyFrameInfoListener(this));
        mAliyunVodPlayerView.setOnChangeQualityListener(new MyChangeQualityListener(this));
        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));
        mAliyunVodPlayerView.setmOnPlayerViewClickListener(new MyPlayViewClickListener());
        mAliyunVodPlayerView.setOrientationChangeListener(new MyOrientationChangeListener(this));
        mAliyunVodPlayerView.setOnUrlTimeExpiredListener(new MyOnUrlTimeExpiredListener(this));
        mAliyunVodPlayerView.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
            @Override
            public void onError(int i, int i1, String s) {
                mAliyunVodPlayerView.rePlay();
            }
        });
        mAliyunVodPlayerView.setOnTimeExpiredErrorListener(new IAliyunVodPlayer.OnTimeExpiredErrorListener() {
            @Override
            public void onTimeExpiredError() {
                mAliyunVodPlayerView.rePlay();
            }
        });
        mAliyunVodPlayerView.enableNativeLog();

        AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
        alsb.setSource(PlayParameter.PLAY_PARAM_URL);
        Uri uri = Uri.parse(PlayParameter.PLAY_PARAM_URL);
        if ("rtmp".equals(uri.getScheme())) {
            alsb.setTitle("");
        }
        AliyunLocalSource localSource = alsb.build();
        mAliyunVodPlayerView.setLiveSource(localSource);

        for (int i = 0; i < 10; i++) {
            mAlivcChatRoomView.onAddMsg(6,"黑怕不怕黑黑怕不怕黑黑怕不怕黑"+i);
        }
        mAlivcChatRoomView.getLikeCount(188);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.glass_bg);
        Drawable drawable = new BitmapDrawable(getResources(),GaussianBlur(this, bitmap, 25));
        root_layout.setBackground(drawable);

        mAlivcChatRoomView.setOnBackClickListener(new AlivcLiveRoomView.OnBackClickListener() {
            @Override
            public void onBack() {
                Toast.makeText(MainActivity.this,"关闭",Toast.LENGTH_SHORT).show();
                showLiveWindow();
                finish();

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mAlivcChatRoomView.toggleFullScreen(MainActivity.this);

        if (mAlivcChatRoomView.getScreenOrientation(getContext()) ==
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){ //横屏关闭飘心
            if (countDownTimer!=null){
                countDownTimer.cancel();
            }

        }else {
            if (countDownTimer!=null){
                countDownTimer.start();
            }
        }
    }

    private void showLiveWindow() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getContext())) {
                //没有悬浮窗权限,跳转申请
                Toast.makeText(getApplicationContext(), "请开启悬浮窗权限", Toast.LENGTH_LONG).show();
                //魅族不支持直接打开应用设置
                if (!MEIZU.isMeizuFlymeOS()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 0);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, 0);
                }
            } else {
                LiveUtils.initLive(MainActivity.this);
                finish();
            }
        } else {
            //6.0以下　只有MUI会修改权限
            if (MIUI.rom()) {
                if (PermissionUtils.hasPermission(getContext())) {
                    LiveUtils.initLive(MainActivity.this);
                    finish();
                } else {
                    MIUI.req(getContext());
                }
            } else {
                LiveUtils.initLive(MainActivity.this);
                finish();
            }
        }

    }

    private Context getContext() {

        return MainActivity.this;
    }

    /**高斯模糊　
     *
     * context 上下文　source 原图　 radius 模糊半径 0－25
     *
     * */
    public Bitmap GaussianBlur(Context context, Bitmap source, int radius){

        Bitmap inputBmp = source;
        //(1)
        RenderScript renderScript =  RenderScript.create(context);

        Log.i(TAG,"scale size:"+inputBmp.getWidth()+"*"+inputBmp.getHeight());

        // Allocate memory for Renderscript to work with
        //(2)
        final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
        final Allocation output = Allocation.createTyped(renderScript,input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        //(8)
        renderScript.destroy();

        return inputBmp;
    }

    private static class MyOnUrlTimeExpiredListener implements IAliyunVodPlayer.OnUrlTimeExpiredListener {
        WeakReference<MainActivity> weakReference;

        public MyOnUrlTimeExpiredListener(MainActivity activity) {
            weakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onUrlTimeExpired(String s, String s1) {
            MainActivity activity = weakReference.get();
            activity.onUrlTimeExpired(s, s1);
        }
    }

    private void onUrlTimeExpired(String oldVid, String oldQuality) {
        //requestVidSts();
//        AliyunVidSts vidSts = VidStsUtil.getVidSts(oldVid);
//        PlayParameter.PLAY_PARAM_VID = vidSts.getVid();
//        PlayParameter.PLAY_PARAM_AK_SECRE = vidSts.getAkSceret();
//        PlayParameter.PLAY_PARAM_AK_ID = vidSts.getAcId();
//        PlayParameter.PLAY_PARAM_SCU_TOKEN = vidSts.getSecurityToken();

    }


    private static class MyOrientationChangeListener implements AliyunVodPlayerView.OnOrientationChangeListener {

        private final WeakReference<MainActivity> weakReference;

        public MyOrientationChangeListener(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void orientationChange(boolean from, AliyunScreenMode currentMode) {
            MainActivity activity = weakReference.get();
            if ( currentMode == AliyunScreenMode.Full){ //全屏切换设置高度
                activity.setScreen(true);
            }else if ( currentMode == AliyunScreenMode.Small){
                activity.setScreen(false);
            }
//            activity.hideDownloadDialog(from, currentMode);
//            activity.hideShowMoreDialog(from, currentMode);
        }
    }

    private void setScreen(boolean isAll){
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        if (isAll) {
            params.height = ScreenUtils.getHeight(this);
            mAliyunVodPlayerView.setLayoutParams(params);
            mAlivcChatRoomView.setVisibility(View.GONE);
//            iv_close.setVisibility(View.GONE);
        } else {
            params.height = DensityUtil.dp2px(this, 200);
            params.gravity = Gravity.CENTER;
            mAliyunVodPlayerView.setLayoutParams(params);
            mAlivcChatRoomView.setVisibility(View.VISIBLE);
//            iv_close.setVisibility(View.VISIBLE);
        }
    }

    private void hideShowMoreDialog(boolean from, AliyunScreenMode currentMode) {
//        if (showMoreDialog != null) {
//            if (currentMode == AliyunScreenMode.Small) {
//                showMoreDialog.dismiss();
//                currentScreenMode = currentMode;
//            }
//        }
    }

    private void hideDownloadDialog(boolean from, AliyunScreenMode currentMode) {

//        if (downloadDialog != null) {
//            if (currentScreenMode != currentMode) {
//                downloadDialog.dismiss();
//                currentScreenMode = currentMode;
//            }
//        }
    }

    private class MyPlayViewClickListener implements AliyunVodPlayerView.OnPlayerViewClickListener {
        @Override
        public void onClick(AliyunScreenMode screenMode, AliyunVodPlayerView.PlayViewType viewType) {
            // 如果当前的Type是Download, 就显示Download对话框
//            if (viewType == AliyunVodPlayerView.PlayViewType.Download) {
//                showAddDownloadView(screenMode);
//            }
        }
    }



    private static class MyStoppedListener implements IAliyunVodPlayer.OnStoppedListener {

        private WeakReference<MainActivity> activityWeakReference;

        public MyStoppedListener(MainActivity skinActivity) {
            activityWeakReference = new WeakReference<MainActivity>(skinActivity);
        }

        @Override
        public void onStopped() {

            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    private void onStopped() {
        Toast.makeText(MainActivity.this.getApplicationContext(), R.string.log_play_stopped,
                Toast.LENGTH_SHORT).show();
    }


    private static class MyChangeQualityListener implements IAliyunVodPlayer.OnChangeQualityListener {

        private WeakReference<MainActivity> activityWeakReference;

        public MyChangeQualityListener(MainActivity skinActivity) {
            activityWeakReference = new WeakReference<MainActivity>(skinActivity);
        }

        @Override
        public void onChangeQualitySuccess(String finalQuality) {

            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualitySuccess(finalQuality);
            }
        }

        @Override
        public void onChangeQualityFail(int code, String msg) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onChangeQualityFail(code, msg);
            }
        }
    }

    private void onChangeQualitySuccess(String finalQuality) {
        logStrs.add(format.format(new Date()) + getString(R.string.log_change_quality_success));
        Toast.makeText(MainActivity.this.getApplicationContext(),
                getString(R.string.log_change_quality_success), Toast.LENGTH_SHORT).show();
    }

    void onChangeQualityFail(int code, String msg) {
        logStrs.add(format.format(new Date()) + getString(R.string.log_change_quality_fail) + " : " + msg);
        Toast.makeText(MainActivity.this.getApplicationContext(),
                getString(R.string.log_change_quality_fail), Toast.LENGTH_SHORT).show();
    }

    private static class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {

        private WeakReference<MainActivity> activityWeakReference;

        public MyCompletionListener(MainActivity skinActivity) {
            activityWeakReference = new WeakReference<MainActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {

            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompletion();
            }
        }
    }

    private void onCompletion() {
//        logStrs.add(format.format(new Date()) + getString(R.string.log_play_completion));
//        for (String log : logStrs) {
//            tvLogs.append(log + "\n");
//        }
//        Toast.makeText(MainActivity.this.getApplicationContext(), R.string.toast_play_compleion,
//                Toast.LENGTH_SHORT).show();
//
//        // 当前视频播放结束, 播放下一个视频
//        onNext();
    }

    private void onNext() {
//        if (currentError == ErrorInfo.UnConnectInternet) {
//            // 此处需要判断网络和播放类型
//            // 网络资源, 播放完自动波下一个, 无网状态提示ErrorTipsView
//            // 本地资源, 播放完需要重播, 显示Replay, 此处不需要处理
//            if ("vidsts".equals(PlayParameter.PLAY_PARAM_TYPE)) {
//                mAliyunVodPlayerView.showErrorTipView(4014, -1, "当前网络不可用");
//            }
//            return;
//        }
//
//        currentVideoPosition++;
//        if (currentVideoPosition >= alivcVideoInfos.size() - 1) {
//            //列表循环播放，如发现播放完成了从列表的第一个开始重新播放
//            currentVideoPosition = 0;
//        }
//
//        AlivcVideoInfo.Video video = alivcVideoInfos.get(currentVideoPosition);
//        if (video != null) {
//            changePlayVidSource(video.getVideoId(), video.getTitle());
//        }

    }


    private static class MyFrameInfoListener implements IAliyunVodPlayer.OnFirstFrameStartListener {

        private WeakReference<MainActivity> activityWeakReference;

        public MyFrameInfoListener(MainActivity skinActivity) {
            activityWeakReference = new WeakReference<MainActivity>(skinActivity);
        }

        @Override
        public void onFirstFrameStart() {

            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFirstFrameStart();
            }
        }
    }

    private void onFirstFrameStart() {
        Map<String, String> debugInfo = mAliyunVodPlayerView.getAllDebugInfo();
        long createPts = 0;
        if (debugInfo.get("create_player") != null) {
            String time = debugInfo.get("create_player");
            createPts = (long)Double.parseDouble(time);
            logStrs.add(format.format(new Date(createPts)) + getString(R.string.log_player_create_success));
        }
        if (debugInfo.get("open-url") != null) {
            String time = debugInfo.get("open-url");
            long openPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_open_url_success));
        }
        if (debugInfo.get("find-stream") != null) {
            String time = debugInfo.get("find-stream");
            long findPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(findPts)) + getString(R.string.log_request_stream_success));
        }
        if (debugInfo.get("open-stream") != null) {
            String time = debugInfo.get("open-stream");
            long openPts = (long)Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_start_open_stream));
        }
        logStrs.add(format.format(new Date()) + getString(R.string.log_first_frame_played));
        for (String log : logStrs) {
            Log.e(TAG,log);
        }
    }

    /**
     * 判断是否有网络的监听
     */
    private class MyNetConnectedListener implements AliyunVodPlayerView.NetConnectedListener {
        WeakReference<MainActivity> weakReference;

        public MyNetConnectedListener(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReNetConnected(boolean isReconnect) {
            MainActivity activity = weakReference.get();
            activity.onReNetConnected(isReconnect);
        }

        @Override
        public void onNetUnConnected() {
            MainActivity activity = weakReference.get();
            activity.onNetUnConnected();
        }
    }

    private void onNetUnConnected() {
        currentError = ErrorInfo.UnConnectInternet;
//        if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
//            downloadManager.stopDownloadMedias(aliyunDownloadMediaInfoList);
//        }
    }

    private void onReNetConnected(boolean isReconnect) {
        if (isReconnect) {
//            if (aliyunDownloadMediaInfoList != null && aliyunDownloadMediaInfoList.size() > 0) {
//                int unCompleteDownload = 0;
//                for (AliyunDownloadMediaInfo info : aliyunDownloadMediaInfoList) {
//                    //downloadManager.startDownloadMedia(info);
//                    if (info.getStatus() == AliyunDownloadMediaInfo.Status.Stop) {
//
//                        unCompleteDownload++;
//                    }
//                }
//
//                if (unCompleteDownload > 0) {
//                    Toast.makeText(this, "网络恢复, 请手动开启下载任务...", Toast.LENGTH_SHORT).show();
//                }
//            }
//            VidStsUtil.getVidSts(PlayParameter.PLAY_PARAM_VID, new MyStsListener(this));
        }
    }


    private static class MyPrepareListener implements IAliyunVodPlayer.OnPreparedListener {

        private WeakReference<MainActivity> activityWeakReference;

        public MyPrepareListener(MainActivity skinActivity) {
            activityWeakReference = new WeakReference<MainActivity>(skinActivity);
        }

        @Override
        public void onPrepared() {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }
    }

    private void onPrepared() {
        logStrs.add(format.format(new Date()) + getString(R.string.log_prepare_success));

        for (String log : logStrs) {
            Log.e(TAG,log);
        }

        Toast.makeText(MainActivity.this.getApplicationContext(), R.string.toast_prepare_success,
                Toast.LENGTH_SHORT).show();
    }
}
