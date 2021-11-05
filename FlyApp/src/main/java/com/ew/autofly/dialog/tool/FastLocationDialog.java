package com.ew.autofly.dialog.tool;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ew.autofly.R;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.utils.coordinate.CoordConvertManager;
import com.ew.autofly.utils.StringUtils;
import com.ew.autofly.utils.ToastUtil;



public class FastLocationDialog extends DialogFragment implements View.OnClickListener {
    private EditText lon1, lon2, lon3, lat1, lat2, lat3;
    private TextView btnClean, btnLocator;
    private ImageView mBtnLocateSwitch;
    private LinearLayout mLlLocate;
    private LinearLayout mLlLocateTen;
    private EditText mEtLonTen;
    private EditText mEtLatTen;
    private OnFastLocationConfirm onFastLocationConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.45), ViewGroup.LayoutParams.WRAP_CONTENT);
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.45), ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCanceledOnTouchOutside(false);
        }
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fast_location_setting, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        lon1 = (EditText) view.findViewById(R.id.et_lon_1);
        lon1.addTextChangedListener(new MyTextWatcher(-180, 180, lon1));
        lon2 = (EditText) view.findViewById(R.id.et_lon_2);
        lon2.addTextChangedListener(new MyTextWatcher(0, 60, lon2));
        lon3 = (EditText) view.findViewById(R.id.et_lon_3);
        lon3.addTextChangedListener(new MyTextWatcher(0, 60, lon3));
        lat1 = (EditText) view.findViewById(R.id.et_lat_1);
        lat1.addTextChangedListener(new MyTextWatcher(-90, 90, lat1));
        lat2 = (EditText) view.findViewById(R.id.et_lat_2);
        lat2.addTextChangedListener(new MyTextWatcher(0, 60, lat2));
        lat3 = (EditText) view.findViewById(R.id.et_lat_3);
        lat3.addTextChangedListener(new MyTextWatcher(0, 60, lat3));

        btnClean = (TextView) view.findViewById(R.id.btn_clean);
        btnClean.setOnClickListener(this);
        btnLocator = (TextView) view.findViewById(R.id.btn_locator);
        btnLocator.setOnClickListener(this);

        mBtnLocateSwitch = (ImageView) view.findViewById(R.id.btn_locate_switch);
        mBtnLocateSwitch.setOnClickListener(this);
        mLlLocate = (LinearLayout) view.findViewById(R.id.ll_locate);
        mLlLocateTen = (LinearLayout) view.findViewById(R.id.ll_locate_ten);

        mEtLonTen = (EditText) view.findViewById(R.id.et_lon_ten);
        mEtLonTen.addTextChangedListener(new MyDecimalTextWatcher(-180, 180, mEtLonTen));
        mEtLatTen = (EditText) view.findViewById(R.id.et_lat_ten);
        mEtLatTen.addTextChangedListener(new MyDecimalTextWatcher(-90, 90, mEtLatTen));
    }

    private void clearLatLng() {
        lon1.setText("");
        lon2.setText("");
        lon3.setText("");
        lat1.setText("");
        lat2.setText("");
        lat3.setText("");
    }

    private void clearDecimalLatLng() {
        mEtLonTen.setText("");
        mEtLatTen.setText("");
    }

    private LatLngInfo getLatLng() {
        String slon1, slon2, slon3, slat1, slat2, slat3;
        slon1 = lon1.getText().toString();
        slon2 = lon2.getText().toString();
        slon3 = lon3.getText().toString();
        slat1 = lat1.getText().toString();
        slat2 = lat2.getText().toString();
        slat3 = lat3.getText().toString();
        /*if (StringUtils.isEmptyOrNull(slon1) || StringUtils.isEmptyOrNull(slat1)) {
            ToastUtil.show(getActivity(), "请填写度数");
            return;
        }*/

        if (StringUtils.isEmptyOrNull(slon1)) {
            slon1 = "0";
        }

        if (StringUtils.isEmptyOrNull(slon2)) {
            slon2 = "0";
        }
        if (StringUtils.isEmptyOrNull(slon3) || slon3.endsWith(".")) {
            slon3 += "0";
            if (slon3.startsWith(".")) {
                slon3 = "0" + slon3;
            }
        }

        if (StringUtils.isEmptyOrNull(slat1)) {
            slat1 = "0";
        }

        if (StringUtils.isEmptyOrNull(slat2)) {
            slat2 = "0";
        }
        if (StringUtils.isEmptyOrNull(slat3) || slat3.endsWith(".")) {
            slat3 += "0";
            if (slat3.startsWith(".")) {
                slat3 = "0" + slat3;
            }
        }

        double lon = Double.parseDouble(slon1)
                + Double.parseDouble(slon2) / 60
                + Double.parseDouble(slon3) / 3600;
        double lat = Double.parseDouble(slat1)
                + Double.parseDouble(slat2) / 60
                + Double.parseDouble(slat3) / 3600;

        return new LatLngInfo(lat, lon);
    }

    private LatLngInfo getDecimalLatLng() {
        String slon, slat;
        slon = mEtLonTen.getText().toString();
        slat = mEtLatTen.getText().toString();

        double lon = Double.parseDouble(slon);
        double lat = Double.parseDouble(slat);

        return new LatLngInfo(lat, lon);
    }

    private void onClickBtnSwitch() {

        try {
            if (mLlLocate.getVisibility() == View.VISIBLE) {

                LatLngInfo latLng = getLatLng();

                mLlLocate.setVisibility(View.GONE);
                mLlLocateTen.setVisibility(View.VISIBLE);

                mEtLonTen.setText(latLng.longitude + "");
                mEtLatTen.setText(latLng.latitude + "");

            } else {

                LatLngInfo latLng = getDecimalLatLng();

                mLlLocate.setVisibility(View.VISIBLE);
                mLlLocateTen.setVisibility(View.GONE);

                int lon_du = (int) Math.floor(Math.abs(latLng.longitude));
                double lon_temp = CoordConvertManager.getdPoint(Math.abs(latLng.longitude)) * 60;
                int lon_fen = (int) Math.floor(lon_temp);
                double lon_miao = CoordConvertManager.getdPoint(lon_temp) * 60;

                lon1.setText(lon_du + "");
                lon2.setText(lon_fen + "");
                lon3.setText(lon_miao + "");

                int lat_du = (int) Math.floor(Math.abs(latLng.latitude));
                double lat_temp = CoordConvertManager.getdPoint(Math.abs(latLng.latitude)) * 60;
                int lat_fen = (int) Math.floor(lat_temp);
                double lat_miao = CoordConvertManager.getdPoint(lat_temp) * 60;

                lat1.setText(lat_du + "");
                lat2.setText(lat_fen + "");
                lat3.setText(lat_miao + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            clearLatLng();
            clearDecimalLatLng();
        }

    }

    private class MyTextWatcher implements TextWatcher {
        private int numSmall = -180, numBig = 180;
        private EditText mView;

        public MyTextWatcher(int small, int big, EditText view) {
            numSmall = small;
            numBig = big;
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (start > 0) {
                try {
                    String tmp = s.toString();
                    if (tmp.endsWith(".")) {
                        tmp += "0";
                    }
                    double num = Double.parseDouble(tmp);
                    if (num > numBig || num < numSmall) {
                        mView.setText("");
                        ToastUtil.show(getActivity(), "数值越界,请检查后重新输入.");
                    }
                } catch (Exception ex) {

                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    private class MyDecimalTextWatcher implements TextWatcher {
        private int numSmall = -180, numBig = 180;
        private EditText mView;

        public MyDecimalTextWatcher(int small, int big, EditText view) {
            numSmall = small;
            numBig = big;
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (start > 0) {
                try {
                    String tmp = s.toString();
                    double num = Double.parseDouble(tmp);
                    if (num > numBig || num < numSmall) {
                        mView.setText("");
                        ToastUtil.show(getActivity(), "数值越界,请检查后重新输入.");
                    }
                } catch (Exception ex) {

                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;
            case R.id.btn_clean: //清空
                clearLatLng();
                clearDecimalLatLng();
                break;
            case R.id.btn_locator:
                this.dismiss();
                LatLngInfo latLng;
                if (mLlLocate.getVisibility() == View.VISIBLE) {
                    latLng = getLatLng();
                } else {
                    latLng = getDecimalLatLng();
                }
                LatLngInfo gcj = new LatLngInfo(latLng.latitude, latLng.longitude);
                onFastLocationConfirm.onLocationConfirm(gcj);
                break;
            case R.id.btn_locate_switch:
                onClickBtnSwitch();
                break;
        }
    }

    public interface OnFastLocationConfirm {
        void onLocationConfirm(LatLngInfo latLngInfo);
    }

    public void setOnFastLocaionConfirm(OnFastLocationConfirm onFastLocaionConfirm) {
        this.onFastLocationConfirm = onFastLocaionConfirm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
