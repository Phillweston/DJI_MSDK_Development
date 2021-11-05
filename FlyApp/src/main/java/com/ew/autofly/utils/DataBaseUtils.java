package com.ew.autofly.utils;

import android.content.Context;
import android.util.Log;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.MultiPoint;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.ExportMission;
import com.ew.autofly.entity.ImportFileEntity;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.Mission2;
import com.ew.autofly.entity.MissionBatch2;
import com.ew.autofly.entity.MissionPhoto;
import com.ew.autofly.entity.PhotoWithName;
import com.ew.autofly.entity.PointWithName;
import com.ew.autofly.entity.PolygonWithName;
import com.ew.autofly.entity.PolylineWithName;
import com.ew.autofly.logger.Logger;
import com.ew.autofly.utils.math.BigDecimalOperatorUtil;
import com.ew.autofly.xflyer.utils.CoordinateUtils;
import com.ew.autofly.xflyer.utils.DateHelperUtils;
import com.flycloud.autofly.base.util.SysUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsqlite.Database;
import jsqlite.Exception;
import jsqlite.Stmt;

/**
 * 请不要再使用此类，请使用GreenDao或者其他ORM数据库
 * 若要使用，请继承使用，不要直接在此增加不是公用的方法
 */
@Deprecated
public class DataBaseUtils {
    protected LogUtilsOld log;
    protected static DataBaseUtils mInstance = null;
    private final String FILENAME = "Geometrys.sdb";
    private Context mContext;
    private String databaseFilePath = "";
    private File mDatabaseFile = null;
    protected Database mDatabase;
    protected ExecutorService mPool = null;

    public interface onExecResult {
        void execResult(boolean succ, String errStr);

        void execResultWithResult(boolean succ, Object result, String errStr);

        void setExecCount(int i, int count);
    }

    public static DataBaseUtils getInstance(Context appContext) throws Exception {
        if (mInstance == null) {
            synchronized (DataBaseUtils.class) {
                if (mInstance == null) {
                    try {
                        mInstance = new DataBaseUtils(appContext);
                    } catch (Exception e) {
                        throw new Exception(e.getMessage());
                    }
                }
            }
        }
        return mInstance;
    }

