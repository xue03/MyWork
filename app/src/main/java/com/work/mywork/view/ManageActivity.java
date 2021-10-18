package com.work.mywork.view;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.work.mywork.R;
import com.work.mywork.base.BaseActivity;
import com.work.mywork.interfaces.IBasePresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Date:2021/8/26
 * Description:新闻管理
 * Author:XueTingTing
 */
public class ManageActivity extends BaseActivity {
    private static final String CHANNEL_DATA_FILE = "channel_data.json";

    private List<String> mUserList;
    private List<String> mOtherList;
    @BindView(R.id.image_edit)
    ImageView imageEdit;
    @BindView(R.id.main_grid)
    GridView mainGrid;
    @BindView(R.id.other_grid)
    GridView otherGrid;
    private ChannelAdapter userAdapter;
    private ChannelAdapter otherAdapter;


    @Override
    protected void initData() {

    }

    @Override
    protected IBasePresenter setPresenter() {
        return null;
    }

    @Override
    protected void initView() {
        mUserList = new ArrayList<>();
        mOtherList = new ArrayList<>();
        try {
            InputStream open = getAssets().open(CHANNEL_DATA_FILE);
            int length = open.available();
            byte[] bytes = new byte[length];
            open.read(bytes);
            String result = new String(bytes, "utf-8");
            JSONObject jsonObject = new JSONObject(result);
            JSONArray user = jsonObject.getJSONArray("user");
            JSONArray other = jsonObject.getJSONArray("other");
            for (int i = 0; i < user.length(); i++) {
                mUserList.add(user.optString(i));
            }
            for (int i = 0; i < other.length(); i++) {
                mOtherList.add(other.optString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        userAdapter = new ChannelAdapter(this, mUserList,1);
        otherAdapter = new ChannelAdapter(this, mOtherList,0);
        mainGrid.setAdapter(userAdapter);
        otherGrid.setAdapter(otherAdapter);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_manager;
    }

    @OnClick(R.id.image_edit)
    public void onViewClicked() {
        boolean edit = ChannelAdapter.isEdit();
        ChannelAdapter.setEdit(!edit);

    }

}
