package com.work.mywork.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.work.mywork.R;

import java.util.List;

/**
 * Date:2021/8/30
 * Description:
 * Author:XueTingTing
 */
public class ChannelAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private int CHANNEL;
    public ImageView select;
    private static boolean mEdit;

    public ChannelAdapter(Context context, List<String> list, int CHANNEL) {
        this.context = context;
        this.list = list;
        this.CHANNEL = CHANNEL;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.channel_item, null);
        TextView title = inflate.findViewById(R.id.tv_title);
        select = inflate.findViewById(R.id.iv_select);
        title.setText(list.get(position));
        if (mEdit){
            select.setVisibility(View.VISIBLE);
            if (CHANNEL == 1) {
                select.setImageResource(R.drawable.ic_clear_24);
            }
            if (CHANNEL == 0) {
                select.setImageResource(R.drawable.ic_add_24);
            }
        }else {
            select.setVisibility(View.INVISIBLE);
        }
        return inflate;
    }

    public void add(String channelName) {
        list.add(channelName);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        if (index > 0 && index < list.size()) {
            list.remove(index);
        }
        notifyDataSetChanged();
    }

    static boolean isEdit() {
        return mEdit;
    }

    static void setEdit(boolean isEdit) {
        mEdit = isEdit;
    }
}
