package com.ew.autofly.fragments.dem;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ew.autofly.R;
import com.ew.autofly.config.SharedConfig;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.interfaces.OnDemFragmentClickListener;
import com.ew.autofly.utils.IOUtils;
import com.flycloud.autofly.base.util.NetworkUtil;
import com.ew.autofly.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ew.autofly.utils.IOUtils.getRootStoragePath;



public class DemDataListsFragment extends Fragment implements View.OnClickListener, OnDemFragmentClickListener{

    private static final int GET_DEM_DATA_RESULT = 1001;
    public static final String DATA_CHANGED = "com.ew.autofly.fragment.dem.PAGE_DATALISTS_CHANGED";
    public static final int ADAPTER_CHANGER = 1002;
    private DownloadManager downloadManager;
    private List<File> fileOnDisk;
    public DemListsAdapter listsAdapter;
    private BroadcastReceiver broadcastReceiver;
    private PageDownLoadChangedReceiver mReceiver = null;
    private Handler handler;
    private Context mActivity;
    private SharedConfig config;
    private String mSelectFileName = "";
    private Button btnDel;
    private DemDownloadingFragment mDemDownloadingFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
    }

    public static DemDataListsFragment newInstance(){
        DemDataListsFragment fragment = new DemDataListsFragment();
        return fragment;
    }

    private void registerDownloadFinishReceiver(final long Id, final String filePath) {


        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    File file = new File(filePath);
                    file.renameTo(new File(filePath.replace(".TEMP", "")));

                    String demConfig = config.getDemList();
                    String[] demLists = demConfig.split(",");
                    String newDemList = "";
                    for(int i = 0; i<demLists.length; i++){
                        if(Integer.parseInt(demLists[i]) != ID){
                            newDemList += demLists[i] + ",";
                        }
                    }
                    if(mDemDownloadingFragment.thread != null && newDemList.equals("") && mDemDownloadingFragment.thread.isAlive() && mDemDownloadingFragment.isStart){
                        mDemDownloadingFragment.thread.interrupt();
                        mDemDownloadingFragment.isStart = false;
                    }

                    config.setDemDownloadList(newDemList);

                    handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));

                    Intent intentBroadCast = new Intent();
                    intentBroadCast.setAction(DemDataListsFragment.DATA_CHANGED);
                    mActivity.sendBroadcast(intentBroadCast);
                }
            }
        };

        mActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    private void registerDownloadingFragmentChangedReceiver() {


        IntentFilter filter = new IntentFilter();
        if (mReceiver != null && isAdded())
            getActivity().unregisterReceiver(mReceiver);

        mReceiver = new PageDownLoadChangedReceiver();
        filter.addAction(DATA_CHANGED);
        if (isAdded())
            getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public void loadDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public void onStartDownload() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        config = new SharedConfig(mActivity);
        View view = inflater.inflate(R.layout.fragment_dem_list, container, false);
        initView(view);
        initHandler();
        mDemDownloadingFragment = DemDownloadingFragment.newInstance();

        registerDownloadingFragmentChangedReceiver();
        return view;
    }

    private void initHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case ADAPTER_CHANGER:
                        notifyAdapterChange(listsAdapter);
                        break;

                    default:
                        break;
                }
            }
        };
    }

    private void initView(View view) {
        btnDel = (Button) view.findViewById(R.id.btn_delete);
        btnDel.setOnClickListener(this);
        btnDel.setVisibility(View.GONE);
        view.findViewById(R.id.btn_import).setOnClickListener(this);
        ListView listView = (ListView) view.findViewById(R.id.lv_dem_lists);

        fileOnDisk = new ArrayList<>();
        fileOnDisk = getDemFiles();

        listsAdapter = new DemListsAdapter(fileOnDisk);
        listView.setAdapter(listsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.txt_dem_name);
                if(position == listsAdapter.getSelectItem()){
                    listsAdapter.setSelectItem(-1);
                    listsAdapter.notifyDataSetInvalidated();
                    btnDel.setVisibility(View.GONE);
                }else{
                    mSelectFileName = tv.getText().toString();
                    listsAdapter.setSelectItem(position);
                    listsAdapter.notifyDataSetInvalidated();
                    btnDel.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private List<File> getDemFiles(){
        fileOnDisk.clear();
        String dirPath = getRootStoragePath(getActivity())+ File.separator+ AppConstant.DIR_MAP;
        File file=new File(dirPath);
        File[] files=file.listFiles();
        if(files.length>0){
            for(File subfile:files){
                if(subfile.getName().endsWith(".TRA")){
                    fileOnDisk.add(subfile);
                }
            }
        }
        return fileOnDisk;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                if(!mSelectFileName.equals("")){
                    String filePath = IOUtils.getRootStoragePath(getActivity()) + AppConstant.DIR_MAP + File.separator+mSelectFileName;
                    File file = new File(filePath);
                    file.delete();
                    listsAdapter.setSelectItem(-1);
                    btnDel.setVisibility(View.GONE);
                    listsAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_import:
                Intent intent = new Intent(getActivity(), GetDemDataActivity.class);
                startActivityForResult(intent, GET_DEM_DATA_RESULT);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GET_DEM_DATA_RESULT && data != null){
            final String url = data.getStringExtra("url");
            if(url==null){return;}
            int netWorkType = NetworkUtil.checkNetWorkType(getActivity());
            if(netWorkType == NetworkUtil.NONETWORK){
                ToastUtil.show(getActivity(), "无法连接网络");
            }else if(netWorkType == NetworkUtil.NOWIFI){
                showComfirmStartDownloadDialog(url);
            }else if(netWorkType == NetworkUtil.WIFI){
                startDownLoading(url);
            }
        }
    }

    private void showComfirmStartDownloadDialog(final String url) {
        CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
        deleteDialog.setTitle("高程数据下载")
                .setMessage("当前处于移动网络\n确认下载？")
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                startDownLoading(url);
                            }
                        });
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void startDownLoading(String url){
        String filePath = getRootStoragePath(getActivity()) + AppConstant.DIR_MAP + File.separator+url+".TEMP";
        String fileName = url;
        url = AppConstant.DOWNLOADTRA_BASEURL+url;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(getResources().getString(R.string.channel_app_name));
        request.setDescription("高程数据"+fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationUri(Uri.fromFile(new File(filePath)));
        long id = (int) downloadManager.enqueue(request);


        registerDownloadFinishReceiver(id, filePath);


        Intent intent = new Intent();
        intent.setAction(DemDownloadingFragment.DATA_CHANGED);
        getActivity().sendBroadcast(intent);


        String demList = config.getDemList();
        demList += id + ",";
        config.setDemDownloadList(demList);


        OnDemFragmentClickListener parentFragment = (OnDemFragmentClickListener) getParentFragment();
        parentFragment.onStartDownload();
    }

    private void notifyAdapterChange(final DemListsAdapter listsAdapter) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fileOnDisk = getDemFiles();
                listsAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    private class DemListsAdapter extends BaseAdapter{

        private List<File> adapterFiles = new ArrayList<>();
        private int select = -1;

        public DemListsAdapter(List<File> adapterFiles){
            this.adapterFiles = adapterFiles;
        }

        @Override
        public int getCount() {
            this.adapterFiles = getDemFiles();
            return adapterFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterFiles.size() == 0 ? null : adapterFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        void setSelectItem(int selectItem){
            this.select = selectItem;
        }

        int getSelectItem(){
            return select;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_dem_list, parent, false);
            }
            final File file = adapterFiles.get(position);
            ((TextView) convertView.findViewById(R.id.txt_dem_name)).setText(file.getName());
            final ImageView imgSelect = (ImageView) convertView.findViewById(R.id.img_select_state);
            final RelativeLayout bgRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.insert_dem_linear);

            if (select != position) {
                imgSelect.setImageResource(R.drawable.ic_checkbox_uncheck);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#252932"));
            } else {
                imgSelect.setImageResource(R.drawable.ic_checkbox_checked);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#1A1D22"));
            }
            return convertView;
        }
    }

    class PageDownLoadChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DATA_CHANGED)) {
                handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
