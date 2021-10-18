//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import cn.com.csii.mobile.zxing.view.ViewfinderView;
import com.google.zxing.Result;

public interface CaptureInterface {
  ViewfinderView getViewfinderView();

  Handler getHandler();

  Activity getCurrentActivity();

  void handleDecode(Result var1, Bitmap var2);

  void drawViewfinder();
}
