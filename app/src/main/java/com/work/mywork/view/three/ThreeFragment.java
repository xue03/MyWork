package com.work.mywork.view.three;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.Error;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.work.mywork.R;
import com.work.mywork.base.BaseFragment;
import com.work.mywork.dialog.CustomDialog;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.interfaces.ResultCallBack;
import com.work.mywork.utils.CheckPermission;
import com.work.mywork.utils.OcrCardUtil;
import com.work.mywork.utils.OcrFaceUtil;
import com.work.mywork.utils.Permissions;
import com.work.mywork.utils.SpUtils;
import com.work.mywork.view.LoginActivity;
import butterknife.BindView;
import butterknife.OnClick;

public class ThreeFragment extends BaseFragment {
    private static String TAG = "ThreeFragment";

    @BindView(R.id.txt_login)
    TextView login;
    @BindView(R.id.txt_login2)
    TextView login2;
    @BindView(R.id.btn_show)
    Button btnShow;
    @BindView(R.id.txt_exit)
    TextView exit;

    private static boolean isLogin = false;
    @BindView(R.id.btn_face)
    Button btnFace;
    @BindView(R.id.img_service)
    ImageView imgService;
    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        Boolean token = SpUtils.getInstance().getBoolean("token");
        String name = SpUtils.getInstance().getString("name");
        isLogin = token;
        if (isLogin) {
            login.setVisibility(View.GONE);
            exit.setVisibility(View.VISIBLE);
            login2.setText(name);
        } else {
            login.setVisibility(View.VISIBLE);
            exit.setVisibility(View.GONE);
        }

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_three;
    }

    @OnClick({R.id.txt_login, R.id.txt_login2, R.id.btn_show, R.id.txt_exit, R.id.btn_face, R.id.btn_idCard, R.id.img_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_service:
                String userName = "1613535005";
                String password = "031600";
                //注册环信
                ChatClient.getInstance().register(userName, password, new Callback() {
                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //获取登录状态
                                if (ChatClient.getInstance().isLoggedInBefore()) {
                                    //已经登录，可以直接进入会话界面
                                    goTelk();
                                } else {
                                    //未登录，需要登录后，再进入会话界面
                                    goLogin(userName, password);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(int code, String error) {
//                        Error.NETWORK_ERROR 网络不可用
//                        Error.USER_ALREADY_EXIST  用户已存在
//                        Error.USER_AUTHENTICATION_FAILED 无开放注册权限（后台管理界面设置[开放|授权]）
//                        Error.USER_ILLEGAL_ARGUMENT 用户名非法
                        Log.e(TAG + "kefu", "onError: code:" + code + "  error:" + error);
                        if (code == 203) {
                            goLogin(userName, password);
                        } else if (code == Error.NETWORK_ERROR) {

                        }
                    }

                    @Override
                    public void onProgress(int progress, String status) {


                    }
                });
                break;
            case R.id.txt_login:
            case R.id.txt_login2:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.btn_show:
                CustomDialog customDialog = new CustomDialog(getContext());
                customDialog.setDesc("弹窗");
                customDialog.setTitle("弹窗");
                customDialog.setOnDialogCancelListener(new CustomDialog.OnDialogCancelListener() {
                    @Override
                    public void onDialogCancelListener() {
                        customDialog.dismiss();
                    }
                });
                customDialog.setOnDialogOklListener(new CustomDialog.OnDialogOklListener() {
                    @Override
                    public void onDialogOkListener() {
                        Toast.makeText(getContext(), "展示成功", Toast.LENGTH_LONG).show();
                        customDialog.dismiss();
                    }
                });
                customDialog.show();
                break;
            case R.id.txt_exit:
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("您确定是否退出?")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                SpUtils.getInstance().setValue("token", false);
                            }
                        })
                        .create();
                alertDialog.show();
                break;
            case R.id.btn_face:
                //活体检测
                if (CheckPermission.checkPermission(getContext(), getActivity(), Permissions.CAMERA, 200)) {
                    Toast.makeText(getActivity(), "开始活体检测", Toast.LENGTH_LONG).show();
                    String licence = "Mzg1NDE0bm9kZXZpY2Vjd2F1dGhvcml6ZZfk4+bn5+Tq3+bg5efm5Of65Obn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+er4Ofr5+vn64vn5+Tm5+bn";
                    OcrFaceUtil.setLicence(licence, getActivity());
                    OcrFaceUtil.startOpenFace(new ResultCallBack() {
                        @Override
                        public void Success(Object string) {
                            Log.e("OcrFace:succecc:", string.toString());
                        }

                        @Override
                        public void Filed(String error) {
                            Log.e("OcrFace:file:", error);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CONTACTS, 200);
                }
                break;
            case R.id.btn_idCard:
                //身份认证
                if (CheckPermission.checkPermission(getContext(), getActivity(), Permissions.CAMERA, 200)) {
                    Toast.makeText(getActivity(), "开始实名认证", Toast.LENGTH_LONG).show();
                    String licence = "Mzg1NDE0bm9kZXZpY2Vjd2F1dGhvcml6ZZfk4+bn5+Tq3+bg5efm5Of65Obn4Obg5Yjm5uvl5ubrkeXm5uvl5uai6+Xm5uvl5uTm6+Xm5uDm1efr5+vn6+er4Ofr5+vn64vn5+Tm5+bn";
                    OcrCardUtil.setLicence(licence, getActivity(),this);
                    OcrCardUtil.startIdCardCamera("front", new ResultCallBack() {
                        @Override
                        public void Success(Object string) {
                            Log.e("OcrIDCard:succecc:", string.toString());
                        }

                        @Override
                        public void Filed(String error) {
                            Log.e("OcrIDCard:file:", error);
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(getActivity(), Permissions.CONTACTS, 200);
                }
                break;
        }
    }

    /**
     * 登录环信
     *
     * @param userName
     * @param password
     */
    private void goLogin(String userName, String password) {
        ChatClient.getInstance().login(userName, password, new Callback() {
            @Override
            public void onSuccess() {
                //进入会话
                goTelk();
            }

            @Override
            public void onError(int code, String error) {
                Log.e(TAG + "kefu", "onError: code:" + code + "  error:" + error);

            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private void goTelk() {
        Intent intent = new IntentBuilder(getActivity())
                .setServiceIMNumber("kefuchannelimid_038877") //获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“IM服务号”
                .build();
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OcrCardUtil.onActivityCardResult(requestCode, resultCode, data);
    }
}