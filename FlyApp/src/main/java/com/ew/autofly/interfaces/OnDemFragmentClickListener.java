package com.ew.autofly.interfaces;

import android.app.DownloadManager;

public interface OnDemFragmentClickListener {
    void loadDownloadManager(DownloadManager downloadManager);

    void onStartDownload();
}