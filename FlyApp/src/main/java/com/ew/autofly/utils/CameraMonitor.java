package com.ew.autofly.utils;

import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class CameraMonitor {

    /**服务器地址*/
    private String pUsername="WQZ";
    /**服务器地址*/
    private String serverUrl="192.168.191.1";


    /**服务器端口*/
    private int serverPort=6969;
    /**视频刷新间隔*/
    private int VideoPreRate=1;
    /**当前视频序号*/
    private int tempPreRate=0;
    /**视频质量*/
    private int VideoQuality=85;

    /**发送视频宽度比例*/
    private float VideoWidthRatio=1;
    /**发送视频高度比例*/
    private float VideoHeightRatio=1;

    /**发送视频宽度*/
    private int VideoWidth=4000;
    /**发送视频高度*/
    private int VideoHeight=3000;
    /**视频格式索引*/
    private int VideoFormatIndex=0;
    /**是否发送视频*/
    private boolean startSendVideo=false;
    /**是否连接主机*/
    private boolean connectedServer=false;

    public void connectToServer(){

        if(connectedServer)
        {

            startSendVideo=false;
            connectedServer=false;
            startSendVideo=false;

            Thread th = new MySendCommondThread("PHONEDISCONNECT|"+pUsername+"|");
            th.start();
        }
        else//连接主机
        {

            Thread th = new MySendCommondThread("PHONECONNECT|"+pUsername+"|");
            th.start();
            connectedServer=true;
            startSendVideo=true;
        }
    }

    public void sendCamera(byte[] data){

        if(!startSendVideo)
            return;
        if(tempPreRate<VideoPreRate)
        {
            tempPreRate++;
            return;
        }
        tempPreRate=0;
        try
        {
            if(data!=null)
            {
                YuvImage image = new YuvImage(data,VideoFormatIndex, VideoWidth, VideoHeight,null);
                if(image!=null)
                {
                    ByteArrayOutputStream outstream = new ByteArrayOutputStream();

                    image.compressToJpeg(new Rect(0, 0, (int)(VideoWidthRatio*VideoWidth),
                            (int)(VideoHeightRatio*VideoHeight)), VideoQuality, outstream);
                    outstream.flush();

                    Thread th = new MySendFileThread(outstream,pUsername,serverUrl,serverPort);
                    th.start();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**发送命令线程*/
    class MySendCommondThread extends Thread
    {
        private String commond;
        public MySendCommondThread(String commond)
        {
            this.commond=commond;
        }
        public void run()
        {

            try
            {
                Socket socket=new Socket(serverUrl,serverPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(commond);
                out.flush();
            }
            catch (UnknownHostException e)
            {
            }
            catch (IOException e)
            {
            }
        }
    }

    /**发送文件线程*/
    class MySendFileThread extends Thread
    {
        private String username;
        private String ipname;
        private int port;
        private byte byteBuffer[] = new byte[1024];
        private OutputStream outsocket;
        private ByteArrayOutputStream myoutputstream;

        public MySendFileThread(ByteArrayOutputStream myoutputstream,String username,String ipname,int port)
        {
            this.myoutputstream = myoutputstream;
            this.username=username;
            this.ipname = ipname;
            this.port=port;
            try
            {
                myoutputstream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try
            {

                Socket tempSocket = new Socket(ipname, port);
                outsocket = tempSocket.getOutputStream();

                String msg=java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|","utf-8");
                byte[] buffer= msg.getBytes();
                outsocket.write(buffer);

                ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
                int amount;
                while ((amount = inputstream.read(byteBuffer)) != -1)
                {
                    outsocket.write(byteBuffer, 0, amount);
                }
                myoutputstream.flush();
                myoutputstream.close();
                tempSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
