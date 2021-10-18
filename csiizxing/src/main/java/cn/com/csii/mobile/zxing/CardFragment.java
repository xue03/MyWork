//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.com.csii.mobile.zxing;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CardFragment extends Fragment {
    public CardFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TextView textview = new TextView(this.getActivity());
        textview.setText(CardFragment.class.getSimpleName() + "test!");
        textview.setTextColor(-16776961);
        textview.setTextSize(28.0F);
        return textview;
    }
}
