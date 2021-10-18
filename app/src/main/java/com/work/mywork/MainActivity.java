package com.work.mywork;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    private int times = 3;
    @BindView(R.id.txt_time)
    TextView time;
    private Timer timer;
    private TimerTask timerTask;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        time.setText("跳过 3");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        times--;
                        time.setText("跳过 " + times);
                        if (times == 0) {
                            time.setVisibility(View.GONE);
                            //进入主页
                            startActivity(new Intent(MainActivity.this, AppActivity.class));
                            finish();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    @OnClick(R.id.txt_time)
    public void onClick() {
        startActivity(new Intent(MainActivity.this, AppActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }
}