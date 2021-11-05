package com.ew.autofly.module.media;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ew.autofly.R;
import com.ew.autofly.module.media.view.AlbumFragment;
import com.ew.autofly.module.media.view.PlayDjiVideoActivity;
import com.flycloud.autofly.base.base.BaseFragment;


public class MediaFragment extends BaseFragment {

    private TabLayout mTLSwitchContent;

    private AlbumFragment albumFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_media, container, false);
        initView(view);
        return view;
    }

    private void initView(View rootView) {

        mTLSwitchContent = (TabLayout) rootView.findViewById(R.id.tl_switch_content);
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText("全部"));
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText("视频"));
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText("照片"));
        mTLSwitchContent.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Intent intent=new Intent(getActivity(), PlayDjiVideoActivity.class);
                startActivity(intent);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        albumFragment = (AlbumFragment) getFragmentManager().findFragmentByTag("album");
        if (albumFragment == null) {
            albumFragment = new AlbumFragment();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flParent, albumFragment);
        ft.commit();
    }

    private void switchContent() {

    }

}
