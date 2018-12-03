package com.example.pang.testlive.AlivcLiveRoom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pang.testlive.R;


/**
 * Created by liuli on 2018/1/14.
 */

public class InputDialog extends Dialog implements View.OnClickListener {
    private final String mUserId;
    private int MAX_CHAT_INPUT_LENGTH = 600;
    private static final int MSG_SEND = 0x0001;
    private static final int MSG_NO_INTERNET = 0x0002;
    private static final int MSG_OVER_MAX = 0x0003;
    private static final int MSG_SHOW_KEYBOARD = 0x0004;

    private InputMethodManager mInputMethodManager;
    private EditText mEditText;
    private String mWillSendMsg;
    private Toast mToast;
    private Activity mActivity;
    private TextView mSendBtn;

    private Handler mWeakHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEND: {
                    if (mWillSendMsg.isEmpty() || mWillSendMsg.trim().isEmpty()) {
                        showTextToast("请输入聊天内容");
                        return false;
                    }
                    //根据服务端的配置而决定启用消息方式
                    sendMessage(mWillSendMsg);
                    break;
                }
                case MSG_NO_INTERNET:
                    showTextToast("当前无网络连接");
                    break;
                case MSG_OVER_MAX:
                    showTextToast("聊天字数超过限制");
                    break;
                case MSG_SHOW_KEYBOARD:
                    if (mEditText != null) {
                        mEditText.requestFocus();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public InputDialog(Activity activity, String userId) {
        super(activity, R.style.customDialog);
        mUserId=userId;
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        mWillSendMsg = "";
        this.mActivity = activity;
    }


    private void showTextToast(String msg) {
        if (mToast == null) {
            if (mActivity != null) {
                mToast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
            }
        } else {
            mToast.setText(msg);
        }
        if (mToast != null) {
            mToast.show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        setWindow();
    }


    private void setWindow() {
        Window window = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        window.setLayout(dm.widthPixels, getWindow().getAttributes().height);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.dimAmount = 0.0f;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alivc_layout_input);
        mSendBtn = findViewById(R.id.quizzes_send_btn);
        mEditText = findViewById(R.id.et_comment_input);
        mEditText.requestFocus();
        mWeakHandler.sendEmptyMessageDelayed(MSG_SHOW_KEYBOARD, 500);
//        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE ) {
//                    preSendMessage();
//                    return true;
//                }
//                return false;
//            }
//        });


        mSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.quizzes_send_btn) {
            preSendMessage();
        }
    }

    /**
     * 发送前判断字符长度 网络状态...
     */
    private void preSendMessage() {
        Message msg = Message.obtain();
        if (!Util.isNetworkAvailable(getContext().getApplicationContext())) {
            msg.what = MSG_NO_INTERNET;
            mWeakHandler.sendMessage(msg);
            return;
        }

        if (MAX_CHAT_INPUT_LENGTH < Util.getSpaceCount(mEditText.getText().toString())) {
            msg.what = MSG_OVER_MAX;
            mWeakHandler.sendMessage(msg);
            return;
        }
        mWillSendMsg = mEditText.getText().toString();
        msg.what = MSG_SEND;
        mWeakHandler.sendMessage(msg);
    }

    @Override
    public void dismiss() {
        if (mInputMethodManager != null) {
            mInputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        super.dismiss();
    }

    public void setmOnTextSendListener(OnTextSendListener onTextSendListener) {
        this.mOnTextSendListener = onTextSendListener;
    }

    private OnTextSendListener mOnTextSendListener;

    public interface OnTextSendListener {
        void onTextSend(String msg);
    }

    /**
     * 对外提供的发送接口
     *
     * @param messageText
     */
    public void sendMessage(String messageText) {

        if (mOnTextSendListener != null) {
            mOnTextSendListener.onTextSend(messageText);
            mEditText.setText("");
            dismiss();
        }
    }


}
