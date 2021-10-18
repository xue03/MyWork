package com.work.mywork;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;

import java.util.HashMap;

public class PdfWebviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_webview);

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        QbSdk.openFileReader(this, "/sdcard/Download/xxx.doc", null, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {

            }
        });
    }

}