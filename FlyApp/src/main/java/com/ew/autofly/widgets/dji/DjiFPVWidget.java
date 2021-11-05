package com.ew.autofly.widgets.dji;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ew.autofly.R;
import com.ew.autofly.application.EWApplication;
import com.ew.autofly.model.AircraftManager;
import com.ew.autofly.utils.LogUtilsOld;

import de.mrapp.android.dialog.MaterialDialog;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.airlink.AirLink;
import dji.sdk.airlink.LightbridgeLink;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.sdkmanager.DJISDKManager;



/*public class DjiFPVWidget extends FPVWidget{

    public DjiFPVWidget(Context context) {
        super(context);
    }

    public DjiFPVWidget(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DjiFPVWidget(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}*/

public class DjiFPVWidget extends FrameLayout implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private Context mContext;

    private ImageView mIvSwitchSource;

    private boolean enableSwitchVideoSource = false;

    private boolean isPrimaryVideoFeed = false;


    private VideoFeeder.VideoFeed primaryVideoFeed;


    private VideoFeeder.VideoFeed secondaryVideoFeed;


    private VideoFeeder.VideoDataListener primaryVideoDataCallback = null;

    private VideoFeeder.VideoDataListener secondaryVideoDataCallback = null;

    private VideoFeeder.PhysicalSourceListener mPhysicalSourceListener = null;

    private DJICodecManager codecManager = null;

    private VideoSource mVideoSource = VideoSource.AUTO;
    private int videoHeight;
    private int videoWidth;
    private SurfaceTexture videoSurface;
    private TextureView mVideoTextureView;

    public DjiFPVWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.view_fpv_and_camera_display, this, true);

        mVideoTextureView = (TextureView) findViewById(R.id.texture_video_previewer_surface);
        mIvSwitchSource = (ImageView) findViewById(R.id.iv_switch_source);
        mIvSwitchSource.setOnClickListener(this);

        if (null != mVideoTextureView) {
            mVideoTextureView.setSurfaceTextureListener(this);
        }

        setVideoCallBack();
    }

    public VideoSource getVideoSource() {
        return mVideoSource;
    }


    /**
     * 设置视频源
     *
     * @param videoSource
     **/
    public void setVideoSource(VideoSource videoSource) {
        this.mVideoSource = videoSource;
        removeVideoCallBack();
        setVideoCallBack();
    }

    /**
     * 切换视频源
     *
     * @param videoSource
     **/
    public void switchVideoSource(VideoSource videoSource) {

        this.mVideoSource = videoSource;
        updateVideoFeed();

    }

    public void enableSwitchVideoSource(boolean enable) {
        mIvSwitchSource.setVisibility(enable ? VISIBLE : GONE);
    }

    public void updateVideoFeed() {
        removeVideoCallBack();
        destroyCodecManger();
        setVideoCallBack();
        this.onSurfaceTextureAvailable(this.videoSurface, this.videoWidth, this.videoHeight);
    }

    /**
     * 获取截图Bitmap
     *
     * @return
     */
    public Bitmap getBitmap() {
        if (this.mVideoTextureView != null) {
            return this.mVideoTextureView.getBitmap();
        }
        return null;
    }

    private void setVideoCallBack() {
        if (VideoFeeder.getInstance() != null) {

            if (mVideoSource == VideoSource.AUTO) {
                primaryVideoFeed = VideoFeeder.getInstance().getPrimaryVideoFeed();
                secondaryVideoFeed = VideoFeeder.getInstance().getSecondaryVideoFeed();
                isPrimaryVideoFeed = false;
            } else if (mVideoSource == VideoSource.PRIMARY || mVideoSource == VideoSource.SECONDARY) {
                primaryVideoFeed = VideoFeeder.getInstance().getPrimaryVideoFeed();
                isPrimaryVideoFeed = true;
            } else {
                primaryVideoFeed = VideoFeeder.getInstance().getSecondaryVideoFeed();
                isPrimaryVideoFeed = false;
            }



            if (!AircraftManager.isOnlyFlightController()) {
                setBandwidthAllocation();
            }

            primaryVideoDataCallback = new VideoFeeder.VideoDataListener() {
                @Override
                public void onReceive(byte[] bytes, int size) {
                    isPrimaryVideoFeed = true;
                    if (null != codecManager) {
                        codecManager.sendDataToDecoder(bytes, size);
                    }
                }
            };

            secondaryVideoDataCallback = new VideoFeeder.VideoDataListener() {
                @Override
                public void onReceive(byte[] bytes, int size) {
                    if (!isPrimaryVideoFeed) {
                        if (null != codecManager) {
                            codecManager.sendDataToDecoder(bytes, size);
                        }
                    } else {
                        secondaryVideoFeed.destroy();
                    }
                }
            };


            if (primaryVideoFeed != null) {
                primaryVideoFeed.addVideoDataListener(primaryVideoDataCallback);
            }

            if (secondaryVideoFeed != null && !isPrimaryVideoFeed) {
                secondaryVideoFeed.addVideoDataListener(secondaryVideoDataCallback);
            }
        }
    }

    private void getBandWidthAllocation() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        AirLink airLink = product.getAirLink();
        LightbridgeLink lightbridgeLink = airLink.getLightbridgeLink();
        LogUtilsOld.getInstance(mContext).e("getBandwidthAllocation");

        if (lightbridgeLink != null) {
            LogUtilsOld.getInstance(mContext).e("getBandwidthAllocation!=null");
        }

        if (product != null && (airLink = product.getAirLink()) != null && (lightbridgeLink = airLink.getLightbridgeLink()) != null) {

            lightbridgeLink.getBandwidthAllocationForLBVideoInputPort(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForLBVideoInputPort:" + aFloat);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForLBVideoInputPort:" + djiError.getDescription());
                }
            });

            lightbridgeLink.getBandwidthAllocationForMainCamera(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForMainCamera:" + aFloat);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForMainCamera:" + djiError.getDescription());
                }
            });

            lightbridgeLink.getBandwidthAllocationForLeftCamera(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForLeftCamera:" + aFloat);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForLeftCamera:" + djiError.getDescription());
                }
            });

            lightbridgeLink.getBandwidthAllocationForHDMIVideoInputPort(new CommonCallbacks.CompletionCallbackWith<Float>() {
                @Override
                public void onSuccess(Float aFloat) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForHDMIVideoInputPort:" + aFloat);
                }

                @Override
                public void onFailure(DJIError djiError) {
                    LogUtilsOld.getInstance(mContext).e("getBandwidthAllocationForHDMIVideoInputPort:" + djiError.getDescription());
                }
            });
        }
    }

    private void setBandwidthAllocation() {

        BaseProduct product = DJISDKManager.getInstance().getProduct();

        AirLink airLink;
        final LightbridgeLink lightbridgeLink;
        if (product != null && (airLink = product.getAirLink()) != null && (lightbridgeLink = airLink.getLightbridgeLink()) != null) {
            if (product.getModel() != null) {
                if (isExtPortSupportedProduct(product.getModel())) {


                    return;
                }
            }
            if (this.mVideoSource == VideoSource.AUTO) {
                lightbridgeLink.setBandwidthAllocationForLBVideoInputPort(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForMainCamera(1.0F, null);
                lightbridgeLink.setBandwidthAllocationForLeftCamera(0.5F, null);
            } else if (this.mVideoSource == VideoSource.PRIMARY) {
                lightbridgeLink.setBandwidthAllocationForLBVideoInputPort(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForMainCamera(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForLeftCamera(1.0F, null);
            } else if (this.mVideoSource == VideoSource.SECONDARY) {
                lightbridgeLink.setBandwidthAllocationForLBVideoInputPort(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForMainCamera(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForLeftCamera(0.0F, null);
            } else if (this.mVideoSource == VideoSource.FPV) {
                lightbridgeLink.setBandwidthAllocationForLBVideoInputPort(0.9F, null);
                lightbridgeLink.setBandwidthAllocationForMainCamera(0.0F, null);
                lightbridgeLink.setBandwidthAllocationForLeftCamera(1.0F, null);
            }
        }
    }

    private boolean isExtPortSupportedProduct(Model var1) {
        return var1 == Model.MATRICE_600 || var1 == Model.MATRICE_600_PRO || var1 == Model.A3 || var1 == Model.N3;
    }

    private void removeVideoCallBack() {
        removePrimaryVideoCallBack();
        removeSecondaryVideoCallBack();
    }

    private void removePrimaryVideoCallBack() {
        isPrimaryVideoFeed = false;
        if (primaryVideoFeed != null) {
            primaryVideoFeed.destroy();
        }
    }

    private void removeSecondaryVideoCallBack() {
        if (secondaryVideoFeed != null) {
            secondaryVideoFeed.destroy();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            if (codecManager == null) {
                this.videoSurface = surface;
                this.videoWidth = width;
                this.videoHeight = height;
                codecManager = new DJICodecManager(getContext(), surface, width, height);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (this.codecManager != null) {
            this.videoSurface = surface;
            this.videoWidth = width;
            this.videoHeight = height;
            this.codecManager.onSurfaceSizeChanged(width, height, 0);

        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        destroyCodecManger();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
       /* if (this.codecManager != null) {
            if (this.videoHeight != this.codecManager.getVideoHeight() || this.videoWidth != this.codecManager.getVideoWidth()) {
                this.videoWidth = this.codecManager.getVideoWidth();
                this.videoHeight = this.codecManager.getVideoHeight();
            }

            showToast("Updated-"+"width:"+this.videoWidth+"  height:"+this.videoHeight);

        }*/
    }

    private void destroyCodecManger() {
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager.destroyCodec();
            codecManager = null;
        }
    }

    private static final int MSG_WHAT_SHOW_TOAST = 0;

    public Handler mainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SHOW_TOAST:
                    Toast.makeText(EWApplication.getInstance(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void showToast(String s) {
        mainHandler.sendMessage(
                mainHandler.obtainMessage(MSG_WHAT_SHOW_TOAST, s)
        );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeVideoCallBack();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_switch_source:
                showSwitchVideoSourceDialog();
                break;
        }
    }

    private int mTempVideoSourceIndex;

    private void showSwitchVideoSourceDialog() {

        int source = this.getVideoSource().value();
        mTempVideoSourceIndex = source;

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext, R.style.TransparentDialogTheme);

        builder.setItemColor(getResources().getColor(R.color.black));
        builder.setMessage("若图传不能正常显示，请切换视频源");
        builder.setMessageColor(getResources().getColor(R.color.black));
        builder.setSingleChoiceItems(R.array.video_source, source, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int position) {
                mTempVideoSourceIndex = position;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle), null);
        builder.setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position) {
                VideoSource videoSource = VideoSource.find(mTempVideoSourceIndex);
                switchVideoSource(videoSource);
            }
        });
        builder.create().show();

    }

    public static enum VideoSource {

        AUTO(0),
        PRIMARY(1),
        SECONDARY(2),
        FPV(3);

        private int value;

        private VideoSource(int var3) {
            this.value = var3;
        }

        public int value() {
            return this.value;
        }

        private boolean _equals(int var1) {
            return this.value == var1;
        }

        public static VideoSource find(int var0) {
            VideoSource var1 = PRIMARY;

            for (int var2 = 0; var2 < values().length; ++var2) {
                if (values()[var2]._equals(var0)) {
                    var1 = values()[var2];
                    break;
                }
            }

            return var1;
        }

    }
}
