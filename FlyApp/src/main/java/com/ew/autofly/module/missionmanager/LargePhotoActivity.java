package com.ew.autofly.module.missionmanager;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ew.autofly.R;
import com.ew.autofly.activities.BaseActivity;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.entity.MissionPhoto;
import com.ew.autofly.db.helper.MissionPhotoDbHelper;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.media.DownloadListener;
import dji.sdk.media.MediaFile;





public class LargePhotoActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_MISSIONPHOTO = "currentMissionPhoto";

    public static final String EXTRA_MISSIONPHOTO_LIST = "missionPhotoList";

    private MissionPhoto mCurrentMissionPhoto;
    private List<MissionPhoto> mMissionPhotoList;
    private ImageView mPhotoIv;
    private TextView mPhotoName;
    private TextView mIndexTv;
    private ImageView mLeftArrowIv;
    private ImageView mRightArrowIv;
    private ImageButton mBackImageBtn;
  
    private ViewGroup mProgressLayout;
    private ViewPager mViewPager;
    private PhotoViewPagerAdapter mPhotoViewPagerAdapter;

    public List<MediaFile> mDJIMediaInfoList = null;
    private BaseProduct mProduct = null;
    private MissionPhotoDbHelper mMissionPhotoDbHelper;

    private boolean hasUpdatePicture = false;

    private List<ViewGroup> mImageViewList;

    private Context mContext = LargePhotoActivity.this;

    private String TAG = this.getClass().getName();

    private boolean isCheckDrone = false;

    private SharedConfig sharedConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_large_photo);
        initData();
        initView();
        sharedConfig = new SharedConfig(this);
    }

    private void initData() {

        try {
            mMissionPhotoDbHelper = MissionPhotoDbHelper.getInstance();
        } catch (Exception e) {
            ToastUtil.show(mContext, "数据库初始化错误,系统即将退出");
            finish();
            return;
        }

        mProduct = EWApplication.getProductInstance();
        mCurrentMissionPhoto = (MissionPhoto) getIntent().getExtras().getSerializable(EXTRA_MISSIONPHOTO);
        mMissionPhotoList = (List<MissionPhoto>) getIntent().getExtras().getSerializable(EXTRA_MISSIONPHOTO_LIST);
        mImageViewList = new ArrayList<>();

    }

    private void initView() {

        mPhotoIv = (ImageView) findViewById(R.id.iv_photo);
        mPhotoName = (TextView) findViewById(R.id.tv_photo_name);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mIndexTv = (TextView) findViewById(R.id.tv_index);
      
        mLeftArrowIv = (ImageView) findViewById(R.id.iv_left_arrow);
        mRightArrowIv = (ImageView) findViewById(R.id.iv_right_arrow);
        mBackImageBtn = (ImageButton) findViewById(R.id.imgBtnBack);
        mLeftArrowIv.setOnClickListener(this);
        mRightArrowIv.setOnClickListener(this);
        mBackImageBtn.setOnClickListener(this);

        initPhotoViewPager();

    }

    private void initPhotoViewPager() {
        int size = mMissionPhotoList.size();
        for (int i = 0; i < size; i++) {
            ViewGroup parentView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.item_large_photo, mViewPager, false);
            mImageViewList.add(parentView);
        }

        mPhotoViewPagerAdapter = new PhotoViewPagerAdapter();
        mViewPager.setAdapter(mPhotoViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        int index = mMissionPhotoList.indexOf(mCurrentMissionPhoto);
        if (index == 0) {
            mOnPageChangeListener.onPageSelected(index);
        } else {
            mViewPager.setCurrentItem(index);
        }

    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                mLeftArrowIv.setVisibility(View.GONE);
                if (position == mPhotoViewPagerAdapter.getCount() - 1) {
                    mRightArrowIv.setVisibility(View.GONE);
                }
            } else if (position == mPhotoViewPagerAdapter.getCount() - 1) {
                mRightArrowIv.setVisibility(View.GONE);
            } else {
                mLeftArrowIv.setVisibility(View.VISIBLE);
                mRightArrowIv.setVisibility(View.VISIBLE);
            }

            mCurrentMissionPhoto = mMissionPhotoList.get(position);
            mIndexTv.setText(position + 1 + "/" + mMissionPhotoList.size());

            final String bigPhotoPath = mCurrentMissionPhoto.getBigPhotoPath();
            final String missionId = mCurrentMissionPhoto.getMissionId();
            final String missionPath = IOUtils.getRootStoragePath(getApplicationContext()) + File.separator +
                    AppConstant.DIR_DRONE_PHOTO + "/" + missionId + "/";

            ViewGroup parentView = mImageViewList.get(position);
            ImageView childImage = (ImageView) parentView.findViewById(R.id.iv_photo);

            if (bigPhotoPath != null) {
                mPhotoName.setText(mCurrentMissionPhoto.getBigPhotoPath());
                String filePath = missionPath + bigPhotoPath;
                File file = new File(filePath);
                if (file.exists()) {
                    Glide.with(mContext).load(filePath).diskCacheStrategy(DiskCacheStrategy.NONE).into(childImage);
                } else {
                    downloadPhoto(childImage, missionPath, position);
                }

            } else {
                mPhotoName.setText("暂无名称");
                downloadPhoto(childImage, missionPath, position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private class PhotoViewPagerAdapter extends PagerAdapter {

        public PhotoViewPagerAdapter() {
        }

        @Override
        public int getCount() {
            return mImageViewList == null ? 0 : mImageViewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ViewGroup parentView = mImageViewList.get(position);
            container.addView(parentView, 0);
            return parentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void downloadPhoto(final ImageView childImage, final String missionPath, final int position) {
        checkDroneMediaFile(new CheckDroneMediaFileCallBack() {
            @Override
            public void onSuccess() {
                downAndUpdate(childImage, missionPath);
            }

            @Override
            public void onFail() {

            }
        });

        downAndUpdate(childImage, missionPath);
    }

    private void downAndUpdate(final ImageView childImage, final String missionPath) {

        downAndUpdate(new DownloadPhotoCallback() {

            @Override
            public void onSuccess(final String fileName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoName.setText(fileName);

                        Glide.with(mContext).load(missionPath + fileName).diskCacheStrategy(DiskCacheStrategy.NONE).into(childImage);

                        mMissionPhotoDbHelper.updateMissionBigFile(mCurrentMissionPhoto.getId(), fileName);
                        hasUpdatePicture = true;
                    }
                });
            }

            @Override
            public void onFail(final DJIError djiError) {
                if (djiError != null)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(mContext, "对应的大图下载失败" + djiError.getDescription());
                        }
                    });

            }

        });
    }

    private interface CheckDroneMediaFileCallBack {
        void onSuccess();

        void onFail();
    }

    private interface DownloadPhotoCallback {
        void onSuccess(String fileName);

        void onFail(DJIError djiError);
    }

    private void checkDroneMediaFile(@NonNull final CheckDroneMediaFileCallBack callBack) {
        if (mDJIMediaInfoList == null) {
            if (mProduct != null && mProduct.getCamera() != null && mProduct.getCamera().getMediaManager() != null) {
                if (!isCheckDrone) {
                    isCheckDrone = true;

                    mProduct.getCamera().getMediaManager().refreshFileListOfStorageLocation(SettingsDefinitions.StorageLocation.INTERNAL_STORAGE, new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError == null) {
                                List<MediaFile> mediaFileList = mProduct.getCamera().getMediaManager().getInternalStorageFileListSnapshot();
                                if (mediaFileList != null) {
                                    mDJIMediaInfoList = mediaFileList;
                                    callBack.onSuccess();
                                } else {
                                    isCheckDrone = false;
                                    ToastUtil.show(mContext, "无法获取无人机媒体文件");
                                    callBack.onFail();
                                }
                            } else {
                                isCheckDrone = false;
                                ToastUtil.show(mContext, "获取无人机媒体文件错误：" + djiError);
                                callBack.onFail();
                            }
                        }
                    });
                }
            } else {
                ToastUtil.show(mContext, "请先连接上无人机");
            }
        }
    }


    private void downAndUpdate(@NonNull final DownloadPhotoCallback callback) {
      
        MediaFile tmpMedia = null;
        if (mDJIMediaInfoList != null) {
            for (MediaFile media : mDJIMediaInfoList) {
                if (media != null) {
                    if (media.getIndex() == mCurrentMissionPhoto.getPhotoIndex()) {
                        tmpMedia = media;
                        break;
                    }
                }
            }


            if (tmpMedia == null) {
                Toast.makeText(mContext, "无人机无对应大图", Toast.LENGTH_LONG).show();
            /*    mProgressLayout.setVisibility(View.GONE);
                mPhotoIv.setImageResource(R.drawable.no_pic);*/
            } else {

                Toast.makeText(mContext, "开始从无人机获取 " + tmpMedia.getFileName(), Toast.LENGTH_LONG).show();
                final String photoId = mCurrentMissionPhoto.getId();
                final String fileName = "max_" + tmpMedia.getFileName().toLowerCase();

                final File destDir = new File(IOUtils.getRootStoragePath(mContext) +
                        AppConstant.DIR_DRONE_PHOTO + File.separator + mCurrentMissionPhoto.getMissionId() + File.separator);

                mCurrentMissionPhoto.setBigPhotoPath(fileName);
                try {
                    tmpMedia.fetchFileData(destDir, fileName.substring(0, fileName.lastIndexOf(".")), new DownloadListener<String>() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onRateUpdate(long l, long l1, long l2) {

                        }

                        @Override
                        public void onRealtimeDataUpdate(byte[] bytes, long l, boolean b) {

                        }

                        @Override
                        public void onProgress(long l, long l1) {

                        }

                        @Override
                        public void onSuccess(String s) {

                            callback.onSuccess(fileName);
                        }

                        @Override
                        public void onFailure(DJIError djiError) {
                            callback.onFail(djiError);
                        }
                    });
                } catch (Exception e) {
                    ToastUtil.show(mContext, "下载后错误：" + e.toString());
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (mProgressLayout.getVisibility() == View.VISIBLE) {
            mProgressLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_arrow:
                int nowIndex = mViewPager.getCurrentItem();
                nowIndex--;
                if (nowIndex >= 0) {
                    mViewPager.setCurrentItem(nowIndex, true);
                }
                break;
            case R.id.iv_right_arrow:
                int index = mViewPager.getCurrentItem();
                index++;
                if (index <= mPhotoViewPagerAdapter.getCount()) {
                    mViewPager.setCurrentItem(index, true);
                }
                break;
            case R.id.imgBtnBack:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        if (hasUpdatePicture) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        exitMediaDownloading();
        super.finish();
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

}

