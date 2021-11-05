package com.ew.autofly.dialog.common;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.FileExportDialogFragment;
import com.ew.autofly.dialog.ui.adapter.FileListCheckAdapter;
import com.ew.autofly.dialog.ui.fragment.FileListFragment;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.ToastUtil;
import com.ew.autofly.utils.io.file.FileInfo;
import com.flycloud.autofly.ux.adapter.BaseViewPagerFragmentAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_TOWER_EXCEL;
import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_TOWER_KML;



public class ImportTowerLineDialog extends BaseSortFileDialog implements View.OnClickListener {

    public final static String PARAMS_SELECTED_FILES = "PARAMS_SELECTED_FILES";
    public final static String PARAMS_SELECTED_TYPE = "PARAMS_SELECTED_TYPE";
    public final static String PARAMS_SELECTED_SINGLE = "PARAMS_SELECTED_SINGLE";

    public final String IMPORT_KML_PATH = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_TOWER_KML;
    public final String IMPORT_EXCEL_PATH = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_TOWER_EXCEL;

    public final String OUTPUT_KML_PATH = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_TOWER_KML + File.separator;
    public final String OUTPUT_EXCEL_PATH = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_TOWER_EXCEL + File.separator;

    public final static String TAG_IMPORT = "import";
    public final static String TAG_NEW = "new";
    public final static String TAG_DELETE = "delete";
    public final static String TAG_APPEND = "append";

    public final static int TYPE_KML = 0;
    public final static int TYPE_EXCEL = 1;

    private List<FileInfo> kmlFileInfoList = new ArrayList<>();
    private List<FileInfo> excelFileInfoList = new ArrayList<>();

    private FileListCheckAdapter kmlFileAdapter;
    private FileListCheckAdapter excelFileAdapter;


    private FileListFragment kmlFileListFragment;
    private FileListFragment excelFileListFragment;

    private View view;
    private TabLayout mTLSwitchContent;
    private ViewPager mVpFileList;

    private BaseViewPagerFragmentAdapter mViewPagerFragmentAdapter;

    private CharSequence[] mListTitle = {"杆塔KML", "杆塔EXCEL"};
    
    private Button mBtnImport;

    private Button mBtnNew;
    private Button mBtnDelete;
    private Button mBtnAppend;
    private Button mBtnExport;

    private int mType;

    private boolean isSingleCheck = false;

