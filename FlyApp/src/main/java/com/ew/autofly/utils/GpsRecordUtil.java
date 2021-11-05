package com.ew.autofly.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.ew.autofly.constant.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class GpsRecordUtil extends SQLiteOpenHelper {
    private static GpsRecordUtil mInstance = null;
    public static final String DATABASE_NAME = "GpsTrack.gpx";
    public static final int VERSION = 1;
    private LogUtilsOld log = null;
    private ExecutorService mPool = null;

    private GpsRecordUtil(Context context) {
        super(context, getMyDatabaseName(context), null, VERSION);
        mPool = Executors.newSingleThreadExecutor();
        log = LogUtilsOld.getInstance(context).setTag("GpsRecordUtil");
    }

    public static GpsRecordUtil getInstance(Context context) {
        if (mInstance == null) {
            synchronized (GpsRecordUtil.class) {
                if (mInstance == null) {
                    mInstance = new GpsRecordUtil(context);
                }
            }
        }
        return mInstance;
    }

    private static String getMyDatabaseName(Context context) {
        return IOUtils.getRootStoragePath(context) + AppConstant.DIR_RECORD + File.separator + DATABASE_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE G_ROUTES (G_ROUTE_ID varchar(32) NOT NULL,G_ROUTE_NAME varchar(128)," +
                "G_ROUTE_CREATER varchar(20) DEFAULT NULL,G_ROUTE_CREATE_TIME number(13),G_STATE int(2),PRIMARY KEY (G_ROUTE_ID))");
        db.execSQL("CREATE TABLE G_ROUTE_POINTS (G_ID INTEGER PRIMARY KEY AUTOINCREMENT,G_X double(12,9) DEFAULT NULL," +
                "G_Y double(12,9) DEFAULT NULL,G_ROUTE_ID varchar(32) DEFAULT NULL,G_DATE number(13)," +
                "G_Z double(12,2) DEFAULT NULL)");
        log.i("创建GPS库");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * 新增GPS记录,自动记录模式
     *
     * @param trackName
     * @return
     */
    public String insertNewGpsTracklog(String trackName) {
        SQLiteDatabase db = getWritableDatabase();
        String id = StringUtils.newGUID();
        try {
            db.execSQL("insert into G_ROUTES (G_ROUTE_ID,G_ROUTE_NAME,G_ROUTE_CREATER,G_ROUTE_CREATE_TIME,G_STATE) " +
                    "values ('" + id + "','" + trackName + "','auto'," + System.currentTimeMillis() + ",'0')");
            return id;
        } catch (Exception ex) {
            log.e(ex.getMessage());
            return "";
        }
    }

    public void insertNewPoint(String recordId, double x, double y, double z) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL("insert into G_ROUTE_POINTS (G_ROUTE_ID,G_X,G_Y,G_Z,G_DATE) " +
                    "values ('" + recordId + "'," + x + "," + y + "," + z + "," + System.currentTimeMillis() + ")");
        } catch (Exception ex) {
            log.e(ex.getMessage());
        }
    }

    public JSONArray getAllLocalGpsRecords() {
        String sql = "SELECT G_ROUTE_ID,G_ROUTE_NAME,G_ROUTE_CREATER,G_ROUTE_CREATE_TIME,G_STATE FROM G_ROUTES" +
                " WHERE G_ROUTE_CREATER='auto' ORDER BY G_ROUTE_CREATE_TIME DESC";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        JSONArray jsonArray = new JSONArray();
        try {
            while (cursor.moveToNext()) {
                JSONObject j = new JSONObject();
                try {
                    j.put("G_ROUTE_ID", cursor.getString(0));
                    j.put("G_ROUTE_NAME", cursor.getString(1));
                    j.put("G_ROUTE_CREATER", cursor.getString(2));
                    j.put("G_ROUTE_CREATE_TIME", cursor.getString(3));
                    j.put("G_STATE", cursor.getString(4));
                    jsonArray.put(j);
                } catch (JSONException e) {
                    log.e(e.getMessage());
                }
            }
        } catch (Exception ex) {
            log.e(ex.getMessage());
        } finally {
            cursor.close();
            db.close();
        }
        return jsonArray;
    }

    public Polyline getLineById(String id) {
        String sql = "SELECT G_X,G_Y FROM G_ROUTE_POINTS" + " WHERE G_ROUTE_ID='" + id + "' ORDER BY G_DATE";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Point> ps = new ArrayList<Point>();
        try {
            while (cursor.moveToNext()) {
                Point p = new Point(cursor.getDouble(0), cursor.getDouble(1));
                ps.add(p);
            }
        } catch (Exception ex) {
            log.e(ex.getMessage());
            return null;
        } finally {
            cursor.close();
            db.close();
        }
        int size = ps.size();
        if (size < 2) {
            return null;
        }
        Polyline line = new Polyline();
        line.startPath(ps.get(0));
        for (int i = 1; i < size; i++) {
            line.lineTo(ps.get(i));
        }
        return line;
    }

    public boolean delTrackLogById(String id) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("delete from G_ROUTES where G_ROUTE_ID='" + id + "'");
            db.execSQL("delete from G_ROUTE_POINTS where G_ROUTE_ID='" + id + "'");
            db.setTransactionSuccessful();
            db.endTransaction();
            return true;
        } catch (Exception ex) {
            db.endTransaction();
            log.e(ex.getMessage());
            return false;
        }
    }
}
