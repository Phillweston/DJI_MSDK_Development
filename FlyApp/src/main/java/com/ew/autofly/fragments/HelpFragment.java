package com.ew.autofly.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.ew.autofly.R;
import com.ew.autofly.config.SharedConfig;

public class HelpFragment extends BaseFragment {
    private View view;
    private WebView mWvHelp;
    private SharedConfig config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        config = new SharedConfig(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_help, null);
        mWvHelp = (WebView) view.findViewById(R.id.wv_help);
        mWvHelp.getSettings().setJavaScriptEnabled(true);
        mWvHelp.getSettings().setUseWideViewPort(true);
        mWvHelp.loadUrl("file:///android_asset/channel_help.html");
        /*if (AppConstant.CHANNEL_ENWEI)
            mWvHelp.loadUrl("file:///android_asset/help_enwei.html");
        else if (AppConstant.CHANNEL_JIXUN)
            mWvHelp.loadUrl("file:///android_asset/help_dingxin.html");
        else if (!config.getCodeModeFromServer().contains("BQ"))
            mWvHelp.loadUrl("file:///android_asset/help_no_copyright.html");
        else
            mWvHelp.loadUrl("file:///android_asset/help.html");*/
        return view;
    }
}