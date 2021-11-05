package com.ew.autofly.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Process;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bulong.rudeness.RudenessScreenHelper;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.ew.autofly.BuildConfig;
import com.ew.autofly.constant.AirLineConstant;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.db.DBOpenHelper;
import com.ew.autofly.db.dao.DaoMaster;
import com.ew.autofly.db.dao.DaoSession;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.mode.linepatrol.point.ui.db.LocalDataSource;
import com.ew.autofly.mode.linepatrol.point.ui.db.LocalDataSourceImpl;
import com.ew.autofly.mode.linepatrol.point.ui.db.Repository;
import com.ew.autofly.mode.linepatrol.point.ui.db.RepositoryImpl;
import com.ew.autofly.utils.CrashLogCatchUtils;
import com.ew.autofly.utils.DataBaseUtils;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.SoundPlayerUtil;
import com.ew.autofly.xflyer.utils.ThreadPoolUtils;
import com.flycloud.autofly.base.util.ALog;
import com.flycloud.autofly.base.util.ToastUtil;
import com.lzy.okgo.OkGo;
import com.secneo.sdk.Helper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;



public class EWApplication extends Application {
    private static final String TAG = EWApplication.class.getName();


    private DJIApplication djiApplication;

    private DBOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public static synchronized BaseProduct getProductInstance() {
        return DJIApplication.getProductInstance();
    }

    public static boolean isAircraftConnected() {
        return getProductInstance() != null && getProductInstance() instanceof Aircraft;
    }

    public static synchronized Aircraft getAircraftInstance() {
        if (!isAircraftConnected())
            return null;
        return (Aircraft) getProductInstance();
    }

    private static EWApplication instance;

    public static EWApplication getInstance() {
        return instance;
    }

    public void startInitProcess() {

        initLog();
        initAllDirs();
        setDatabase();


        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud7546770145,none,LHH93PJPXJPFLMZ59028");
        ArcGISRuntimeEnvironment.initialize();

        OkGo.getInstance().init(this);

        delayLoadThread();
    }

    
    public void startSDKRegistration() {
        if (djiApplication != null) {
            djiApplication.startSDKRegistration();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Helper.install(this);

        if (djiApplication == null) {
            djiApplication = new DJIApplication();
            djiApplication.setContext(this);
        }
    }


    @NotNull
    public Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


        djiApplication.onCreate();

        initARouter();

        new RudenessScreenHelper(this, AppConstant.DESIGN_RESOLUTION_WIDTH).activate();

    }

    private void delayLoadThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                setExcelModel();
                initSound();
                initStatistics();
                initDem();
            }
        }).start();
    }

    private void initARouter() {
        ARouter.init(this);
        if (BuildConfig.LOG_DEBUG && BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
    }

    private void initLog() {

        Logger.initLogger(this);
        CrashLogCatchUtils crashHandler = CrashLogCatchUtils.getInstance();
        crashHandler.init(getApplicationContext(), IOUtils.getRootStoragePath(this) + AppConstant.DIR_LOG);

        ALog.Builder builder = new ALog.Builder()
                .isLogEnable(BuildConfig.DEBUG);

        ALog.init(builder);
    }

    private void initStatistics() {







        if (BuildConfig.DEBUG) {



           /* if (LeakCanary.isInAnalyzerProcess(this))
                return;
            LeakCanary.install(this);*/









        } else {


//






        }
    }

    private void initSound() {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                SoundPlayerUtil.getInstance(getApplicationContext()).init();
            }
        });
    }

    private void setDatabase() {

        try {
            DataBaseUtils.getInstance(this);
            String greendaoPath = Environment.getExternalStorageDirectory() + File.separator + AppConstant.APP_STORAGE_PATH
                    + File.separator + AppConstant.DIR_GEOMETRYS + File.separator + AppConstant.DATABASE_GREENDAO_NAME;
            mHelper = new DBOpenHelper(this, greendaoPath, null);
            db = mHelper.getWritableDatabase();
            mDaoMaster = new DaoMaster(db);

            mDaoSession = mDaoMaster.newSession();

            LocalDataSource localDataSource = new LocalDataSourceImpl(mDaoSession);
            repository = new RepositoryImpl(localDataSource);
        } catch (Exception e) {
            ToastUtil.show(this, "数据库初始化错误");
        }
    }

    private void setExcelModel() {
        String excelFilePath = IOUtils.getRootStoragePath(this)
                + AppConstant.DIR_TOWER_EXCEL + File.separator
                + "示例模板.xls";

        if (new File(excelFilePath).exists())
            return;

        try {
            InputStream myInput = null;
            OutputStream myOutput = new FileOutputStream(excelFilePath);
            try {
                myInput = getAssets().open("示例模板.xls");
                byte[] buffer = new byte[4096];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }
            } catch (Exception e) {
                Logger.i("复制模板excel出错，详情：" + e.getStackTrace());
            } finally {
                if (myInput != null) {
                    myInput.close();
                }
                if (myOutput != null) {
                    myOutput.flush();
                    myOutput.close();
                }
            }
        } catch (IOException e) {
            Logger.i("复制模板excel出错，详情：" + e.getStackTrace());
        }
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void initAllDirs() {
        createFolder(AppConstant.DIRECTORIES);
        createFolder(AppConstant.DIR_MISSION.DIRECTORIES);
        AirLineConstant.createFolder(IOUtils.getRootStoragePath(this));
    }

    
    public void initDem() {
        DemReaderUtils.getInstance(this).addDemFilePath(IOUtils.getRootStoragePath(this)
                + "Maps/");
    }

    /**
     * 创建文件夹
     *
     * @param dirs
     */
    private void createFolder(String[] dirs) {
        String root = IOUtils.getRootStoragePath(this);
        for (String dir : dirs) {
            File d = new File(root + dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }

    }

}