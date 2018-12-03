package com.example.pang.testlive.VideoList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.pang.testlive.R;

/**
 * Description:
 * Created by PangHaHa on 18-9-6.
 * Copyright (c) 2018 PangHaHa All rights reserved.
 */
public class TestListActivity extends AppCompatActivity {

    BaseVideoListFragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_testlist);
        initView();
    }

    private void initView() {

        fragment = new BaseVideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("p_uid",getIntent().getStringExtra("p_uid"));
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout,fragment,"视频列表");
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment!=null){
            fragment.onBackPressed();
        }else {
            super.onBackPressed();
        }

    }
}
