package cn.cloudwalk.libproject.callback;

import java.util.Calendar;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class NoDoubleClickListener implements OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1500;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        Log.i("timehere", (currentTime - lastClickTime) + "");
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
