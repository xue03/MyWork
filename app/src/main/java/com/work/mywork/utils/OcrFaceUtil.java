package com.work.mywork.utils;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.work.mywork.app.MyApplication;
import com.work.mywork.interfaces.ResultCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cn.cloudwalk.FaceInterface;
import cn.cloudwalk.libproject.Bulider;
import cn.cloudwalk.libproject.LiveStartActivity;
import cn.cloudwalk.libproject.callback.FrontLiveCallback;
import cn.cloudwalk.libproject.util.Base64Util;
import cn.cloudwalk.libproject.util.FileUtil;

/**
 * Date:2021/10/12
 * Description:ocr人脸识别
 * Author:XueTingTing
 */
public class OcrFaceUtil {
    //Mzg1NDE0bm9kZXZpY2Vjd2F1dGhvcml6ZZfk4+bn5+Tq3+bg5efm5Of65Obn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+er4Ofr5+vn64vn5+Tm5+bn
    static String licence=null;
    static Activity activity;
    public static void setLicence(String licences,Activity ac){
        if (licences!=null){
           licence=licences;
           activity=ac;
        }
    }
    public static void startOpenFace(ResultCallBack callBack){
        if (TextUtils.isEmpty(licence)) {
            Log.d(OcrFaceUtil.class.getSimpleName(), "调用face前请先设定云从科技颁发的licence,设定方法参考face module里的方法:setLicence(String licence)");
            return;
        }
        startCheck(3,callBack);
    }

    private static void startCheck(int liveCount, ResultCallBack callBack) {
        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            path = FileUtils.getQPATH(MyApplication.mContext, Environment.DIRECTORY_PICTURES);
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        final String publicFilePath = new StringBuilder(path)
                .append(File.separator).append("cloudwalk").append(File.separator).append(new SimpleDateFormat("yyMMddHHmm")
                        .format(new Date()))
                .toString();
        FileUtil.mkDir(publicFilePath);


//        MADPLogger.d("WeexFaceModule:开始调用活体检测");
        ArrayList<Integer> liveList = new ArrayList<Integer>();
        liveList.add(FaceInterface.LivessType.LIVESS_MOUTH);
        liveList.add(FaceInterface.LivessType.LIVESS_HEAD_LEFT);
        liveList.add(FaceInterface.LivessType.LIVESS_HEAD_RIGHT);
        liveList.add(FaceInterface.LivessType.LIVESS_EYE);

        final Bulider bulider = new Bulider();
        bulider.setLicence(licence).setResultCallBack(new cn.cloudwalk.libproject.callback.ResultCallBack() {
            @Override
            public void result(boolean isLivePass, boolean isVerfyPass, String faceSessionId, double face_score, int resultType, byte[] bestFaceImgData, byte[] clipedBestFaceImgData, HashMap<Integer, byte[]> liveImgDatas) {

                if (resultType==-1){
                    if (callBack != null) {
                        JSONObject result = new JSONObject();
                        try {
                            result.put("errorCode", resultType+"");
                            result.put("errorMsg", "返回");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.Success(result.toString());
                    }
                }

            }
        });

        //#if CID=="BANKOFTIANJINZX"
        bulider.setLicence(licence).setFrontLiveFace(new FrontLiveCallback() {
            @Override
            public void onFrontLivessFinished(final byte[] bestface, final String bestInfo, final byte[] nextface,
                                              final String nextInfo, final byte[] clipedBestface, boolean isLive) {
               // AppManagerDelegate.getInstance().removeActivity(LiveStartActivity.class);
                if (bestface != null && bestInfo != null && !TextUtils.isEmpty(bestInfo)
                        && nextface != null && nextInfo != null && !TextUtils.isEmpty(nextInfo)) {
                    final String imgBestBase64 = Base64Util.encode(bestface);
                    String imgNextBase64 = Base64Util.encode(nextface);
                    String strFaceInfo = imgBestBase64 + "," + bestInfo + "_" + imgNextBase64 + "," + nextInfo;
                   // MADPLogger.d("WeexFaceModule:onFrontLivessFinished--->bestface = [" + imgBestBase64 + "], bestInfo = [" + bestInfo + "], nextface = [" + nextface + "], nextInfo = [" + nextInfo + "], clipedBestface = [" + clipedBestface + "], isLive = [" + isLive + "]");
                    bulider.setFaceLiveResult(MyApplication.mContext, Bulider.FACE_LIVE_PASS, 1);
                    if (callBack != null) {
                        JSONObject result = new JSONObject();
                        try {
                            result.put("code", "200");
                            result.put("base64", strFaceInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        MADPLogger.d("WeexFaceModule:onFrontLivessFinished--->success" + result.toJSONString());
                        callBack.Success(result.toString());
                    }
                } else {

                    bulider.setFaceLiveResult(MyApplication.mContext, Bulider
                            .FACE_LIVE_FAIL, Bulider.FACE_LIVE_FAIL);
//                    if (callBack != null) {
//                        JSONObject result = new JSONObject();
//                        try {
//                            result.put("errorCode", "700");
//                            result.put("errorMsg", getErrorMsg(700));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        callBack.Success(result.toString());
//                    }
                }
            }

            @Override
            public void onLivenessFail(int errorCode) {

            }
        }).isServerLive(true)//后端活体的总开关
                .isFrontHack(false)//前端活体的总开关
                .isResultPage(false)//活体检测页面开关
                .setPublicFilePath(publicFilePath)
                .setLives(liveList, liveCount, true, false/*不返回动作图*/, FaceInterface.LevelType.LEVEL_STANDARD/*废弃*/)
                .setLiveTime(8)
                .startActivity(activity, LiveStartActivity.class);
    }
    private static String getErrorMsg(int code) {
        String msg = "";
        switch (code) {
            case -1:
                msg = "活体检测取消";
                break;
            case 700:
                msg = "没有检测到人脸";
                break;
            case 702:
                msg = "检测到换人";
                break;
            case 703:
                msg = "检测超时";
                break;
            case 704:
                msg = "检测到黑白图片攻击";
                break;
            case 20000:
                msg = "空图像";
                break;
            case 20001:
                msg = "图像格式不支持";
                break;
            case 20002:
                msg = "没有检测到人脸";
                break;
            case 20003:
                msg = "ROI设置失败";
                break;
            case 20004:
                msg = "最小最大人脸设置失败";
                break;
            case 20005:
                msg = "数据范围错误";
                break;
            case 20006:
                msg = "方法无效";
                break;
            case 20007:
                msg = "未授权";
                break;
            case 20008:
                msg = "尚未初始化";
                break;
            case 20009:
                msg = "加载检测模型失败";
                break;
            case 20010:
                msg = "加载关键点模型失败";
                break;
            case 20011:
                msg = "加载质量评估模型失败";
                break;
            case 20012:
                msg = "加载活体检测模型失败";
                break;
            case 20013:
                msg = "检测失败";
                break;
            case 20014:
                msg = "提取关键点失败";
                break;
            case 20015:
                msg = "对齐人脸失败";
                break;
            case 20016:
                msg = "质量评估失败";
                break;
            case 20017:
                msg = "活体检测失败";
                break;
            default:
                break;
        }
        return msg;
    }
}
