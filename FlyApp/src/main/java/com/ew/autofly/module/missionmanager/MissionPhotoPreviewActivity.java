package com.ew.autofly.module.missionmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionBase;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.db.helper.MissionPhotoDbHelper;
import com.ew.autofly.module.missionmanager.adapter.MissionPhotoAdapter;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.LogUtilsOld;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.xflyer.utils.ThreadPoolUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.MediaFile;





public class MissionPhotoPreviewActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String PARAMS_PREVIEW_MISSION = "PARAMS_PREVIEW_MISSION";

    private static final int REQUEST_CODE_MEDIA = 1;

    private ImageButton mBackImgBtn;
    private TextView mEmptyView;
    private GridView mGridView;
    private MissionPhotoAdapter mPhotoAdapter;
    private MissionBase mMission;
    public ArrayList<MissionPhoto> mMissionPhotoList;

    public List<MediaFile> mDJIMediaInfoList = null;
    private BaseProduct mProduct = null;

    private MissionPhotoDbHelper mMissionPhotoDbHelper;

    private String rootPath;

    private String TAG = this.getClass().getName();

    private LogUtilsOld log;

    private ProgressDialog mLoading;

    private SharedConfig sharedConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_preview);
        initData();
        initView();
        sharedConfig = new SharedConfig(this);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.GridView);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mBackImgBtn = (ImageButton) findViewById(R.id.imgBtnBack);
        mBackImgBtn.setOnClickListener(this);
        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(this);
        mPhotoAdapter = new MissionPhotoAdapter(this, mMissionPhotoList);
        mGridView.setAdapter(mPhotoAdapter);

        if (loadOnDisk()) {
            loadOnFlight();
        }
    }

    private void initData() {
        mMission = (MissionBase) getIntent().getExtras().getSerializable(PARAMS_PREVIEW_MISSION);
        mMissionPhotoDbHelper = MissionPhotoDbHelper.getInstance();
        mDJIMediaInfoList = new ArrayList<>();
        mMissionPhotoList = new ArrayList<>();
        rootPath = IOUtils.getRootStoragePath(getApplicationContext());

        log = LogUtilsOld.getInstance(this);
    }

    private boolean loadOnDisk() {

        mMissionPhotoList.clear();
        List<MissionPhoto> resultList = mMissionPhotoDbHelper.getPhotoByMissionId(mMission.getMissionId());
        mMissionPhotoList.addAll(resultList);

        if (mMissionPhotoList.isEmpty()) {
            return false;
        }

        if (mMissionPhotoList.size() > 1000) {
            ToastUtil.show(this, "载入图片异常：图片大于1000张");
            return false;
        }

        if (mMissionPhotoList.size() > 0) {
            int startIndex = mMission.getStartPhotoIndex();
            int endIndex = mMission.getEndPhotoIndex();
            if (startIndex != 0 && endIndex != 0 && endIndex >= startIndex) {
                if (endIndex - startIndex > 1000) {
                  
                    ToastUtil.show(this, "载入图片异常：图片大于1000张");
                    return false;
                }

                List<MissionPhoto> newMissionPhotoList = new ArrayList<>();

                for (int i = startIndex; i <= endIndex; i++) {
                    for (MissionPhoto missionPhoto : mMissionPhotoList) {
                        if (missionPhoto.getPhotoIndex() == i) {
                            newMissionPhotoList.add(missionPhoto);
                        }
                    }
                }

                mMissionPhotoList.clear();
                mMissionPhotoList.addAll(newMissionPhotoList);
            }


               /* List<Integer> indexList = new ArrayList<>();
                for (MissionPhoto missionPhoto : mMissionPhotoList) {
                    indexList.add(missionPhoto.getPhotoIndex());
                }

                for (int i = startIndex; i < endIndex; i++) {
                  
                    if (!indexList.contains(i)) {
                      
                        MissionPhoto missionPhoto = new MissionPhoto();
                        missionPhoto.setMissionId(mMission.getId());
                        missionPhoto.setPhotoIndex(i);
                        mMissionPhotoDbHelper.save(missionPhoto);
                        mMissionPhotoList.add(missionPhoto);
                    }
                }

                Collections.sort(mMissionPhotoList, new Comparator<MissionPhoto>() {
                    @Override
                    public int compare(MissionPhoto lhs, MissionPhoto rhs) {
                        return ((Integer) lhs.getPhotoIndex()).compareTo(rhs.getPhotoIndex());
                    }
                });

            }*/
        }

        mPhotoAdapter.notifyDataSetChanged();

        return true;

    }

    private void loadOnFlight() {
        File photoFile = new File(IOUtils.getRootStoragePath(getApplicationContext()) + File.separator
                + AppConstant.DIR_DRONE_PHOTO + File.separator + mMission.getMissionId() + File.separator);

        if (photoFile.exists()) {
            List<String> fileNameList = Arrays.asList(photoFile.list());
            if (mMissionPhotoList.size() != 0) {
                boolean flag = true;
                for (MissionPhoto missionPhoto : mMissionPhotoList) {
                    if (!fileNameList.contains(missionPhoto.getPhotoPath())) {
                        flag = false;
                    }
                }

                if (flag) {
                  
                    return;
                }
            }
        }

      
        mProduct = EWApplication.getProductInstance();
        if (mProduct == null || !mProduct.isConnected()) {
            ToastUtil.show(this, "请先连接上无人机");
            return;
        }

        mLoading = ProgressDialog.show(this, null, "正在从无人机获取图片……", true, false);

        if (mProduct.getCamera() != null && mProduct.getCamera().getMediaManager() != null) {
            mProduct.getCamera().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {

                        mProduct.getCamera().getMediaManager().refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(final DJIError djiError) {
                                if (djiError == null) {
                                    List<MediaFile> mediaFileList;



                                        mediaFileList = mProduct.getCamera().getMediaManager().getInternalStorageFileListSnapshot();

                                    if (mediaFileList != null) {
                                        mDJIMediaInfoList.addAll(mediaFileList);
                                    }
                                    List<MissionPhoto> downloadPhotoList = new ArrayList<MissionPhoto>();
                                    for (MissionPhoto missionPhoto : mMissionPhotoList) {
                                        if (missionPhoto.getPhotoPath() != null) {
                                            String filePath = rootPath + File.separator +
                                                    AppConstant.DIR_DRONE_PHOTO + "/" + missionPhoto.getMissionId() + "/" + missionPhoto.getPhotoPath();

                                            filePath = filePath.replace(".mov", ".jpg");
                                            filePath = filePath.replace(".mp4", ".jpg");

                                            File photoFile = new File(filePath);
                                            if (photoFile.exists()) {
                                                continue;
                                            }
                                        }

                                        downloadPhotoList.add(missionPhoto);
                                    }

                                    if (downloadPhotoList.size() > 0) {
                                        downloadThumbnail(downloadPhotoList);
                                    } else {
                                        ToastUtil.show(MissionPhotoPreviewActivity.this, "获取缩略图失败: 无缩略图");
                                    }

                                } else {
                                    log.e(TAG + "获取缩略图失败：" + djiError.getDescription());
                                    ToastUtil.show(MissionPhotoPreviewActivity.this, "获取缩略图失败：" + djiError.getDescription());

                                }

                                mLoading.dismiss();
                            }
                        });
                    } else {
                        ToastUtil.show(MissionPhotoPreviewActivity.this, "获取缩略图失败：" + djiError.getDescription());
                        mLoading.dismiss();
                    }
                }
            });
        }
    }

    private void downloadThumbnail(List<MissionPhoto> photoList) {

        List<MediaFile> downloadMedia = new ArrayList<>();
        HashMap<MissionPhoto, MediaFile> map = new HashMap<>();
        for (MissionPhoto missionPhoto : photoList) {
            for (MediaFile djiMedia : mDJIMediaInfoList) {
                if (djiMedia != null) {
                    if (djiMedia.getIndex() == missionPhoto.getPhotoIndex()) {
                        downloadMedia.add(djiMedia);
                        map.put(missionPhoto, djiMedia);
                        break;
                    }
                }
            }

        }

        int validCount = 0;
        int invalidCount = 0;
        for (MediaFile djiMedia : downloadMedia) {
            if (djiMedia == null) {
                invalidCount++;
            } else {
                validCount++;
            }
        }

        ToastUtil.show(this, "需要下载 " + photoList.size() + "张缩略图, " + "无人机中存在" + validCount + "张。");

        if (validCount > 0) {
            for (final MissionPhoto missionPhoto : photoList) {
                final MediaFile media = map.get(missionPhoto);
                if (media != null) {
                    final String fileName = "org_" + media.getFileName().toLowerCase();
                    missionPhoto.setPhotoPath(fileName);
                    media.fetchThumbnail(new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                saveBitmapToFile(media.getThumbnail(), missionPhoto, fileName);
                            } else {
                              
                            }
                        }
                    });
                }
            }
        }
    }

    private void saveBitmapToFile(final Bitmap bitmap, final MissionPhoto missionPhoto, final String fileName) {

        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                File mFolder = new File(rootPath + File.separator
                        + AppConstant.DIR_DRONE_PHOTO + File.separator + missionPhoto.getMissionId() + File.separator);

                if (!mFolder.exists()) {
                    mFolder.mkdirs();
                }

                String newName = fileName.replace(".mov", ".jpg");
                newName = newName.replace(".mp4", ".jpg");
                File photoFile = new File(mFolder.getAbsolutePath(), newName);
                try {
                    photoFile.createNewFile();
                    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(photoFile));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();

                    mMissionPhotoDbHelper.updateMissionFile(missionPhoto.getId(), fileName);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void exitMediaDownloading() {
        if (mProduct != null && mProduct.getCamera() != null
                && mProduct.getCamera().getMediaManager() != null) {
            try {
                mProduct.getCamera().getMediaManager().exitMediaDownloading();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnBack:
                exitMediaDownloading();
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        exitMediaDownloading();
        Intent intent = new Intent(this, LargePhotoActivity.class);
        intent.putExtra(LargePhotoActivity.EXTRA_MISSIONPHOTO, mMissionPhotoList.get(position));
        intent.putExtra(LargePhotoActivity.EXTRA_MISSIONPHOTO_LIST, mMissionPhotoList);
        startActivityForResult(intent, REQUEST_CODE_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MEDIA) {
            if (resultCode == RESULT_OK) {
              
                mPhotoAdapter.notifyDataSetChanged();
            }
        }
    }
}
