package com.example.pang.testlive;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

/**
 * @author Pinger
 * @since 2017/2/16 上午 11:31
 * <p>
 * 屏幕根据重力感应旋转的工具类
 * <p>
 * ================= API ================
 * start()         注册监听
 * stop()          注销监听
 * isLandscape()   当前屏幕朝向是否为横屏
 * setEffetSysSetting(true) 设置系统重力感应按钮是否生效
 * toggleRotate()  点击全屏按钮时调用，自动横竖屏
 * <p>
 * <p>
 * ============== 使用教程 ==============
 * 1，Activity的onResume()方法调用start()方法进行注册监听
 * 2，Activity的onPause()方法调用stop()方法注销监听
 * 3，点击全屏按钮时调用toggleRotate()自动切换横竖屏
 * 4，如果需要手机系统的横竖屏按钮生效则调用setEffetSysSetting(true)
 */

public class ScreenRotateUtil {
    private static final String TAG = ScreenRotateUtil.class.getSimpleName();

    private ScreenRotateUtil() {
    }

    private static ScreenRotateUtil mInstance;

    private Activity mActivity;
    private boolean isClickFullScreen;        // 记录全屏按钮的状态，默认false
    private boolean isOpenSensor = true;      // 是否打开传输，默认打开
    private boolean isLandscape = false;      // 默认是竖屏
    private boolean isChangeOrientation = true;  // 记录点击全屏后屏幕朝向是否改变，默认会自动切换

    private boolean isEffetSysSetting = false;   // 手机系统的重力感应设置是否生效，默认无效，想要生效改成true就好了

    private SensorManager sm;
    private OrientationSensorListener listener;
    private Sensor sensor;


    /**
     * 接收重力感应监听的结果，来改变屏幕朝向
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

            if (msg.what == 888) {
                int orientation = msg.arg1;

                /**
                 * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
                 */
                if (orientation > 45 && orientation < 135) {
                    if (null!=mActivity){
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        isLandscape = true;
                    }
                } else if (orientation > 135 && orientation < 225) {
                    if (null!=mActivity){
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        isLandscape = false;
                    }
                } else if (orientation > 225 && orientation < 315) {
                    if (null!=mActivity){
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        isLandscape = true;
                    }
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    if (null!=mActivity){
                        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        isLandscape = false;
                    }
                }
            }
        }
    };


    /**
     * 初始化，获取实例
     *
     * @param context
     * @return
     */
    public static ScreenRotateUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ScreenRotateUtil.class) {
                if (mInstance == null) {
                    mInstance = new ScreenRotateUtil(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化重力感应传感器
     *
     * @param context
     */
    private ScreenRotateUtil(Context context) {
        // 初始化重力感应器
        sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_GRAVITY);
        listener = new OrientationSensorListener(mHandler);
    }

    /**
     * 重力感应监听者
     */
    public class OrientationSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private Handler rotateHandler;

        public OrientationSensorListener(Handler handler) {
            rotateHandler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }


            /**
             * 获取手机系统的重力感应开关设置，这段代码看需求，不要就删除
             * screenchange = 1 表示开启，screenchange = 0 表示禁用
             * 要是禁用了就直接返回
             */
            if(isEffetSysSetting){
                try {
                    int isRotate = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                    // 如果用户禁用掉了重力感应就直接return
                    if (isRotate == 0) return;
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
            }


            // 只有点了按钮时才需要根据当前的状态来更新状态
            if (isClickFullScreen) {
                if (isLandscape && screenIsPortrait(orientation)) {           // 之前是横屏，并且当前是竖屏的状态
                    Log.d(TAG, "onSensorChanged: 横屏 ----> 竖屏");
                    updateState(false, false, true, true);
                } else if (!isLandscape && screenIsLandscape(orientation)) {  // 之前是竖屏，并且当前是横屏的状态
                    Log.d(TAG, "onSensorChanged: 竖屏 ----> 横屏");
                    updateState(true, false, true, true);
                } else if (isLandscape && screenIsLandscape(orientation)) {    // 之前是横屏，现在还是横屏的状态
                    Log.d(TAG, "onSensorChanged: 横屏 ----> 横屏");
                    isChangeOrientation = false;
                } else if (!isLandscape && screenIsPortrait(orientation)) {  // 之前是竖屏，现在还是竖屏的状态
                    Log.d(TAG, "onSensorChanged: 竖屏 ----> 竖屏");
                    isChangeOrientation = false;
                }
            }

            // 判断是否要进行中断信息传递
            if (!isOpenSensor) {
                return;
            }

            if (rotateHandler != null) {
                rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
            }
        }
    }

    /**
     * 更新状态
     *
     * @param isLandscape         横屏
     * @param isClickFullScreen   全屏点击
     * @param isOpenSensor        打开传输
     * @param isChangeOrientation 朝向改变
     */
    private void updateState(boolean isLandscape, boolean isClickFullScreen, boolean isOpenSensor, boolean isChangeOrientation) {
        this.isLandscape = isLandscape;
        this.isClickFullScreen = isClickFullScreen;
        this.isOpenSensor = isOpenSensor;
        this.isChangeOrientation = isChangeOrientation;
    }


    /**
     * 当前屏幕朝向是否横屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsLandscape(int orientation) {
        return ((orientation > 45 && orientation <= 135) || (orientation > 225 && orientation <= 315));
    }

    /**
     * 当前屏幕朝向是否竖屏
     *
     * @param orientation
     * @return
     */
    private boolean screenIsPortrait(int orientation) {
        return (((orientation > 315 && orientation <= 360) || (orientation >= 0 && orientation <= 45))
                || (orientation > 135 && orientation <= 225));
    }

    /**
     * 根据朝向来改变屏幕朝向
     *
     * @param isLandscape
     * @param isNeedChangeOrientation 是否需要改变判断值
     */
    private void changeOrientation(boolean isLandscape, boolean isNeedChangeOrientation) {
        if (isLandscape) {
            // 切换成竖屏
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (isNeedChangeOrientation) this.isLandscape = false;
        } else {
            // 切换成横屏
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            if (isNeedChangeOrientation) this.isLandscape = true;
        }
    }


    /**
     * 开启监听
     * 绑定切换横竖屏Activity的生命周期
     *
     * @param activity
     */
    public void start(Activity activity) {
        mActivity = activity;
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 注销监听
     */
    public void stop() {
        sm.unregisterListener(listener);
        mActivity = null;  // 防止内存泄漏
    }


    /**
     * 当前屏幕的朝向，是否是横屏
     *
     * @return
     */
    public boolean isLandscape() {
        return this.isLandscape;
    }


    /**
     * 设置系统横竖屏按钮是否生效，默认无效
     *
     * @param isEffet
     */
    public void setEffetSysSetting(boolean isEffet) {
        isEffetSysSetting = isEffet;
    }

    /**
     * 旋转的开关，全屏按钮点击时调用
     */
    public void toggleRotate() {

        /**
         * 先判断是否已经开启了重力感应，没开启就直接普通的切换横竖屏
         */
        if(isEffetSysSetting){
            try {
                int isRotate = Settings.System.getInt(mActivity.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

                // 如果用户禁用掉了重力感应就直接切换
                if (isRotate == 0) {
                    changeOrientation(isLandscape, true);
                    return;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 如果开启了重力i感应就需要修改状态
         */
        isOpenSensor = false;
        isClickFullScreen = true;
        if (isChangeOrientation) {
            changeOrientation(isLandscape, false);
        } else {
            isLandscape = !isLandscape;
            changeOrientation(isLandscape, false);
        }
    }

}