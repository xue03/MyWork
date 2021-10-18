package com.work.mywork.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.work.mywork.R;

/**
 * Date:2021/10/15
 * Description:
 * Author:XueTingTing
 */
public class CityDialog extends Dialog {

    public CityDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_item);

    }

    @Override
    public void show() {
        super.show();
    }
}
