package com.ew.autofly.module.ars100;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ew.autofly.module.ars100.request.request.sevice.BeatRequest;
import com.ew.autofly.module.ars100.request.request.sevice.ConnectServiceRequest;
import com.ew.autofly.module.ars100.response.ResponseImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;



public class ARS100Service {

    public final static int SOCKET_ERROR = 0;

    private Context mContext;

    /*socket*/
    private Socket socket;
    /*连接线程*/
    private Thread connectThread;
    private Timer timer = new Timer();
    private OutputStream outputStream;
    private InputStream inputStream;

    private String ip;
    private int port;
    private TimerTask task;

    /*默认重连*/
    private boolean isReConnect = true;

    private boolean runFlag = false;

    private Handler handler = new Handler(Looper.getMainLooper());

    private ResponseListener mResponseListener;

    public void setResponseListener(ResponseListener responseListener) {
        mResponseListener = responseListener;
    }

    private SocketStatusListener mSocketStatusListener;

    public void setSocketStatusListener(SocketStatusListener socketStatusListener) {
        mSocketStatusListener = socketStatusListener;
    }

    public ARS100Service(Context context, String ip, int port) {
        this.mContext = context;
        this.ip = ip;
        this.port = port;

    }

    public void connect() {
        /*启动连接线程*/
        initSocket();
        connectThread.start();
    }

    public boolean isConnect(){
        return runFlag;
    }

    /*初始化socket*/
    private void initSocket() {
        if (socket == null) {
            socket = new Socket();
        }
        if (connectThread == null) {

            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        /*超时时间为2秒*/
                        socket.connect(new InetSocketAddress(ip, port), 2000);
                        /*连接成功的话  发送心跳包*/
                        if (socket.isConnected()) {
                            connected();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            toastMsg("连接超时,请检查无线局域网连接");


                        } else if (e instanceof NoRouteToHostException) {
                            toastMsg("该地址不存在,请检查无线局域网连接");

                        } else if (e instanceof ConnectException) {
                            toastMsg("连接异常或被拒绝,请检查无线局域网连接");

                        }
                        disConnect();
                        onSocketError();
                    }

                }
            });

        }


    }

    private void connected() {

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();


            runFlag = true;

            sendOrder(new ConnectServiceRequest().getData());

            while (runFlag) {
                try {
                    if (inputStream != null) {
                        byte[] inputByte = new byte[256];
                        if (inputStream.read(inputByte) != -1) {
                            final ResponseImpl response = new ResponseImpl(inputByte);


                            if (mResponseListener != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mResponseListener.onResponse(response);
                                    }
                                });
                            }
                        }
                    }
                } catch (IOException e) {


                    e.printStackTrace();
                    disConnect();
                    onSocketError();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            disConnect();
            onSocketError();
        }

    }

    /*发送数据*/
    public void sendOrder(final byte[] bytes) {
        if (socket != null && socket.isConnected()) {
            /*发送指令*/
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (outputStream != null) {
                            ARSConstant.PACKAGE_COUNT++;
                            outputStream.write(bytes);
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        disConnect();
                        onSocketError();
                    }

                }
            }).start();

        } else {
            toastMsg("socket连接错误,请重试");
        }
    }

    /*定时发送心跳数据*/
    public void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {

                        ARSConstant.PACKAGE_COUNT++;
                        outputStream = socket.getOutputStream();
                        outputStream.write(new BeatRequest().getData());
                        outputStream.flush();
                    } catch (Exception e) {


                        /*重连*/

                        e.printStackTrace();
                        disConnect();
                        onSocketError();


                    }
                }
            };
        }

        timer.schedule(task, 1000, 3000);
    }

    /*释放资源*/
    public void disConnect() {

        runFlag = false;

        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (outputStream != null) {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (IOException e) {
            }
            socket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }
    }

    private void reConnect() {
        disConnect();
        if (isReConnect) {
            initSocket();
            connect();
        }
    }

    private void onSocketError() {
        if (mSocketStatusListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mSocketStatusListener.onStatus(SOCKET_ERROR);
                }
            });
        }
    }

    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
    private void toastMsg(final String msg) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ResponseListener {

        void onResponse(ResponseImpl response);
    }

    public interface SocketStatusListener {

        void onStatus(int status);
    }
}
