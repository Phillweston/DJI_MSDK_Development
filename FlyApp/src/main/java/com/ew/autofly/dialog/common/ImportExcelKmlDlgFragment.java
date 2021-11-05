package com.ew.autofly.dialog.common;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.dialog.base.BaseDialogFragment;
import com.ew.autofly.dialog.CloudKmlDialogFragment;
import com.ew.autofly.dialog.CustomDialog;
import com.ew.autofly.dialog.FileExportDialogFragment;
import com.ew.autofly.entity.ImportFileEntity;
import com.ew.autofly.interfaces.KMLFragmentListener;
import com.ew.autofly.interfaces.TowerKMLFragmentListener;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.io.excel.ExcelLoader;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.io.kml.KmlLoader;
import com.ew.autofly.utils.StringUtils;
import com.ew.autofly.utils.ToastUtil;
import com.flycloud.autofly.base.util.DateHelperUtils;
import com.flycloud.autofly.base.widgets.dialog.BaseProgressDialog;
import com.flycloud.autofly.control.service.RouteService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dji.thirdparty.rx.Observable;
import dji.thirdparty.rx.Subscriber;
import dji.thirdparty.rx.android.schedulers.AndroidSchedulers;
import dji.thirdparty.rx.functions.Func1;
import dji.thirdparty.rx.schedulers.Schedulers;

import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_EXCEL;
import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_KML;
import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_TOWER_EXCEL;
import static com.ew.autofly.constant.AppConstant.DIR_FTP_EXPORT_TOWER_KML;

public class ImportExcelKmlDlgFragment extends BaseDialogFragment implements OnClickListener {

    private BaseProgressDialog mLoadKmlDlg;

    public final static int TYPE_KML = 1;
    public final static int TYPE_EXCEL = 2;

    private RadioGroup rgMode;
    private TextView btnNew, btnAppend, btnDelete, btnConfirm, btnExport, btnCloudKML;
    private ListView mLvExcelAndKml;
    private DataBaseUtils mDB = null;
    private List<ImportFileEntity> entityList = new ArrayList<>();
    private ArrayList<String> selectedIdList = new ArrayList<>();
    private List<File> kmlFiles = new ArrayList<>();
    private List<File> excelFiles = new ArrayList<>();
    private List<File> adapterFiles = new ArrayList<>();
    private ArrayList<String> selectedFileNames;
    private String mFindId = "";
    private FileAdapter fileAdapter = new FileAdapter();
    private boolean isExcel = false;

    private String DIR_KML = "";
    private String DIR_EXCEL = "";

    private TowerKMLFragmentListener mTowerKMLListener;

    private KMLFragmentListener mKMLFragmentListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        try {
            mDB = DataBaseUtils.getInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
        setSize(0.7f,0.9f);
    }

    private void initData() {
        if (getTag().equals("tower_kml_excel")) {
            DIR_KML = AppConstant.DIR_TOWER_KML;
            DIR_EXCEL = AppConstant.DIR_TOWER_EXCEL;
        } else {
            DIR_KML = AppConstant.DIR_KML;
            DIR_EXCEL = AppConstant.DIR_EXCEL;
        }

        loadKmlTable(true);
        loadExcelTable();
    }

    public void reLoadKmlTable() {
        kmlFiles.clear();
        adapterFiles.clear();
        loadKmlTable(false);
    }

