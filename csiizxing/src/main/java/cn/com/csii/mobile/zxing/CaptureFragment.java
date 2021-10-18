//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.os.Build.VERSION;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import cn.com.csii.mobile.zxing.camera.CameraManager;
import cn.com.csii.mobile.zxing.decoding.CaptureActivityHandler;
import cn.com.csii.mobile.zxing.interfaces.DecodeQRCodeResult;
import cn.com.csii.mobile.zxing.util.LightControl;
import cn.com.csii.mobile.zxing.util.ZxingUtil;
import cn.com.csii.mobile.zxing.view.ViewfinderView;

import com.csii.zxing.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

@SuppressLint({"NewApi"})
public class CaptureFragment extends Fragment implements Callback, CaptureInterface {
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.1F;
    private boolean vibrate;
    private ImageView code_photo;
    private ImageView code_back;
    private CheckBox light;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    public Activity activity;
    private boolean isShow = false;
    private CaptureFragment captureFragment;
    private boolean photoDecode = false;
    private Handler mHandler = new Handler();
    private static final int KITKAT_LESS = 0;
    private static final int KITKAT_ABOVE = 1;
    private static final int PHOTO_REQ = 2;
    private DecodeQRCodeResult decodeQRCodeResult;
    private Button buttonScan;
    private Button buttonCard;
    private int MY_SCAN_REQUEST_CODE = 100;
    private static final long VIBRATE_DURATION = 200L;
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public CaptureFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = this.getActivity();
        this.captureFragment = this;

        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.zxing_item,null);
        LinearLayout layoutRoot = new LinearLayout(this.getActivity());
        LayoutParams params = new LayoutParams(-1, -1);
        params.gravity = 1;
        layoutRoot.setLayoutParams(params);
        layoutRoot.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout layout = new RelativeLayout(this.activity);
        LayoutParams params_match = new LayoutParams(-1, -2);
        params_match.weight = 1.0F;
        android.widget.RelativeLayout.LayoutParams params_wrap = new android.widget.RelativeLayout.LayoutParams(-2, -2);
        params_wrap.addRule(13);
        layout.setLayoutParams(params_match);
        RelativeLayout capture = new RelativeLayout(this.activity);
        capture.setLayoutParams(params_match);
        this.surfaceView = new SurfaceView(this.activity);
        this.surfaceView.setLayoutParams(params_wrap);
        this.viewfinderView = new ViewfinderView(this.activity);
        this.viewfinderView.setLayoutParams(params_wrap);
        capture.addView(this.surfaceView);
        capture.addView(this.viewfinderView);
        android.widget.RelativeLayout.LayoutParams params_light = new android.widget.RelativeLayout.LayoutParams(this.dip2px(this.activity, 40.0F), this.dip2px(this.activity, 40.0F));
        params_light.addRule(11);
        params_light.setMargins(0, this.dip2px(this.activity, 20.0F), this.dip2px(this.activity, 20.0F), 0);
        this.light = new CheckBox(this.activity);
        this.light.setLayoutParams(params_light);
        this.light.setButtonDrawable(0);
        android.widget.RelativeLayout.LayoutParams params_photo = new android.widget.RelativeLayout.LayoutParams(this.dip2px(this.activity, 40.0F), this.dip2px(this.activity, 40.0F));
        params_photo.addRule(14);
        params_photo.setMargins(0, this.dip2px(this.activity, 20.0F), 0, 0);
        this.code_photo = new ImageView(this.activity);
        this.code_photo.setLayoutParams(params_photo);
        android.widget.RelativeLayout.LayoutParams params_back = new android.widget.RelativeLayout.LayoutParams(this.dip2px(this.activity, 40.0F), this.dip2px(this.activity, 40.0F));
        params_back.addRule(9);
        params_back.setMargins(this.dip2px(this.activity, 20.0F), this.dip2px(this.activity, 20.0F), 0, 0);
        this.code_back = new ImageView(this.activity);
        this.code_back.setLayoutParams(params_back);
        layout.addView(capture);
        layout.addView(this.light);
        layout.addView(this.code_photo);
        layout.addView(this.code_back);
        layoutRoot.addView(layout);
        LayoutParams layoutParamsBottom = new LayoutParams(-1, -2);
        LinearLayout layoutButtonBottom = new LinearLayout(this.getActivity());
        layoutButtonBottom.setLayoutParams(layoutParamsBottom);
        layoutButtonBottom.setOrientation(LinearLayout.HORIZONTAL);
        layoutButtonBottom.setBackgroundColor(Color.parseColor("#00000000"));
        LayoutParams layoutParamsButtonScan = new LayoutParams(-2, this.dip2px(this.activity, 50.0F));
        layoutParamsButtonScan.gravity = 17;
        layoutParamsButtonScan.weight = 1.0F;
        this.buttonScan = new Button(this.getActivity());
        this.buttonScan.setLayoutParams(layoutParamsButtonScan);
        this.buttonScan.setBackground(ZxingUtil.getInstance().getAssetsDrawable(this.activity, "scan_code.png"));
        layoutButtonBottom.addView(this.buttonScan);
        LayoutParams layoutParamsButtonCard = new LayoutParams(-2, -2);
        layoutParamsButtonCard.gravity = 17;
        layoutParamsButtonCard.weight = 1.0F;
        this.buttonCard = new Button(this.getActivity());
        this.buttonCard.setLayoutParams(layoutParamsButtonCard);
        this.buttonCard.setBackground(ZxingUtil.getInstance().getAssetsDrawable(this.activity, "scan_card.png"));
        layoutButtonBottom.addView(this.buttonCard);
        CameraManager.init(this.activity.getApplicationContext());
        this.hasSurface = false;
        this.code_back.setBackground(ZxingUtil.getInstance().getAssetsDrawable(this.activity, "back_info.png"));
        this.code_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decodeResult2("");
                CaptureFragment.this.activity.finish();
            }
        });
        this.code_photo.setBackgroundDrawable(ZxingUtil.getInstance().getAssetsDrawable(this.activity, "picture_btn.png"));
        this.code_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1;
                if(VERSION.SDK_INT < 20) {
                    intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction("android.intent.action.GET_CONTENT");
                    CaptureFragment.this.startActivityForResult(intent1, 0);
                } else {
                    intent1 = new Intent();
                    intent1.setType("image/*");
                    intent1.setAction("android.intent.action.OPEN_DOCUMENT");
                    CaptureFragment.this.startActivityForResult(intent1, 1);
                }

            }
        });
        this.light.setButtonDrawable(ZxingUtil.getInstance().getRadioButtonBg(this.activity, "light.png", "light.png"));
        this.light.setBackground(ZxingUtil.getInstance().getRadioButtonBg(this.activity, "light_off.png", "light_on.png"));
        this.light.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CaptureFragment.this.lightSwitch(isChecked);
            }
        });
        this.buttonCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        this.initBeepSound();
        return layoutRoot;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume-----");
        System.out.println("activyty取景器大小-----width--" + this.viewfinderView.getWidth() + "---height---" + this.viewfinderView.getHeight());
        this.surfaceHolder = this.surfaceView.getHolder();
        if(this.hasSurface) {
            this.initCamera(this.surfaceHolder);
        } else {
            this.surfaceHolder.addCallback(this);
            this.surfaceHolder.setType(3);
        }

        this.decodeFormats = null;
        this.characterSet = null;
        this.playBeep = true;
        AudioManager audioService = (AudioManager)this.activity.getSystemService(Context.AUDIO_SERVICE);
        if(audioService.getRingerMode() != 2) {
            this.playBeep = false;
        }

        this.vibrate = true;
    }

    private int dip2px(Context activity, float dipValue) {
        float m = activity.getResources().getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5F);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.handler != null) {
            this.handler.quitSynchronously();
            this.handler = null;
        }

        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException var3) {
            return;
        } catch (RuntimeException var4) {
            return;
        }

        if(this.handler == null) {
            this.handler = new CaptureActivityHandler(this, this.decodeFormats, this.characterSet);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println("surfaceChanged-----------------");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("surfaceCreated-----------------");
        if(!this.hasSurface) {
            this.hasSurface = true;
            this.initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("surfaceDestroyed-----------------");
        this.hasSurface = false;
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return this.viewfinderView;
    }

    @Override
    public Handler getHandler() {
        return this.handler;
    }

    @Override
    public void drawViewfinder() {
        this.viewfinderView.drawViewfinder();
    }

    @Override
    public Activity getCurrentActivity() {
        return this.activity;
    }

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        this.viewfinderView.drawResultBitmap(barcode);
        this.playBeepSoundAndVibrate();
        this.photoDecode = false;
        if(this.activity instanceof CaptureActivity) {
            this.decodeResult2(result.getText());
        } else {
            this.decodeResult(result.getText());
        }

    }

    private void initBeepSound() {
        if(this.playBeep && this.mediaPlayer == null) {
            this.activity.setVolumeControlStream(3);
            this.mediaPlayer = new MediaPlayer();
            this.mediaPlayer.setAudioStreamType(3);
            this.mediaPlayer.setOnCompletionListener(this.beepListener);

            try {
                AssetManager assetMg = this.activity.getAssets();
                AssetFileDescriptor fileDescriptor = assetMg.openFd("beep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException var3) {
                this.mediaPlayer = null;
            }
        }

    }

    private void playBeepSoundAndVibrate() {
        if(this.playBeep && this.mediaPlayer != null) {
            this.mediaPlayer.start();
        }

        if(this.vibrate) {
            Vibrator vibrator = (Vibrator)this.activity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200L);
        }

    }

    public void lightSwitch(boolean isOn) {
        LightControl lightControl = new LightControl();
        if(isOn) {
            this.isShow = true;
            lightControl.turnOn();
        } else {
            this.isShow = false;
            lightControl.turnOff();
        }

    }

    public void setResultListener(DecodeQRCodeResult decodeQRCodeResult) {
        this.decodeQRCodeResult = decodeQRCodeResult;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("capture----------onActivityResult");
        if(data != null) {
            Uri photoUri = data.getData();
            switch(requestCode) {
                case 0:
                    this.photoDecode = true;
                    System.out.println("KITKAT_LESS----<4.4:" + photoUri);
                    String newPath1 = this.getCacheDir(this.activity.getPackageName()).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
                    this.copyFile(this.getPath(this.activity, data.getData()), newPath1, false);
                    this.compressImage(newPath1);
                    String string1 = (new QRCodeUtil(this.activity)).DecodeQRCode(BitmapFactory.decodeFile(newPath1));
                    this.deleteFile(newPath1);
                    if(this.activity instanceof CaptureActivity) {
                        this.decodeResult2(string1);
                    } else {
                        this.decodeResult(string1);
                    }
                    break;
                case 1:
                    this.photoDecode = true;
                    String thePath = this.getPath(this.activity, photoUri);
                    System.out.println("KITKAT_ABOVE---->4.4:" + thePath);
                    String newPath2 = this.getCacheDir(this.activity.getPackageName()).getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg";
                    this.copyFile(thePath, newPath2, false);
                    this.compressImage(newPath2);
                    String string2 = (new QRCodeUtil(this.activity)).DecodeQRCode(BitmapFactory.decodeFile(newPath2));
                    this.deleteFile(newPath2);
                    if(this.activity instanceof CaptureActivity) {
                        this.decodeResult2(string2);
                    } else {
                        this.decodeResult(string2);
                    }
                    break;
                case 2:
                    this.photoDecode = true;
                    System.out.println("onActivityResult-------PHOTO_REQ");
                    Bitmap bitmap = (Bitmap)data.getParcelableExtra("data");
                    if(bitmap != null) {
                        this.decodeResult((new QRCodeUtil(this.activity)).DecodeQRCode(bitmap));
                    } else {
                        Toast.makeText(this.activity, "二维码图片获取失败！", Toast.LENGTH_LONG).show();
                    }
            }

        }
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();
        }

    }

    private File getCacheDir(String packageName) {
        File dir = new File("/data/data/" + packageName + "/" + "CaptureImageCache");
        if(dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    private void compressImage(String filePath) {
        Options newOpts = new Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800.0F;
        float ww = 480.0F;
        int be = 1;
        if(w >= h && (float)w > ww) {
            be = (int)((float)newOpts.outWidth / ww);
        } else if(w < h && (float)h > hh) {
            be = (int)((float)newOpts.outHeight / hh);
        }

        if(be <= 0) {
            be = 1;
        }

        newOpts.inSampleSize = be;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, newOpts);
        File file = new File(filePath);
        if(file.exists()) {
            file.delete();

            try {
                file.createNewFile();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

            try {
                FileOutputStream output = new FileOutputStream(file);
                bitmap.compress(CompressFormat.JPEG, 80, output);
                output.flush();
                output.close();
            } catch (FileNotFoundException var11) {
                ;
            } catch (IOException var12) {
                ;
            }

        }
    }

    private void copyFile(String oldPath, String newPath, boolean delete) {
        try {
            int bytesum = 0;
            File oldfile = new File(oldPath);
            if(oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[5120];

                int byteread;
                while((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }

                inStream.close();
                if(delete) {
                    oldfile.delete();
                }
            }
        } catch (Exception var10) {
            ;
        }

    }

    private void cropPicture(final Uri uri) {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                Intent innerIntent = new Intent("com.android.camera.action.CROP");
                innerIntent.setDataAndType(uri, "image/*");
                innerIntent.putExtra("crop", "true");
                innerIntent.putExtra("aspectX", 1);
                innerIntent.putExtra("aspectY", 1);
                innerIntent.putExtra("outputX", 320);
                innerIntent.putExtra("outputY", 320);
                innerIntent.putExtra("return-data", true);
                innerIntent.putExtra("scale", true);
                CaptureFragment.this.startActivityForResult(innerIntent, 2);
            }
        }, 100L);
    }

    public Bitmap getBitmap(Uri uri) {
        ContentResolver resolver = this.activity.getContentResolver();
        Object var3 = null;

        try {
            byte[] mContent = readStream(resolver.openInputStream(uri));
            return getPicFromBytes(mContent, (Options)null);
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
            return null;
        } catch (Exception var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static Bitmap getPicFromBytes(byte[] bytes, Options opts) {
        return bytes != null?(opts != null?BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts):BitmapFactory.decodeByteArray(bytes, 0, bytes.length)):null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int len;
        while((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public void decodeResult(String result) {
        if(this.decodeQRCodeResult != null) {
            this.decodeQRCodeResult.result(result);
        }

        if(!result.equals("")) {
            this.activity.finish();
        }

        if(this.photoDecode) {
            if(this.handler == null) {
                return;
            }

            this.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CaptureFragment.this.drawViewfinder();
                    if(CaptureFragment.this.handler != null) {
                        CaptureFragment.this.handler.quitSynchronously();
                        CaptureFragment.this.handler = null;
                    }

                    CaptureFragment.this.decodeFormats = null;
                    CaptureFragment.this.characterSet = null;
                    if(CaptureFragment.this.handler == null) {
                        CaptureFragment.this.handler = new CaptureActivityHandler(CaptureFragment.this.captureFragment, CaptureFragment.this.decodeFormats, CaptureFragment.this.characterSet);
                    }

                }
            }, 2000L);
            this.photoDecode = false;
        }

    }

    public void decodeResult2(String result) {
        Intent intent = new Intent();
        intent.setAction(ZxingUtil.receiver_action);
        intent.putExtra("result", result);
        this.activity.sendBroadcast(intent);
        if(!result.isEmpty()) {
            this.activity.finish();
        }

    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{"_data"};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, (String)null);
            if(cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow("_data");
                String var10 = cursor.getString(index);
                return var10;
            }
        } finally {
            if(cursor != null) {
                cursor.close();
            }

        }

        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @SuppressLint({"NewApi"})
    private String getPath(Context context, Uri uri) {
        boolean isKitKat = VERSION.SDK_INT >= 19;
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            String docId;
            String[] split;
            String type;
            if(isExternalStorageDocument(uri)) {
                docId = DocumentsContract.getDocumentId(uri);
                split = docId.split(":");
                type = split[0];
                StorageManager sm = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);

                try {
                    String[] paths = (String[])sm.getClass().getMethod("getVolumePaths", (Class[])null).invoke(sm, (Object[])null);

                    for(int i = 0; i < paths.length; ++i) {
                        String filepath = paths[i] + "/" + split[1];
                        File file = new File(filepath);
                        if(file.exists()) {
                            return filepath;
                        }
                    }
                } catch (IllegalAccessException var12) {
                    ;
                } catch (IllegalArgumentException var13) {
                    ;
                } catch (InvocationTargetException var14) {
                    ;
                } catch (NoSuchMethodException var15) {
                    ;
                }
            } else {
                if(isDownloadsDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId).longValue());
                    return this.getDataColumn(context, contentUri, (String)null, (String[])null);
                }

                if(isMediaDocument(uri)) {
                    docId = DocumentsContract.getDocumentId(uri);
                    split = docId.split(":");
                    type = split[0];
                    Uri contentUri = null;
                    if("image".equals(type)) {
                        contentUri = Media.EXTERNAL_CONTENT_URI;
                    } else if("video".equals(type)) {
                        contentUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if("audio".equals(type)) {
                        contentUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return this.getDataColumn(context, contentUri, "_id=?", selectionArgs);
                }

                if("content".equalsIgnoreCase(uri.getScheme())) {
                    if(isGooglePhotosUri(uri)) {
                        return uri.getLastPathSegment();
                    }

                    return this.getDataColumn(context, uri, (String)null, (String[])null);
                }

                if("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
        } else {
            if("content".equalsIgnoreCase(uri.getScheme())) {
                if(isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }

                return this.getDataColumn(context, uri, (String)null, (String[])null);
            }

            if("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }
}
