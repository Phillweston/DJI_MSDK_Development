package com.ew.autofly.home;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.adapter.LeftMenuAdapter2;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.constant.FlyCollectMode;
import com.ew.autofly.dialog.MainSecondMenuPop;
import com.ew.autofly.event.MultiSpectralEvent;
import com.ew.autofly.event.ui.topbar.MainMenuShowEvent;
import com.ew.autofly.event.ui.topbar.MoreMenuShowEvent;
import com.ew.autofly.fragments.BaseCollectFragment;
import com.ew.autofly.interfaces.OnTopLeftMenuClickListener;
import com.ew.autofly.mode.linepatrol.LinePatrolCollectFragment;
import com.ew.autofly.mode.manualcollect.ManualCollectFragment;
import com.ew.autofly.module.flightrecord.RecordFragment;
import com.ew.autofly.module.missionmanager.MissionManagerFragment;
import com.ew.autofly.module.setting.event.MapChangeEvent;
import com.ew.autofly.module.setting.fragment.MainSettingDialog;
import com.ew.autofly.utils.GpsUtils2;
import com.flycloud.autofly.control.event.LoginEvent;
import com.flycloud.autofly.control.event.LogoutEvent;
import com.flycloud.autofly.control.event.UserEditEvent;
import com.flycloud.autofly.control.logic.RouteController;
import com.flycloud.autofly.control.service.RouteService;
import com.flycloud.autofly.ux.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dji.sdk.sdkmanager.DJISDKManager;


