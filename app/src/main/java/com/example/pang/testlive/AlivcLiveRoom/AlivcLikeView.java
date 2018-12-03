package com.example.pang.testlive.AlivcLiveRoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.pang.testlive.R;
import com.example.pang.testlive.heart.HeartHonorLayout;

import org.limlee.hipraiseanimationlib.HiPraiseAnimationView;
import org.limlee.hipraiseanimationlib.HiPraiseWithCallback;
import org.limlee.hipraiseanimationlib.OnDrawCallback;
import org.limlee.hipraiseanimationlib.base.IPraise;

import java.lang.ref.SoftReference;
import java.util.Random;
import java.util.concurrent.ExecutorService;

/**
 * 点赞交互View
 * @author Mulberry
 *         create on 2018/4/19.
 */

public class AlivcLikeView extends RelativeLayout {

    private final String TAG = AlivcLikeView.class.getName().toString();
    private static final int HEARDS[] = new int[]{
        R.drawable.heart,
        R.drawable.heart_border,
        R.drawable.heart_1,
        R.drawable.heart_2,
        R.drawable.heart_3,
        R.drawable.heart_4,
        R.drawable.heart_5,
        R.drawable.heart_6
    };
    private SparseArray<SoftReference<Bitmap>> mBitmapCacheArray = new SparseArray<>();
    private ExecutorService executorService = ThreadUtil.newDynamicSingleThreadedExecutor();

    private HiPraiseAnimationView mHiPraiseAnimationView;
    private HeartHonorLayout heartHonorLayout;
    private CountDownTimer countDownTimer;

    public AlivcLikeView(Context context) {
        super(context);
        initView();
    }

    public AlivcLikeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AlivcLikeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.alivc_room_like_view,this,true);
        mHiPraiseAnimationView = (HiPraiseAnimationView) view.findViewById(R.id.praise_layout);
        heartHonorLayout = (HeartHonorLayout)view.findViewById(R.id.heart_layout);
    }

    /**
     * add Praise
     */
    public void addPraiseWithCallback() {
        final IPraise hiPraiseWithCallback = new HiPraiseWithCallback(getHeartBitmap(),
            new OnDrawCallback() {
                @Override
                public void onFinish() {
                    Log.e(TAG, "Add Praise Done!");
                        onLikeClickListener.onLike();
                }
            });

        mHiPraiseAnimationView.addPraise(hiPraiseWithCallback);
    }

    /**
     * add many Praise
     * @param likeCount
     */
    public void addPraise(final int likeCount){
        final IPraise hiPraiseWithCallback = new HiPraiseWithCallback(getHeartBitmap(),
            new OnDrawCallback() {
                @Override
                public void onFinish() {
                    Log.e(TAG, "Add Praise Done!");
                }
            });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0;i< likeCount; i++){
                    mHiPraiseAnimationView.addPraise(hiPraiseWithCallback);
                }
            }
        });

        heartHonorLayout.addHeart(randomColor());

    }

    private Random mRandom = new Random();
    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    private Bitmap getHeartBitmap() {
        final int id = HEARDS[new Random().nextInt(HEARDS.length)];
        SoftReference<Bitmap> bitmapRef = mBitmapCacheArray.get(id);
        Bitmap retBitmap = null;
        if (null != bitmapRef) {
            retBitmap = bitmapRef.get();
        }
        if (null == retBitmap) {
            retBitmap = BitmapFactory.decodeResource(getResources(),
                id);
            mBitmapCacheArray.put(id, new SoftReference<>(retBitmap));
        }
        return retBitmap;
    }

    /**
     * /添加点赞动画之前要先开始启动绘制，如果没有，是添加不了任何的动画对象
     */
    public void onStart(){
        mHiPraiseAnimationView.start();
    }

    /**
     * 停止绘制点赞动画，停止后会clear掉整个画布和清空掉所有绘制的对象
     */
    public void onStop(){
        mHiPraiseAnimationView.stop();
        mHiPraiseAnimationView = null;
    }

    OnLikeClickListener onLikeClickListener;

    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
    }

    public interface OnLikeClickListener {
        /**
         * 喜欢
         */
        void onLike();
    }
}
