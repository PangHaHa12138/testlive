package com.example.pang.testlive;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.VcPlayerLog;
import com.example.playerlibrary.AlivcPlayer.AlivcPlayer;
import com.example.playerlibrary.config.PlayerConfig;
import com.example.playerlibrary.config.PlayerLibrary;
import com.example.playerlibrary.entity.DecoderPlan;

/**
 * Description:
 * Created by PangHaHa on 18-8-17.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class LiveApplication  extends Application{

    private static LiveApplication instance;
    public static final int PLAN_ID_ALI = 1;
    public static LiveApplication get(){
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        instance = this;
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //查看log
        VcPlayerLog.enableLog();

        //初始化播放器
        AliVcMediaPlayer.init(getApplicationContext());

        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_ALI, AlivcPlayer.class.getName(), "AlivcPlayer"));
        PlayerConfig.setDefaultPlanId(PLAN_ID_ALI);

        //use default NetworkEventProducer.
        PlayerConfig.setUseDefaultNetworkEventProducer(true);

        PlayerLibrary.init(this);

    }
}
