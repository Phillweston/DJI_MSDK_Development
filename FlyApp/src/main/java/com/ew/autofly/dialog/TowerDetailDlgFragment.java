package com.ew.autofly.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.CoordinateUtils;

import java.text.DecimalFormat;



public class TowerDetailDlgFragment extends BaseDialogFragment implements View.OnClickListener {

    private double mPlaneLatitude;
    private double mPlaneLongitude;

    private Tower mTower;

    private onTowerDetailClickListener mClickListener;

    private DecimalFormat mDecimalFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        this.mTower = getArguments().getParcelable("tower");
        this.mPlaneLatitude=getArguments().getDouble("latitude");
        this.mPlaneLongitude=getArguments().getDouble("longitude");
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.5), WindowManager.LayoutParams.WRAP_CONTENT);
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.5), WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tower_detail, container, false);
        initData();
        bindField(view);
        return view;
    }

    private void initData() {

        mDecimalFormat = new DecimalFormat(".000000");
    }

    private void bindField(View view) {

        view.findViewById(R.id.ib_cancel).setOnClickListener(this);
        view.findViewById(R.id.btn_update_location).setOnClickListener(this);

        if (mTower != null) {
            ((TextView) view.findViewById(R.id.tv_tower_no)).setText(mTower.getTowerNo());
            ((TextView) view.findViewById(R.id.tv_tower_latitude))
                    .setText(mTower.getLatitude() != 0 ? String.valueOf(mDecimalFormat.format(mTower.getLatitude())) : "");
            ((TextView) view.findViewById(R.id.tv_tower_longitude))
                    .setText(mTower.getLongitude() != 0 ? String.valueOf(mDecimalFormat.format(mTower.getLongitude())) : "");
            ((TextView) view.findViewById(R.id.tv_tower_altitude))
                    .setText(mTower.getTowerAltitude() != 0 ? String.valueOf(mTower.getTowerAltitude()) : "");
            ((TextView) view.findViewById(R.id.tv_tower_elevation))
                    .setText(mTower.getAltitude() != 0 ? String.valueOf(mTower.getAltitude()) : "");
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
                            mDecimalFormat.format(mPlaneLongitude) + "°E " + mDecimalFormat.format(mPlaneLatitude) + "°N" + ")?")
                    .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mClickListener!=null){
                                mTower.setLatitude(mPlaneLatitude);
                                mTower.setLongitude(mPlaneLongitude);
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

        if (LocationCoordinateUtils.checkGpsCoordinate(mPlaneLatitude, mPlaneLongitude)) {
            return true;
        }
        ToastUtil.show(EWApplication.getInstance(), "未定位到无人机所在位置");
        return false;
    }

}