    public DataBaseUtils(Context context) throws Exception {
        if (context == null) {
            throw new NullPointerException("传入的Context为Null");
        }
        mPool = Executors.newSingleThreadExecutor();
        log = LogUtilsOld.getInstance(context).setTag("DataBaseUtils");
        mContext = context.getApplicationContext();
        databaseFilePath = IOUtils.getRootStoragePath(context)
                + AppConstant.DIR_GEOMETRYS + File.separator
                + FILENAME;
        mDatabaseFile = new File(databaseFilePath);
        File dir = new File(mDatabaseFile.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!mDatabaseFile.exists()) {

            try {
                copyBigDataBase();
            } catch (IOException e) {
                throw new Exception(e.getMessage());
            }
        }

        mDatabase = new Database();
        try {
            mDatabase.open(databaseFilePath, jsqlite.Constants.SQLITE_OPEN_READWRITE
                    | jsqlite.Constants.SQLITE_OPEN_CREATE);
            Stmt stmt01 = mDatabase.prepare("SELECT spatialite_version();");
            if (stmt01.step()) {

            }

            stmt01 = mDatabase.prepare("SELECT proj4_version();");
            if (stmt01.step()) {

            }

            stmt01 = mDatabase.prepare("SELECT geos_version();");
            if (stmt01.step()) {

            }

            stmt01.close();

            if (!checkColumnExist("POINTS", "description")) {
                execSql("alter table POINTS add description TEXT", null);
            }

            if (!checkColumnExist("POINTS", "altitude")) {
                execSql("alter table POINTS add altitude TEXT", null);
            }

            if (!checkColumnExist("POLYLINES", "description")) {
                execSql("alter table POLYLINES add description TEXT", null);
            }

            if (!checkColumnExist("POLYGONS", "description")) {
                execSql("alter table POLYGONS add description TEXT", null);
            }

            if (!checkColumnExist("Mission2", "start_point")) {
                execSql("alter table Mission2 add start_point Integer", null);
            }
            if (!checkColumnExist("MissionBatch2", "fly_buffer")) {
                execSql("alter table MissionBatch2 add fly_buffer Integer", null);
            }
            if (!checkColumnExist("MissionBatch2", "id_create_user")) {
                execSql("alter table MissionBatch2 add id_create_user Integer", null);
            }
            if (!checkColumnExist("MissionBatch2", "is_cloud")) {
                execSql("alter table MissionBatch2 add is_cloud Bool", null);
            }
            if (!checkColumnExist("Mission2", "fly_buffer")) {
                execSql("alter table Mission2 add fly_buffer Integer", null);
            }
            if (!checkColumnExist("Mission2", "fixed_altitude_list")) {
                execSql("alter table Mission2 add fixed_altitude_list TEXT", null);
            }
            if (!checkColumnExist("Mission2", "fly_angle")) {
                execSql("alter table Mission2 add fly_angle Integer", null);
            }
            if (!checkColumnExist("Mission2", "min_altitude")) {
                execSql("alter table Mission2 add min_altitude Integer", null);
            }
            if (!checkColumnExist("Mission2", "flying_layer")) {
                execSql("alter table Mission2 add flying_layer Integer", null);
            }
            if (!checkColumnExist("Mission2", "end_point")) {
                execSql("alter table Mission2 add end_point Integer", null);
            }
            if (!checkColumnExist("Mission2", "rotating")) {
                execSql("alter table Mission2 add rotating double", null);
            }
            if (!checkColumnExist("Mission2", "return_mode")) {
                execSql("alter table Mission2 add return_mode Integer", null);
            }
            if (!checkColumnExist("Mission2", "baseline_height")) {
                execSql("alter table Mission2 add baseline_height Integer", null);
            }
            if (!checkColumnExist("Mission2", "entry_height")) {
                execSql("alter table Mission2 add entry_height Integer", null);
            }
            if (!checkColumnExist("Mission2", "is_polygon")) {
                execSql("alter table Mission2 add is_polygon Integer", null);
            }
            if (checkTableExist("Mission")) {
                execSql("drop table Mission", null);
            }
            if (checkTableExist("MissionBatch")) {
                execSql("drop table MissionBatch", null);
            }
            if (!checkTableExist("Mission2")) {
                String sql = "create table Mission2 (ID TEXT,batch_id TEXT,name TEXT,snap_shot TEXT,status INTEGER,fixed_altitude INTEGER,altitude INTEGER,fly_speed INTEGER,gimbal_angle INTEGER,resolution_rate double,geom_type INTEGER,geometry BLOB,side_overlap INTEGER,route_overlap INTEGER,create_date date,start_time date,end_time date,point_index INTEGER,work_mode TEXT,work_step INTEGER,flight_num INTEGER,start_photo_index INTEGER,end_photo_index INTEGER, min_altitude INTEGER, fly_angle INTEGER, fly_layer INTEGER, rotating double, fixed_altitude_list TEXT, fly_buffer INTEGER, start_point INTEGER, end_point INTEGER, baseline_height INTEGER, entry_height INTEGER, is_polygon INTEGER)";
                execSql(sql, null);
            }
            if (!checkTableExist("MissionBatch2")) {
                String sql = "create table MissionBatch2 (ID TEXT,name TEXT,status INTEGER,snap_shot TEXT,side_overlap INTEGER,route_overlap INTEGER,create_date date,altitude INTEGER,resolution_rate double,flight_num INTEGER,work_mode TEXT,fixed_altitude Bool, fly_buffer INTEGER, id_create_user INTEGER, is_cloud BOOL)";
                execSql(sql, null);
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Copy db from assets to sdcard
     *
     * @throws IOException
     */
    private void copyBigDataBase() throws IOException {
        InputStream myInput = null;
        String outFileName = databaseFilePath;
        OutputStream myOutput = new FileOutputStream(outFileName);
        try {
            myInput = mContext.getAssets().open("Geometrys.sdb");
            byte[] buffer = new byte[4096];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
        } catch (java.lang.Exception e) {
            log.e("复制数据库出错，详情：" + e.getStackTrace());
        } finally {
            if (myInput != null) {
                myInput.close();
            }
            if (myOutput != null) {
                myOutput.flush();
                myOutput.close();
            }
        }
    }

    public void destroy() {
        if (mDatabase != null) {
            try {
                mDatabase.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mDatabase = null;
                mInstance = null;
            }
        }
    }


    private boolean checkColumnExist(String tableName, String columnName) {
        boolean result = false;
        Stmt stmt = null;
        try {

            stmt = mDatabase.prepare("select * from sqlite_master where name = '" + tableName + "' and sql like '%" + columnName + "%'");
            result = stmt != null && stmt.step();
        } catch (Exception e) {
            Log.e("DataBaseUtils", "checkColumnExists1..." + e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                log.e(e.getMessage());
            }
            stmt = null;
        }

        return result;
    }

    protected boolean checkTableExist(String tableName) {
        boolean result = false;
        Stmt stmt = null;
        try {
            stmt = mDatabase.prepare("select * from sqlite_master where name = '" + tableName + "'");
            result = stmt != null && stmt.step();
        } catch (Exception e) {
            log.e("checkTableExist..." + e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                log.e(e.getMessage());
            }
            stmt = null;
        }
        return result;
    }

    protected synchronized void execSql(final String sql, final ArrayList<String> args) {
        Stmt stmt = null;
        try {
            stmt = mDatabase.prepare(sql);
            if (args != null && args.size() > 0) {
                for (int i = 0; i < args.size(); i++) {
                    stmt.bind(i + 1, args.get(i));
                }
            }
            stmt.step();
        } catch (Exception e) {
            log.e(e.getMessage());
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                log.e(e.getMessage());
            }
            stmt = null;
        }
    }

    protected synchronized void execSql(final String sql, final ArrayList<String> args, final onExecResult cb) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    if (args != null && args.size() > 0) {
                        for (int i = 0; i < args.size(); i++) {
                            stmt.bind(i + 1, args.get(i));
                        }
                    }
                    stmt.step();
                    if (cb != null) {
                        cb.execResult(true, null);
                    }
                } catch (Exception e) {
                    log.e("操作数据库execSql失败" + e.getMessage());
                    if (cb != null) {
                        cb.execResult(false, e.getMessage());
                    }
                } finally {
                    try {
                        stmt.close();
                    } catch (Exception e) {
                        log.e("关闭数据库execSql失败" + e.getMessage());
                    }
                    stmt = null;
                }
            }
        });
    }

    /**
     * 批量执行SQL语句
     *
     * @param htSQL
     * @param htArgs
     * @param callback
     */
    public void execSqls(final Hashtable<String, String> htSQL, final Hashtable<String, ArrayList<String>> htArgs, final onExecResult callback) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare("BEGIN");
                    stmt.step();

                    for (Iterator<String> sql = htSQL.keySet().iterator(); sql.hasNext(); ) {
                        String sqlKey = sql.next();
                        stmt = mDatabase.prepare(htSQL.get(sqlKey));
                        if (htArgs != null && htArgs.get(sqlKey) != null) {
                            ArrayList<String> argList = htArgs.get(sqlKey);
                            for (int i = 0; i < argList.size(); i++) {
                                stmt.bind(i + 1, argList.get(i));
                            }
                        }
                        stmt.step();
                    }
                    stmt = mDatabase.prepare("COMMIT");
                    stmt.step();
                    if (callback != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callback.execResult(true, null);
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    try {
                        stmt = mDatabase.prepare("ROLLBACK");
                        stmt.step();
                        stmt.close();
                        stmt = null;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    public void savePhoto(String fileName, Point p, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }

        ArrayList<String> args = new ArrayList<String>();
        args.add(SysUtils.newGUID());
        args.add(fileName);
        execSql("insert into photos (id,filename,geometrys) values (?,?," +
                "GeomFromText('POINT(" + p.getX() + " " + p.getY() + ")', 4326))", args, callback);
    }

    public void getPhotos(final onExecResult callback) {
        final String sql = "SELECT id,filename,X(GEOMETRYS),Y(GEOMETRYS) FROM photos";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PhotoWithName> points = new ArrayList<PhotoWithName>();
                    while (stmt.step()) {
                        PhotoWithName point = new PhotoWithName();
                        point.setId(stmt.column_string(0));
                        point.setFileName(stmt.column_string(1));
                        point.setPoint(new Point(stmt.column_double(2), stmt.column_double(3)));
                        points.add(point);
                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(true, points, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 保存点信息到数据库中(非kml导入)
     *
     * @param gid
     * @param name
     * @param p
     * @param callback
     */
    public void savePoint(String gid, String name, String symbol, Point p, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }

        if (StringUtils.isEmptyOrNull(gid)) {
            ArrayList<String> args = new ArrayList<String>();
            args.add(SysUtils.newGUID());
            args.add(name);
            args.add(symbol);
            args.add("0");
            args.add("DPS");
            execSql("insert into points (GID, NAME, SYMBOL, STATE, FILEID, geometrys) values (?,?,?,?,?," +
                    "GeomFromText('POINT(" + p.getX() + " " + p.getY() + ")', 4326))", args, callback);
        }

        else {
            ArrayList<String> args = new ArrayList<String>();
            args.add(name);
            args.add(symbol);
            args.add(gid);
            execSql("update points set name=?,symbol=?," +
                    "geometrys=GeomFromText('POINT(" + p.getX() + " " + p.getY() + ")',4326) " +
                    "where gid=?", args, callback);
        }
    }

    public void getPoints(final Envelope env, final String fileids, final onExecResult callback) {
        final String sql = "SELECT GID,NAME,SYMBOL,X(GEOMETRYS),Y(GEOMETRYS) FROM POINTS " +
                " WHERE ROWID IN(SELECT pkid FROM idx_POINTS_Geometrys " +
                " WHERE xmin > " + env.getXMin() + " AND xmax < " + env.getXMax()
                + " AND ymin > " + env.getYMin() + " AND ymax < " + env.getYMax()
                + ") AND FILEID IN (" + (StringUtils.isEmptyOrNull(fileids) ? "''" : fileids) + ")";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PointWithName> points = new ArrayList<>();
                    while (stmt.step()) {
                        PointWithName point = new PointWithName();
                        point.setGid(stmt.column_string(0));
                        point.setName(stmt.column_string(1));
                        point.setSymbol(stmt.column_string(2));
                        point.setPoint(new Point(stmt.column_double(3), stmt.column_double(4)));
                        points.add(point);
                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(true, points, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }


    public void getAllPoints(final onExecResult callback) {
        getPointsByFileId("DPS", callback);
    }

    public void deletePoint(final String gid, final onExecResult callback) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                String sql = "DELETE FROM POINTS WHERE GID='" + gid + "'";
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    stmt.step();
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResult(true, "");
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    public void deletePolyline(final String gid, final onExecResult callback) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                String sql = "DELETE FROM POLYLINES WHERE GID='" + gid + "'";
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    stmt.step();
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResult(true, "");
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    public void deletePolygon(final String gid, final onExecResult callback) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                String sql = "DELETE FROM POLYGONS WHERE GID='" + gid + "'";
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    stmt.step();
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResult(true, "");
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    public void getFileId(String fileName, final onExecResult callback) {
        final String sql = "SELECT FILEID FROM IMPORTFILES WHERE FILENAME='" + fileName + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    String fileId = "";

                    while (stmt.step()) {
                        fileId = stmt.column_string(0);
                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(true, fileId, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    public void execSqls(final ArrayList<String> sqls, final onExecResult callback) {
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    int count = sqls.size();
                    stmt = mDatabase.prepare("BEGIN");
                    stmt.step();

                    for (int i = 0; i < count; i++) {
                        stmt = mDatabase.prepare(sqls.get(i));
                        stmt.step();
                        if (callback != null) {
                            callback.setExecCount(i, count);
                        }
                    }
                    stmt = mDatabase.prepare("COMMIT");
                    stmt.step();

                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callback.execResult(true, null);
                            }
                        }).start();
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    try {
                        stmt = mDatabase.prepare("ROLLBACK");
                        stmt.step();
                        stmt.close();
                        stmt = null;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 保存线信息到数据库中(非kml导入)
     *
     * @param gid
     * @param name
     * @param line
     * @param callback
     */
    public void savePolyline(String gid, String name, int lineColor, float lineWidth, Polyline line, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        int count = line.getPointCount();
        for (int i = 0; i < count; i++) {
            Point p = line.getPoint(i);
            sb.append(p.getX() + " " + p.getY() + ",");
        }
        String xys = sb.toString();
        xys = xys.substring(0, xys.length() - 1);


        if (StringUtils.isEmptyOrNull(gid)) {
            ArrayList<String> args = new ArrayList<String>();
            args.add(SysUtils.newGUID());
            args.add(name);
            args.add(lineColor + "");
            args.add(lineWidth + "");
            args.add("0");
            args.add("DPLS");
            execSql("insert into POLYLINES (GID, NAME, LINECOLOR,LINEWIDTH, STATE, FILEID, geometrys) values (?,?,?,?,?,?," +
                    "MultiLineStringFromText('MULTILINESTRING((" + xys + "))', 4326))", args, callback);
        }

        else {
            ArrayList<String> args = new ArrayList<String>();
            args.add(name);
            args.add(lineColor + "");
            args.add(lineWidth + "");
            args.add(gid);
            execSql("update POLYLINES set name=?,linecolor=?,linewidth=?," +
                    "geometrys=MultiLineStringFromText('MULTILINESTRING((" + xys + "))', 4326)" +
                    " where gid=?", args, callback);
        }
    }

    public void getPolylines(final boolean isGoogleMap, final Envelope env, final String fileids, final onExecResult callback) {

        final String sql = "SELECT GID,NAME,LINECOLOR,LINEWIDTH,STATE,ASWKT(GEOMETRYS) FROM POLYLINES " +
                " WHERE FILEID IN (" + (StringUtils.isEmptyOrNull(fileids) ? "''" : fileids) + ")";
       /* final String sql = "SELECT GID,NAME,LINECOLOR,LINEWIDTH,STATE,ASWKT(GEOMETRYS) FROM POLYLINES " +
                " WHERE ROWID IN (SELECT pkid FROM idx_POLYLINES_Geometrys " +
                " WHERE pkid MATCH RTreeIntersects(" + env.getXMin()
                + "," + env.getYMin() + "," + env.getXMax() + ","
                + env.getYMax() + ")) AND FILEID IN (" + (StringUtils.isEmptyOrNull(fileids) ? "''" : fileids) + ")";*/

        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PolylineWithName> polyLineList = new ArrayList<>();

                    while (stmt.step()) {
                        PolylineWithName polylineWithName = new PolylineWithName();
                        polylineWithName.setGid(stmt.column_string(0));
                        polylineWithName.setName(stmt.column_string(1));
                        polylineWithName.setLineColor(stmt.column_int(2));
                        polylineWithName.setLineWidth(stmt.column_int(3));


                        int multiGeometry = stmt.column_int(4);

                        polylineWithName.setMultiGeometry(multiGeometry);

                        Polyline line = new Polyline();
                        String wktStr = stmt.column_string(5);

                        wktStr = wktStr.replace("MULTILINESTRING", "").replace("(", "").replace(")", "");

                        String[] points = wktStr.split(",");

                        int length = points.length;

                        for (int i = 0; i < length; i++) {
                            String[] xy = points[i].split(" ");

                            double longitude = Double.parseDouble(xy[0]);
                            double latitude = Double.parseDouble(xy[1]);

                            LatLngInfo pMer;


                            if (isGoogleMap) {
                                LatLngInfo latLngInfo = CoordinateUtils.gps84_To_Gcj02(latitude, longitude);
                                pMer = CoordinateUtils.lonLatToMercator(latLngInfo.longitude, latLngInfo.latitude);
                            } else {

                                pMer = new LatLngInfo(latitude, longitude);
                            }

                            if (i == 0) {
                                line.startPath(new Point(pMer.longitude, pMer.latitude));
                            } else {
                                line.lineTo(new Point(pMer.longitude, pMer.latitude));
                            }


                           /* if (multiGeometry == 0) {//连续线段（0）

                                if (i == 0) {
                                    line.startPath(new Point(pMer.longitude, pMer.latitude));
                                } else {
                                    line.lineTo(new Point(pMer.longitude, pMer.latitude));
                                }

                            } else if (multiGeometry == 1) {//非连续线段（1）
                                if (i % 2 == 0) {
                                    line.startPath(new Point(pMer.longitude, pMer.latitude));
                                } else {
                                    line.lineTo(new Point(pMer.longitude, pMer.latitude));
                                }

                            }*/
                        }

                        polylineWithName.setPolyline(line);
                        polyLineList.add(polylineWithName);

                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(true, polyLineList, null);
                    }
                } catch (java.lang.Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 获取所有自绘制的线信息
     *
     * @param callback
     */
    public void getAllPolylines(final onExecResult callback) {
        getPolylinesByFieldId("DPLS", callback);
    }

    public void getPolygons(final Envelope env, final String fileids, final onExecResult callback) {
        final String sql = "SELECT GID,NAME,LINECOLOR,FILLCOLOR,ASWKT(GEOMETRYS) FROM POLYGONS " +
                " WHERE ROWID IN (SELECT pkid FROM idx_POLYGONS_Geometrys " +
                " WHERE pkid MATCH RTreeIntersects(" + env.getXMin()
                + "," + env.getYMin() + "," + env.getXMax() + ","
                + env.getYMax() + ")) AND FILEID IN (" + (StringUtils.isEmptyOrNull(fileids) ? "''" : fileids) + ")";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PolygonWithName> polygons = new ArrayList<PolygonWithName>();
                    while (stmt.step()) {
                        PolygonWithName polygon = new PolygonWithName();
                        polygon.setGid(stmt.column_string(0));
                        polygon.setName(stmt.column_string(1));







//





                        polygon.setLineColor(stmt.column_int(2));
                        polygon.setFillColor(stmt.column_int(3));
                        Polygon line = new Polygon();
                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("POLYGON", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(",");
                        for (int i = 0; i < points.length; i++) {
                            String[] xy = points[i].split(" ");
                            if (i == 0) {
                                line.startPath(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                            } else {
                                line.lineTo(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                            }
                        }
                        polygon.setPolygon(line);
                        polygons.add(polygon);
                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(true, polygons, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 得到所有自绘的面信息
     *
     * @param callback
     */
    public void getAllPolygons(final onExecResult callback) {
        getPolygonsByFileId("DPGS", callback);
    }

    public void savePolygon(String gid, String name, int lineColor, int fillColor, Polygon polygon, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        int count = polygon.getPointCount();
        for (int i = 0; i < count; i++) {
            Point p = polygon.getPoint(i);
            sb.append(p.getX() + " " + p.getY() + ",");
        }
        if (count < 4) {
            Point p = polygon.getPoint(0);
            sb.append(p.getX() + " " + p.getY() + ",");
        }
        String xys = sb.toString();
        xys = xys.substring(0, xys.length() - 1);


        if (StringUtils.isEmptyOrNull(gid)) {
            ArrayList<String> args = new ArrayList<String>();
            args.add(SysUtils.newGUID());
            args.add(name);
            args.add(lineColor + "");
            args.add(fillColor + "");
            args.add("0");
            args.add("DPGS");
            execSql("insert into POLYGONS (GID, NAME, LINECOLOR,FILLCOLOR, STATE, FILEID, geometrys) values (?,?,?,?,?,?," +
                    "GeomFromText('POLYGON((" + xys + "))', 4326))", args, callback);
        }

        else {
            ArrayList<String> args = new ArrayList<>();
            args.add(name);
            args.add(lineColor + "");
            args.add(fillColor + "");
            args.add(gid);
            execSql("update POLYGONS set name=?,linecolor=?,fillcolor=?," +
                    "geometrys=GeomFromText('POLYGON((" + xys + "))', 4326)" +
                    " where gid=?", args, callback);
        }
    }

    public void delAllGeometrys(onExecResult callback) {
        ArrayList<String> dels = new ArrayList<String>();
        dels.add("delete from POINTS where fileid='DPS'");
        dels.add("delete from POLYLINES where fileid='DPLS'");
        dels.add("delete from POLYGONS where fileid='DPGS'");
        execSqls(dels, callback);
    }

    /**
     * 获取所有KML文件
     *
     * @param callback
     */
    public void getAllKMLFiles(final onExecResult callback) {
        final String sql = "SELECT ID, FILEID, FILENAME, IMPORTDATE FROM IMPORTFILES";
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<ImportFileEntity> kmlList = new ArrayList<ImportFileEntity>();
                    while (stmt.step()) {
                        ImportFileEntity importFileEntity = new ImportFileEntity();
                        importFileEntity.setId(stmt.column_string(0));
                        importFileEntity.setFileId(stmt.column_string(1));
                        importFileEntity.setFileName(stmt.column_string(2));
                        importFileEntity.setImportDate(stmt.column_string(3));
                        kmlList.add(importFileEntity);
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, kmlList, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        }).start();*/
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<ImportFileEntity> kmlList = new ArrayList<ImportFileEntity>();
                    while (stmt.step()) {
                        ImportFileEntity importFileEntity = new ImportFileEntity();
                        importFileEntity.setId(stmt.column_string(0));
                        importFileEntity.setFileId(stmt.column_string(1));
                        importFileEntity.setFileName(stmt.column_string(2));
                        importFileEntity.setImportDate(stmt.column_string(3));
                        kmlList.add(importFileEntity);
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, kmlList, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }


    public void getPointsByFileId(final String fileId, final onExecResult callback) {
       /* final String sql = "SELECT GID,NAME,SYMBOL,X(GEOMETRYS),Y(GEOMETRYS) FROM POINTS " +
                " WHERE FILEID='" + fileId + "'";*/
        final String sql = "SELECT POINTS.GID,POINTS.NAME,POINTS.description,altitude,POINTS.SYMBOL,X(POINTS.GEOMETRYS),Y(POINTS.GEOMETRYS),IMPORTFILES.FILENAME" +
                " FROM POINTS" + " LEFT JOIN IMPORTFILES ON POINTS.FILEID=IMPORTFILES.FILEID WHERE POINTS.FILEID='" + fileId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PointWithName> points = new ArrayList<>();
                    while (stmt.step()) {
                        PointWithName point = new PointWithName();
                        point.setGid(stmt.column_string(0));
                        point.setName(stmt.column_string(1));
                        point.setDescription(stmt.column_string(2));
                        String altitude=stmt.column_string(3);
                        if(altitude!=null){
                            point.setAltitude(Float.valueOf(altitude));
                        }
                        point.setSymbol(stmt.column_string(4));
                        point.setPoint(new Point(stmt.column_double(5), stmt.column_double(6)));
                        point.setFileId(fileId);
                        point.setFileName(stmt.column_string(7));
                        points.add(point);
                    }
                    stmt.close();
                    stmt = null;
                    if (callback != null) {
                        callback.execResultWithResult(!points.isEmpty(), points, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                }
            }
        });
    }


    public void getMercatorPolygonsByFiledId(String fileId, final onExecResult callback) {

        final String sql = "SELECT GID,NAME,LINECOLOR,FILLCOLOR,ASWKT(GEOMETRYS) FROM POLYGONS " +
                " WHERE FILEID='" + fileId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    List<List<LatLngInfo>> allPolygonsList = new ArrayList<>();

                    stmt = mDatabase.prepare(sql);
                    while (stmt.step()) {

                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("POLYGON", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(",");
                        allPolygonsList.add(getFilterMercatorPoints(points));
                    }

                    if (callback != null) {
                        callback.execResultWithResult(!allPolygonsList.isEmpty(), allPolygonsList, null);
                    }


                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }


    public void getPolygonsByFileId(String fileId, final onExecResult callback) {
        final String sql = "SELECT GID,NAME,LINECOLOR,FILLCOLOR,ASWKT(GEOMETRYS) FROM POLYGONS " +
                " WHERE FILEID='" + fileId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PolygonWithName> polygons = new ArrayList<>();
                    while (stmt.step()) {
                        PolygonWithName polygon = new PolygonWithName();
                        polygon.setGid(stmt.column_string(0));
                        polygon.setName(stmt.column_string(1));
                        polygon.setLineColor(stmt.column_int(2));
                        polygon.setFillColor(stmt.column_int(3));
                        Polygon line = new Polygon();
                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("POLYGON", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(",");

                        double tempLongitude = 0.0f;
                        double tempLatitude = 0.0f;

                        for (int i = 0; i < points.length; i++) {
                            String[] xy = points[i].split(" ");
                            double longitude = Double.parseDouble(xy[0]);
                            double latitude = Double.parseDouble(xy[1]);

                            if (i == 0) {
                                line.startPath(longitude, latitude);
                                tempLongitude = longitude;
                                tempLatitude = latitude;
                            } else if (i == points.length - 1) {
                                line.lineTo(longitude, latitude);
                            } else {
                                if (!(Math.abs(BigDecimalOperatorUtil.sub(longitude, tempLongitude)) < 0.0001f
                                        && Math.abs(BigDecimalOperatorUtil.sub(latitude, tempLatitude)) < 0.0001f)) {
                                    line.lineTo(longitude, latitude);
                                    tempLongitude = longitude;
                                    tempLatitude = latitude;
                                }
                            }
                        }
                        polygon.setPolygon(line);
                        polygons.add(polygon);
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, polygons, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }



    public void getMercatorPolylinesByFieldId(String fileId, final onExecResult callback) {

        final String sql = "SELECT GID,NAME,LINECOLOR,LINEWIDTH,ASWKT(GEOMETRYS) FROM POLYLINES " +
                " WHERE FILEID='" + fileId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    List<List<LatLngInfo>> allPolylinesList = new ArrayList<>();

                    while (stmt.step()) {

                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("MULTILINESTRING", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(",");

                        allPolylinesList.add(getFilterMercatorPoints(points));
                    }
                    if (callback != null) {
                        callback.execResultWithResult(!allPolylinesList.isEmpty(), allPolylinesList, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }


    public void getPolylinesByFieldId(String fileId, final onExecResult callback) {
        final String sql = "SELECT GID,NAME,LINECOLOR,LINEWIDTH,ASWKT(GEOMETRYS) FROM POLYLINES " +
                " WHERE FILEID='" + fileId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<PolylineWithName> polylines = new ArrayList<>();
                    while (stmt.step()) {
                        PolylineWithName polyline = new PolylineWithName();
                        polyline.setGid(stmt.column_string(0));
                        polyline.setName(stmt.column_string(1));
                        polyline.setLineColor(stmt.column_int(2));
                        polyline.setLineWidth(stmt.column_int(3));

                        Polyline line = new Polyline();
                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("MULTILINESTRING", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(",");
                        for (int i = 0; i < points.length; i++) {
                            String[] xy = points[i].split(" ");
                            if (i == 0) {
                                line.startPath(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                            } else {
                                line.lineTo(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                            }
                        }
                        polyline.setPolyline(line);
                        polylines.add(polyline);
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, polylines, null);
                    }
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    /**
     * 过滤不影响图形的点
     * @param points
     * @return
     */
    private List<LatLngInfo> getFilterMercatorPoints(String[] points) {

        double tempLongitude = 0.0f;
        double tempLatitude = 0.0f;

        List<LatLngInfo> mercatorList = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            String[] xy = points[i].split(" ");
            double longitude = Double.parseDouble(xy[0]);
            double latitude = Double.parseDouble(xy[1]);

            if (i == 0) {
                LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(longitude, latitude);
                mercatorList.add(mercator);

                tempLongitude = longitude;
                tempLatitude = latitude;

            } else if (i == points.length - 1) {

                LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(longitude, latitude);
                mercatorList.add(mercator);

            } else {
                if (!(Math.abs(BigDecimalOperatorUtil.sub(longitude, tempLongitude)) < 0.0001f
                        && Math.abs(BigDecimalOperatorUtil.sub(latitude, tempLatitude)) < 0.0001f)) {

                    LatLngInfo mercator = CoordinateUtils.gps84ToMapMercator(longitude, latitude);
                    mercatorList.add(mercator);

                    tempLongitude = longitude;
                    tempLatitude = latitude;
                }
            }
        }

        return mercatorList;
    }

    /**************以下操作为航点任务相关 Start***********/


    public void insertMission(Mission2 mission, onExecResult callback) {
        int geomType = 0;
        if (checkDatabaseStatus(callback)) {
            StringBuilder sb = new StringBuilder();
            if (mission.getPolygon() != null) {
                geomType = 2;
                int count = mission.getPolygon().getPointCount();
                for (int i = 0; i < count; i++) {
                    Point p = mission.getPolygon().getPoint(i);
                    String res = concatCoords(p);
                    sb.append(res);
                }
            } else if (mission.getPolyLine() != null) {
                geomType = 1;
                int count = mission.getPolyLine().getPointCount();
                for (int i = 0; i < count; i++) {
                    Point p = mission.getPolyLine().getPoint(i);
                    String res = concatCoords(p);
                    sb.append(res);
                }
            }
            String xys = "";
            try {
                xys = sb.substring(0, sb.length() - 1);
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            int iKey = 0;

            ArrayList<String> args = new ArrayList<>();
            args.add(mission.getId());
            args.add(mission.getBatchId());
            args.add(mission.getName());
            args.add(mission.getSnapshot());
            args.add(String.valueOf(mission.isFixedAltitude()));
            args.add(String.valueOf(mission.getAltitude()));
            args.add(String.valueOf(mission.getFlySpeed()));
            args.add(String.valueOf(mission.getGimbalAngle()));
            args.add(String.valueOf(mission.getResolutionRate()));
            args.add(String.valueOf(geomType));
            args.add(String.valueOf(mission.getSideOverlap()));
            args.add(String.valueOf(mission.getRouteOverlap()));
            args.add(DateHelperUtils.getSystemTime());
            args.add(DateHelperUtils.getSystemTime());
            args.add(null);
            args.add(String.valueOf(mission.getPointIndex()));
            args.add(mission.getWorkMode());
            args.add(mission.getWorkStep());
            args.add(String.valueOf(mission.getFlightNum()));
            args.add(String.valueOf(mission.getStartPhotoIndex()));
            args.add(String.valueOf(mission.getEndPhotoIndex()));
            args.add(String.valueOf(mission.getFlyAngle()));
            args.add(String.valueOf(mission.getMinAltitude()));
            args.add(String.valueOf(mission.getFlyingLayer()));
            args.add(String.valueOf(mission.getRotating()));
            args.add(String.valueOf(mission.getReturnMode()));
            args.add(String.valueOf(mission.getFixedAltitudeList()));
            args.add(String.valueOf(mission.getBuffer()));
            args.add(String.valueOf(mission.getStartPoint()));
            args.add(String.valueOf(mission.getEndPoint()));
            args.add(String.valueOf(mission.getBaseLineHeight()));
            args.add(String.valueOf(mission.getEntryHeight()));
            args.add(String.valueOf(mission.isPolygon()));

            Hashtable<String, String> htSQL = new Hashtable<>();

            Hashtable<String, ArrayList<String>> htArgs = new Hashtable<>();
            String missionSQL = null;
            if (geomType == 2)
                missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, baseline_height, entry_height, is_polygon) values (?,?,?,?,0,?,?,?,?,?,?," + "GeomFromText('POLYGON((" + xys + "))', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            else if (geomType == 1)
                missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, baseline_height, entry_height, is_polygon) values (?,?,?,?,0,?,?,?,?,?,?," + "GeomFromText('LINESTRING(" + xys + ")', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            else {
                callback.execResult(false, "polygon or polyline is null !");
                return;
            }

            htSQL.put(String.valueOf(iKey), missionSQL);
            htArgs.put(String.valueOf(iKey++), args);

            execSqls(htSQL, htArgs, callback);
        }
    }

    private String concatCoords(Point point) {
        String xStr = String.valueOf(point.getX());
        if (xStr.contains("E") || xStr.contains("e")) {
            BigDecimal bd = new BigDecimal(xStr);
            xStr = bd.toPlainString();
        }
        String yStr = String.valueOf(point.getY());
        if (yStr.contains("E") || yStr.contains("e")) {
            BigDecimal bd = new BigDecimal(yStr);
            yStr = bd.toPlainString();
        }
        return xStr + " " + yStr + ",";
    }

    /**
     * @param geomType
     * @param missionBatch
     * @param mission
     * @param callback
     */
    public void insertMission2(int geomType, MissionBatch2 missionBatch, Mission2 mission, onExecResult callback) {

        if (mDatabase == null) {
            if (callback != null)
                callback.execResult(false, "数据库没打开");
            return;
        }

        String xys = "";
        try {
            StringBuilder sb = new StringBuilder();
            int count;
            switch (geomType) {
                case 0:
                    count = mission.getMultiPoint().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getMultiPoint().getPoint(i);
                        sb.append(concatCoords(p));
                    }
                    break;
                case 1:
                    count = mission.getPolyLine().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getPolyLine().getPoint(i);
                        sb.append(concatCoords(p));
                    }
                    break;
                case 2:
                    count = mission.getPolygon().getPointCount();
                    for (int i = 0; i < count; i++) {
                        Point p = mission.getPolygon().getPoint(i);
                        String res = concatCoords(p);
                        sb.append(res);
                    }
                    if (count < 4) {
                        Point p = mission.getPolygon().getPoint(0);
                        String res = concatCoords(p);
                        sb.append(res);
                    }
                    break;
            }
            xys = sb.substring(0, sb.length() - 1);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }

        int iKey = 0;

        ArrayList<String> args = new ArrayList<>();
        args.add(mission.getId());
        args.add(missionBatch.getId());
        args.add(mission.getName());
        args.add(mission.getSnapshot());
        args.add(String.valueOf(mission.getStatus()));
        args.add(String.valueOf(mission.isFixedAltitude()));
        args.add(String.valueOf(mission.getAltitude()));
        args.add(String.valueOf(mission.getFlySpeed()));
        args.add(String.valueOf(mission.getGimbalAngle()));
        args.add(String.valueOf(mission.getResolutionRate()));
        args.add(String.valueOf(geomType));
        args.add(String.valueOf(mission.getSideOverlap()));
        args.add(String.valueOf(mission.getRouteOverlap()));
        args.add(DateHelperUtils.format(mission.getCreateDate()));
        args.add(mission.getStartTime() != null ? DateHelperUtils.format(mission.getStartTime()) : DateHelperUtils.getSystemTime());
        args.add(DateHelperUtils.format(mission.getEndTime()));
        args.add(String.valueOf(mission.getPointIndex()));
        args.add(mission.getWorkMode());
        args.add(mission.getWorkStep());
        args.add(String.valueOf(mission.getFlightNum()));
        args.add(String.valueOf(mission.getStartPhotoIndex()));
        args.add(String.valueOf(mission.getEndPhotoIndex()));
        args.add(String.valueOf(mission.getFlyAngle()));
        args.add(String.valueOf(mission.getMinAltitude()));
        args.add(String.valueOf(mission.getFlyingLayer()));
        args.add(String.valueOf(mission.getRotating()));
        args.add(String.valueOf(mission.getReturnMode()));
        args.add(String.valueOf(mission.getFixedAltitudeList()));
        args.add(String.valueOf(mission.getBuffer()));
        args.add(String.valueOf(mission.getStartPoint()));
        args.add(String.valueOf(mission.getEndPoint()));
        args.add(String.valueOf(mission.getBaseLineHeight()));
        args.add(String.valueOf(mission.getEntryHeight()));
        args.add(String.valueOf(mission.isPolygon()));

        ArrayList<String> batchArgs = new ArrayList<>();
        batchArgs.add(missionBatch.getId());
        batchArgs.add(missionBatch.getName());
        batchArgs.add(String.valueOf(missionBatch.getStatus()));
        batchArgs.add(missionBatch.getSnapShot());
        batchArgs.add(String.valueOf(missionBatch.getSideOverlap()));
        batchArgs.add(String.valueOf(missionBatch.getRouteOverlap()));
        batchArgs.add(missionBatch.getCreateDate() != null ? DateHelperUtils.format(missionBatch.getCreateDate()) : DateHelperUtils.getSystemTime());
        batchArgs.add(String.valueOf(missionBatch.getAltitude()));
        batchArgs.add(String.valueOf(missionBatch.getResolutionRate()));
        batchArgs.add(String.valueOf(missionBatch.getFlightNum()));
        batchArgs.add(missionBatch.getWorkMode());
        batchArgs.add(String.valueOf(missionBatch.isFixedAltitude()));
        batchArgs.add(String.valueOf(missionBatch.getBuffer()));
        batchArgs.add(String.valueOf(missionBatch.getCreateUserId()));
        batchArgs.add(String.valueOf(missionBatch.isCloud()));

        Hashtable<String, String> htSQL = new Hashtable<>();

        Hashtable<String, ArrayList<String>> htArgs = new Hashtable<>();
        String missionSQL;
        if (geomType == 2)
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, baseline_height, entry_height, is_polygon) values (?,?,?,?,?,?,?,?,?,?,?," + "GeomFromText('POLYGON((" + xys + "))', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        else if (geomType == 1)
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, baseline_height, entry_height, is_polygon) values (?,?,?,?,?,?,?,?,?,?,?," + "GeomFromText('LINESTRING(" + xys + ")', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        else
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, baseline_height, entry_height, is_polygon) values (?,?,?,?,?,?,?,?,?,?,?," + "GeomFromText('MULTIPOINT(" + xys + ")', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String missionBatchSQL = "insert into MissionBatch2 (ID, name,status,snap_shot,side_overlap,route_overlap,create_date,altitude,resolution_rate,flight_num,work_mode,fixed_altitude,fly_buffer, id_create_user, is_cloud) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        htSQL.put(String.valueOf(iKey), missionBatchSQL);
        htArgs.put(String.valueOf(iKey++), batchArgs);
        htSQL.put(String.valueOf(iKey), missionSQL);
        htArgs.put(String.valueOf(iKey++), args);

        execSqls(htSQL, htArgs, callback);
    }

    /**
     * 保存接收任务
     *
     * @param callback
     */
    public void saveTransmissionMission(ExportMission exportMission, onExecResult callback) {

        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }

        Mission2 mission = exportMission.getMission();
        mission.setId(SysUtils.newGUID());
        mission.setBatchId(SysUtils.newGUID());
        int geomType = mission.getGeomType();

        int iKey = 0;

        ArrayList<String> args = new ArrayList<>();
        args.add(mission.getId());
        args.add(mission.getBatchId());
        args.add(mission.getName());
        args.add(mission.getSnapshot());
        args.add(String.valueOf(mission.isFixedAltitude()));
        args.add(String.valueOf(mission.getAltitude()));
        args.add(String.valueOf(mission.getFlySpeed()));
        args.add(String.valueOf(mission.getGimbalAngle()));
        args.add(String.valueOf(mission.getResolutionRate()));
        args.add(String.valueOf(geomType));
        args.add(String.valueOf(mission.getSideOverlap()));
        args.add(String.valueOf(mission.getRouteOverlap()));
        args.add(DateHelperUtils.format(mission.getCreateDate()));
        args.add(DateHelperUtils.format(mission.getStartTime()));
        args.add(DateHelperUtils.format(mission.getEndTime()));
        args.add(String.valueOf(mission.getPointIndex()));
        args.add(mission.getWorkMode());
        args.add(mission.getWorkStep());
        args.add(String.valueOf(mission.getFlightNum()));
        args.add(String.valueOf(mission.getStartPhotoIndex()));
        args.add(String.valueOf(mission.getEndPhotoIndex()));
        args.add(String.valueOf(mission.getFlyAngle()));
        args.add(String.valueOf(mission.getMinAltitude()));
        args.add(String.valueOf(mission.getFlyingLayer()));
        args.add(String.valueOf(mission.getRotating()));
        args.add(String.valueOf(mission.getReturnMode()));
        args.add(String.valueOf(mission.getFixedAltitudeList()));
        args.add(String.valueOf(mission.getBuffer()));
        args.add(String.valueOf(mission.getStartPoint()));
        args.add(String.valueOf(mission.getEndPoint()));
        args.add(String.valueOf(mission.isPolygon()));

        ArrayList<String> batchArgs = new ArrayList<>();
        batchArgs.add(mission.getBatchId());
        batchArgs.add(mission.getName());
        batchArgs.add(mission.getSnapshot());
        batchArgs.add(String.valueOf(mission.getSideOverlap()));
        batchArgs.add(String.valueOf(mission.getRouteOverlap()));
        batchArgs.add(DateHelperUtils.format(mission.getCreateDate()));
        batchArgs.add(String.valueOf(mission.getAltitude()));
        batchArgs.add(String.valueOf(mission.getResolutionRate()));
        batchArgs.add(String.valueOf(mission.getFlightNum()));
        batchArgs.add(mission.getWorkMode());
        batchArgs.add(String.valueOf(mission.isFixedAltitude()));
        batchArgs.add(String.valueOf(mission.getBuffer()));

        Hashtable<String, String> htSQL = new Hashtable<>();

        Hashtable<String, ArrayList<String>> htArgs = new Hashtable<>();
        String missionSQL;
        String xys = exportMission.getGeometry();
        if (geomType == 2)
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, is_polygon) values (?,?,?,?,0,?,?,?,?,?,?," + "GeomFromText('POLYGON((" + xys + "))', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        else if (geomType == 1)
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, is_polygon) values (?,?,?,?,0,?,?,?,?,?,?," + "GeomFromText('LINESTRING(" + xys + ")', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        else
            missionSQL = "insert into Mission2 (ID, batch_id, name, snap_shot, status, fixed_altitude, altitude, fly_speed, gimbal_angle, resolution_rate, geom_type, geometry, side_overlap, route_overlap, create_date, start_time, end_time, point_index, work_mode, work_step, flight_num, start_photo_index, end_photo_index, fly_angle, min_altitude, flying_layer, rotating, return_mode, fixed_altitude_list, fly_buffer, start_point, end_point, is_polygon) values (?,?,?,?,0,?,?,?,?,?,?," + "GeomFromText('MULTIPOINT(" + xys + ")', 4326),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String missionBatchSQL = "insert into MissionBatch2 (ID, name,status,snap_shot,side_overlap,route_overlap,create_date,altitude,resolution_rate,flight_num,work_mode,fixed_altitude,fly_buffer) values (?,?,0,?,?,?,?,?,?,?,?,?,?)";

        htSQL.put(String.valueOf(iKey), missionBatchSQL);
        htArgs.put(String.valueOf(iKey++), batchArgs);
        htSQL.put(String.valueOf(iKey), missionSQL);
        htArgs.put(String.valueOf(iKey++), args);

        execSqls(htSQL, htArgs, callback);
    }


    public void deleteMissionBatchById(String batchId, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }
        ArrayList<String> args = new ArrayList<>();
        args.add(batchId);
        String missionSQL = "delete from MissionBatch2 where id=?";
        execSql(missionSQL, args, callback);
    }

    public void deleteMissionById(String missionId, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }
        ArrayList<String> args = new ArrayList<>();
        args.add(missionId);
        String missionSQL = "delete from Mission2 where id=?";
        execSql(missionSQL, args, callback);
    }

    public void deleteMissionByBatchId(String batchId, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没有打开");
            }
            return;
        }
        ArrayList<String> args = new ArrayList<>();
        args.add(batchId);
        String sql = "delete from mission2 where batch_id=?";
        execSql(sql, args, callback);
    }

    public void updateMissionBatchStatus(String missionBatchId, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(1));
            args.add(missionBatchId);
            String sql = "update MissionBatch2 set status=? where ID=?";
            execSql(sql, args, callback);
        }
    }

    public void updateMissionStartInfo2(String missionId, int startPhotoIndex, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(startPhotoIndex));
            args.add(DateHelperUtils.getSystemTime());
            args.add(missionId);
            String sql = "update Mission2 set start_photo_index=?,start_time=? where ID=?";
            execSql(sql, args, callback);
        }
    }

    public void updateMissionEndInfo2(String missionId, int endPhotoIndex, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(endPhotoIndex));
            args.add(DateHelperUtils.getSystemTime());
            args.add(missionId);
            String sql = "update Mission2 set end_photo_index=?,end_time=?,status=1 where ID=?";
            execSql(sql, args, callback);
        }
    }

    public void updateMissionExecuteStatus(String id, int status) {
        try {
            if (checkDatabaseStatus(null)) {
                ArrayList<String> args = new ArrayList<>();
                args.add(status + "");
                args.add(id);
                String sql = "update Mission2 set status = ? where ID = ?";
                execSql(sql, args, null);
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePhotoNum(String missionId, long photoNum, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(photoNum));
            args.add(DateHelperUtils.getSystemTime());
            args.add(missionId);
            String sql = "update Mission2 set end_photo_index=?,end_time=? where ID=?";
            execSql(sql, args, callback);
        }
    }

    public void updateMissionPointIndex(String missionId, int pointIndex, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(pointIndex));
            args.add(missionId);
            String sql = "update Mission2 set point_index=? where ID=?";
            execSql(sql, args, callback);
        }
    }

    public void updateMissionBatchName(String batchId, String name, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(name);
            args.add(batchId);
            String sql = "update MissionBatch2 set name=? where ID=?";
            String sql2 = "update Mission2 set name=? where batch_id=?";
            execSql(sql, args, callback);
            execSql(sql2, args, callback);
        }
    }

    private boolean checkDatabaseStatus(onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return false;
        }
        return true;
    }


    public void insertMissionPhoto(MissionPhoto missionPhoto, onExecResult callback) {
        if (mDatabase == null) {
            if (callback != null) {
                callback.execResult(false, "数据库没打开");
            }
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (null != missionPhoto.getPoint()) {
            sb.append(missionPhoto.getPoint().getX() + " " + missionPhoto.getPoint().getY());
        }
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(missionPhoto.getPhotoIndex()));
        args.add(missionPhoto.getMissionId());
        args.add(String.valueOf(missionPhoto.getPhotoIndex()));
        args.add(DateHelperUtils.getSystemTime());

        String missionSQL = "insert into MissionPhoto(id, MissionBatchId, PhotoIndex, CreateDate, geometrys) values (?,?,?,?," +
                "GeomFromText('POINT(" + sb.toString() + ")', 4326))";

        execSql(missionSQL, args, callback);
    }


    public void deleteMissionPhoto(String missionBatchId, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(missionBatchId);
            String missionSQL = "delete from MissionPhoto where missionBatchId=?";
            execSql(missionSQL, args, callback);
        }
    }

    /**
     * 修改状态
     *
     * @param callback
     */
    public void updateMissionPhoto(String missionPhotoId, String fileName, onExecResult callback) {
        if (checkDatabaseStatus(callback)) {
            ArrayList<String> args = new ArrayList<>();
            args.add(String.valueOf(fileName));
            args.add(missionPhotoId);
            String missionSQL = "update MissionPhoto set photoPath=? where id=?";
            execSql(missionSQL, args, callback);
        }
    }


    public void getAllMissionBatch2(String mode, final onExecResult callback) {
        final String sql;
        if (mode == null)
            sql = "select * from MissionBatch2";
        else
            sql = "select * from MissionBatch2 where work_mode like '" + mode + "%'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<MissionBatch2> missionBatchList = new ArrayList<>();
                    while (stmt.step()) {
                        MissionBatch2 missionBatch = new MissionBatch2();
                        missionBatch.setId(stmt.column_string(0));
                        missionBatch.setName(stmt.column_string(1));
                        missionBatch.setStatus(stmt.column_int(2));
                        missionBatch.setSnapShot(stmt.column_string(3));
                        missionBatch.setSideOverlap(stmt.column_int(4));
                        missionBatch.setRouteOverlap(stmt.column_int(5));
                        missionBatch.setCreateDate(DateHelperUtils.string2DateTime(stmt.column_string(6)));
                        missionBatch.setAltitude(stmt.column_int(7));
                        missionBatch.setResolutionRate(stmt.column_double(8));
                        missionBatch.setFlightNum(stmt.column_int(9));
                        missionBatch.setWorkMode(stmt.column_string(10));
                        missionBatch.setFixedAltitude(Boolean.parseBoolean(stmt.column_string(11)));
                        missionBatch.setBuffer(stmt.column_int(12));
                        missionBatch.setCreateUserId(stmt.column_int(13));
                        missionBatch.setCloud(Boolean.parseBoolean(stmt.column_string(14)));
                        missionBatchList.add(missionBatch);
                    }
                    if (callback != null) {
                        Collections.reverse(missionBatchList);
                        callback.execResultWithResult(true, missionBatchList, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    public void getAllMissionBatchId(String mode, final onExecResult callback) {
        final String sql;
        if (mode == null)
            sql = "select ID from MissionBatch2 order by create_date desc";
        else
            sql = "select ID from MissionBatch2 where work_mode like '" + mode + "%' order by create_date desc";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<String> missionBatchList = new ArrayList<>();
                    while (stmt.step()) {
                        missionBatchList.add(stmt.column_string(0));
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, missionBatchList, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }


    private Polygon getPolygon(String wktStr) {
        if (wktStr == null || "NULL".equals(wktStr))
            return null;
        Polygon polygon = new Polygon();
        try {
            wktStr = wktStr.replace("POLYGON", "").replace("(", "").replace(")", "");
            String[] points = wktStr.split(",");
            for (int i = 0; i < points.length; i++) {
                String[] xy = points[i].split(" ");
                if (i == 0) {
                    polygon.startPath(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                } else {
                    polygon.lineTo(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                }
            }
        } catch (java.lang.Exception e) {

        }
        return polygon;
    }

    private Polyline getPolyline(String wktStr) {
        if (wktStr == null || "NULL".equals(wktStr))
            return null;
        Polyline polyline = new Polyline();
        try {
            wktStr = wktStr.replace("LINESTRING", "").replace("(", "").replace(")", "");
            String[] points = wktStr.split(",");
            for (int i = 0; i < points.length; i++) {
                String[] xy = points[i].split(" ");
                if (i == 0) {
                    polyline.startPath(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                } else {
                    polyline.lineTo(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
                }
            }
        } catch (java.lang.Exception e) {

        }
        return polyline;
    }

    private MultiPoint getMultiPoint(String wktStr) {
        if (wktStr == null || "NULL".equals(wktStr))
            return null;
        MultiPoint multiPoint = new MultiPoint();
        try {
            wktStr = wktStr.replace("MULTIPOINT", "").replace("(", "").replace(")", "");
            String[] points = wktStr.split(",");
            for (int i = 0; i < points.length; i++) {
                String[] xy = points[i].split(" ");
                multiPoint.add(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
            }
        } catch (java.lang.Exception e) {

        }
        return multiPoint;
    }

    public void getMissionBatchById(final String batchId, final onExecResult callback) {
        final String sql = "select name,status,snap_shot,side_overlap,route_overlap,create_date,altitude,resolution_rate,flight_num,work_mode,fixed_altitude,fly_buffer,id_create_user,is_cloud from MissionBatch2 where ID ='" + batchId + "'";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    MissionBatch2 missionBatch = new MissionBatch2();
                    while (stmt.step()) {
                        missionBatch.setId(batchId);
                        missionBatch.setName(stmt.column_string(0));
                        missionBatch.setStatus(stmt.column_int(1));
                        missionBatch.setSnapShot(stmt.column_string(2));
                        missionBatch.setSideOverlap(stmt.column_int(3));
                        missionBatch.setRouteOverlap(stmt.column_int(4));
                        missionBatch.setCreateDate(DateHelperUtils.string2DateTime(stmt.column_string(5)));
                        missionBatch.setAltitude(stmt.column_int(6));
                        missionBatch.setResolutionRate(stmt.column_double(7));
                        missionBatch.setFlightNum(stmt.column_int(8));
                        missionBatch.setWorkMode(stmt.column_string(9));
                        missionBatch.setFixedAltitude(Boolean.parseBoolean(stmt.column_string(10)));
                        missionBatch.setBuffer(stmt.column_int(stmt.column_int(11)));
                        missionBatch.setCreateUserId(stmt.column_int(12));
                        missionBatch.setCloud(Boolean.parseBoolean(stmt.column_string(13)));
                    }
                    if (callback != null)
                        callback.execResultWithResult(true, missionBatch, null);
                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    public void getMissionListByBatchId(String batchId, final onExecResult callback) {
        final String sql = "select ID,batch_id,name,snap_shot,status,fixed_altitude,altitude,fly_speed,gimbal_angle,resolution_rate,geom_type,ASWKT(geometry),side_overlap,route_overlap,create_date,start_time,end_time,point_index,work_mode,work_step,flight_num,start_photo_index,end_photo_index,fly_angle,min_altitude,flying_layer,rotating,return_mode,fixed_altitude_list,fly_buffer,start_point,end_point,baseline_height,entry_height,is_polygon from Mission2 where batch_id ='" + batchId + "' order by create_date desc";
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<Mission2> missionList = new ArrayList<>();
                    while (stmt.step()) {
                        Mission2 mission = new Mission2();
                        mission.setId(stmt.column_string(0));
                        mission.setBatchId(stmt.column_string(1));
                        mission.setName(stmt.column_string(2));
                        mission.setSnapshot(stmt.column_string(3));
                        mission.setStatus(stmt.column_int(4));
                        mission.setFixedAltitude(Boolean.parseBoolean(stmt.column_string(5)));
                        mission.setAltitude(stmt.column_int(6));
                        mission.setFlySpeed(stmt.column_int(7));
                        mission.setGimbalAngle(stmt.column_int(8));
                        mission.setResolutionRate(stmt.column_double(9));
                        mission.setGeomType(stmt.column_int(10));
                        if (stmt.column_int(10) == 0)
                            mission.setMultiPoint(getMultiPoint(stmt.column_string(11)));
                        else if (stmt.column_int(10) == 1)
                            mission.setPolyLine(getPolyline(stmt.column_string(11)));
                        else if (stmt.column_int(10) == 2) stmt.column_string(11);
                        mission.setPolygon(getPolygon(stmt.column_string(11)));
                        mission.setSideOverlap(stmt.column_int(12));
                        mission.setRouteOverlap(stmt.column_int(13));
                        mission.setCreateDate(DateHelperUtils.string2DateTime(stmt.column_string(14)));
                        mission.setStartTime(DateHelperUtils.string2DateTime(stmt.column_string(15)));
                        mission.setEndTime(DateHelperUtils.string2DateTime(stmt.column_string(16)));
                        mission.setPointIndex(stmt.column_int(17));
                        mission.setWorkMode(stmt.column_string(18));
                        mission.setWorkStep(stmt.column_string(19));
                        mission.setFlightNum(stmt.column_int(20));
                        mission.setStartPhotoIndex(stmt.column_int(21));
                        mission.setEndPhotoIndex(stmt.column_int(22));
                        mission.setFlyAngle(stmt.column_int(23));
                        mission.setMinAltitude(stmt.column_int(24));
                        mission.setFlyingLayer(stmt.column_int(25));
                        mission.setRotating(stmt.column_double(26));
                        mission.setReturnMode(stmt.column_int(27));
                        mission.setFixedAltitudeList(stmt.column_string(28));
                        mission.setBuffer(stmt.column_int(29));
                        mission.setStartPoint(stmt.column_int(30));
                        mission.setEndPoint(stmt.column_int(31));
                        mission.setBaseLineHeight(stmt.column_int(32));
                        mission.setEntryHeight(stmt.column_int(33));
                        mission.setIsPolygon(stmt.column_int(34));
                        missionList.add(mission);
                    }
                    if (callback != null) {
                        callback.execResultWithResult(true, missionList, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    /**
     * 获取当前任务的所有照片
     *
     * @param callback
     */
    public void getMissionPhotoByBatch(String missionBatchId, final onExecResult callback) {
        final String sql = "SELECT id,MISSIONBATCHID, PHOTOINDEX,PhotoPath, CREATEDATE "
                + "FROM MissionPhoto where MissionBatchId='" + missionBatchId
                + "' order by createdate asc";
        Logger.d("mmmm" + sql);
        mPool.submit(new Runnable() {
            @Override
            public void run() {
                Stmt stmt = null;
                try {
                    stmt = mDatabase.prepare(sql);
                    ArrayList<MissionPhoto> missionPhotoList = new ArrayList<>();
                    while (stmt.step()) {
                        MissionPhoto missionPhoto = new MissionPhoto();
                        missionPhoto.setId(stmt.column_string(0));
                        missionPhoto.setMissionId(stmt.column_string(1));
                        missionPhoto.setPhotoIndex(stmt.column_int(2));
                        missionPhoto.setPhotoPath(stmt.column_string(3));
                        missionPhoto.setCreateDate(DateHelperUtils.string2DateTime(stmt.column_string(4)));

                        /*Point point = new Point();
                        String wktStr = stmt.column_string(4);
                        wktStr = wktStr.replace("Point", "").replace("(", "").replace(")", "");
                        String[] points = wktStr.split(" ");
                        point.setX(Double.parseDouble(points[0]));
                        point.setY(Double.parseDouble(points[1]));
                        missionPhoto.setPoint(point);*/

                        missionPhotoList.add(missionPhoto);
                    }
                    log.i("missionPhotoList==" + missionPhotoList.size());
                    if (callback != null) {
                        callback.execResultWithResult(true, missionPhotoList, null);
                    }

                } catch (Exception e) {
                    log.e(e.getMessage());
                    if (callback != null) {
                        callback.execResult(false, e.getMessage());
                    }
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        stmt = null;
                    }
                }
            }
        });
    }

    /********航点任务相关 End**********/
}
