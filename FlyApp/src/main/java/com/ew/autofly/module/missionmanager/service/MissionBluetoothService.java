/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ew.autofly.module.missionmanager.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.ExportMission;
import com.ew.autofly.entity.Mission2;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;


public class MissionBluetoothService {

    private static final String TAG = "MissionBluetoothService";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_SEND = 2;
    public static final int MESSAGE_RECEIVE = 3;
    public static final int MESSAGE_TOAST = 4;
    public static final int MESSAGE_PROGRESS = 5;

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";


    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a67");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a67");

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ServerThread mSecureServerThread;

    private ClientThread mClientThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private int mNewState;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public static final int SEND_FAILED = 0;
    public static final int SEND_SUCCESS = 1;
    public static final int SEND_PROGRESS = 2;

    public static final int RECEIVE_FAILED = 3;
    public static final int RECEIVE_SUCCESS = 4;
    public static final int RECEIVE_PROGRESS = 5;

    public MissionBluetoothService(Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }


    private synchronized void updateUI() {
        mState = getState();
        Log.d(TAG, "updateUI() " + mNewState + " -> " + mState);
        mNewState = mState;

        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, mNewState).sendToTarget();
    }


    public synchronized int getState() {
        return mState;
    }


    public synchronized void startServer() {
        Log.d(TAG, "startServer");

        if (getState() != STATE_NONE) {
            updateUI();
            return;
        }

        if (mClientThread != null) {
            mClientThread.cancel();
            mClientThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureServerThread == null) {
            mSecureServerThread = new ServerThread(true);
            mSecureServerThread.start();
        }

        updateUI();
    }

    /**
     * 客户端连接服务端
     *
     * @param device 要连接的蓝牙设备
     */
    public synchronized void connectServer(BluetoothDevice device) {
        Log.d(TAG, "connectServer to: " + device);

        if (getState() != STATE_NONE) {
            updateUI();
            return;
        }

        if (mClientThread != null) {
            mClientThread.cancel();
            mClientThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mClientThread = new ClientThread(device, true);
        mClientThread.start();

        updateUI();
    }


    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        if (mClientThread != null) {
            mClientThread.cancel();
            mClientThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }



        if (mSecureServerThread != null) {
            mSecureServerThread.cancel();
            mSecureServerThread = null;
        }

        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        mHandler.obtainMessage(MESSAGE_TOAST, "已成功连接" + device.getName())
                .sendToTarget();

        updateUI();
    }


    private void connectionFailed() {

        mHandler.obtainMessage(MESSAGE_TOAST, "连接发送端设备失败,请重新发送").sendToTarget();

        mState = STATE_NONE;

        updateUI();



    }

    private void connectionLost() {

        mHandler.obtainMessage(MESSAGE_TOAST, "设备连接断开").sendToTarget();

        mState = STATE_NONE;

        updateUI();


    }


    public void sendMission(Mission2 mission) {
        ConnectedThread r;
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        r.sendMission(mission);
    }


    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mClientThread != null) {
            mClientThread.cancel();
            mClientThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureServerThread != null) {
            mSecureServerThread.cancel();
            mSecureServerThread = null;
        }

        mState = STATE_NONE;

        updateUI();
    }


    private class ServerThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public ServerThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            mState = STATE_LISTENING;


            try {

                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                        MY_UUID_SECURE);
               /* } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }*/
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
            mState = STATE_LISTENING;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("ServerThread" + mSocketType);

            BluetoothSocket socket = null;

            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (MissionBluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTENING:
                            case STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice(),
                                        mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }



    private class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ClientThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";


            try {

                tmp = device.createRfcommSocketToServiceRecord(
                        MY_UUID_SECURE);

                /*} else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }*/
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }

            mmSocket = tmp;
            mState = STATE_CONNECTING;

        }

        public void run() {
            Log.i(TAG, "BEGIN mClientThread SocketType:" + mSocketType);
            setName("ClientThread" + mSocketType);

            try {

                mmSocket.connect();
            } catch (IOException e) {

                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            synchronized (MissionBluetoothService.this) {
                mClientThread = null;
            }

            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connectServer " + mSocketType + " socket failed", e);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final DataInputStream mmInStream;
        private final DataOutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            DataInputStream tmpIn = null;
            DataOutputStream tmpOut = null;


            try {
                tmpIn = new DataInputStream(socket.getInputStream());
                tmpOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

            while (mState == STATE_CONNECTED) {
                try {

                    String missionStr = mmInStream.readUTF();
                    Gson gson = new Gson();
                    ExportMission exportMission = gson.fromJson(missionStr, ExportMission.class);
                    Mission2 mission = exportMission.getMission();

                    if (!TextUtils.isEmpty(mission.getSnapshot())) {
                        File file = new File(AppConstant.ROOT_PATH + File.separator
                                + AppConstant.DIR_MISSION_PHOTO + "/" + mission.getSnapshot());

                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        FileOutputStream fos = new FileOutputStream(file);

                        long fileLength = mmInStream.readLong();

                        byte[] buffer = new byte[1024];
                        int sumLen = 0;
                        int len;
                        int progress = 0;
                        try {
                            while (sumLen < fileLength) {

                                len = mmInStream.read(buffer);
                                fos.write(buffer, 0, len);

                                sumLen += len;
                                progress = (int) (((float) sumLen / fileLength) * 100);
                                mHandler.obtainMessage(MESSAGE_PROGRESS, RECEIVE_PROGRESS, progress)
                                        .sendToTarget();
                                Log.d(TAG, "receive_progress->" + progress);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fos.flush();
                        fos.close();
                    }

                    mHandler.obtainMessage(MESSAGE_RECEIVE, RECEIVE_SUCCESS, -1, exportMission).sendToTarget();

                } catch (Exception e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }


        public void sendMission(Mission2 mission) {
            try {

                Gson gson = new Gson();
                ExportMission exportMission = new ExportMission();
                exportMission.setMission(mission);
                String missionStr = gson.toJson(exportMission);

                mmOutStream.writeUTF(missionStr);

                if (!TextUtils.isEmpty(mission.getSnapshot())) {
                    String imageUrl = AppConstant.ROOT_PATH + File.separator
                            + AppConstant.DIR_MISSION_PHOTO + "/" + mission.getSnapshot();
                    File file = new File(imageUrl);
                    if (file.exists()) {

                        FileInputStream fis = new FileInputStream(file);
                        long fileLength = file.length();

                        mmOutStream.writeLong(fileLength);

                        byte[] buffer = new byte[1024];
                        int len;
                        int sumLen = 0;
                        int progress;
                        while (((len = fis.read(buffer)) > 0)) {
                            mmOutStream.write(buffer, 0, len);
                            sumLen += len;
                            progress = (int) (((float) sumLen / fileLength) * 100);
                            mHandler.obtainMessage(MESSAGE_PROGRESS, SEND_PROGRESS, progress)
                                    .sendToTarget();
                            Log.d(TAG, "send_progress->" + progress);
                        }
                        fis.close();
                    }
                }

                mHandler.obtainMessage(MESSAGE_SEND, SEND_SUCCESS, -1, mission.getName()).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
