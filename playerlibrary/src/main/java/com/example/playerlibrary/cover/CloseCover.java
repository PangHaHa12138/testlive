package com.example.playerlibrary.cover;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.playerlibrary.R;
import com.example.playerlibrary.assist.DataInter;
import com.example.playerlibrary.receiver.BaseCover;


public class CloseCover extends BaseCover {

    ImageView mCloseIcon;

    public CloseCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mCloseIcon = findViewById(R.id.iv_close);

        mCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_CLOSE, null);
            }
        });
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();

    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_close_cover, null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public int getCoverLevel() {
        return levelMedium(10);
    }
}
