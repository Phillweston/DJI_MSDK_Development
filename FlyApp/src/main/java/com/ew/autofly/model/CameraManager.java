package com.ew.autofly.model;

import com.ew.autofly.application.EWApplication;
import com.ew.autofly.config.CameraConfig;
import com.ew.autofly.entity.sequoia.config.Config;

import java.util.List;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.remotecontroller.HardwareState;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;


public class CameraManager {

    public interface GetFocalLengthCallback {

        void onCallback(boolean getFocusLenSuccess, int focalLen);
    }

    public static Camera getCamera() {
        BaseProduct productInstance = EWApplication.getProductInstance();
        if (productInstance != null) {
            return productInstance.getCamera();
        }

        return null;
    }

    /**
     * 是否支持光学变焦（暂时只显示Mavic 2 Zoom）
     *
     * @return
     */
    public static boolean isSupportCameraOpticalZoom() {
        Model model = AircraftManager.getModel();
        if (model == null) {
            return false;
        }
        return model == Model.MAVIC_2_ZOOM;
    }

    /**
     * 获取御2双光镜头
     *
     * @return
     */
    public static Camera getMavic2EnterpriseDualCamera() {
        BaseProduct productInstance = EWApplication.getProductInstance();
        if (productInstance != null) {
            List<Camera> cameraList = productInstance.getCameras();
            if (cameraList != null && cameraList.size() > 1) {
                return cameraList.get(1);
            } else if (cameraList != null && !cameraList.isEmpty()){
                return cameraList.get(0);
            }
        }
        return null;
    }

    public static boolean isXT2CameraConnected() {

        Model model = AircraftManager.getModel();
        if (AircraftManager.isM200Series()
                || AircraftManager.isM200V2Series()
                || model == Model.MATRICE_600 || model == Model.MATRICE_600_PRO) {
            BaseProduct productInstance = EWApplication.getProductInstance();
            return productInstance != null && productInstance.getCameraWithComponentIndex(0) != null
                    && Camera.DisplayNameXT2_VL.equals(productInstance.getCameraWithComponentIndex(0).getDisplayName());
        }

        return false;
    }

    public static boolean isZ30CameraConnected() {
        if (AircraftManager.isM200Series()
                || AircraftManager.isM200V2Series()) {
            BaseProduct productInstance = EWApplication.getProductInstance();
            return productInstance != null && productInstance.getCameraWithComponentIndex(0) != null
                    && Camera.DisplayNameZ30.equals(productInstance.getCameraWithComponentIndex(0).getDisplayName());
        }

        return false;
    }

    /**
     * 判断变焦焦距是否合适
     *
     * @param zoomLen
     * @return
     */
    public static boolean isAppropriateCameraZoom(int zoomLen) {
        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            if (Camera.DisplayNameZ30.equals(camera.getDisplayName())) {
                if (zoomLen >= CameraConfig.Z30.FOCAL_LENGTH_MIN
                        && zoomLen <= CameraConfig.Z30.FOCAL_LENGTH_MAX) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否为变焦相机
     *
     * @return
     */
    public static boolean isZoomCamera() {
        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            if (Camera.DisplayNameZ30.equals(camera.getDisplayName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以在航点添加变焦动作
     *
     * @param cameraName {@link CameraConfig Name参数}
     * @return
     */
    public static boolean isCanAddCameraZoomAction(String cameraName) {
        if (CameraConfig.Z30.NAME.equals(cameraName)) {
            return true;
        }
        return false;
    }

    /**
     * 没有相机照片回调
     *
     * @return
     */
    public static boolean isNoCameraMediaCallback() {
        return isZ30CameraConnected() || isXT2CameraConnected();
    }

    /**
     * 是否支持遥控器按钮拍照
     *
     * @return
     */
    public static boolean isSupportShootPhotoAndRecordByRC() {
        Model model = AircraftManager.getModel();
        if (model == null) {
            return false;
        }
        /**
         * The implementation of shoot photo button and record video button in MSDK have been removed,
         * developers need to implement their own function by listening to the press events of Remote Controller
         * for Mavic Pro and Mavic 2 platforms
         */
        return !(model == Model.MAVIC_PRO || model == Model.MAVIC_2 || model == Model.MAVIC_2_ZOOM || model == Model.MAVIC_2_PRO || model == Model.MAVIC_2_ENTERPRISE || model == Model.MAVIC_2_ENTERPRISE_DUAL);
    }

    
    public static void shootPhotoAndRecordByRC(HardwareState hardwareState, SystemState cameraState) {
        if (!CameraManager.isSupportShootPhotoAndRecordByRC()) {
            Camera camera = CameraManager.getCamera();
            if (camera != null && cameraState != null) {
                if (hardwareState.getShutterButton().isClicked()) {
                    if (cameraState.getMode() == SettingsDefinitions.CameraMode.SHOOT_PHOTO) {
                        if (cameraState.isShootingSinglePhoto() || cameraState.isShootingSinglePhotoInRAWFormat()
                                || cameraState.isShootingBurstPhoto() || cameraState.isShootingIntervalPhoto()
                                || cameraState.isShootingPanoramaPhoto() || cameraState.isShootingRAWBurstPhoto()
                                || cameraState.isShootingShallowFocusPhoto()) {
                            camera.stopShootPhoto(null);
                        } else {
                            camera.startShootPhoto(null);
                        }
                    } else {
                        camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
                    }
                } else if (hardwareState.getRecordButton().isClicked()) {
                    if (cameraState.getMode() == SettingsDefinitions.CameraMode.RECORD_VIDEO) {
                        if (cameraState.isRecording()) {
                            camera.stopRecordVideo(null);
                        } else {
                            camera.startRecordVideo(null);
                        }
                    } else {
                        camera.setMode(SettingsDefinitions.CameraMode.RECORD_VIDEO, null);
                    }
                }
            }

        }
    }

    public static void getFocalLength(GetFocalLengthCallback callback) {

        if (CameraManager.isZ30CameraConnected()) {
            Camera camera = CameraManager.getCamera();
            if (camera != null) {
                camera.getOpticalZoomFocalLength(new CommonCallbacks.CompletionCallbackWith<Integer>() {
                    @Override
                    public void onSuccess(Integer zoomLen) {
                        callback.onCallback(true, zoomLen);
                    }

                    @Override
                    public void onFailure(DJIError djiError) {
                        callback.onCallback(false, 0);
                    }
                });
            } else {
                callback.onCallback(false, 0);
            }
        } else {
            callback.onCallback(false, 0);
        }
    }

    public static String getSaveName() {
        String saveName = "NULL";
        Camera camera = CameraManager.getCamera();
        if (camera != null) {
            if (Camera.DisplayNameZ30.equals(camera.getDisplayName())) {
                saveName = CameraConfig.Z30.NAME;
            } else if (Camera.DisplaynamePhantom4RTKCamera.equals(camera.getDisplayName())) {
                saveName = CameraConfig.Phantom4RTKCamera.NAME;
            }
        }
        return saveName;
    }

}
