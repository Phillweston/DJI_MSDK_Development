package com.ew.autofly.fragments.dem;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.fragments.BaseFragment;


public class GisBottomNewFragment extends BaseFragment {
    private TextView txtCoord, txtAlt, txtScale;
    private View mContent;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj != null) {
                        String[] tmp = (String[]) msg.obj;
                        if (tmp.length != 6) {
                            return false;
                        }
                        txtCoord.setText(String.format(getString(R.string.statebar_label_coord), tmp[0], tmp[1], tmp[2], tmp[3]));
                        txtAlt.setText(String.format(getString(R.string.statebar_label_z), tmp[4]));
                        txtScale.setText(String.format(getString(R.string.statebar_label_scale), tmp[5]));
                    }
                    break;

                case 1:
                    if (msg.obj != null) {
                        String[] tmp = (String[]) msg.obj;
                        if (tmp.length != 4) {
                            return false;
                        }
                        txtCoord.setText(String.format(getString(R.string.statebar_label_coord_for_bing), tmp[0], tmp[1]));
                        txtAlt.setText(String.format(getString(R.string.statebar_label_z), tmp[2]));
                        txtScale.setText(String.format(getString(R.string.statebar_label_scale), tmp[3]));
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContent = inflater.inflate(R.layout.layout_statebar, container, false);
        txtCoord = (TextView) mContent.findViewById(R.id.txt_coord);
        txtAlt = (TextView) mContent.findViewById(R.id.txt_z);
        txtScale = (TextView) mContent.findViewById(R.id.txt_scale);
        return mContent;
    }

    public void show() {
        mContent.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mContent.setVisibility(View.INVISIBLE);
    }

    public void setValues(String lon, String lat, String east, String north, String alt, String scale) {
        String[] tmp = new String[6];
        tmp[0] = lon;
        tmp[1] = lat;
        tmp[2] = east;
        tmp[3] = north;
        tmp[4] = alt;
        tmp[5] = scale;
        mHandler.obtainMessage(0, tmp).sendToTarget();
    }

    public void setValues(String lon, String lat, String alt, String scale) {
        String[] tmp = new String[4];
        tmp[0] = lon;
        tmp[1] = lat;
        tmp[2] = alt;
        tmp[3] = scale;
        mHandler.obtainMessage(1, tmp).sendToTarget();
    }

    public void setHide(boolean hide) {
        mContent.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