    private void loadExcelTable() {
        try {
            mDB.getAllKMLFiles(new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {
                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    if (succ) {
                        List<ImportFileEntity> resultList = (List<ImportFileEntity>) result;
                        String kmlPath = IOUtils.getRootStoragePath(getContext()) + DIR_EXCEL;
                        File dir = new File(kmlPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                            return;
                        }
                        File[] files = dir.listFiles();
                        for (int i = 0, len = files.length; i < len; i++) {
                            final String fileName = files[i].getName();



                            if (fileName.toLowerCase().endsWith(".xls") /*|| fileName.toLowerCase().endsWith(".xlsx")*/) {
                                filterImportFileEntitys(files[i], resultList);
                                excelFiles.add(files[i]);







                            }
                        }
                    }
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadKmlTable(final boolean isFirstLoad) {
        try {
            mDB.getAllKMLFiles(new DataBaseUtils.onExecResult() {
                @Override
                public void execResult(boolean succ, String errStr) {
                }

                @Override
                public void execResultWithResult(boolean succ, Object result, String errStr) {
                    if (succ) {
                        List<ImportFileEntity> resultList = (List<ImportFileEntity>) result;
                        String kmlPath = IOUtils.getRootStoragePath(getContext()) + DIR_KML;
                        File dir = new File(kmlPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                            return;
                        }
                        File[] files = dir.listFiles();
                        for (int i = 0, len = files.length; i < len; i++) {
                            String fileName = files[i].getName();
                            if (!fileName.toLowerCase().endsWith(".kml")) {
                                continue;
                            }
                            filterImportFileEntitys(files[i], resultList);
                            kmlFiles.add(files[i]);
                            adapterFiles.add(files[i]);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isFirstLoad) {
                                    mLvExcelAndKml.setAdapter(fileAdapter);
                                } else {
                                    fileAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }

                @Override
                public void setExecCount(int i, int count) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void filterImportFileEntitys(File file, List<ImportFileEntity> importFileEntities) {
        for (ImportFileEntity entity : importFileEntities) {
            final String fileName = file.getName();
            if (fileName.equals(entity.getFileName())) {

                final String fileModifyTime = DateHelperUtils.format(new Date(file.lastModified()));
                if (fileModifyTime.equals(entity.getImportDate())) {
                    entityList.add(entity);
                } else {
                    for (String fileId : selectedIdList) {
                        if (fileId.equals(entity.getFileId())) {
                            selectedIdList.remove(fileId);
                        }
                    }
                }
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(setLayoutId(), container, false);
        initView(view);
        return view;
    }

    protected int setLayoutId(){
        return R.layout.dialog_import_excel_kml;
    }

    protected void initView(View view) {
        view.findViewById(R.id.tv_close).setOnClickListener(this);
        mLvExcelAndKml = (ListView) view.findViewById(R.id.lv_excel_kml);
        btnNew =  view.findViewById(R.id.btn_new);
        btnAppend =  view.findViewById(R.id.btn_append);
        btnDelete =  view.findViewById(R.id.btn_delete);
        btnConfirm =  view.findViewById(R.id.btn_import);
        btnExport =  view.findViewById(R.id.btn_export);
        btnCloudKML =  view.findViewById(R.id.btn_cloud_import);
        if (getArguments() != null && !getArguments().getBoolean("no_excel")) {
            rgMode = (RadioGroup) view.findViewById(R.id.rg_mode);
            rgMode.setVisibility(View.VISIBLE);
            if (getTag().equals("tower_kml_excel")) {
                ((RadioButton) rgMode.findViewById(R.id.rb_kml)).setText("杆塔KML");
                ((RadioButton) rgMode.findViewById(R.id.rb_excel)).setText("杆塔EXCEL");
                btnNew.setVisibility(View.VISIBLE);
                btnAppend.setVisibility(View.VISIBLE);
                btnExport.setVisibility(View.VISIBLE);
                btnCloudKML.setVisibility(View.GONE);
            }
            rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    adapterFiles.clear();
                    if (checkedId == R.id.rb_kml) {
                        isExcel = false;
                        adapterFiles.addAll(kmlFiles);
                    } else {
                        isExcel = true;
                        adapterFiles.addAll(excelFiles);
                    }
                    fileAdapter.notifyDataSetChanged();
                }
            });
        }
        selectedIdList = getArguments().getStringArrayList("fileIds");

        btnAppend.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnNew.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnCloudKML.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_export:
                exportFile();
                break;
            case R.id.btn_import:
                if (isExcel && (selectedIdList == null || selectedIdList.size() == 0)) {
                    ToastUtil.show(getActivity(), "请选择要导入的EXCEL文件");
                    return;
                }
                if (!isExcel && (selectedIdList == null || selectedIdList.size() == 0)) {
                    ToastUtil.show(getActivity(), "请选择要导入的KML文件");
                    return;
                }
                if (getTag().equals("kml_excel")) {
                    if(mKMLFragmentListener!=null){
                        mKMLFragmentListener.onSelectKMLComplete("import", selectedIdList);
                    }
                } else if (getTag().equals("tower_kml_excel")) {
                    if (mTowerKMLListener != null) {
                        mTowerKMLListener.onSelectTowerKMLComplete("import", selectedIdList, isExcel ? TYPE_EXCEL : TYPE_KML);
                    }
                }
                dismiss();
                break;
            case R.id.btn_new:
                if (getTag().equals("kml_excel")) {
                    if(mKMLFragmentListener!=null){
                        mKMLFragmentListener.onSelectKMLComplete("new", null);
                    }
                } else if (getTag().equals("tower_kml_excel")) {
                    if (mTowerKMLListener != null) {
                        mTowerKMLListener.onSelectTowerKMLComplete("new", null, isExcel ? TYPE_EXCEL : TYPE_KML);
                    }
                }
                dismiss();
                break;
            case R.id.btn_append:
                if (getTag().equals("tower_kml_excel")) {
                    if (selectedIdList == null || selectedIdList.size() == 0) {
                        ToastUtil.show(getActivity(), "请选择要更新的杆塔文件");
                        return;
                    } else if (selectedIdList.size() > 1) {
                        ToastUtil.show(getActivity(), "请选择单个杆塔文件");
                        return;
                    }
                    if (mTowerKMLListener != null) {
                        mTowerKMLListener.onSelectTowerKMLComplete("update", selectedIdList, isExcel ? TYPE_EXCEL : TYPE_KML);
                    }
                }
                dismiss();
                break;
            case R.id.btn_delete:
                if (selectedIdList == null || selectedIdList.isEmpty()) {
                    ToastUtil.show(getActivity(), "请选择要删除的文件");
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
            case R.id.btn_cloud_import:
                if (RouteService.getUserToken().equals("")) {
                    ToastUtil.show(getActivity(), "用户未登录或登录过期");
                    return;
                }
                CloudKmlDialogFragment cloudKmlFragment = new CloudKmlDialogFragment();
                cloudKmlFragment.show(getFragmentManager(), "cloud_kml");
                break;
            case R.id.tv_close:
                dismiss();
                break;
        }
    }

    private void exportFile() {

        if (null == selectedIdList || selectedIdList.isEmpty()) {
            ToastUtil.show(getContext(), "请选择需要导出的文件");
            return;
        }

        String inputPath;
        String outputPath;

        if (!isExcel) {
            if (getArguments() != null && getTag().equals("tower_kml_excel")) {
                inputPath = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_TOWER_KML + File.separator;
                outputPath = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_TOWER_KML + File.separator;
            } else {
                inputPath = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_KML + File.separator;
                outputPath = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_KML + File.separator;
            }

        } else {
            if (getArguments() != null && getTag().equals("tower_kml_excel")) {
                inputPath = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_TOWER_EXCEL + File.separator;
                outputPath = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_TOWER_EXCEL + File.separator;
            } else {
                inputPath = AppConstant.ROOT_PATH + File.separator + AppConstant.DIR_EXCEL + File.separator;
                outputPath = AppConstant.ROOT_PATH + File.separator + DIR_FTP_EXPORT_EXCEL + File.separator;
            }
        }

        selectedFileNames = new ArrayList<>();
        for (String fileId : selectedIdList) {
            for (ImportFileEntity entity : entityList) {
                if (fileId.equalsIgnoreCase(entity.getFileId())) {
                    try {
                        InputStream is = new FileInputStream(inputPath + entity.getFileName());
                        IOUtils.copy(is, outputPath + entity.getFileName());
                        selectedFileNames.add(entity.getFileName());
                    } catch (FileNotFoundException e) {

                    }
                }
            }
        }


        FileExportDialogFragment export = new FileExportDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("export_Path", outputPath);
        bundle.putSerializable("filenames", selectedFileNames);
        export.setArguments(bundle);
        export.show(getFragmentManager(), "export");
    }

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_insert_excel_kml, parent, false);
            }
            final File file = adapterFiles.get(position);
            TextView fileName = convertView.findViewById(R.id.txt_layer_name);
            fileName.setText(file.getName());
            fileName.setSelected(true);
            final ImageView imgSelect = (ImageView) convertView.findViewById(R.id.img_select_state);
            final RelativeLayout bgRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.insert_excel_kml_linear);
            if (isSelect(file.getName())) {
                imgSelect.setImageResource(R.drawable.ic_checkbox_checked);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#1A1D22"));
            } else {
                imgSelect.setImageResource(R.drawable.ic_checkbox_uncheck);
                bgRelativeLayout.setBackgroundColor(Color.parseColor("#252932"));
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelect(file.getName())) {
                        imgSelect.setImageResource(R.drawable.ic_checkbox_uncheck);
                        bgRelativeLayout.setBackgroundColor(Color.parseColor("#252932"));
                        selectedIdList.remove(mFindId);
                    } else {
                        if (StringUtils.isEmptyOrNull(mFindId)) { //未插入过
                            mFindId = StringUtils.newGUID();
                            final ImportFileEntity entity = new ImportFileEntity();
                            entity.setId(StringUtils.newGUID());
                            entity.setFileName(file.getName());
                            entity.setFileId(mFindId);

                            entity.setImportDate(DateHelperUtils.format(new Date(file.lastModified())));
                            if (!isExcel) {

                                if (mLoadKmlDlg == null) {
                                    mLoadKmlDlg = new BaseProgressDialog(getContext(), "正在载入……");
                                    mLoadKmlDlg.setCancelable(false);
                                }
                                mLoadKmlDlg.show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        importKml(entity, imgSelect);
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        importExcel(entity, imgSelect);
                                    }
                                }).start();
                            }

                        } else { //已经插入过
                            selectedIdList.add(mFindId);
                            bgRelativeLayout.setBackgroundColor(Color.parseColor("#1A1D22"));
                            imgSelect.setImageResource(R.drawable.ic_checkbox_checked);
                        }
                    }
                }
            });
            return convertView;
        }
    }

    private void importKml(final ImportFileEntity entity, final ImageView img) {

        Observable.create(new Observable.OnSubscribe<ArrayList<String>>() {

            @Override
            public void call(Subscriber<? super ArrayList<String>> subscriber) {
                String fileId = entity.getFileId();

                String kmlPath = IOUtils.getRootStoragePath(getContext()) + DIR_KML + File.separator;
                KmlLoader loader = new KmlLoader();
                ArrayList<String> kmlData = loader.loadKml(kmlPath + entity.getFileName(), fileId);
                if (kmlData == null) {
                    subscriber.onError(new Throwable("kml文件导入错误"));
                } else {
                    subscriber.onNext(kmlData);
                }
            }
        }).map(new Func1<ArrayList<String>, ArrayList<String>>() {

            @Override
            public ArrayList<String> call(ArrayList<String> kmlData) {

                ArrayList<String> sqls = new ArrayList<>();
                sqls.add("insert into importfiles (id,fileid,filename,importdate) values ('"
                        + entity.getId() + "','"
                        + entity.getFileId() + "','"
                        + entity.getFileName() + "','"
                        + entity.getImportDate() + "')");
                sqls.addAll(kmlData);

                return sqls;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (mLoadKmlDlg != null && mLoadKmlDlg.isShowing()) {
                            mLoadKmlDlg.dismiss();
                        }
                        ToastUtil.show(getContext(), throwable.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<String> sqls) {

                        mDB.execSqls(sqls, new DataBaseUtils.onExecResult() {
                            @Override
                            public void execResult(boolean succ, String errStr) {
                                if (succ && isAdded()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            img.setImageResource(R.drawable.ic_checkbox_checked);
                                        }
                                    });
                                    selectedIdList.add(mFindId);
                                    entityList.add(entity);
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.show(getActivity(), "导入数据库错误");
                                        }
                                    });
                                }

                                if (mLoadKmlDlg != null && mLoadKmlDlg.isShowing()) {
                                    mLoadKmlDlg.dismiss();
                                }
                            }

                            @Override
                            public void execResultWithResult(boolean succ, Object result, String errStr) {

                            }

                            @Override
                            public void setExecCount(int i, int count) {

                            }
                        });

                    }
                });

    }

    private void importExcel(final ImportFileEntity entity, final ImageView img) {
        ArrayList<String> sqls = new ArrayList<>();
        sqls.add("insert into importfiles (id,fileid,filename,importdate) values ('"
                + entity.getId() + "','"
                + entity.getFileId() + "','"
                + entity.getFileName() + "','"
                + entity.getImportDate() + "')");
        String fileId = entity.getFileId();
        String excelPath = IOUtils.getRootStoragePath(getContext()) + DIR_EXCEL + File.separator;
        ExcelLoader loader = new ExcelLoader(getContext());
        List<String> excelData;

        excelData = loader.load03Excel(excelPath + entity.getFileName(), fileId);


        if (excelData == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.show(getActivity(), "excel文件错误或损坏");
                }
            });
            return;
        }
        sqls.addAll(excelData);

        mDB.execSqls(sqls, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {
                if (succ) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageResource(R.drawable.ic_checkbox_checked);
                        }
                    });
                    selectedIdList.add(mFindId);
                    entityList.add(entity);
                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getActivity(), "导入数据库错误");
                        }
                    });
                }
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {

            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
    }

    private void deleteFile() {

        StringBuilder sqlSb = new StringBuilder();

        sqlSb.append("DELETE FROM IMPORTFILES WHERE FILEID IN (");

        for (int i = 0; i < selectedIdList.size(); i++) {
            if (i != 0) {
                sqlSb.append(",");
            }
            sqlSb.append("'").append(selectedIdList.get(i)).append("'");
        }

        sqlSb.append(")");

        ArrayList<String> sqls = new ArrayList<>();
        sqls.add(sqlSb.toString());

        mDB.execSqls(sqls, new DataBaseUtils.onExecResult() {
            @Override
            public void execResult(boolean succ, String errStr) {
                if (succ) {
                    List<String> selectedFileNameList = new ArrayList<>();
                    for (String fileId : selectedIdList) {
                        for (ImportFileEntity kml : entityList) {
                            if (kml.getFileId().equalsIgnoreCase(fileId)) {
                                selectedFileNameList.add(kml.getFileName());
                            }
                        }
                    }

                    for (String fileName : selectedFileNameList) {
                        if (isExcel) {
                            for (int i = 0; i < excelFiles.size(); i++) {
                                File file = excelFiles.get(i);
                                String name = file.getName();
                                if (name.equals(fileName)) {
                                    excelFiles.remove(i);
                                    String path = IOUtils.getRootStoragePath(getContext()) + DIR_EXCEL + "/" + name;
                                    File deleteFile = new File(path);
                                    if (deleteFile.isFile() && deleteFile.exists()) {
                                        deleteFile.delete();
                                    }
                                }
                            }
                        } else {
                            for (int i = 0; i < kmlFiles.size(); i++) {
                                File file = kmlFiles.get(i);
                                String name = file.getName();
                                if (name.equals(fileName)) {
                                    kmlFiles.remove(i);
                                    String path = IOUtils.getRootStoragePath(getContext()) + DIR_KML + "/" + name;
                                    File deleteFile = new File(path);
                                    if (deleteFile.isFile() && deleteFile.exists()) {
                                        deleteFile.delete();
                                    }
                                }
                            }
                        }
                    }

                    selectedIdList.clear();
                    adapterFiles.clear();
                    adapterFiles.addAll(isExcel ? excelFiles : kmlFiles);


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fileAdapter.notifyDataSetChanged();
                            ToastUtil.show(getContext(), getString(R.string.default_delete_success));
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.show(getContext(), getString(R.string.default_delete_fail));
                        }
                    });
                }
            }

            @Override
            public void execResultWithResult(boolean succ, Object result, String errStr) {

            }

            @Override
            public void setExecCount(int i, int count) {

            }
        });
    }

    /**
     * 判断是否已经选中某个KML文件
     *
     * @param fileName
     * @return
     */
    private boolean isSelect(String fileName) {
        mFindId = "";
        for (ImportFileEntity kml : entityList) {
            if (kml.getFileName().equalsIgnoreCase(fileName)) {
                mFindId = kml.getFileId();
                break;
            }
        }
        if (StringUtils.isEmptyOrNull(mFindId)) {
            return false;
        }

        for (String fileId : selectedIdList) {
            if (fileId.equalsIgnoreCase(mFindId)) {
                return true;
            }
        }
        return false;
    }

    public void setTowerKMLListener(TowerKMLFragmentListener towerKMLListener) {
        this.mTowerKMLListener = towerKMLListener;
    }

    public void setKMLListener(KMLFragmentListener KMLFragmentListener) {
        mKMLFragmentListener = KMLFragmentListener;
    }
}