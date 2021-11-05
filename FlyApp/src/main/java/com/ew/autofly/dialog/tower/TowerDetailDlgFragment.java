package com.ew.autofly.dialog.tower;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.text.DecimalFormat;



public class TowerDetailDlgFragment extends BaseDialogFragment implements View.OnClickListener {

    public final static String ARG_PARAM_TOWER = "ARG_PARAM_TOWER";
    public final static String ARG_PARAM_AIRCRAFT_LOCATION = "ARG_PARAM_AIRCRAFT_LOCATION";

    private LocationCoordinate mPlaneLoc;

    private Tower mTower;

    private onTowerDetailClickListener mClickListener;

    private DecimalFormat mDecimalFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onCreateSize() {
        setSize(0.5f, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tower_detail, container, false);
        bindField(view);
        return view;
    }

    private void initData() {
        this.mTower = getArguments().getParcelable(ARG_PARAM_TOWER);
        this.mPlaneLoc = getArguments().getParcelable(ARG_PARAM_AIRCRAFT_LOCATION);
        mDecimalFormat = new DecimalFormat(".000000");
    }

    private void bindField(View view) {

        view.findViewById(R.id.ib_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_update_location).setOnClickListener(this);

        if (mTower != null) {
            ((TextView) view.findViewById(R.id.tv_tower_no)).setText(mTower.getTowerNo());
            ((TextView) view.findViewById(R.id.tv_tower_latitude))
                    .setText(mTower.getLatitude() != Tower.ALTITUDE_NO_VALUE ? String.valueOf(mDecimalFormat.format(mTower.getLatitude())) : "");
            ((TextView) view.findViewById(R.id.tv_tower_longitude))
                    .setText(mTower.getLongitude() != Tower.ALTITUDE_NO_VALUE ? String.valueOf(mDecimalFormat.format(mTower.getLongitude())) : "");
            ((TextView) view.findViewById(R.id.tv_tower_altitude))
                    .setText(mTower.getTowerAltitude() != Tower.ALTITUDE_NO_VALUE ? String.valueOf(mTower.getTowerAltitude()) : "");
            ((TextView) view.findViewById(R.id.tv_tower_elevation))
                    .setText(mTower.getAltitude() != Tower.ALTITUDE_NO_VALUE ? String.valueOf(mTower.getAltitude()) : "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_cancel:
                dismiss();
                break;
            case R.id.btn_update_location:
                showUpdateConfirmDialog();
                break;
        }
    }

    public void setClickListener(onTowerDetailClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface onTowerDetailClickListener {


        void onUpdateLocation(Tower tower);

    }

    private void showUpdateConfirmDialog() {
        if (checkPlanePositionAvailable()) {

            CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getContext());
            deleteDialog.setTitle("更新")
                    .setMessage("是否更新杆塔的位置为当前无人机所在位置(" +
                            mDecimalFormat.format(mPlaneLoc.getLongitude()) + "°E " + mDecimalFormat.format(mPlaneLoc.getLatitude()) + "°N" + ")?")
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mClickListener != null) {
                                mTower.setLatitude(mPlaneLoc.getLatitude());
                                mTower.setLongitude(mPlaneLoc.getLongitude());
                                mClickListener.onUpdateLocation(mTower);
                            }
                            dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ToastUtil.show(getContext(), "无法获取无人机的位置");
        }
    }


    /**
     * 检查无人机位置
     *
     * @return
     */
    private boolean checkPlanePositionAvailable() {

        if (mPlaneLoc != null && LocationCoordinateUtils.checkGpsCoordinate(mPlaneLoc.getLatitude(), mPlaneLoc.getLongitude())) {
            return true;
        }
        ToastUtil.show(EWApplication.getInstance(), "未定位到无人机所在位置");
        return false;
    }

}
