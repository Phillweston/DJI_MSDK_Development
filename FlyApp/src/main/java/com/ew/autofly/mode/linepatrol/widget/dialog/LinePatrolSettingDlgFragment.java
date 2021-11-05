package com.ew.autofly.mode.linepatrol.widget.dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.common.AltitudeSettingDlgFragment;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.WayPointTask;
import com.ew.autofly.interfaces.OnSetAltitudeListener;
import com.ew.autofly.interfaces.OnSettingDialogClickListener;
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask;
import com.ew.autofly.widgets.CustomBar;
import com.ew.autofly.widgets.CustomSeekbar.BubbleSeekBar;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class LinePatrolSettingDlgFragment extends BaseDialogFragment implements OnClickListener, OnSetAltitudeListener {
    private RadioGroup rgActionMode, rgReturnMode;
  
    private TextView tvResolution;
  
    private CheckBox chbAltitude;
  
    private LinearLayout llAltitude;
    private CustomBar cbAltitude;
    private TextView tvAltitude;
  
    private CustomBar cbGimbal;
    private TextView tvGimbalAngle;
    private LinearLayout llFlySpeed;
  
    private CustomBar cbFlySpeed;
    private TextView tvFlySpeed;
    private TextView tvSpeed;
  
    private TextView tvPlaneYaw;
    private CustomBar cbPlaneYaw;
  
    private LinearLayout llForwardOverlap;
    private TextView tvForwardOverlap;
    private CustomBar cbForwardOverLap;
  
    private TextView tvEntryHeight;
    private CustomBar cbEntryHeight;
    private CheckBox chbEntryHeight;
  
    private CheckBox chbRecord;

    private AirRouteParameter airRouteParameter;
    private int actionMode = AppConstant.ACTION_MODE_VIDEO;
    private int returnMode = AppConstant.RETURN_MODE_STRAIGHT;
    private int recordMode = AppConstant.MIDWAY_RECODE_VIDEO;

    private RadioGroup.OnCheckedChangeListener onRadioChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_video_shot:
                    actionMode = AppConstant.ACTION_MODE_VIDEO;
                    llFlySpeed.setVisibility(View.VISIBLE);
                    tvSpeed.setVisibility(View.GONE);
                    llForwardOverlap.setVisibility(View.GONE);
                    chbRecord.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_schedule_shot:
                    llFlySpeed.setVisibility(View.GONE);
                    tvSpeed.setVisibility(View.VISIBLE);
                    llForwardOverlap.setVisibility(View.VISIBLE);
                    actionMode = AppConstant.ACTION_MODE_PHOTO;
                    chbRecord.setVisibility(View.GONE);
                    break;
                case R.id.rb_straight_back:
                    returnMode = AppConstant.RETURN_MODE_STRAIGHT;
                    break;
                case R.id.rb_original_back:
                    returnMode = AppConstant.RETURN_MODE_ORIGIN;
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.cb_altitude:
                    if (isChecked) {
                        AltitudeSettingDlgFragment altitudeSetDlgFrag = new AltitudeSettingDlgFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("routeInfoList", getArguments().getSerializable("routeInfoList"));
                        bundle.putSerializable("aircraftLocation", getArguments().getSerializable("aircraftLocation"));
                        bundle.putSerializable("airRoutePara", airRouteParameter);
                        altitudeSetDlgFrag.setArguments(bundle);
                        altitudeSetDlgFrag.show(getFragmentManager(), "line_altitude_setting");
                        llAltitude.setVisibility(View.GONE);
                    } else {
                        llAltitude.setVisibility(View.VISIBLE);
                        if (cbEntryHeight != null && cbAltitude != null) {
                            int altitude = Integer.parseInt(tvAltitude.getText().toString().replace("m", ""));
                            cbEntryHeight.bubbleSeekBar.getConfigBuilder().min(altitude).hideBubble().sectionCount((500 - altitude)).build();
                            cbAltitude.bubbleSeekBar.getConfigBuilder().min(AppConstant.MIN_ALTITUDE).max(AppConstant.MAX_ALTITUDE - 5).progress(airRouteParameter.getAltitude()).build();
                        }
                    }
                    break;
                case R.id.cb_record:
                    if (isChecked)
                        recordMode = AppConstant.START_RECODE_VIDEO;
                    else
                        recordMode = AppConstant.MIDWAY_RECODE_VIDEO;
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        this.airRouteParameter = (AirRouteParameter) getArguments().getSerializable("params");
        this.actionMode = getArguments().getInt("actionMode");
        this.returnMode = getArguments().getInt("returnMode");
        this.recordMode = getArguments().getInt("recordMode");
    }

    @Override
    public void onStart() {
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_line_patrol_setting, container, false);
        initView(view);
        initData();
        bindField(view);
        return view;
    }

    private void initView(View view) {
        rgActionMode = (RadioGroup) view.findViewById(R.id.rg_action_mode);
        rgReturnMode = (RadioGroup) view.findViewById(R.id.rg_return_mode);

        tvResolution = (TextView) view.findViewById(R.id.tv_resolution);
        tvSpeed = (TextView) view.findViewById(R.id.tv_speed);

        chbEntryHeight = (CheckBox) view.findViewById(R.id.cb_entry_height);
        chbAltitude = (CheckBox) view.findViewById(R.id.cb_altitude);
        chbRecord = (CheckBox) view.findViewById(R.id.cb_record);

        cbEntryHeight = (CustomBar) view.findViewById(R.id.sb_entry_height);
        tvEntryHeight = (TextView) view.findViewById(R.id.tv_entry_height);

        llFlySpeed = (LinearLayout) view.findViewById(R.id.ll_fly_speed);
        cbFlySpeed = (CustomBar) view.findViewById(R.id.sb_fly_speed);
        tvFlySpeed = (TextView) view.findViewById(R.id.tv_fly_speed);

        llAltitude = (LinearLayout) view.findViewById(R.id.ll_altitude);
        cbAltitude = (CustomBar) view.findViewById(R.id.sb_altitude);
        tvAltitude = (TextView) view.findViewById(R.id.tv_altitude);

        llForwardOverlap = (LinearLayout) view.findViewById(R.id.ll_forward_overlap);
        tvForwardOverlap = (TextView) view.findViewById(R.id.tv_forward_overlap);
        cbForwardOverLap = (CustomBar) view.findViewById(R.id.sb_forward_overlap);

        cbGimbal = (CustomBar) view.findViewById(R.id.sb_gimbal_angle);
        tvGimbalAngle = (TextView) view.findViewById(R.id.tv_gimbal_angle);

        cbPlaneYaw = (CustomBar) view.findViewById(R.id.sb_plane_yaw);
        tvPlaneYaw = (TextView) view.findViewById(R.id.tv_plane_yaw);

        if (getArguments().getSerializable("routeInfoList") == null) {
            chbAltitude.setVisibility(View.GONE);
        }
    }

    private void initData() {
        rgActionMode.check(actionMode == AppConstant.ACTION_MODE_VIDEO ? R.id.rb_video_shot : R.id.rb_schedule_shot);
        rgReturnMode.check(returnMode == AppConstant.RETURN_MODE_STRAIGHT ? R.id.rb_straight_back : R.id.rb_original_back);

        chbRecord.setChecked(recordMode == AppConstant.START_RECODE_VIDEO);
        chbAltitude.setChecked(!airRouteParameter.isFixedAltitude());

        if (!airRouteParameter.isFixedAltitude()) {
            llAltitude.setVisibility(View.GONE);
        }

        if (actionMode == AppConstant.ACTION_MODE_VIDEO) {
            llFlySpeed.setVisibility(View.VISIBLE);
            llForwardOverlap.setVisibility(View.GONE);
            chbRecord.setVisibility(View.VISIBLE);
        } else {
            tvSpeed.setVisibility(View.VISIBLE);
            llFlySpeed.setVisibility(View.GONE);
            llForwardOverlap.setVisibility(View.VISIBLE);
            chbRecord.setVisibility(View.GONE);
        }

        tvResolution.setText(new DecimalFormat("#0.00").format((airRouteParameter.getResolutionRateByAltitude())) + "cm");
        tvForwardOverlap.setText(String.format("%.0f", airRouteParameter.getRouteOverlap() * 100) + "%");
        tvSpeed.setText("速度：" + airRouteParameter.getActualSpeed() + "m/s");
        tvAltitude.setText(airRouteParameter.getAltitude() + "m");
        tvGimbalAngle.setText((airRouteParameter.getGimbalAngle() + 90) + "°");
        tvFlySpeed.setText((int) airRouteParameter.getFlySpeed() + "m/s");
        tvForwardOverlap.setText(String.format("%.0f", airRouteParameter.getRouteOverlap() * 100) + "%");
        tvEntryHeight.setText(airRouteParameter.getEntryHeight() + "m");
        chbEntryHeight.setChecked(airRouteParameter.isCheckEntryHeight());
    }

    private void bindField(View view) {
        rgActionMode.setOnCheckedChangeListener(onRadioChangeListener);
        rgReturnMode.setOnCheckedChangeListener(onRadioChangeListener);
        chbRecord.setOnCheckedChangeListener(onCheckedChangeListener);
        View cancelBtn = view.findViewById(R.id.tv_cancel);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(this);
        }
        View okBtn = view.findViewById(R.id.tv_ok);
        if (okBtn != null) {
            okBtn.setOnClickListener(this);
        }
        chbAltitude.setOnCheckedChangeListener(onCheckedChangeListener);

        initAltitude();
        initGimbal();
        initFlySpeed();
        initPlaneYaw();
        initOverlap();
        initEntryHeightBar();
    }

    private void initEntryHeightBar() {
        int max = AppConstant.MAX_ALTITUDE;
        int min = Integer.parseInt(tvAltitude.getText().toString().replace("m", ""));
        cbEntryHeight.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(airRouteParameter.getEntryHeight())
                .sectionCount(max - min)
                .hideBubble()
                .build();
        cbEntryHeight.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvEntryHeight.setText(progress + "m");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    private void initOverlap() {
        int max = 18;
        int min = 3;
        int progress = (int) (airRouteParameter.getRouteOverlap() * 100) / 5;
        cbForwardOverLap.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(progress)
                .sectionCount(max - min)
                .hideBubble()
                .build();
        cbForwardOverLap.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvForwardOverlap.setText(progress * 5 + "%");
                int iSpeed = Double.valueOf((Integer.parseInt(tvAltitude.getText().toString().replace("m", "")) - airRouteParameter.getBaseLineHeight()) * airRouteParameter.getSensorHeight() / airRouteParameter.getFocalLength() * (1.0D - ((progress * 5) / 100.0D)) / 2.0D).intValue();
                if (iSpeed > 15)
                    iSpeed = 15;
                if (iSpeed < 3)
                    iSpeed = 3;
                tvSpeed.setText("速度：" + iSpeed + "m/s");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });
    }

    private void initAltitude() {
        int max = AppConstant.MAX_ALTITUDE - 5;
        int min = AppConstant.MIN_ALTITUDE;
        int progress = airRouteParameter.getAltitude();
        cbAltitude.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(progress)
                .sectionCount(max - min)
                .hideBubble()
                .build();
        cbAltitude.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvAltitude.setText(progress + "m");
                Double Resolution = Double.valueOf(String.format("%.4f",
                        new Object[]{Double.valueOf((double) (Integer.parseInt(tvAltitude.getText().toString().replace("m", "")) - airRouteParameter.getBaseLineHeight()) * airRouteParameter.getPixelSizeWidth() / airRouteParameter.getFocalLength() / 1000.0D)})).doubleValue();
                tvResolution.setText(new DecimalFormat("#0.00").format((Resolution * 100)) + "cm");
                cbEntryHeight.bubbleSeekBar.getConfigBuilder().min(progress < 0 ? 0 : progress).hideBubble().sectionCount((int) (cbEntryHeight.bubbleSeekBar.getMax() - progress)).build();
                int iSpeed = Double.valueOf((Integer.parseInt(tvAltitude.getText().toString().replace("m", "")) - airRouteParameter.getBaseLineHeight()) * airRouteParameter.getSensorHeight() / airRouteParameter.getFocalLength() * (1.0D - Integer.parseInt(tvForwardOverlap.getText().toString().replace("%", "")) / 100.0D) / 2.0D).intValue();
                if (iSpeed > 15)
                    iSpeed = 15;
                if (iSpeed < 3)
                    iSpeed = 3;
                tvSpeed.setText("速度：" + iSpeed + "m/s");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    private void initGimbal() {
        int max = 18;
        int min = 0;
        int progress = (airRouteParameter.getGimbalAngle() + 90) / 5;
        cbGimbal.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(progress)
                .hideBubble()
                .sectionCount(max - min)
                .build();
        cbGimbal.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvGimbalAngle.setText((progress * 5) + "°");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
            }
        });
    }

    private void initFlySpeed() {
        int max = 15;
        int min = 3;
        int progress = (int) airRouteParameter.getFlySpeed();
        cbFlySpeed.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(progress)
                .hideBubble()
                .sectionCount((max - min))
                .build();
        cbFlySpeed.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvFlySpeed.setText(progress + "m/s");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });
    }

    private void initPlaneYaw() {
        int max = 4;
        int min = 0;
        int progress = airRouteParameter.getPlaneYaw() / 90;
        tvPlaneYaw.setText(airRouteParameter.getPlaneYaw() + "°");
        cbPlaneYaw.bubbleSeekBar.getConfigBuilder()
                .max(max)
                .min(min)
                .progress(progress)
                .hideBubble()
                .sectionCount((max - min))
                .build();
        cbPlaneYaw.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                tvPlaneYaw.setText(progress * 90 + "°");
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

            }
        });

        cbPlaneYaw.bubbleSeekBar.getProgress();

    }

    @Override
    public void onClick(View v) {
        OnSettingDialogClickListener act;
        if (getTag().equals("line_patrol"))
            act = (OnSettingDialogClickListener) getActivity().getSupportFragmentManager().findFragmentByTag("line");
        else
            act = (OnSettingDialogClickListener) getActivity().getSupportFragmentManager().findFragmentByTag("river");
        switch (v.getId()) {
            case R.id.tv_ok:
                airRouteParameter.setAltitude(Integer.parseInt(tvAltitude.getText().toString().replace("m", "")));
                airRouteParameter.setEntryHeight(Integer.parseInt(tvEntryHeight.getText().toString().replace("m", "")));
                airRouteParameter.setRouteOverlap(Double.parseDouble(tvForwardOverlap.getText().toString().replace("%", "")) / 100);
                airRouteParameter.setFixedAltitude(!chbAltitude.isChecked());
                airRouteParameter.setPlaneYaw(cbPlaneYaw.bubbleSeekBar.getProgress() * 90);
                airRouteParameter.setGimbalAngle(Integer.parseInt(tvGimbalAngle.getText().toString().replace("°", "")) - 90);
                if (actionMode == AppConstant.ACTION_MODE_VIDEO)
                    airRouteParameter.setFlySpeed(Integer.parseInt(tvFlySpeed.getText().toString().replace("m/s", "")));
                else
                    airRouteParameter.setFlySpeed(airRouteParameter.getActualSpeed());
                HashMap<String, Integer> hashMap = new HashMap<>();
                hashMap.put("actionMode", actionMode);
                hashMap.put("returnMode", returnMode);
                hashMap.put("recordMode", recordMode);
                act.onSettingDialogConfirm(getTag(), hashMap, airRouteParameter);
                FlyTask flyTask = new FlyTask();


                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onSetAltitudeComplete(boolean result, List<WayPointTask> taskList) {
        if (result) {
            String[] altitudeList = airRouteParameter.getFixedAltitudeList().split(",");
            int maxAltitude = AppConstant.MIN_ALTITUDE;
            int minAltitude = AppConstant.MAX_ALTITUDE;
            for (String s : altitudeList) {
                if (maxAltitude < Integer.parseInt(s)) {
                    maxAltitude = Integer.parseInt(s);
                }
                if (minAltitude > Integer.parseInt(s)) {
                    minAltitude = Integer.parseInt(s);
                }
            }
            airRouteParameter.setEntryHeight(maxAltitude);
            airRouteParameter.setAltitude(maxAltitude);
            cbAltitude.bubbleSeekBar.getConfigBuilder().max(maxAltitude).min(AppConstant.MIN_ALTITUDE).progress(maxAltitude).build();
            cbEntryHeight.bubbleSeekBar.getConfigBuilder().max(AppConstant.MAX_ALTITUDE).min(maxAltitude).progress(maxAltitude).build();
        } else {
            chbAltitude.setChecked(false);
            llAltitude.setVisibility(View.VISIBLE);
        }
    }
}