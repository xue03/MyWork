package com.work.mywork.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.utils.Permissions;
import com.work.mywork.utils.SpUtils;
import com.work.mywork.utils.SystemUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.csii.mobile.zxing.CaptureActivity;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.btn_manage)
    Button btnManage;
    @BindView(R.id.btn_qrCard)
    Button btnQrCard;
    @BindView(R.id.btn_pdf)
    Button btnPdf;
    @BindView(R.id.txt_register)
    TextView txtRegister;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.cb_rememberName)
    CheckBox cbRememberName;
    @BindView(R.id.txt_forgotPsw)
    TextView txtForgotPsw;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private String name;
    private String password;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        String remember = SpUtils.getInstance().getString("remember");
        etName.setText(remember);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_login;
    }

    @OnClick({R.id.txt_register, R.id.et_name, R.id.et_password, R.id.cb_rememberName, R.id.txt_forgotPsw, R.id.btn_login,R.id.btn_manage, R.id.btn_qrCard, R.id.btn_pdf})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_register:
                new Intent(this,RegisterActivity.class);
                break;
            case R.id.et_name:


                break;
            case R.id.et_password:
                break;
            case R.id.cb_rememberName:
                if (cbRememberName.isChecked()){
                    SpUtils.getInstance().setValue("remember",etName.getText().toString().trim());
                }

                break;
            case R.id.txt_forgotPsw:
                startActivity(new Intent());
                break;
            case R.id.btn_login:
                name = etName.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                verify(name,password);
                break;
            case R.id.btn_manage:
                startActivity(new Intent(this, ManageActivity.class));
                break;
            case R.id.btn_qrCard:
                boolean b = CheckPermission.checkPermission(this, this, Permissions.CAMERA, 500);
                if (b) {
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivity(intent);
                } else {
                    ActivityCompat.requestPermissions(this, Permissions.CAMERA, 500);

                }
                break;
            case R.id.btn_pdf:
                startActivity(new Intent(this, ShowPdfActivity.class));
                break;
        }
    }

    /**
     * ????????????????????????
     * @param name ????????????11???????????????
     * @param password 6-12??? ???????????????????????????
     */
    private void verify(String name, String password) {
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(password)){
            Toast.makeText(this,"???????????????????????????",Toast.LENGTH_LONG).show();
            return;
        }
        if (!isTelPhoneNumber(name)){
            Toast.makeText(this,"?????????????????????",Toast.LENGTH_LONG).show();
            return;
        }
        if (!isPswNumber(password)){
            Toast.makeText(this,"??????????????????",Toast.LENGTH_LONG).show();
            return;
        }
        if (isPswNumber(password)&&isTelPhoneNumber(name)){
            SpUtils.getInstance().setValue("token",true);
            SpUtils.getInstance().setValue("name",name);
            finish();
        }
    }
    /**
     * ????????????????????????
     ???1??????1???
     ???2??????{3???4???5???6???7???8???9}???????????????
     ???3???11??????0???9????????????
     * @param value
     * @return
     */
    public static boolean isTelPhoneNumber(String value) {
        if (value != null && value.length() == 11) {
            Pattern pattern = Pattern.compile("^1[3|5|6|7|8|9][0-9]\\d{8}$");
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }
    public static boolean isPswNumber(String value) {
        if (value != null && value.length() == 11) {
            Pattern pattern = Pattern.compile("[a-zA-Z][0-9]{2,12}");
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return false;
    }
}