package com.ew.autofly.dialog;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ew.autofly.R;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.entity.KmlInfo;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.ew.autofly.constant.AppConstant.DIR_KML;



public class CloudKmlDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private View view;
    private ListView mListview;
    private FileAdapter fileAdapter;
    private String kmlPath = IOUtils.getRootStoragePath(getContext()) + DIR_KML;
    private BroadcastReceiver broadcastReceiver;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private long enqueueId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.dialog_cloud_kml, container, false);
        initView();
        return view;
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f,0.85f);
    }

    private void initView() {
        mListview = (ListView) view.findViewById(R.id.lv_excel_kml);
        view.findViewById(R.id.ib_close).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm).setOnClickListener(this);
    }

    private void initData() {
        downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
    }

    private void initListView() {
        fileAdapter = new FileAdapter();
        mListview.setAdapter(fileAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fileAdapter.resetView();
                adapterFiles.get(position).setSelect(true);
                fileAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_close:
                try {
                    ((com.ew.autofly.dialog.common.ImportExcelKmlDlgFragment)getFragmentManager().findFragmentByTag("kml_excel")).reLoadKmlTable();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ((com.ew.autofly.dialog.common.ImportExcelKmlDlgFragment)getFragmentManager().findFragmentByTag("kml_excel")).reLoadKmlTable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
                break;
            case R.id.tv_confirm:
                int selectPosition = -1;
                for (int i = 0; i < adapterFiles.size(); i++) {
                    if (adapterFiles.get(i).isSelect()) {
                        selectPosition = i;
                        break;
                    }
                }

                if (selectPosition == -1)
                    return;

                if (enqueueId != -1) {
                    ToastUtil.show(getActivity(), "请等待当前任务下载完成");
                    return;
                }

                final String filePath = kmlPath + File.separator + cloudKmls.get(selectPosition).getName() + ".kml";
                final int selectID = selectPosition;

                if (new File(filePath).exists()) {
                    CustomDialog.Builder confirmDialog = new CustomDialog.Builder(getActivity());
                    confirmDialog.setTitle("警告！")
                            .setMessage("文件已存在，是否覆盖下载？")
                            .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new File(filePath).delete();
                                    startDownloadKml(selectID, filePath);
                                }
                            })
                            .setNegativeButton(getActivity().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                    return;
                }
                startDownloadKml(selectID, filePath);
                break;
        }
    }

    private void startDownloadKml(int selectPosition, String filePath) {
        request = new DownloadManager.Request(Uri.parse(cloudKmls.get(selectPosition).getPath()));
        request.setTitle(cloudKmls.get(selectPosition).getName());
        request.setDestinationUri(Uri.fromFile(new File(filePath)));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        enqueueId = downloadManager.enqueue(request);
        registerDownloadFinishReceiver(enqueueId);
        ToastUtil.show(getActivity(), "开始下载，请稍后...");
    }

    private void registerDownloadFinishReceiver(final long Id) {
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    ToastUtil.show(getActivity(), "下载完成");
                    enqueueId = -1;
                }
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private List<KmlInfo.RowsBean> cloudKmls = new ArrayList<>();
    private List<KmlDetailInfo> adapterFiles = new ArrayList<>();

    class FileAdapter extends BaseAdapter {
        @Override
        public int getCount() {
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

        public void resetView() {
            for (KmlDetailInfo adapterFile : adapterFiles) {
                adapterFile.setSelect(false);
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_insert_excel_kml, parent, false);
            }
            final KmlDetailInfo file = adapterFiles.get(position);
            final TextView tvName = (TextView) convertView.findViewById(R.id.txt_layer_name);
            final ImageView imgSelect = (ImageView) convertView.findViewById(R.id.img_select_state);
            final RelativeLayout bgRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.insert_excel_kml_linear);
            tvName.setText(file.getName());
            if (file.isSelect) {
                imgSelect.setImageResource(R.drawable.ic_checkbox_checked);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#1A1D22"));
            } else {
                imgSelect.setImageResource(R.drawable.ic_checkbox_uncheck);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#252932"));
            }
            return convertView;
        }
    }

    class KmlDetailInfo {
        private String name;
        private boolean isSelect;

        public KmlDetailInfo(String name, boolean isSelect) {
            this.name = name;
            this.isSelect = isSelect;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }
    }
}