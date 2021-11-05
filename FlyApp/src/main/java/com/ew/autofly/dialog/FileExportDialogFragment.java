package com.ew.autofly.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.flycloud.autofly.ux.base.BaseUxDialog;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;



public class FileExportDialogFragment extends BaseUxDialog implements View.OnClickListener, IWXAPIEventHandler {
    private View view;
    private ImageView ivWeChat, ivFTP;


    private IWXAPI WXApi;
    private ArrayList<String> files;
    private String exportPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWXApi();
        files = (ArrayList<String>) getArguments().getSerializable("filenames");
        exportPath = getArguments().getString("export_Path");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_file_export, container, false);
        ivWeChat = (ImageView) view.findViewById(R.id.iv_wechat);
        ivFTP = (ImageView) view.findViewById(R.id.iv_ftp);
        ivWeChat.setOnClickListener(this);
        ivFTP.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wechat:
                sendFileToWX();
                break;
            case R.id.iv_ftp:
                FTPDialogFragment ftp = new FTPDialogFragment();
                ftp.show(getFragmentManager(), "ftp");
                dismiss();
                break;
        }
    }

    @Override
    protected void onCreateSize() {
        setSize(0.4f, 0.4f);
    }

    @Override
    protected void setWindowParams() {
        super.setWindowParams();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
        }
    }

    private void initWXApi() {

        WXApi = WXAPIFactory.createWXAPI(getContext(), AppConstant.WX_APP_ID, false);
        WXApi.registerApp(AppConstant.WX_APP_ID);

        try {
            WXApi.handleIntent(getActivity().getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendFileToWX() {

        if (files != null && !files.isEmpty() && exportPath != null) {

            if (!WXApi.isWXAppInstalled()) {
                showToast(getContext(), "请先安装微信");
                return;
            }

            if (files.size() > 1) {
                showToast(getContext(), "微信一次只能分享一个文件,请重新选择");
                return;
            }

            WXFileObject fileObj = new WXFileObject();
            fileObj.filePath = exportPath + files.get(0);


            WXMediaMessage msg = new WXMediaMessage(fileObj);
            msg.title = files.get(0);


            SendMessageToWX.Req req = new SendMessageToWX.Req();

            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;

            WXApi.sendReq(req);

        } else {
            showToast(getContext(), "请选择要发送的文件");
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

    }
}