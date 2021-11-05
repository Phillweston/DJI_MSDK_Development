package com.ew.autofly.fragments.dem;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;



public class DemDownloadingFragment extends Fragment implements View.OnClickListener, OnDemFragmentClickListener{

    private static final int GET_DEM_DATA_RESULT = 1001;
    public static final String DATA_CHANGED = "com.ew.autofly.fragment.dem.PAGE_DOWNLOAD_CHANGED";
    public static final int ADAPTER_CHANGER = 1002;
    private DownloadManager downloadManager;
    public DemListsAdapter listsAdapter;
    private List<File> fileList;
    private List<File> filesOnDisk;
    private PageDataListsChangedReceiver mReceiver = null;
    private Handler handler;
    private BroadcastReceiver broadcastReceiver;
    private Context mActivity;
    private SharedConfig config;
    private int mSelect;
    private Button btnDel;
    private ListView mListView;
    public Thread thread;
    public boolean isStart = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = context;
    }

    public static DemDownloadingFragment newInstance() {
        DemDownloadingFragment fragment = new DemDownloadingFragment();
        return fragment;
    }

    @Override
    public void loadDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }

    @Override
    public void onStartDownload() {
        if(!thread.isAlive() && !isStart) {
            isStart = true;
            thread.start();
        }
    }

    private void registerDataListsFragmentChangedReceiver() {
        IntentFilter filter = new IntentFilter();
        if (mReceiver != null && isAdded())
            getActivity().unregisterReceiver(mReceiver);


        mReceiver = new PageDataListsChangedReceiver();
        filter.addAction(DATA_CHANGED);
        if (isAdded())
            getActivity().registerReceiver(mReceiver, filter);
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
                    if(newDemList.equals("") && thread.isAlive()){
                        thread.interrupt();
                        isStart = false;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        config = new SharedConfig(mActivity);
        View view = inflater.inflate(R.layout.fragment_dem_downloading, container, false);
        initThread();
        initView(view);
        initHandler();

      
        if (mListView.getCount() != 0 && mReceiver == null) {
            String[] demList = config.getDemList().split(",");
            for (int i = 0; i < demList.length; i++) {
                View v = (View) listsAdapter.getItem(i);
                TextView tvName = (TextView) v.findViewById(R.id.txt_dem_name);
                if(!demList[i].equals("")) {
                    String status = queryStatus(Long.valueOf(demList[i]));
                    if (status.contains("SUCCESSFUL") && isAdded()) {
                        String filePath = IOUtils.getRootStoragePath(getActivity()) + AppConstant.DIR_MAP + File.separator + tvName.getText().toString();
                        File file = new File(filePath);
                        file.renameTo(new File(filePath.replace(".TEMP", "")));
                        handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
                    }
                }
            }
        }

        fileList = getDemFiles();
        if(fileList.size() == 0)
            config.setDemDownloadList("");
        else{
            thread.start();
            isStart = true;
        }


        registerDataListsFragmentChangedReceiver();
        return view;
    }

    private void initThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart && isAdded()) {
                    try {
                        Thread.sleep(2000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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
        mListView = (ListView) view.findViewById(R.id.lv_dem_lists);


        filesOnDisk = new ArrayList<>();
        fileList = getDemFiles();

        listsAdapter = new DemListsAdapter(fileList);
        mListView.setAdapter(listsAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == listsAdapter.getSelect()){
                    listsAdapter.setSelect(-1);
                    listsAdapter.notifyDataSetInvalidated();
                    btnDel.setVisibility(View.GONE);
                }else{
                    mSelect = position;
                    listsAdapter.setSelect(position);
                    listsAdapter.notifyDataSetInvalidated();
                    btnDel.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private List<File> getDemFiles(){
        filesOnDisk.clear();
        String dirPath = IOUtils.getRootStoragePath(getActivity())+ File.separator+ AppConstant.DIR_MAP;
        File file=new File(dirPath);
        File[] files=file.listFiles();
        if(files.length>0){
            for(File subfile:files){
                if(subfile.getName().endsWith(".TEMP")){
                    filesOnDisk.add(subfile);
                }
            }
        }
        return filesOnDisk;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                String demConfig = config.getDemList();
                String[] demLists = demConfig.split(",");
                String newDemLists = "";
                if(!demLists[mSelect].equals(""))
                    downloadManager.remove(Long.parseLong(demLists[mSelect]));
                for (int i = 0; i < demLists.length; i++){
                    if(demLists[i] != demLists[mSelect]){
                        newDemLists += demLists[i] + ",";
                    }
                }
                config.setDemDownloadList(newDemLists);
                listsAdapter.setSelect(-1);
                btnDel.setVisibility(View.GONE);
                handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
                break;
            case R.id.btn_import:
                Intent intent = new Intent(getActivity(), GetDemDataActivity.class);
                startActivityForResult(intent, GET_DEM_DATA_RESULT);
                thread.interrupt();
                isStart = false;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == GET_DEM_DATA_RESULT){
            handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
            String url = data.getStringExtra("url");
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

    private void startDownLoading(String url) {
        String filePath = IOUtils.getRootStoragePath(getActivity()) + AppConstant.DIR_MAP + File.separator+url+".TEMP";
        String fileName = url;
        url = AppConstant.DOWNLOADTRA_BASEURL+url;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("高程数据");
        request.setDescription(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationUri(Uri.fromFile(new File(filePath)));
        long id = (int) downloadManager.enqueue(request);

      
        String demConfig = config.getDemList();
        config.setDemDownloadList(demConfig + id + ",");

        registerDownloadFinishReceiver(id, filePath);
        handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));

        initThread();
        isStart = true;
        thread.start();

    }

    private void notifyAdapterChange(final DemListsAdapter listsAdapter) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isAdded()) {
                    fileList = getDemFiles();
                    listsAdapter.isChangeAdapter();
                    listsAdapter.notifyDataSetChanged();
                    listsAdapter.notifyDataSetInvalidated();
                }
            }
        }, 1000);
    }

    class DemListsAdapter extends BaseAdapter{

        private List<File> adapterFiles = new ArrayList<>();
        private int select = -1;

        public DemListsAdapter(List<File> adapterFiles){
            this.adapterFiles =  adapterFiles;
        }

        @Override
        public int getCount() {
            return adapterFiles.size();
        }

        @Override
        public Object getItem(int position) {
            return this.getView(position, null, mListView);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void setSelect(int selectItem){
            this.select = selectItem;
        }

        public int getSelect(){
            return select;
        }

        public void isChangeAdapter(){

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
            TextView tvProgress = (TextView) convertView.findViewById(R.id.txt_download_progress);
            String[] demLists = config.getDemList().split(",");
            for (int i = 0; i < demLists.length; i++) {
                if (!demLists[position].equals("")) {
                    if (queryProgress(demLists[position]) != null) {
                        tvProgress.setText(queryProgress(demLists[position]) + "%");
                    }
                }
            }
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

    private String queryStatus(Long Id) {
        if(downloadManager != null) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(Id);
            Cursor cursor = downloadManager.query(query);

            String statusMsg = "";
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        statusMsg = "STATUS_PAUSED";
                    case DownloadManager.STATUS_PENDING:
                        statusMsg = "STATUS_PENDING";
                    case DownloadManager.STATUS_RUNNING:
                        statusMsg = "STATUS_RUNNING";
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        statusMsg = "STATUS_SUCCESSFUL";
                        break;
                    case DownloadManager.STATUS_FAILED:
                        statusMsg = "STATUS_FAILED";
                        break;

                    default:
                        statusMsg = "未知状态";
                        break;
                }
                return statusMsg;
            }
        }
        return "";
    }

    private String queryProgress(String id) {
        DownloadManager.Query downloadQuery = new DownloadManager.Query();
        downloadQuery.setFilterById(Long.parseLong(id));
        if (downloadManager != null) {
            Cursor cursor = downloadManager.query(downloadQuery);
            if (cursor != null && cursor.moveToFirst()) {
                int totalSizeBytesIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int bytesDownloadSoFarIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
              
                int totalSizeBytes = cursor.getInt(totalSizeBytesIndex);
              
                int bytesDownloadSoFar = cursor.getInt(bytesDownloadSoFarIndex);
                cursor.close();

                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String result = numberFormat.format((float) bytesDownloadSoFar / (float) totalSizeBytes * 100);
                return result;
            }
        }
        return null;
    }

    class PageDataListsChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DATA_CHANGED)) {
                handler.sendMessage(handler.obtainMessage(ADAPTER_CHANGER));
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null && isAdded()) {
            getActivity().unregisterReceiver(mReceiver);
        }
        if (thread.isAlive()){
            isStart = false;
            thread.yield();
            thread.interrupt();
        }
        super.onDestroy();
    }
}
