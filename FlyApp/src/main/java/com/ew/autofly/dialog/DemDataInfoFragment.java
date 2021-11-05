package com.ew.autofly.dialog;

import android.app.Dialog;
import android.app.DownloadManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ew.autofly.R;
import com.ew.autofly.adapter.BaseViewPagerFragmentAdapter;
import com.ew.autofly.fragments.dem.DemDownloadingFragment;
import com.ew.autofly.fragments.dem.DemDataListsFragment;
import com.ew.autofly.interfaces.OnDemFragmentClickListener;

import java.util.ArrayList;
import java.util.List;



public class DemDataInfoFragment extends DialogFragment implements OnDemFragmentClickListener{

    private List<CharSequence> mListTitle;
    private DemDataListsFragment mDemListFragment;
    private DemDownloadingFragment mDemDownloadingFragment;
    private ArrayList<Fragment> mFragments;
    private BaseViewPagerFragmentAdapter mViewPageFragmentAdapter;
    private TabLayout mTLSwitchContent;
    private ViewPager mViewPager;
    private DownloadManager downloadManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = R.style.Theme_AppCompat_Light_NoActionBar;
        setStyle(style, theme);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (dm.widthPixels > dm.heightPixels)
                dialog.getWindow().setLayout((int) (dm.widthPixels * 0.7), (int) (dm.heightPixels * 0.9));
            else
                dialog.getWindow().setLayout((int) (dm.heightPixels * 0.7), (int) (dm.widthPixels * 0.9));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_dem_manager, container, false);
        view.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initBaseView(view);
        initViewPage();
        return view;
    }

    private void initBaseView(View view) {
        mTLSwitchContent = (TabLayout) view.findViewById(R.id.tl_switch_content);
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(getText(R.string.weather_current)));
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(getText(R.string.weather_forecast)));
        mViewPager = (ViewPager) view.findViewById(R.id.vp_weather);
    }

    private void initViewPage() {
        mListTitle = new ArrayList<>();
        mListTitle.add("已下载");
        mListTitle.add("正在下载");

        mDemListFragment = DemDataListsFragment.newInstance();
        mDemDownloadingFragment = DemDownloadingFragment.newInstance();

        OnDemFragmentClickListener dftList = mDemListFragment;
        dftList.loadDownloadManager(downloadManager);
        OnDemFragmentClickListener dftDownloading = mDemDownloadingFragment;
        dftDownloading.loadDownloadManager(downloadManager);

        mFragments = new ArrayList<>();
        mFragments.add(mDemListFragment);
        mFragments.add(mDemDownloadingFragment);

        mViewPageFragmentAdapter = new BaseViewPagerFragmentAdapter(getChildFragmentManager(), mFragments, mListTitle);
        mViewPager.setAdapter(mViewPageFragmentAdapter);
        mTLSwitchContent.setupWithViewPager(mViewPager);
    }

    @Override
    public void loadDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public void onStartDownload() {
        mViewPager.setCurrentItem(1);
        mDemDownloadingFragment.onStartDownload();
    }
}
