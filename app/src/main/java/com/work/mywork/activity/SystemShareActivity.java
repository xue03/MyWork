package com.work.mywork.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.utils.Permissions;
import com.work.mywork.utils.SystemShareUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2021/10/21
 * Description:
 * Author:XueTingTing
 */
public class SystemShareActivity extends BaseActivity {
    @BindView(R.id.txt_activity_title)
    TextView txtActivityTitle;
    @BindView(R.id.btn_shareText)
    Button btnShareText;
    @BindView(R.id.btn_shareImage)
    Button btnShareImage;
    @BindView(R.id.btn_shareTxtImg)
    Button btnShareTxtImg;
    @BindView(R.id.btn_shareUrl)
    Button btnShareUrl;
    @BindView(R.id.btn_shareMusic)
    Button btnShareMusic;
    @BindView(R.id.btn_shareVideo)
    Button btnShareVideo;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {

        txtActivityTitle.setText("SystemShare");

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_share_system;
    }
    @OnClick({R.id.btn_shareText,R.id.btn_shareImage,R.id.btn_shareTxtImg,R.id.btn_shareVideo,R.id.btn_shareUrl})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_shareText:
//5a9e5a06
                SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5dcbaf01");
                if (CheckPermission.checkPermission(this, this, Permissions.LOCATION, 300)){
                    iflyTTS("语音读写初始化失败。");

                }
//                SystemShareUtil.shareText(,"This is my text to send.");
                break;
        }
    }
    public void iflyTTS(final String param) {
       // MADPLogger.d("语音读写 合成文本（param）" + param);

        if (TextUtils.isEmpty(param)) {
           // MADPLogger.d("语音读写 合成文本（param）不能为空");
            return;
        }
        SpeechSynthesizer speechSynthesizer=SpeechSynthesizer.createSynthesizer(this, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("code", code);
                        jsonObject.put("msg", "语音读写初始化失败");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "onInit: "+jsonObject.toString());
//                    jsCallback.invoke(jsonObject.toString());
//                    MADPLogger.d("语音读写初始化失败，错误码：" + code);
                }
            }
        });
        if (speechSynthesizer == null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "1");
                jsonObject.put("msg", "讯飞语音读写初始化失败");
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            jsCallback.invoke(jsonObject.toString());
//            MADPLogger.d("讯飞语音读写初始化失败");
            return;
        }

        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        int result = speechSynthesizer.startSpeaking(param, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                //MADPLogger.d("语音读写onSpeakBegin");
                Log.d("TAG", "onSpeakBegin: ");

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {
               // MADPLogger.d("语音读写onBufferProgress:" + i + " " + i1 + " " + i2 + " " + s);
//                Log.d(TAG, "onBufferProgress: ");
            }

            @Override
            public void onSpeakPaused() {
                //MADPLogger.d("语音读写onSpeakPaused");
            }

            @Override
            public void onSpeakResumed() {
              //  MADPLogger.d("语音读写onSpeakResumed");

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {
               // MADPLogger.d("语音读写onSpeakProgress" + i + " " + i1 + " " + i2);

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (speechError != null) {
                        jsonObject.put("code", "1");
                        jsonObject.put("msg", speechError.getPlainDescription(true));
                      //  MADPLogger.d("语音：onCompleted：" + speechError.getPlainDescription(true));
                    } else {
                        jsonObject.put("code", "0");
                        jsonObject.put("msg", "语音读写成功");
                    }
                   // jsCallback.invoke(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });
        if (result != ErrorCode.SUCCESS) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", "1");
                jsonObject.put("msg", "语音读写失败");
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            jsCallback.invoke(jsonObject.toString());
//            MADPLogger.d("读写失败,错误码：" + result);
        }
    }

}
