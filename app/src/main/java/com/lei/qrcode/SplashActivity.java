package com.lei.qrcode;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import common.GetAsyncTask;
import common.Utils;
import okhttp3.internal.Util;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

        private int recLen = 2;//跳过倒计时提示5秒
        private TextView tv;
        Timer timer = new Timer();
        private Handler handler;
        private Runnable runnable;
        SharedPreferences pref;
        private final String loginUrl = "/user/login";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //定义全屏参数
            int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            pref = getSharedPreferences("userInfo",MODE_PRIVATE);
            //设置当前窗体为全屏显示
            getWindow().setFlags(flag, flag);
            setContentView(R.layout.activity_splash);

            initView();
            timer.schedule(task, 1000, 1000);//等待时间一秒，停顿时间一秒
            /**
             * 正常情况下不点击跳过
             */
            handler = new Handler();
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    pageTransition();
                }
            }, 2000);//延迟2S后发送handler信息

        }

        private  void pageTransition(){
            String studentId = pref.getString("STUDENTID","");
            String password = pref.getString("PASSWORD","");
            Log.d("lei","studentId = "+studentId+"password = "+password);
            if(studentId.length()>0 && password.length() > 0) {
                Utils.start_Activity(SplashActivity.this, MainActivity.class);
            }else{
                Utils.start_Activity(SplashActivity.this, LoginActivity.class);
            }
            //Utils.start_Activity(SplashActivity.this, MainActivity.class);
            finish();
        }

        private void initView() {
            tv = findViewById(R.id.tv);//跳过
            tv.setOnClickListener(this);//跳过监听
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() { // UI thread
                    @Override
                    public void run() {
                        recLen--;
                        tv.setText("跳过 " + recLen);
                        if (recLen < 0) {
                            timer.cancel();
                            tv.setVisibility(View.GONE);//倒计时到0隐藏字体
                        }
                    }
                });
            }
        };

        /**
         * 点击跳过
         */
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv:
                    pageTransition();
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                    }
                    break;
                default:
                    break;
            }
        }

}
