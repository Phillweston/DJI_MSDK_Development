package com.ew.autofly.dialog;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.dialog.ui.adapter.WayPointGridAdapter;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.interfaces.OnSetAltitudeListener;
import com.ew.autofly.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;



public class LatLngAltitudeSettingDlgFragment extends BaseDialogFragment implements View.OnClickListener {
    private TextView tvConfirm;
    private ImageButton ibCancel;
    private List<LatLngInfo> latLngInfoList = new ArrayList<>();
    private List<Tower> selectedTowerList = new ArrayList<>();
    private AirRouteParameter airRouteParameter;
    private GridView gvEdit;
    private WayPointGridAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_waypoint_altitude_setting, container, false);
        bindField(view);
        initData();
        return view;
    }

    private void bindField(View view) {
        gvEdit = (GridView) view.findViewById(R.id.gv_edit);
        ibCancel = (ImageButton) view.findViewById(R.id.ib_cancel);
        tvConfirm = (TextView) view.findViewById(R.id.tv_ok);

        ibCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    private void initData() {
        if (getArguments() != null) {
            latLngInfoList = (List<LatLngInfo>) getArguments().getSerializable("latLngInfoList");
            airRouteParameter = (AirRouteParameter) getArguments().getSerializable("airRoutePara");
            selectedTowerList = (List<Tower>) getArguments().getSerializable("selectedTowerList");
        }
        if (selectedTowerList == null || selectedTowerList.size() == 0) {
            adapter = new WayPointGridAdapter(getContext(), latLngInfoList, airRouteParameter.getFixedAltitudeList());
        } else {
            adapter = new WayPointGridAdapter(getContext(), latLngInfoList, airRouteParameter.getFixedAltitudeList(), selectedTowerList);
        }
        gvEdit.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        OnSetAltitudeListener act = (OnSetAltitudeListener) getFragmentManager().findFragmentByTag(getTag().replace("_altitude_setting", "_patrol"));
        switch (v.getId()) {
            case R.id.tv_ok:
                if (isValid()) {
                    changeAltitude();
                    act.onSetAltitudeComplete(true, null);
                    dismiss();
                }
                break;
            case R.id.ib_cancel:
                act.onSetAltitudeComplete(false, null);
                dismiss();
                break;
        }
    }

    private boolean isValid() {
        for (int i = 0; i < gvEdit.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) gvEdit.getChildAt(i);
            EditText et = (EditText) layout.findViewById(R.id.et_altitude);
            String str = et.getText().toString().trim();
            if (str.equals("")) {
                ToastUtil.show(getActivity(), "航高不能为空!");
                return false;
            }
            if (Integer.parseInt(str) > 495 || Integer.parseInt(str) < 20) {
                ToastUtil.show(getActivity(), "航高范围应设置在20m~495m之间");
                return false;
            }
        }
        return true;
    }

    private void changeAltitude() {

        String listAltitude = "";
        for (int i = 0; i < latLngInfoList.size(); i++) {
            listAltitude += String.valueOf((adapter.getItem(i))) + ",";
            if (selectedTowerList != null && i < selectedTowerList.size()) {
                int altitude = (int) adapter.getItem(i);
                selectedTowerList.get(i).setFlyAltitude(altitude);
            }
        }
        airRouteParameter.setFixedAltitude(false);
        airRouteParameter.setFixedAltitudeList(listAltitude);
    }
}