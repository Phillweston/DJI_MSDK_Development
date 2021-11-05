package com.ew.autofly.module.media.helper;

import java.util.List;

import dji.sdk.media.MediaFile;


public class MediaDataHelper {

    private List<MediaFile> mMediaFileList = null;

    private static class LazyHolder {
        private static final MediaDataHelper INSTANCE = new MediaDataHelper();
    }

    public static MediaDataHelper getInstance() {
        return MediaDataHelper.LazyHolder.INSTANCE;
    }

    public List<MediaFile> getMediaFileList() {
        return mMediaFileList;
    }

    public void setMediaFileList(List<MediaFile> mediaFileList) {
        mMediaFileList = mediaFileList;
    }

    public void destroy() {
        mMediaFileList = null;
    }
}
