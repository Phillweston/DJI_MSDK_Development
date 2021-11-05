package com.ew.autofly.module.setting.fragment;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.module.setting.constant.SettingType;
import com.ew.autofly.module.setting.event.EnterSecondMenuEvent;
import com.ew.autofly.module.setting.event.ExitMainSettingEvent;
import com.ew.autofly.module.setting.fragment.base.BaseSettingFragment;
import com.ew.autofly.module.setting.fragment.battery.BatterySettingFragment;
import com.ew.autofly.module.setting.fragment.camera.CameraSettingFragment;
import com.ew.autofly.module.setting.fragment.common.CommonSettingFragment;
import com.ew.autofly.module.setting.fragment.flight.FlightSettingFragment;
import com.ew.autofly.module.setting.fragment.hd.HDSettingFragment;
import com.ew.autofly.module.setting.fragment.perception.PerceptionSettingFragment;
import com.ew.autofly.module.setting.fragment.remote.RemoteSettingFragment;
import com.flycloud.autofly.ux.base.BaseUxDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainSettingDialog extends BaseUxDialog implements View.OnClickListener {

    private static int selectType = 0;
    private static int selectPosition = 0;

    private String mMainTitleStr;

    private View view;
    private ListView mLvSetting;
    private TextView mTvSettingTitle;
    private ImageView mIvSettingBack;
    private ImageView mIvSettingClose;

    private BaseSettingFragment mSettingFragment;


    private int mScreenWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getActivity() != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.heightPixels < dm.widthPixels ? dm.heightPixels : dm.widthPixels;
        }
    }

    @Override
    protected void onCreateBackground() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().getAttributes().gravity = Gravity.RIGHT;
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_side_setting, container, false);
        initView(view);
        return view;
    }

    private void initView(@NonNull View view) {

        mLvSetting = (ListView) view.findViewById(R.id.lv_setting);
        mTvSettingTitle = (TextView) view.findViewById(R.id.tv_setting_title);
        mIvSettingBack = (ImageView) view.findViewById(R.id.iv_setting_back);
        mIvSettingClose = (ImageView) view.findViewById(R.id.iv_setting_close);

        mIvSettingBack.setOnClickListener(this);
        mIvSettingClose.setOnClickListener(this);
        SettingMenuAdapter settingMenuAdapter = new SettingMenuAdapter();
        settingMenuAdapter.setItemHeight(mScreenWidth / menu_icons.length);
        mLvSetting.setAdapter(settingMenuAdapter);
        mLvSetting.setOnItemClickListener(mMenuItemClickListener);

        replaceFragment(selectType);
        mLvSetting.setItemChecked(selectPosition, true);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void replaceFragment(int type) {

        switch (type) {
            case SettingType.COMMON:
                mSettingFragment = new CommonSettingFragment();
                mMainTitleStr = "通用设置";
                break;
            case SettingType.FLIGHT_CONTROLLER:
                mSettingFragment = new FlightSettingFragment();
                mMainTitleStr = "飞行器设置";

                break;
            case SettingType.PERCEPTION:
                mSettingFragment = new PerceptionSettingFragment();
                mMainTitleStr = "感知设置";
                break;
            case SettingType.REMOTE_CONTROLLER:
                mSettingFragment = new RemoteSettingFragment();
                mMainTitleStr = "遥控器设置";
                break;
            case SettingType.HD:
                mSettingFragment = new HDSettingFragment();
                mMainTitleStr = "图传设置";
                break;
            case SettingType.BATTERY:
                mSettingFragment = new BatterySettingFragment();
                mMainTitleStr = "电池设置";
                break;
            case SettingType.CAMERA:
                mSettingFragment = new CameraSettingFragment();
                mMainTitleStr = "云台相机设置";
                break;
            default:
                mSettingFragment = new CommonSettingFragment();
                mMainTitleStr = "通用设置";
                break;
        }

        setMainTitle();

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_replace_setting, mSettingFragment);
        ft.commit();
    }

    private void setMainTitle() {
        mTvSettingTitle.setText(mMainTitleStr);
    }

    private AdapterView.OnItemClickListener mMenuItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int type = 0;
            switch (position) {
                case 0:
                    type = SettingType.COMMON;
                    break;
                case 1:
                    type = SettingType.FLIGHT_CONTROLLER;
                    break;
                case 2:
                    type = SettingType.PERCEPTION;
                    break;
                case 3:
                    type = SettingType.REMOTE_CONTROLLER;
                    break;
                case 4:
                    type = SettingType.HD;
                    break;
                case 5:
                    type = SettingType.BATTERY;
                    break;
                case 6:
                    type = SettingType.CAMERA;
                    break;
            }

            selectPosition = position;

            if (selectType != type) {
                replaceFragment(type);
                selectType = type;
            }
        }
    };


    private final int[] menu_icons = {
            R.drawable.selector_btn_setting_common,
            R.drawable.selector_btn_setting_flightcontroller,
            R.drawable.selector_btn_setting_perception,
            R.drawable.selector_btn_setting_remotecontroller,
            R.drawable.selector_btn_setting_hd,
            R.drawable.selector_btn_setting_battery,
            R.drawable.selector_btn_setting_camera
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting_back:
                mSettingFragment.goBackToMainMenu();
                mIvSettingBack.setVisibility(View.GONE);
                setMainTitle();
                break;
            case R.id.iv_setting_close:
                dismiss();
                break;
        }
    }

    private class SettingMenuAdapter extends BaseAdapter {

        private int itemHeight;

        public void setItemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
        }

        @Override
        public int getCount() {
            return menu_icons.length;
        }

        @Override
        public Object getItem(int position) {
            return menu_icons[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_setting_menu, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.ivMenu = convertView.findViewById(R.id.iv_setting_type);
                convertView.setTag(viewHolder);
                convertView.setMinimumHeight(itemHeight);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.ivMenu.setImageResource(menu_icons[position]);
            return convertView;
        }

        class ViewHolder {

            ImageView ivMenu;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEnterSecondMenuEvent(EnterSecondMenuEvent event) {
        mTvSettingTitle.setText(event.getTitle());
        mIvSettingBack.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onExitMainSettingEvent(ExitMainSettingEvent event) {
        dismiss();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
