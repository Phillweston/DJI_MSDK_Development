package com.ew.autofly.module.media.presenter;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.ew.autofly.base.BaseMvpPresenter;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.model.CameraManager;
import com.ew.autofly.module.media.bean.AlbumBean;
import com.ew.autofly.module.media.bean.BaseDataBean;
import com.ew.autofly.module.media.bean.TitleBean;
import com.ew.autofly.module.media.helper.MediaDataHelper;
import com.ew.autofly.module.media.view.IAlbumView;
import com.ew.autofly.module.media.view.PreviewMediaActivity;
import com.flycloud.autofly.base.util.DateHelperUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.media.FetchMediaTask;
import dji.sdk.media.FetchMediaTaskContent;
import dji.sdk.media.FetchMediaTaskScheduler;
import dji.sdk.media.MediaFile;
import dji.sdk.media.MediaManager;


public class AlbumPresenter extends BaseMvpPresenter<IAlbumView> implements IAlbumPresenter {

    private Handler mUIThreadHandler = new Handler(Looper.getMainLooper());

    private MediaManager mMediaManager;
    private MediaManager.FileListState mCurrentFileListState = MediaManager.FileListState.UNKNOWN;
    private FetchMediaTaskScheduler mMediaTaskScheduler;
    private List<MediaFile> mMediaFileList = new ArrayList<MediaFile>();

    private ArrayList<BaseDataBean> mDataList;

    private SharedConfig sharedConfig;

    public AlbumPresenter() {

    }

    /**
     *
     * @param dataList 不能为空
     */
    @Override
    public void init(@NonNull ArrayList<BaseDataBean> dataList) {
        this.mDataList = dataList;
        MediaDataHelper.getInstance().setMediaFileList(mMediaFileList);
        initMediaManager();
    }

    @Override
    public void loadAllData() {

    }

    @Override
    public void enterPreview(Context context, int position) {
        sharedConfig = new SharedConfig(context);
        BaseDataBean baseDataBean = mDataList.get(position);
        if (baseDataBean.getType() == BaseDataBean.TYPE_MEDIA) {

            int titleCount = 0;
            for (int i = 0; i <= position; i++) {
                if (mDataList.get(i).getType() == BaseDataBean.TYPE_TITLE) {
                    titleCount++;
                }
            }
            Intent intent = new Intent(context, PreviewMediaActivity.class);
            intent.putExtra(PreviewMediaActivity.PREVIEW_POSITION, position - titleCount);
            context.startActivity(intent);
        }
    }


    private MediaManager.FileListStateListener updateFileListStateListener = new MediaManager.FileListStateListener() {
        @Override
        public void onFileListStateChange(MediaManager.FileListState state) {
            mCurrentFileListState = state;
        }
    };

    private MediaManager.VideoPlaybackStateListener updatedVideoPlaybackStateListener =
            new MediaManager.VideoPlaybackStateListener() {
                @Override
                public void onUpdate(MediaManager.VideoPlaybackState videoPlaybackState) {

                }
            };


