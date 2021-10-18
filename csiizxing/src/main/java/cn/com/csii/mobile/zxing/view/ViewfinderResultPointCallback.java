//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.view;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

public final class ViewfinderResultPointCallback implements ResultPointCallback {
    private final ViewfinderView viewfinderView;

    public ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
        this.viewfinderView = viewfinderView;
    }

    @Override
    public void foundPossibleResultPoint(ResultPoint point) {
        this.viewfinderView.addPossibleResultPoint(point);
    }
}
