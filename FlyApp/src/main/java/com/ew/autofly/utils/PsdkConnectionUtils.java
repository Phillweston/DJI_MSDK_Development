package com.ew.autofly.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.keysdk.CameraKey;
import dji.keysdk.KeyManager;
import dji.keysdk.PayloadKey;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;

public class PsdkConnectionUtils {

    public void initPayloadDevices(final OnPsdkCallback onPsdkCallback) {

        KeyManager.getInstance().getValue(PayloadKey.create(PayloadKey.CONNECTION, 0), new GetCallback() {
            @Override
            public void onSuccess(@NonNull Object o) {

                receivePayloadDevicesData(0, onPsdkCallback);
                onPsdkCallback.onPsdkConnected();
            }

            @Override
            public void onFailure(@NonNull DJIError djiError) {
            }
        });

        KeyManager.getInstance().getValue(PayloadKey.create(PayloadKey.CONNECTION, 1), new GetCallback() {
            @Override
            public void onSuccess(@NonNull Object o) {

                receivePayloadDevicesData(1, onPsdkCallback);
                onPsdkCallback.onPsdkConnected();
            }

            @Override
            public void onFailure(@NonNull DJIError djiError) {
            }
        });
    }

    private void receivePayloadDevicesData(int psdkIndex, final OnPsdkCallback onPsdkCallback) {
        if (KeyManager.getInstance() != null) {

            KeyListener mReceivePayloadDataKeyListener = new KeyListener() {
                @Override
                public void onValueChange(@Nullable Object oldValue, @Nullable final Object newValue) {

                    if (newValue instanceof byte[]) {

                        byte[] buffer = (byte[]) newValue;

                        final String resultBuffer = new String(buffer);

                        if (resultBuffer.toUpperCase().contains("NONE")) {
                            onPsdkCallback.onNoneDataReceive(resultBuffer);
                        } else if (resultBuffer.toUpperCase().contains("BZ")) {

                            String[] dataResult = resultBuffer.split("-");

                            Double angle = Double.parseDouble(dataResult[1]);

                            int distance = Integer.parseInt(dataResult[2]);

                            onPsdkCallback.onAvoidanceDataReceive(distance, angle);
                        }
                    }
                }
            };

            KeyManager.getInstance().addListener(
                    PayloadKey.create(PayloadKey.GET_DATA_FROM_PAYLOAD, psdkIndex), mReceivePayloadDataKeyListener);
            KeyManager.getInstance().setValue(CameraKey.create(CameraKey.MODE),
                    SettingsDefinitions.CameraMode.SHOOT_PHOTO, null);
            KeyManager.getInstance().setValue(CameraKey.create(CameraKey.SHOOT_PHOTO_MODE),
                    SettingsDefinitions.ShootPhotoMode.SINGLE, null);
        }
    }

    public interface OnPsdkCallback {
        void onPsdkConnected();

        void onAvoidanceDataReceive(int distance, Double angle);

        void onNoneDataReceive(String data);
    }

}
