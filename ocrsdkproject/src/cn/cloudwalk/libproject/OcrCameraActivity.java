package cn.cloudwalk.libproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.cloudwalk.IDCardSDK;
import cn.cloudwalk.callback.IDCardImgCallback;
import cn.cloudwalk.callback.IDCardInfoCallback;
import cn.cloudwalk.jni.IDCardImg;
import cn.cloudwalk.jni.IDFaceImg;
import cn.cloudwalk.jni.IdCardInfo;
import cn.cloudwalk.libproject.camera.AutoFocusCameraPreview;
import cn.cloudwalk.libproject.camera.Delegate;
import cn.cloudwalk.libproject.util.ByteImgUtil;
import cn.cloudwalk.libproject.util.FileUtil;
import cn.cloudwalk.libproject.util.ImgUtil;
import cn.cloudwalk.libproject.util.Util;
import cn.cloudwalk.libproject.view.OcrMaskView;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class OcrCameraActivity extends Activity implements Delegate, IDCardImgCallback, IDCardInfoCallback, View.OnClickListener {
    public static final String TAG = "OcrCameraActivity";
    public static String FILEPATH_KEY = "filepath_key";
    private static final int CANCEL_FOCUS = 0, DRAW_LINE = 1, DRAW_PROGRESS = 2;
    int ocr_flag;
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

    AutoFocusCameraPreview mAutoFoucsCameraPreview;
    OcrMaskView maskView;
    ImageView mIv_idrect;

    int initRet = -1;
    IDCardSDK iDCardSDK = null;
    IDCardImg idCardImg;
    boolean isWork;//??????????????????

    final String OutJpgName = "idcard.jpg";//??????????????????
    Bitmap bmpCanLine;//?????????
    Bitmap bmpfocus;//?????????
    Bitmap bmpfocused;//?????????

    /**
     * ???????????????????????????????????????????????? sy:2019/12/20
     */
    private boolean isImgSave;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.cloudwalk_activity_rect_ocr);
        Log.i("here12321", "liangci");

        ocr_flag = getIntent().getIntExtra(Contants.OCR_FLAG, -1);
        if (-1 == ocr_flag) {
            Toast.makeText(this, "params error", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        isImgSave = getIntent().getBooleanExtra("isImgSave",true);

        initView();
        initSDK();
        initCallback();
        Log.i(TAG, "???????????? APP:" + iDCardSDK.SDK_APP_VERSION + " ??????:" + iDCardSDK.SDK_ALGORITHM_VERSION);
        bmpCanLine = BitmapFactory.decodeResource(getResources(), R.drawable.scan_line);
        bmpfocus = BitmapFactory.decodeResource(getResources(), R.drawable.focus);
        bmpfocused = BitmapFactory.decodeResource(getResources(), R.drawable.focused);
        mAutoFoucsCameraPreview.setFlag(ocr_flag);
        mAutoFoucsCameraPreview.setSizeCallback(new AutoFocusCameraPreview.SizeCallback() {
            @Override
            public void onSizeChange(int width, int height, final int ocrRectW, final int ocrRectH) {
                maskView.setOcr(width, height, ocrRectW, ocrRectH, ocr_flag, bmpCanLine, bmpfocus, bmpfocused);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ocrRectW, ocrRectH);
                        params.addRule(RelativeLayout.CENTER_IN_PARENT);
                        mIv_idrect.setLayoutParams(params);
                        if (ocr_flag == 1) {
                            mIv_idrect.setBackgroundResource(R.drawable.zhengmian2);
                        } else {
                            mIv_idrect.setBackgroundResource(R.drawable.beimian2);
                        }

                    }
                });
            }
        });

        deleteCachedJpg();
    }

    /**
     * ?????????????????????
     */
    private void initView() {
        mAutoFoucsCameraPreview = (AutoFocusCameraPreview) findViewById(R.id.preview);
        maskView = (OcrMaskView) findViewById(R.id.ocrMaskView);
        mIv_idrect = (ImageView) findViewById(R.id.iv_idrect);
        back = (ImageView) findViewById(R.id.cloud_back);
        back.setOnClickListener(this);
    }

    /**
     * ????????????
     */
    private void initCallback() {
        iDCardSDK.cwIDCardImgCallback(this);
        iDCardSDK.cwIDCardInfoCallback(this);
        mAutoFoucsCameraPreview.setDelegate(this);
    }

    /**
     * ?????????SDK
     */
    private void initSDK() {
        iDCardSDK = IDCardSDK.getInstance(this);
        initRet = iDCardSDK.cwCreateIdCardRecog(this, Bulider.licence);
        if (initRet != 0) {
            new AlertDialog.Builder(this).setMessage(R.string.facedectfail_appid)
                    .setNegativeButton("??????", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            finish();
                        }
                    }).show();
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mAutoFoucsCameraPreview.cwStartCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onStop() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAutoFoucsCameraPreview.cwStopCamera();
        mHandler.removeCallbacksAndMessages(null);
        isWork = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (null != iDCardSDK)
            iDCardSDK.cwDestroyIdCardRecog();
        if (bmpCanLine != null && !bmpCanLine.isRecycled()) {
            bmpCanLine.recycle();
        }
        if (bmpfocus != null && !bmpfocus.isRecycled()) {
            bmpfocus.recycle();
        }
        if (bmpfocused != null && !bmpfocused.isRecycled()) {
            bmpfocused.recycle();
        }
        super.onDestroy();
    }

    @Override
    public void onOpenCameraError() {
        // ??????????????????
        Toast.makeText(this, "??????????????????,?????????", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFocus(float x, float y) {
        // ????????????
        maskView.setFocus(x, y);
    }

    @Override
    public void onFocused() {
        // ????????????
        maskView.setFocused();
        mHandler.sendEmptyMessageDelayed(CANCEL_FOCUS, 150);
    }

    @Override
    public void IDCardImg(IDCardImg idCardImg) {
        mHandler.obtainMessage(DRAW_LINE, new Rect(idCardImg.left, idCardImg.top, idCardImg.right, idCardImg.bottom)).sendToTarget();
        this.idCardImg = idCardImg;
    }

    @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
    @Override
    public void IDCardDetectOk(IDCardImg idCardImg) {
        mAutoFoucsCameraPreview.stopCameraPreview();

        mHandler.obtainMessage(DRAW_LINE, new Rect(idCardImg.left, idCardImg.top, idCardImg.right, idCardImg.bottom)).sendToTarget();

        mHandler.obtainMessage(DRAW_PROGRESS, new Rect(idCardImg.left, idCardImg.top, idCardImg.right, idCardImg.bottom)).sendToTarget();

        this.idCardImg = idCardImg;
    }

    @Override
    public void IDCardInfo(IdCardInfo idCardInfo, IDFaceImg idFaceImg) {
        if (null == idCardInfo && idFaceImg == null) {//?????????????????????
            isWork = false;
            mAutoFoucsCameraPreview.showCameraPreview();
            mHandler.obtainMessage(DRAW_LINE, new Rect(0, 0, 0, 0)).sendToTarget();
            return;
        }

        //?????????????????????
        if (!isWork) {
            isWork = true;

            mHandler.obtainMessage(DRAW_LINE, new Rect(idCardImg.left, idCardImg.top, idCardImg.right, idCardImg.bottom)).sendToTarget();
            Bitmap bitmap = null;
            String filepath = null;
            if (isImgSave){
                //????????????
                String dirpath = Environment.getExternalStorageDirectory() + "/" + "cloudwalk" + "/" + sdf.format(new Date()) + "_idcard/";
                filepath = dirpath + OutJpgName;
                //????????????
                FileUtil.mkDir(dirpath);
                //??????
                if (idCardImg.ImgData != null && idCardImg.detect_width > 0 && idCardImg.detect_height > 0) {
                    bitmap = ImgUtil.byteArrayBGRToBitmap(idCardImg.ImgData, idCardImg.detect_width, idCardImg.detect_height);
                    ImgUtil.saveJPGE_After(bitmap, filepath, 95);
                }
            }else {
                if (idCardImg.ImgData != null && idCardImg.detect_width > 0 && idCardImg.detect_height > 0) {
                    bitmap = ImgUtil.byteArrayBGRToBitmap(idCardImg.ImgData, idCardImg.detect_width, idCardImg.detect_height);
                }
            }

            if (ocr_flag == Contants.OCR_FLAG_IDFRONT && idCardImg.flag == ocr_flag) {
                //TODO:??????????????????????????????????????????BGR
                if (idFaceImg != null) {
                    OcrResultActivity.faceBitmap = ImgUtil.byteArrayBGRToBitmap(idFaceImg.FaceImgData, idFaceImg.Idcard_width, idFaceImg.Idcard_height);
                }

                //TODO:?????????????????????????????????
                String name = (idCardInfo.name);
                String sex = (idCardInfo.gender);
                String race = (idCardInfo.race);
                String birth = (idCardInfo.birth);
                String address = (idCardInfo.address);
                String id = (idCardInfo.id);
                String city = (idCardInfo.city);

                Intent front = getIntent();
                front.putExtra("name", name);
                front.putExtra("sex", sex);
                front.putExtra("race", race);
                front.putExtra("birth", birth);
                front.putExtra("address", address);
                front.putExtra("id", id);
                front.putExtra("city", city);
                if (isImgSave){
                    front.putExtra(FILEPATH_KEY, filepath);
                    Log.i("pathere", filepath);
                }else {
                    //????????????bitmap??????
                    front.putExtra("bitmap", ByteImgUtil.zoomToSize(bitmap));
                }
                setResult(Activity.RESULT_OK, front);// ????????????
                this.finish();

            } else if (ocr_flag == Contants.OCR_FLAG_IDBACK && idCardImg.flag == ocr_flag) {
                String authority = (idCardInfo.authority);
                String validdate1 = (idCardInfo.validdate1);
                String validdate2 = (idCardInfo.validdate2);

                Intent back = getIntent();
                back.putExtra("authority", authority);
                back.putExtra("validdate1", validdate1);
                back.putExtra("validdate2", validdate2);
                if (isImgSave){
                    back.putExtra(FILEPATH_KEY, filepath);
                }else {
                    //????????????bitmap??????
                    back.putExtra("bitmap",ByteImgUtil.zoomToSize(bitmap));
                    back.putExtra("img",ByteImgUtil.zoomToSize(bitmap));
                }

                setResult(Activity.RESULT_OK, back);// ????????????
                this.finish();
            }

            if (isImgSave){
                File file = new File(filepath);
                Uri uri = Uri.fromFile(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            }

        }
    }

    public void cwDrawLine(int left, int top, int right, int bottom) {
        maskView.setLine(left, top, right, bottom);
    }

    public void cwDrawProgress() {
        maskView.setDrawProgress();
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CANCEL_FOCUS) {
                maskView.clearFocus();

            } else if (msg.what == DRAW_LINE) {
                Rect rect = (Rect) msg.obj;
                cwDrawLine(rect.left, rect.top, rect.right, rect.bottom);

            } else if (msg.what == DRAW_PROGRESS) {
                cwDrawProgress();
            }
            super.handleMessage(msg);
        }
    };

    protected void deleteCachedJpg() {
        try {
            String path = Util.getDiskCacheDir(this) + "/" + OutJpgName;
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }*/

    /**
     * add 2019-12-09 20:40:11 ??????????????????????????????
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("errorCode",-1);
        setResult(RESULT_CANCELED,intent);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.putExtra("errorCode",-1);
        setResult(RESULT_CANCELED,intent);
        finish();
    }
}
