//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing.util;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import cn.com.csii.mobile.zxing.camera.CameraManager;

public class LightControl {
    Camera m_Camera;

    public LightControl() {
    }

    public void turnOn() {
        try {
            this.m_Camera = CameraManager.get().getCamera();
            Parameters mParameters = this.m_Camera.getParameters();
            mParameters.setFlashMode("torch");
            this.m_Camera.setParameters(mParameters);
        } catch (Exception var2) {
            ;
        }

    }

    public void turnOff() {
        try {
            this.m_Camera = CameraManager.get().getCamera();
            Parameters mParameters = this.m_Camera.getParameters();
            mParameters.setFlashMode("off");
            this.m_Camera.setParameters(mParameters);
        } catch (Exception var2) {
            ;
        }

    }
}
