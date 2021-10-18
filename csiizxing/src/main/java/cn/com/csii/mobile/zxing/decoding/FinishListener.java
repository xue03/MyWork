//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.decoding;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;

public final class FinishListener implements OnClickListener, OnCancelListener, Runnable {
    private final Activity activityToFinish;

    public FinishListener(Activity activityToFinish) {
        this.activityToFinish = activityToFinish;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Log.i("MSG", "onCancel");
        this.run();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Log.i("MSG", "onClick");
        this.run();
    }

    @Override
    public void run() {
        Log.i("MSG", "activity finish!");
        this.activityToFinish.finish();
    }
}
