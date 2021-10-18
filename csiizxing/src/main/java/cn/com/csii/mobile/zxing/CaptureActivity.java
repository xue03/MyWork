//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import cn.com.csii.mobile.zxing.interfaces.DecodeQRCodeResult;
import cn.com.csii.mobile.zxing.util.ZxingUtil;

public class CaptureActivity extends FragmentActivity {
    public Context context;
    private CaptureFragment scanFragment;
    private FrameLayout frame;
    private String scan_type = null;
    private Button buttonCard;
    private Button buttonScan;
    private QRCodeUtil codeUtil;
    private CardFragment cardFragment;

    public CaptureActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        this.cardFragment = null;
        this.scan_type = this.getIntent().getStringExtra("scan_type");
        LinearLayout layoutRoot = new LinearLayout(this);
        LayoutParams params = new LayoutParams(-1, -1);
        params.gravity = 1;
        layoutRoot.setLayoutParams(params);
        layoutRoot.setOrientation(LinearLayout.VERTICAL);
        LayoutParams layoutParamsTop = new LayoutParams(-1, -2);
        layoutParamsTop.weight = 1.0F;
        LinearLayout layoutButtonTop = new LinearLayout(this);
        layoutButtonTop.setLayoutParams(layoutParamsTop);
        layoutButtonTop.setOrientation(LinearLayout.HORIZONTAL);
        layoutButtonTop.setBackgroundColor(Color.parseColor("#00000000"));
        android.widget.RelativeLayout.LayoutParams params_frame = new android.widget.RelativeLayout.LayoutParams(-1, -1);
        this.frame = new FrameLayout(this);
        this.frame.setLayoutParams(params_frame);
        layoutButtonTop.addView(this.frame);
        LayoutParams layoutParamsBottom = new LayoutParams(-1, -2);
        LinearLayout layoutButtonBottom = new LinearLayout(this);
        layoutButtonBottom.setLayoutParams(layoutParamsBottom);
        layoutButtonBottom.setOrientation(LinearLayout.HORIZONTAL);
        layoutButtonBottom.setBackgroundColor(Color.parseColor("#00000000"));
        LayoutParams layoutParamsButtonScan = new LayoutParams(-2, -2);
        layoutParamsButtonScan.gravity = 17;
        layoutParamsButtonScan.topMargin = this.dip2px(this, 10.0F);
        layoutParamsButtonScan.rightMargin = this.dip2px(this, 40.0F);
        layoutParamsButtonScan.leftMargin = this.dip2px(this, 40.0F);
        layoutParamsButtonScan.bottomMargin = this.dip2px(this, 10.0F);
        layoutParamsButtonScan.weight = 1.0F;
        this.buttonScan = new Button(this);
        this.buttonScan.setLayoutParams(layoutParamsButtonScan);
        this.buttonScan.setBackground(ZxingUtil.getInstance().getAssetsDrawable(this, "scan_code.png"));
        layoutButtonBottom.addView(this.buttonScan);
        LayoutParams layoutParamsButtonCard = new LayoutParams(-2, -2);
        layoutParamsButtonCard.gravity = 17;
        layoutParamsButtonCard.topMargin = this.dip2px(this, 10.0F);
        layoutParamsButtonCard.rightMargin = this.dip2px(this, 40.0F);
        layoutParamsButtonCard.leftMargin = this.dip2px(this, 40.0F);
        layoutParamsButtonCard.bottomMargin = this.dip2px(this, 10.0F);
        layoutParamsButtonCard.weight = 1.0F;
        this.buttonCard = new Button(this);
        this.buttonCard.setLayoutParams(layoutParamsButtonCard);
        this.buttonCard.setBackground(ZxingUtil.getInstance().getAssetsDrawable(this, "scan_card_default.png"));
        layoutButtonBottom.addView(this.buttonCard);
        layoutRoot.addView(layoutButtonTop);
        if(this.scan_type == "1") {
            layoutRoot.addView(layoutButtonBottom);
        }

        this.setContentView(layoutRoot);
        this.codeUtil = new QRCodeUtil(this);
        this.scanFragment = this.codeUtil.getQRCodeFragment(new DecodeQRCodeResult() {
            @Override
            public void result(String result) {
                Toast.makeText(CaptureActivity.this, "解析结果：---" + result, Toast.LENGTH_SHORT).show();
            }
        });
        this.frame.setId(View.generateViewId());
        this.getSupportFragmentManager().beginTransaction().replace(this.frame.getId(), this.scanFragment, "scanFragment").commit();
        this.buttonScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.buttonScan.setBackground(ZxingUtil.getInstance().getAssetsDrawable(CaptureActivity.this, "scan_code.png"));
                CaptureActivity.this.buttonCard.setBackground(ZxingUtil.getInstance().getAssetsDrawable(CaptureActivity.this, "scan_card_default.png"));
                if(CaptureActivity.this.cardFragment != null) {
                    CaptureActivity.this.switchFragment(CaptureActivity.this.scanFragment, CaptureActivity.this.cardFragment);
                }

                System.out.println("Fragment========scanFragment");
            }
        });
        this.buttonCard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CaptureActivity.this.buttonScan.setBackground(ZxingUtil.getInstance().getAssetsDrawable(CaptureActivity.this, "scan_code_default.png"));
                CaptureActivity.this.buttonCard.setBackground(ZxingUtil.getInstance().getAssetsDrawable(CaptureActivity.this, "scan_card.png"));
                if(CaptureActivity.this.cardFragment == null) {
                    CaptureActivity.this.cardFragment = new CardFragment();
                }

                CaptureActivity.this.switchFragment(CaptureActivity.this.cardFragment, CaptureActivity.this.scanFragment);
                System.out.println("Fragment========cardFragment");
            }
        });
    }

    private int dip2px(Context activity, float dipValue) {
        float m = activity.getResources().getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5F);
    }

    public void switchFragment(Fragment f, Fragment pref) {
        this.getSupportFragmentManager().beginTransaction().remove(pref).replace(this.frame.getId(), f).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scanFragment.decodeResult2("");
        }
        return super.onKeyDown(keyCode, event);
    }
}
