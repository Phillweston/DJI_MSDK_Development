package com.ew.autofly.utils.zip;

/**
 * 压缩回调
 *
 */
public interface IZipCallback {
    
    void onStart();

    /**
     * 进度回调
     *
     * @param percentDone 完成百分比
     */
    void onProgress(int percentDone);

    /**
     * 完成
     *
     * @param success 是否成功
     */
    void onFinish(boolean success);
}