@Route(path = RouteController.Activity.App_Main)
public class MainActivity extends BaseActivity implements OnTopLeftMenuClickListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private SharedConfig config;
    private RelativeLayout mRlTitle;
    private TextView mTvTitle, mTvTitleCenter, mTvUserName;
    private CircleImageView mIvUserLogo;
    private DrawerLayout drawerLayout;
    private LeftMenuAdapter2 menuAdapter;
    private ImageView leftMenu;
    private ListView listView;

    private long exitTime = 0;
    private int mCurrMenuPos = 0;
    private int mMenuSelectPos = 0;

    private Fragment mContentFragment;

    private MainSecondMenuPop popupWindow;

    private List<Integer> second_menu_icon_list = new ArrayList<>();
    private List<String> second_menu_title_list = new ArrayList<>();

    private interface MainMenuIndex {
        int MODE_CHANGE = 0;
        int MISSION_MANAGER = 1;
        int FLY_RECORD = 2;
        int MODE_CHANGE_SUB = 100;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initView();

        GpsUtils2.init(getApplicationContext());

        DJISDKManager.getInstance().startConnectionToProduct();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(RouteService.getUserToken())) {
            loginSuccess();
            uploadUserStatistics();
        } else {
            logout();
        }
    }

    private void initData() {

        initScreenSize();

        EventBus.getDefault().register(this);
        config = new SharedConfig(this);
    }

    private void initView() {

        findViewById(R.id.rl_user).setOnClickListener(this);
        findViewById(R.id.rl_user_logo).setOnClickListener(this);

        mRlTitle = (RelativeLayout) findViewById(R.id.title_bar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitleCenter = (TextView) findViewById(R.id.tv_title_center);
        leftMenu = (ImageView) findViewById(R.id.leftmenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listView = (ListView) findViewById(R.id.left_list_view);

        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mIvUserLogo = (CircleImageView) findViewById(R.id.iv_user_logo);

        initMainMenu();

        initModeMenu();
    }

    private void initScreenSize() {
        if (!(AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0)) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            AppConstant.ScreenHeight = dm.heightPixels < dm.widthPixels ? dm.heightPixels : dm.widthPixels;
            AppConstant.ScreenWidth = dm.widthPixels < dm.heightPixels ? dm.heightPixels : dm.widthPixels;
        }
    }

    
    private void initMainMenu() {


        int[] main_icons = {

                R.drawable.selector_title_mission,
                R.drawable.selector_title_missionsetting,

                R.drawable.selector_title_record};

        String[] main_titles = getResources().getStringArray(R.array.channel_main_menu);
        menuAdapter = new LeftMenuAdapter2(this, main_icons, main_titles);

        listView.setAdapter(menuAdapter);
        listView.setItemChecked(MainMenuIndex.MODE_CHANGE, true);
        listView.setOnItemClickListener(itemClickListener);
        leftMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }

    
    private void initModeMenu() {

        initServerMode();

        popupWindow = new MainSecondMenuPop(this);

        popupWindow.setMenu(second_menu_icon_list, second_menu_title_list);
        popupWindow.setItemClickListener(itemMenuClickListener);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                listView.setItemChecked(mMenuSelectPos, true);
                mCurrMenuPos = mMenuSelectPos;
            }

        });

        changeCollectFragment();
    }

    
    private void initServerMode() {


        String[] all_mode_array = getResources().getStringArray(R.array.channel_server_optional_mode_code);


        String[] secondTitles = getResources().getStringArray(R.array.channel_main_mode_menu);

        TypedArray ar = getResources().obtainTypedArray(R.array.channel_main_mode_menu_icon);
        final int len = ar.length();
        final int[] secondIcons = new int[len];
        for (int i = 0; i < len; i++) {
            secondIcons[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();

        Map<String, Integer> all_mode_icon_map = new LinkedHashMap<>();
        Map<String, String> all_mode_title_map = new LinkedHashMap<>();

        for (int i = 0; i < all_mode_array.length; i++) {
            all_mode_icon_map.put(all_mode_array[i], secondIcons[i]);
            all_mode_title_map.put(all_mode_array[i], secondTitles[i]);
        }


        String[] serverModeArrays = new String[]{"SD","XZ"};
        for (String mode : all_mode_title_map.keySet()) {
            for (String serverMode : serverModeArrays) {
                if (mode.equals(serverMode)) {
                    second_menu_icon_list.add(0, all_mode_icon_map.get(serverMode));
                    second_menu_title_list.add(0, all_mode_title_map.get(serverMode));
                    break;
                }
            }
        }

    }

    private void changeCollectFragment() {
        mRlTitle.setVisibility(View.GONE);
        mTvTitle.setVisibility(View.VISIBLE);
        switch (config.getMode()) {
            case FlyCollectMode.LinePatrol:
                mTvTitle.setText(getResources().getString(R.string.channel_mode_line_patrol));
                replaceContentFragment(new LinePatrolCollectFragment(), null, "line");
                break;
            case FlyCollectMode.Manual:
                replaceContentFragment(MainFragment.newInstance(
                        FlyCollectMode.Manual, getString(R.string.channel_mode_manual_collect)), null, "manual");
                break;
            default:

                mTvTitle.setText(getResources().getString(R.string.channel_mode_manual_collect));
                replaceContentFragment(new ManualCollectFragment(), null, "manual");
                config.setMode(FlyCollectMode.Manual);
                break;
        }
    }

    private void replaceContentFragment(Fragment fragment, Bundle bundle, String TAG) {


        if (mContentFragment != null && mContentFragment instanceof MainFragment) {
            ((MainFragment) mContentFragment).removeMapFragment();
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mContentFragment = fragment;
        if (bundle != null) {
            mContentFragment.setArguments(bundle);
        }
        ft.replace(R.id.content, mContentFragment, TAG);
        ft.commit();
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (banClickMenuItem()) {
                closeMenu();
                return;
            }
            if (mCurrMenuPos == position) {
                if (position != MainMenuIndex.MODE_CHANGE) {
                    closeMenu();
                    return;
                }
            }
            mCurrMenuPos = position;
            switch (position) {
                case MainMenuIndex.MODE_CHANGE_SUB:
                    changeCollectFragment();
                    closeMenu();
                    mMenuSelectPos = MainMenuIndex.MODE_CHANGE_SUB;
                    break;





                case MainMenuIndex.MODE_CHANGE:
                    mMenuSelectPos = MainMenuIndex.MODE_CHANGE_SUB;
                    popupWindow.showAsDropDown(view, (int) getResources().getDimension(R.dimen.main_listview_width), -(int) getResources().getDimension(R.dimen.main_listview_linear));
                    break;
                case MainMenuIndex.MISSION_MANAGER:




                    mRlTitle.setVisibility(View.GONE);


                    closeMenu();
                    mMenuSelectPos = position;
                    replaceContentFragment(new MissionManagerFragment(), null, "mission");
                    break;
               /* case MainMenuIndex.VIEW_ALBUM:
                    mRlTitle.setVisibility(View.VISIBLE);
                    mTvTitle.setVisibility(View.GONE);
                    mTvTitleCenter.setText(getResources().getString(R.string.channel_menu_view_album));
                    closeMenu();
                    mMenuSelectPos = position;
                    replaceContentFragment(new MediaFragment(), null, "media");
                    break;*/
                case MainMenuIndex.FLY_RECORD:
                    mRlTitle.setVisibility(View.VISIBLE);
                    mTvTitle.setVisibility(View.GONE);
                    mTvTitleCenter.setText(getResources().getString(R.string.channel_menu_fly_record));
                    closeMenu();
                    mMenuSelectPos = position;
                    replaceContentFragment(new RecordFragment(), null, "flight");
                    break;
















            }
        }
    };

    private AdapterView.OnItemClickListener itemMenuClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String str = second_menu_title_list.get(position);
            if (str.equals(getResources().getString(R.string.channel_mode_manual_collect)))
                config.setMode(FlyCollectMode.Manual);
            else if (str.equals(getResources().getString(R.string.channel_mode_line_patrol)))
                config.setMode(FlyCollectMode.LinePatrol);
            if (mMenuSelectPos == MainMenuIndex.MISSION_MANAGER) {
                listView.performItemClick(listView.getChildAt(MainMenuIndex.MISSION_MANAGER), MainMenuIndex.MISSION_MANAGER, listView.getItemIdAtPosition(MainMenuIndex.MISSION_MANAGER));
            } else {
                listView.performItemClick(listView.getChildAt(MainMenuIndex.MODE_CHANGE), MainMenuIndex.MODE_CHANGE_SUB, listView.getItemIdAtPosition(MainMenuIndex.MODE_CHANGE));
            }

            popupWindow.dismiss();
        }
    };

    @Override
    public void onMenuClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                showMenu();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainMenuShowEvent(MainMenuShowEvent event) {
        if (event.isShow()) {
            showMenu();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoreMenuShowEvent(MoreMenuShowEvent event) {
        if (event.isShow()) {
            showMoreDialog();
        }
    }

    private void showMenu() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    private void closeMenu() {
        drawerLayout.closeDrawer(Gravity.LEFT, false);
    }

    private void showMoreDialog() {
        MainSettingDialog settingDialog = new MainSettingDialog();
        settingDialog.show(getSupportFragmentManager());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeMenu();
            return;
        }
        super.onBackPressed();






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_user_logo:
                if (banClickMenuItem()) {
                    closeMenu();
                    return;
                }
                if (TextUtils.isEmpty(RouteService.getUserToken())) {
                    RouteController.gotoLoginActivity();
                } else {
                    RouteController.gotoUserEditActivity();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 禁止点击功能菜单
     *
     * @return
     */
    private boolean banClickMenuItem() {

        if (mContentFragment != null) {
            if (mContentFragment instanceof BaseCollectFragment) {
                return !((BaseCollectFragment) mContentFragment).checkTaskFree(true);
            } else if (mContentFragment instanceof MainFragment) {
                return !((MainFragment) mContentFragment).isCanReplace();
            }
        }

        return false;
    }

    private void logout() {
        mTvUserName.setText("未登录");
        Glide.with(this).load(R.drawable.ic_user_logo).into(mIvUserLogo);

    }

    private void loginSuccess() {
        mTvUserName.setText(RouteService.getUserName());
        Glide.with(this).load(RouteService.getUserAvatar()).error(R.drawable.ic_user_logo).into(mIvUserLogo);
    }

    private void uploadUserStatistics() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userId", RouteService.getUserID() + "");
        map.put("userName", RouteService.getUserName());
        MobclickAgent.onEvent(this, "user", map);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        if (event.msg.codeEqual(LoginEvent.SUCCESS)) {
            loginSuccess();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutEvent event) {
        logout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEditEvent(UserEditEvent event) {
        loginSuccess();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiSpectralEvent(MultiSpectralEvent event) {
        if (event.getKey().equals(MultiSpectralEvent.CHANGE_MISSION_TYPE)) {
            int missionType = (int) event.getObject();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapChangeEvent(MapChangeEvent event) {
        changeCollectFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        GpsUtils2.stopLocationClient(true);
    }

}