package com.work.mywork.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;
import com.work.mywork.interfaces.ResultCallBack;
import com.work.mywork.utils.UMengShareUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**开发文档
 * https://developer.umeng.com/docs/128606/detail/193883#h2-u5206u4EABu89C6u98916
 */

public class ShareBoardActivity extends BaseActivity {

    @BindView(R.id.txt_activity_title)
    TextView txtActivityTitle;
    @BindView(R.id.btn_shareboard)
    Button btnShareboard;
    @BindView(R.id.btn_shareImage)
    Button btnShareImage;
    @BindView(R.id.btn_shareVideo)
    Button btnShareVideo;
    private UMShareListener mShareListener;
    private ShareAction mShareAction;

    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
//        mShareListener = new CustomShareListener(this);
//        /*增加自定义按钮的分享面板*/
//        mShareAction = new ShareAction(ShareBoardActivity.this).setDisplayList(
//                SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
//                SHARE_MEDIA.WXWORK,
//                SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
//                SHARE_MEDIA.ALIPAY, SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN,
//                SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL, SHARE_MEDIA.YNOTE,
//                SHARE_MEDIA.EVERNOTE, SHARE_MEDIA.LAIWANG, SHARE_MEDIA.LAIWANG_DYNAMIC,
//                SHARE_MEDIA.LINKEDIN, SHARE_MEDIA.YIXIN, SHARE_MEDIA.YIXIN_CIRCLE,
//                SHARE_MEDIA.TENCENT, SHARE_MEDIA.FACEBOOK, SHARE_MEDIA.TWITTER,
//                SHARE_MEDIA.WHATSAPP, SHARE_MEDIA.GOOGLEPLUS, SHARE_MEDIA.LINE,
//                SHARE_MEDIA.INSTAGRAM, SHARE_MEDIA.KAKAO, SHARE_MEDIA.PINTEREST,
//                SHARE_MEDIA.POCKET, SHARE_MEDIA.TUMBLR, SHARE_MEDIA.FLICKR,
//                SHARE_MEDIA.FOURSQUARE, SHARE_MEDIA.MORE)
//                .addButton("复制文本", "复制文本", "umeng_socialize_copy", "umeng_socialize_copy")
//                .addButton("复制链接", "复制链接", "umeng_socialize_copyurl", "umeng_socialize_copyurl")
//                .setShareboardclickCallback(new ShareBoardlistener() {
//                    @Override
//                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
//                        if (snsPlatform.mShowWord.equals("复制文本")) {
//                            Toast.makeText(ShareBoardActivity.this, "复制文本按钮", Toast.LENGTH_LONG).show();
//                        } else if (snsPlatform.mShowWord.equals("复制链接")) {
//                            Toast.makeText(ShareBoardActivity.this, "复制链接按钮", Toast.LENGTH_LONG).show();
//
//                        } else {
//                            UMWeb web = new UMWeb("http://mobile.umeng.com/social");
//                            web.setTitle("来自分享面板标题");
//                            web.setDescription("来自分享面板内容");
//                            web.setThumb(new UMImage(ShareBoardActivity.this, R.drawable.logo));
//                            new ShareAction(ShareBoardActivity.this).withMedia(web)
//                                    .setPlatform(share_media)
//                                    .setCallback(mShareListener)
//                                    .share();
//                        }
//                    }
//                });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_umshare;
    }

    @OnClick({R.id.btn_shareboard, R.id.btn_shareImage,R.id.btn_shareVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shareboard:

                UMengShareUtil.shareUMText(this, "友盟分享纯文本", new ResultCallBack() {
                    @Override
                    public void Success(Object string) {
                        Log.d("ShareBoardActivity", "Success: " + string.toString());
                    }

                    @Override
                    public void Filed(String error) {

                    }
                });
                break;
            case R.id.btn_shareImage:
                UMengShareUtil.shareUMBitmap(this, new ResultCallBack() {
                    @Override
                    public void Success(Object string) {
                        Log.d("ShareBoardActivity", "Success: " + string.toString());
                    }

                    @Override
                    public void Filed(String error) {

                    }
                });
                break;
            case R.id.btn_shareVideo:
                UMengShareUtil.shareUMVideo(this, new ResultCallBack() {
                    @Override
                    public void Success(Object string) {
                        Log.d("ShareBoardActivity", "Success: " + string.toString());
                    }

                    @Override
                    public void Filed(String error) {

                    }
                });
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

}
