package com.flycloud.autofly.video.rtc;

import android.content.Context;
import android.util.AttributeSet;

import com.tencent.liteav.renderer.TXCGLSurfaceView;
import com.tencent.rtmp.ui.TXCloudVideoView;


public class RTCVideoView extends TXCloudVideoView {
    public RTCVideoView(Context context) {
        this(context,null);
    }

    public RTCVideoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * 解决SurfaceView叠加穿透的bug
     * @param txcglSurfaceView
     */
    @Override
    public void addVideoView(TXCGLSurfaceView txcglSurfaceView) {
        txcglSurfaceView.setZOrderMediaOverlay(true);
        txcglSurfaceView.setZOrderOnTop(true);
        super.addVideoView(txcglSurfaceView);
    }
}