    private void initMediaManager() {
        Camera camera = CameraManager.getCamera();
        if (null != camera) {
            if (camera.isMediaDownloadModeSupported()) {
                mMediaManager = camera.getMediaManager();
                if (null != mMediaManager) {
                    mMediaManager.addUpdateFileListStateListener(this.updateFileListStateListener);
                    mMediaManager.addMediaUpdatedVideoPlaybackStateListener(this.updatedVideoPlaybackStateListener);
                    camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError error) {
                            if (error == null) {
                                getFileList();
                            } else {
                                showToast("设置相机媒体管理模式失败");
                            }
                        }
                    });
                    if (!mMediaManager.isVideoPlaybackSupported()) {
                        showToast("不支持视频回放");

                    }
                    mMediaTaskScheduler = mMediaManager.getScheduler();
                }

            } else {
                showToast("不支持预览媒体数据");
            }
        } else {
            showToast("飞机未连接或相机未连接");
        }

    }

    private void getFileList() {
        showProgress(true);
        if (mMediaManager != null) {

            if ((mCurrentFileListState == MediaManager.FileListState.SYNCING) || (mCurrentFileListState == MediaManager.FileListState.DELETING)) {
                showToast("媒体管理器运行忙碌");
            } else {



                List<MediaFile> mediaFileList;

                    mediaFileList = mMediaManager.getInternalStorageFileListSnapshot();




                mMediaManager.refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (null == djiError) {

                            showToast("获取媒体文件成功");

                            if (mCurrentFileListState != MediaManager.FileListState.INCOMPLETE) {
                                mMediaFileList.clear();


                            }

                            mMediaFileList.addAll(mediaFileList);
                            sortMediaFilesByTime(mMediaFileList);
                            mMediaTaskScheduler.resume(new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError error) {
                                    if (error == null) {
                                        showToast("正在获取缩略图");
                                        getThumbnails();

                                    }
                                }
                            });
                        } else {
                            showToast("获取媒体文件失败");
                        }

                        showProgress(false);
                    }
                });

            }
        } else {
            showToast("连接媒体管理器失败");
            showProgress(false);
        }
    }

    /**
     * 按时间排序
     *
     * @param mediaFiles
     */
    private void sortMediaFilesByTime(List<MediaFile> mediaFiles) {

        Collections.sort(mediaFiles, new Comparator<MediaFile>() {
            @Override
            public int compare(MediaFile lhs, MediaFile rhs) {
                if (lhs.getTimeCreated() < rhs.getTimeCreated()) {
                    return 1;
                } else if (lhs.getTimeCreated() > rhs.getTimeCreated()) {
                    return -1;
                }
                return 0;
            }
        });

        String tempTime = null;
        for (MediaFile mediaFile : mediaFiles) {
            String timeCreated = DateHelperUtils.formatTimeByYMD(mediaFile.getTimeCreated());
            if (tempTime == null || !tempTime.equals(timeCreated)) {
                tempTime = timeCreated;
                TitleBean titleBean = new TitleBean();
                titleBean.setTime(timeCreated);
                mDataList.add(titleBean);
            }

            AlbumBean albumBean = new AlbumBean();
            albumBean.setMediaFile(mediaFile);
            mDataList.add(albumBean);
        }
    }

    private void getThumbnails() {
        if (mMediaFileList.size() <= 0) {
            showToast("没有缩略图");
            return;
        }
        for (int i = 0; i < mMediaFileList.size(); i++) {
            getThumbnailByIndex(i);
        }
    }

    private void getPreviews() {
        if (mMediaFileList.size() <= 0) {
            showToast("没有预览图");
            return;
        }
        for (int i = 0; i < mMediaFileList.size(); i++) {
            getPreviewByIndex(i);
        }
    }

    private FetchMediaTask.Callback mGetThumbnailCallback = new FetchMediaTask.Callback() {
        @Override
        public void onUpdate(MediaFile file, FetchMediaTaskContent option, DJIError error) {
            if (null == error) {
                if (option == FetchMediaTaskContent.THUMBNAIL) {

                    refreshData();
                }
            } else {
                showToast("获取缩略图失败");
            }
        }
    };

    private FetchMediaTask.Callback mGetPreviewCallback = new FetchMediaTask.Callback() {
        @Override
        public void onUpdate(MediaFile file, FetchMediaTaskContent option, DJIError error) {
            if (null == error) {
                if (option == FetchMediaTaskContent.PREVIEW) {
                    refreshData();
                }
            } else {
                showToast("获取预览图失败");
            }
        }
    };

    private void getThumbnailByIndex(final int index) {
        FetchMediaTask task = new FetchMediaTask(mMediaFileList.get(index), FetchMediaTaskContent.THUMBNAIL, mGetThumbnailCallback);
        mMediaTaskScheduler.moveTaskToEnd(task);
    }

    private void getPreviewByIndex(final int index) {
        FetchMediaTask task = new FetchMediaTask(mMediaFileList.get(index), FetchMediaTaskContent.PREVIEW, mGetPreviewCallback);
        mMediaTaskScheduler.moveTaskToEnd(task);
    }

    private void showToast(final String msg) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().showToast(msg);
                }
            }
        });
    }

    private void refreshData() {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().refreshData(mDataList);
                }
            }
        });
    }

    private void showProgress(final boolean isShow) {
        mUIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (isViewAttached()) {
                    getView().showLoading(isShow, "");
                }
            }
        });
    }

    @Override
    public void destroy() {
        if (mMediaManager != null) {
            mMediaManager.stop(null);
            mMediaManager.removeFileListStateCallback(this.updateFileListStateListener);
            mMediaManager.removeMediaUpdatedVideoPlaybackStateListener(updatedVideoPlaybackStateListener);
            mMediaManager.exitMediaDownloading();
            if (mMediaTaskScheduler != null) {
                mMediaTaskScheduler.removeAllTasks();
            }
        }

        if (mMediaFileList != null) {
            mMediaFileList.clear();
            mMediaFileList = null;
        }

        MediaDataHelper.getInstance().destroy();

        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError mError) {
                    if (mError != null) {
                        showToast("设置相机拍照模式失败");
                    }
                }
            });
        }
    }
}
