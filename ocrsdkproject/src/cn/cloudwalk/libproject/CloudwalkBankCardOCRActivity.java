package cn.cloudwalk.libproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.cloudwalk.BankOcrSDK;
import cn.cloudwalk.jni.callback.BankCardCallback;
import cn.cloudwalk.jni.BankCardInfo;
import cn.cloudwalk.libproject.camera.AutoFocusCameraPreview;
import cn.cloudwalk.libproject.camera.Delegate;
import cn.cloudwalk.libproject.progressHUD.CwProgressHUD;
import cn.cloudwalk.libproject.util.ByteImgUtil;
import cn.cloudwalk.libproject.util.FileUtil;
import cn.cloudwalk.libproject.util.ImgUtil;
import cn.cloudwalk.libproject.util.Util;
import cn.cloudwalk.libproject.view.OcrMaskView;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class CloudwalkBankCardOCRActivity extends Activity implements Delegate, BankCardCallback {

    private static final String TAG = "CardFrontOCR";

    public CwProgressHUD processDialog;
    private Dialog mDialog;

    private static final int CANCEL_FOCUS = 0, DRAW_LINE = 1;
    AutoFocusCameraPreview mAutoFoucsCameraPreview;
    OcrMaskView maskView;
    ImageView mIv_idrect;
    Bitmap bitmap;//???????????????bitmap
    int ocr_flag = Contants.OCR_FLAG_BANKCARD;

    public BankOcrSDK bankOcrSDK;
    private BankCardInfo bankCardInfo;
    int initRet = -1;
    String licence;
    volatile boolean isWork;//??????????????????
    Bitmap bmpCanLine;//?????????
    Bitmap bmpfocus;
    Bitmap bmpfocused;
    final String OutJpgName = "bankcard.jpg";//??????????????????
    boolean mAutoRatio;
    int mLastOrientation = SCREEN_ORIENTATION_LANDSCAPE;//??????????????????
    OrientationEventListener mOrientationListener;//??????????????????Listener
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CANCEL_FOCUS:
                    maskView.clearFocus();
                    break;
                case DRAW_LINE:
                    cwDrawLine();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    /**
     * ???????????????????????????????????????????????? sy:2019/12/20
     */
    private boolean isImgSave;

    public void cwDrawLine() {
        maskView.setLine(bankCardInfo.left, bankCardInfo.top, bankCardInfo.right, bankCardInfo.bottom);
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAutoRatio = getIntent().getBooleanExtra("BANKCARD_AUTO_RATIO", false);//?????????????????????????????????
        if (!mAutoRatio && (getRequestedOrientation() == SCREEN_ORIENTATION_LANDSCAPE)) {//Manifest???????????????,?????????????????????,????????????????????????
            setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);//?????????????????????????????????????????????
            mLastOrientation = SCREEN_ORIENTATION_LANDSCAPE;
        } else if (mAutoRatio && (getRequestedOrientation() == SCREEN_ORIENTATION_LANDSCAPE)) {//Manifest???????????????,??????????????????,????????????????????????,?????????Gsensor??????
            setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
            mLastOrientation = SCREEN_ORIENTATION_PORTRAIT;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.cloudwalk_activity_bankocr);
        if (getIntent() != null) {
            licence = getIntent().getStringExtra("LICENCE");
        } else {
            finish();
        }
        isImgSave = getIntent().getBooleanExtra("isImgSave", true);

        initView();
        initSDK();
        bmpCanLine = BitmapFactory.decodeResource(getResources(), R.drawable.scan_line);
        bmpfocus = BitmapFactory.decodeResource(getResources(), R.drawable.focus);
        bmpfocused = BitmapFactory.decodeResource(getResources(), R.drawable.focused);
        mAutoFoucsCameraPreview.setAutoRatio(mAutoRatio);
        maskView.setAutoRatio(mAutoRatio);


        Point point = getScreenSize();
        mAutoFoucsCameraPreview.setScreenSize(point.x, point.y);
        mAutoFoucsCameraPreview.setFlag(ocr_flag);
        mAutoFoucsCameraPreview.setSizeCallback(new AutoFocusCameraPreview.SizeCallback() {
            @Override
            public void onSizeChange(int width, int height, final int ocrRectW, final int ocrRectH) {
                maskView.setOcr(width, height, ocrRectW, ocrRectH, ocr_flag, bmpCanLine, bmpfocus, bmpfocused);
            }
        });

        deleteCachedJpg();

        processDialog = CwProgressHUD.create(this).setStyle(CwProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("???????????????...").setCancellable(true).setAnimationSpeed(2)
                .setCancellable(false).setDimAmount(0.5f);

        //??????GSensor????????????????????????
        mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;  //?????????????????????????????????????????????
                }
                //???????????????????????????????????????
                if (orientation > 350 || orientation < 10) { //0???
                    //?????? ??????
                    if (mLastOrientation != SCREEN_ORIENTATION_PORTRAIT) {
                        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
                        mLastOrientation = SCREEN_ORIENTATION_PORTRAIT;
                    }
                } else if (orientation > 260 && orientation < 280) { //270???
                    //?????? ??????
                    if (mLastOrientation != SCREEN_ORIENTATION_LANDSCAPE) {
                        setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
                        mLastOrientation = SCREEN_ORIENTATION_LANDSCAPE;
                    }
                } else {
                    //do nothing
                    return;
                }
            }
        };


        //??????????????????
        if (mOrientationListener.canDetectOrientation() && mAutoRatio) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }

    }

    protected Point getScreenSize() {
        int realWidth = 0, realHeight = 0;
        Display display = this.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        realWidth = metrics.widthPixels;
        realHeight = metrics.heightPixels;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                Point size = new Point();
                display.getRealSize(size);
                realWidth = size.x;
                realHeight = size.y;
            } else if (android.os.Build.VERSION.SDK_INT < 17
                    && android.os.Build.VERSION.SDK_INT >= 14) {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Point(realWidth, realHeight);
    }


    /**
     * ????????????
     */
    private void initCallback() {
        bankOcrSDK.cwBankCardCallback(this);
        mAutoFoucsCameraPreview.setDelegate(this);
    }

    /**
     * ?????????SDK
     */
    private void initSDK() {
        bankOcrSDK = BankOcrSDK.getInstance(this);
        if (0 != initRet)
            initRet = bankOcrSDK.cwCreateCardHandle(this, licence);
        Log.i("initret", initRet + "");
        if (initRet != 0) {
            mDialog = new AlertDialog.Builder(this).setMessage("?????????????????????????????????")
                    .setNegativeButton("??????", new AlertDialog.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                        }
                    }).show();
        }
    }

    /**
     * ?????????????????????
     */
    private void initView() {
        mAutoFoucsCameraPreview = (AutoFocusCameraPreview) findViewById(R.id.CameraPreview);
        maskView = (OcrMaskView) findViewById(R.id.maskView);
        mIv_idrect = (ImageView) findViewById(R.id.iv_idrect);
    }

    @Override
    protected void onResume() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onResume();
        initCallback();
        mAutoFoucsCameraPreview.cwStartCamera();
    }

    @Override
    protected void onStop() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onStop();
        bitmap = null;
        isWork = false;
        mAutoFoucsCameraPreview.cwStopCamera();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
        bankOcrSDK.cwDestroyCardHandle();
        if (bmpCanLine != null && !bmpCanLine.isRecycled()) {
            bmpCanLine.recycle();
        }
        if (bmpfocus != null && !bmpfocus.isRecycled()) {
            bmpfocus.recycle();
        }
        if (bmpfocused != null && !bmpfocused.isRecycled()) {
            bmpfocused.recycle();
        }
        if (processDialog != null && processDialog.isShowing()) {
            processDialog.dismiss();
        }
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onOpenCameraError() {

    }

    @Override
    public void onFocus(float x, float y) {
        maskView.setFocus(x, y);

    }

    @Override
    public void onFocused() {
        maskView.setFocused();
        mHandler.sendEmptyMessageDelayed(CANCEL_FOCUS, 150);
    }

    @Override
    public void BankCardInfo(BankCardInfo bankCardInfo) {
        this.bankCardInfo = bankCardInfo;//???????????????,??????????????????????????????????????????????????????
        mHandler.sendEmptyMessage(DRAW_LINE);
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    public void BankCardData(BankCardInfo bankCardInfo) {//?????????bankCardInfo????????????????????????????????????????????????????????????????????????
        mHandler.removeCallbacksAndMessages(null);
        if (bankCardInfo != null) {
            String cardNum = bankCardInfo.cardNum;
            String bankName = bankCardInfo.bankName;
            String cardName = bankCardInfo.cardName;
            String cardType = bankCardInfo.cardType;
            Log.e("bank", "cardNum = " + cardNum + " bankName = " + bankName + " cardName = " + cardName + " cardType = " + cardType);

            //???????????????????????????????????????,??????unknown??????
            //sy add 2020.04.29 ??????????????????????????????????????????unknown?????????????????????
//            if (bankName.equals("unknown")) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (processDialog != null && processDialog.isShowing()) {
//                            processDialog.dismiss();
//                        }
//                        mAutoFoucsCameraPreview.showCameraPreview();
//                        isWork = false;
//                    }
//                });
//                return;
//            }
            //bgr???????????????????????????,???????????????????????????,??????????????????
            Log.e("bank", "jpgdata = " + bankCardInfo.jpgdata + " width = " + bankCardInfo.width + " height = " + bankCardInfo.height);
            if (bankCardInfo.jpgdata != null && bankCardInfo.width > 0 && bankCardInfo.height > 0) {
                bitmap = ImgUtil.byteArrayBGRToBitmap(bankCardInfo.jpgdata, bankCardInfo.width, bankCardInfo.height);
            }
            if (isImgSave) {
                //????????????
                String dirpath = Environment.getExternalStorageDirectory() + "/" + "cloudwalk" + "/" + sdf.format(new Date()) + "_bankcard/";
                String path = dirpath + OutJpgName;
                //????????????
                FileUtil.mkDir(dirpath);
                if (bitmap != null) {
                    ImgUtil.saveJPGE_After(bitmap, path, 100);
                    Log.i("here", "hereocr");
                    Bulider.mBankOcrResultCallback.onBankOcrDetFinished(bankCardInfo, path);
//                Intent mIntent = new Intent(CloudwalkBankCardOCRActivity.this, BankCardResultActivity.class);
//                mIntent.putExtra("cardNum", cardNum);
//                mIntent.putExtra("bankName", bankName);
//                mIntent.putExtra("cardName", cardName);
//                mIntent.putExtra("cardType", cardType);
//                mIntent.putExtra("cardPath", path);
//
//                startActivity(mIntent);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (processDialog != null && processDialog.isShowing()) {
                                processDialog.dismiss();
                            }
                            mAutoFoucsCameraPreview.showCameraPreview();
                            isWork = false;
                        }
                    });
                }
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            } else {
                if (bitmap != null) {
                    String data = Base64.encodeToString(ByteImgUtil.zoomToSize(bitmap), Base64.NO_WRAP);
                    Bulider.mBankOcrResultCallback.onBankOcrDetFinished(bankCardInfo, data);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (processDialog != null && processDialog.isShowing()) {
                                processDialog.dismiss();
                            }
                            mAutoFoucsCameraPreview.showCameraPreview();
                            isWork = false;
                        }
                    });
                }
            }
        } else {
            //???????????????
            //finish();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (processDialog != null && processDialog.isShowing()) {
                        processDialog.dismiss();
                    }
                    mAutoFoucsCameraPreview.showCameraPreview();
                    isWork = false;
                }
            });
        }
    }

    /**
     * ???????????????????????????
     */
    @Override
    public void BankCardRecognizing() {
        if (!isWork) {//????????????????????????
            deleteCachedJpg();
            isWork = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (processDialog != null && !processDialog.isShowing()) {
                        processDialog.setLabel(getString(R.string.bank_loading)).show();
                    }
                    mAutoFoucsCameraPreview.stopCameraPreview();
                }
            });
        }
    }

    //??????onConfiguratonChanged,??????????????????????????????Activity?????????????????????????????????????????????
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int type = newConfig.orientation;
        if (type == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mLastOrientation = SCREEN_ORIENTATION_LANDSCAPE;
            //??????
        } else if (type == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //??????
            mLastOrientation = SCREEN_ORIENTATION_PORTRAIT;
        }
        Log.i("jinwei", "newConfig" + type);


    }

    /**
     * add ?????????????????????????????? 2019-12-09 20:40:34
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("errorCode", -1);
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
//        this.finish();
    }

    /**
     * ???????????????????????????????????????
     */
    protected void deleteCachedJpg() {
        try {
            String path = Util.getDiskCacheDir(CloudwalkBankCardOCRActivity.this) + "/" + OutJpgName;
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