    private ArrayList<FileInfo> selectedFileInfoList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_import_tower_line, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadFiles();
    }

    @Override
    protected void onCreateSize() {
        setSize(0.7f, 0.9f);
    }


    @Override
    protected void initTitleBar(View rootView) {
        super.initTitleBar(rootView);
        setTitle("选择文件");
        setTitleIcon(R.drawable.icon_load_kml);
    }

    private void initData() {

        mType = getArguments().getInt(PARAMS_SELECTED_TYPE);
        isSingleCheck = getArguments().getBoolean(PARAMS_SELECTED_SINGLE);
    }

    protected void initView(View view) {

        mBtnImport = (Button) view.findViewById(R.id.btn_import);
        mBtnImport.setOnClickListener(this);

        mTLSwitchContent = (TabLayout) view.findViewById(R.id.tl_switch_content);
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(mListTitle[0]));
        mTLSwitchContent.addTab(mTLSwitchContent.newTab().setText(mListTitle[0]));
        mVpFileList = (ViewPager) view.findViewById(R.id.vp_file_list);

        List<Fragment> mListData = new ArrayList<>();

        kmlFileAdapter = new FileListCheckAdapter(getContext(), kmlFileInfoList);
        kmlFileAdapter.enableSingleCheck(isSingleCheck);
        excelFileAdapter = new FileListCheckAdapter(getContext(), excelFileInfoList);
        excelFileAdapter.enableSingleCheck(isSingleCheck);

        kmlFileListFragment = FileListFragment.newInstance(kmlFileAdapter);
        excelFileListFragment = FileListFragment.newInstance(excelFileAdapter);

        mListData.add(kmlFileListFragment);
        mListData.add(excelFileListFragment);

        mViewPagerFragmentAdapter = new BaseViewPagerFragmentAdapter(getChildFragmentManager(), mListData, Arrays.asList(mListTitle));
        mVpFileList.setAdapter(mViewPagerFragmentAdapter);
        mTLSwitchContent.setupWithViewPager(mVpFileList);
        mVpFileList.setCurrentItem(mType);
        mTLSwitchContent.addOnTabSelectedListener(mOnTabSelectedListener);

        mBtnNew = view.findViewById(R.id.btn_new);
        mBtnNew.setOnClickListener(this);
        mBtnDelete = view.findViewById(R.id.btn_delete);
        mBtnDelete.setOnClickListener(this);
        mBtnAppend = view.findViewById(R.id.btn_append);
        mBtnAppend.setOnClickListener(this);
        mBtnExport = view.findViewById(R.id.btn_export);
        mBtnExport.setOnClickListener(this);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mType = tab.getPosition();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    protected void loadFiles() {
        loadFiles(TYPE_KML);
        loadFiles(TYPE_EXCEL);
    }

    private void loadFiles(int type) {

        if (type == TYPE_KML) {
            kmlFileInfoList.clear();
            List<FileInfo> fileInfos = getSortFiles(IMPORT_KML_PATH);
            for (FileInfo fileInfo : fileInfos) {
                if (fileInfo.getSuffix().equals("kml")) {
                    kmlFileInfoList.add(fileInfo);
                }
            }
            kmlFileListFragment.notifyDataSetChanged();

        } else {
            excelFileInfoList.clear();
            List<FileInfo> fileInfos = getSortFiles(IMPORT_EXCEL_PATH);
            for (FileInfo fileInfo : fileInfos) {
                if (fileInfo.getSuffix().equals("xls")) {
                    excelFileInfoList.add(fileInfo);
                }
            }
            excelFileListFragment.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_import:
                if (checkSelectedFilesEmpty()) {
                    showToast(getActivity(), "请选择要导入的文件");
                    return;
                }
                if (mImportTowerLineListener != null) {
                    mImportTowerLineListener.onImportTowerLine(TAG_IMPORT, mType, getSelectedFileInfo());
                }
                dismiss();
                break;
            case R.id.btn_delete:
                if (checkSelectedFilesEmpty()) {
                    showToast(getActivity(), "请选择要删除的文件");
                    return;
                }
                CustomDialog.Builder deleteDialog = new CustomDialog.Builder(getActivity());
                deleteDialog.setTitle(getActivity().getString(R.string.notice))
                        .setMessage(getActivity().getString(R.string.default_delete_notice))
                        .setPositiveButton(getActivity().getString(R.string.sure), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFile();
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create()
                        .show();
                break;
            case R.id.btn_new:
                if (mImportTowerLineListener != null) {
                    mImportTowerLineListener.onImportTowerLine(TAG_NEW, mType, null);
                }
                dismiss();
                break;
            case R.id.btn_append:
                if (mImportTowerLineListener != null) {
                    List<FileInfo> fileInfos = getSelectedFileInfo();
                    if (fileInfos == null || fileInfos.isEmpty()) {
                        ToastUtil.show(getContext(), "没有选中文件");
                        return;
                    }
                    if (fileInfos.size() > 1) {
                        ToastUtil.show(getContext(), "只能追加更新单个文件，请不要选中多个文件");
                        return;
                    }
                    mImportTowerLineListener.onImportTowerLine(TAG_APPEND, mType, getSelectedFileInfo());
                }
                dismiss();
                break;
            case R.id.btn_export:
                exportFile();
                break;
        }
    }

    private void exportFile() {

        List<FileInfo> fileInfos = getSelectedFileInfo();
        if (fileInfos == null || fileInfos.isEmpty()) {
            showToast(getContext(), "请选择需要导出的文件");
            return;
        }

        String outputPath = (mType == TYPE_KML ? OUTPUT_KML_PATH : OUTPUT_EXCEL_PATH) + File.separator;

        ArrayList<String> selectedFileNames = new ArrayList<>();

        for (FileInfo fileInfo : fileInfos) {
            selectedFileNames.add(fileInfo.getFileName());
            try {
                InputStream is = new FileInputStream(fileInfo.getFilePath());
                IOUtils.copy(is, outputPath + fileInfo.getFileName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        FileExportDialogFragment export = new FileExportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("export_Path", outputPath);
        bundle.putSerializable("filenames", selectedFileNames);
        export.setArguments(bundle);
        export.show(getFragmentManager(), "export");
    }


    private boolean checkSelectedFilesEmpty() {
        List<FileInfo> selectedFileInfos = getSelectedFileInfo();
        return (selectedFileInfos == null || selectedFileInfos.isEmpty());
    }

    private void deleteFile() {
        List<FileInfo> selectedFileInfos = getSelectedFileInfo();
        for (FileInfo selectedFileInfo : selectedFileInfos) {

            File deleteFile = new File(selectedFileInfo.getFilePath());
            if (deleteFile.isFile() && deleteFile.exists()) {
                deleteFile.delete();
            }
        }
        loadFiles(mType);
    }

    private OnImportTowerLineListener mImportTowerLineListener;

    public void setImportTowerLineListener(OnImportTowerLineListener importTowerLineListener) {
        mImportTowerLineListener = importTowerLineListener;
    }

    public interface OnImportTowerLineListener {
        void onImportTowerLine(String tag, int type, Object object);
    }

    private List<FileInfo> getSelectedFileInfo() {
        if (mType == TYPE_KML) {
            return kmlFileAdapter.getSelectedFileInfo();
        } else {
            return excelFileAdapter.getSelectedFileInfo();
        }
    }


    @Override
    protected void sortFiles() {
        loadFiles();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTLSwitchContent.removeOnTabSelectedListener(mOnTabSelectedListener);
    }
}
