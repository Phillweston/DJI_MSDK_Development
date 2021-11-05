package com.ew.autofly.dialog.mission;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.event.flight.LocationStateEvent;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;



public class AddWaypointDialog extends BaseDialogFragment implements View.OnClickListener {

    private TableLayout mTbStatusLatLong;

    private TextView planeLatitudeTv;
    private TextView baseLatitudeTV;
    private TextView planeLongitudeTv;
    private TextView baseLongitudeTv;
    private TextView planeAltitudeTv;
    private TextView baseAltitudeTV;

    private double planeLatitude;
    private double planeLongitude;
    private float planeAltitude;

    private double baseLatitude;
    private double baseLongitude;
    private float baseAltitude;

    @Override
    protected void onCreateSize() {
        setSize(0.5f, ViewGroup.LayoutParams.MATCH_PARENT);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreateBackground() {
        super.onCreateBackground();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_waypoint, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mTbStatusLatLong = (TableLayout) view.findViewById(R.id.tb_status_lat_long);
        initTableLatLong();
        view.findViewById(R.id.tv_ok).setOnClickListener(this);
    }

    private void initTableLatLong() {
        TableRow tableRow1 = new TableRow(getContext());
        TextView row1_1 = createTableType(tableRow1);
        TextView row1_2 = createTableContent(tableRow1);
        row1_2.setText("飞机坐标");
        row1_2.setTextColor(getContext().getResources().getColor(R.color.white_8));
        TextView row1_3 = createTableContent(tableRow1);
        row1_3.setText("基站坐标");
        row1_3.setTextColor(getContext().getResources().getColor(R.color.white_8));

        tableRow1.addView(row1_1);
        tableRow1.addView(row1_2);
        tableRow1.addView(row1_3);

        TableRow tableRow2 = new TableRow(getContext());
        TextView row2_1 = createTableType(tableRow2);
        row2_1.setText("纬度");
        planeLatitudeTv = createTableContent(tableRow2);
        planeLatitudeTv.setText("0.00000000");
        baseLatitudeTV = createTableContent(tableRow2);
        baseLatitudeTV.setText("0.00000000");
        tableRow2.addView(row2_1);
        tableRow2.addView(planeLatitudeTv);
        tableRow2.addView(baseLatitudeTV);

        TableRow tableRow3 = new TableRow(getContext());
        TextView row3_1 = createTableType(tableRow3);
        row3_1.setText("经度");
        planeLongitudeTv = createTableContent(tableRow3);
        planeLongitudeTv.setText("0.00000000");
        baseLongitudeTv = createTableContent(tableRow3);
        baseLongitudeTv.setText("0.00000000");
        tableRow3.addView(row3_1);
        tableRow3.addView(planeLongitudeTv);
        tableRow3.addView(baseLongitudeTv);

        TableRow tableRow4 = new TableRow(getContext());
        TextView row4_1 = createTableType(tableRow4);
        row4_1.setText("高程");
        planeAltitudeTv = createTableContent(tableRow4);
        planeAltitudeTv.setText("0.000");
        baseAltitudeTV = createTableContent(tableRow4);
        baseAltitudeTV.setText("0.000");
        tableRow4.addView(row4_1);
        tableRow4.addView(planeAltitudeTv);
        tableRow4.addView(baseAltitudeTV);

        mTbStatusLatLong.addView(tableRow1);
        mTbStatusLatLong.addView(tableRow2);
        mTbStatusLatLong.addView(tableRow3);
        mTbStatusLatLong.addView(tableRow4);
    }

    private TextView createTableContent(TableRow tableRow) {
        return (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.item_dialog_rtk_status_table_row, tableRow, false);
    }

    private TextView createTableType(TableRow tableRow) {
        return (TextView) LayoutInflater.from(getContext())
                .inflate(R.layout.item_dialog_rtk_status_table_column, tableRow, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationStateEvent(LocationStateEvent locationStateEvent) {

        LocationCoordinate planeLoc = locationStateEvent.getAircraftCoordinate();
        LocationCoordinate baseLoc = locationStateEvent.getBaseCoordinate();

        if (planeLoc != null) {

            planeLatitude = planeLoc.getLatitude();
            planeLongitude = planeLoc.getLongitude();
            planeAltitude = planeLoc.getAltitude();

            baseLatitude = baseLoc.getLatitude();
            baseLongitude = baseLoc.getLongitude();
            baseAltitude = baseLoc.getAltitude();

            planeLatitudeTv.setText(String.format("%.8f", planeLatitude));
            planeLongitudeTv.setText(String.format("%.8f", planeLongitude));
            planeAltitudeTv.setText(String.format("%.3fm", planeAltitude));

            baseLatitudeTV.setText(String.format("%.8f", baseLatitude));
            baseLongitudeTv.setText(String.format("%.8f", baseLongitude));
            baseAltitudeTV.setText(String.format("%.3fm", baseAltitude));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                LocationCoordinate planeLoc = new LocationCoordinate(planeLatitude, planeLongitude, planeAltitude);
                if (!LocationCoordinateUtils.checkGpsCoordinate(planeLoc)) {
                    showToast("当前坐标不合法");
                    return;
                }
                if (mConfirmListener != null) {
                    mConfirmListener.onConfirm("planeLoc", planeLoc);
                }
                dismiss();
                break;
        }
    }
}
