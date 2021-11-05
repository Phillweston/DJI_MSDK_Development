package com.ew.autofly.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyCamera;


/** 
 * DAO for table "FLY_CAMERA".
*/
public class FlyCameraDao extends AbstractDao<FlyCamera, String> {

    public static final String TABLENAME = "FLY_CAMERA";

    /**
     * Properties of entity FlyCamera.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property CreateTime = new Property(1, java.util.Date.class, "createTime", false, "CREATE_TIME");
        public final static Property FocalLength = new Property(2, float.class, "focalLength", false, "FOCAL_LENGTH");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Iso = new Property(4, int.class, "iso", false, "ISO");
        public final static Property Aperture = new Property(5, int.class, "aperture", false, "APERTURE");
        public final static Property ExposureCompensation = new Property(6, int.class, "exposureCompensation", false, "EXPOSURE_COMPENSATION");
        public final static Property ShutterSpeed = new Property(7, int.class, "shutterSpeed", false, "SHUTTER_SPEED");
        public final static Property ImageWidth = new Property(8, int.class, "imageWidth", false, "IMAGE_WIDTH");
        public final static Property ImageHeight = new Property(9, int.class, "imageHeight", false, "IMAGE_HEIGHT");
        public final static Property OpticalFormat = new Property(10, float.class, "opticalFormat", false, "OPTICAL_FORMAT");
        public final static Property OpticalFormatHeight = new Property(11, float.class, "opticalFormatHeight", false, "OPTICAL_FORMAT_HEIGHT");
        public final static Property ImgType = new Property(12, String.class, "imgType", false, "IMG_TYPE");
        public final static Property SensorHeight = new Property(13, float.class, "sensorHeight", false, "SENSOR_HEIGHT");
        public final static Property SensorWidth = new Property(14, float.class, "sensorWidth", false, "SENSOR_WIDTH");
        public final static Property MinInterval = new Property(15, float.class, "minInterval", false, "MIN_INTERVAL");
        public final static Property IsCustom = new Property(16, boolean.class, "isCustom", false, "IS_CUSTOM");
        public final static Property IsCustomSelected = new Property(17, boolean.class, "isCustomSelected", false, "IS_CUSTOM_SELECTED");
    }


    public FlyCameraDao(DaoConfig config) {
        super(config);
    }
    
    public FlyCameraDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"FLY_CAMERA\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"CREATE_TIME\" INTEGER," + // 1: createTime
                "\"FOCAL_LENGTH\" REAL NOT NULL ," + // 2: focalLength
                "\"NAME\" TEXT NOT NULL ," + // 3: name
                "\"ISO\" INTEGER NOT NULL ," + // 4: iso
                "\"APERTURE\" INTEGER NOT NULL ," + // 5: aperture
                "\"EXPOSURE_COMPENSATION\" INTEGER NOT NULL ," + // 6: exposureCompensation
                "\"SHUTTER_SPEED\" INTEGER NOT NULL ," + // 7: shutterSpeed
                "\"IMAGE_WIDTH\" INTEGER NOT NULL ," + // 8: imageWidth
                "\"IMAGE_HEIGHT\" INTEGER NOT NULL ," + // 9: imageHeight
                "\"OPTICAL_FORMAT\" REAL NOT NULL ," + // 10: opticalFormat
                "\"OPTICAL_FORMAT_HEIGHT\" REAL NOT NULL ," + // 11: opticalFormatHeight
                "\"IMG_TYPE\" TEXT," + // 12: imgType
                "\"SENSOR_HEIGHT\" REAL NOT NULL ," + // 13: sensorHeight
                "\"SENSOR_WIDTH\" REAL NOT NULL ," + // 14: sensorWidth
                "\"MIN_INTERVAL\" REAL NOT NULL ," + // 15: minInterval
                "\"IS_CUSTOM\" INTEGER NOT NULL ," + // 16: isCustom
                "\"IS_CUSTOM_SELECTED\" INTEGER NOT NULL );");
      
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_FLY_CAMERA_ID_CREATE_TIME_DESC ON \"FLY_CAMERA\"" +
                " (\"ID\" ASC,\"CREATE_TIME\" DESC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"FLY_CAMERA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FlyCamera entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(2, createTime.getTime());
        }
        stmt.bindDouble(3, entity.getFocalLength());
        stmt.bindString(4, entity.getName());
        stmt.bindLong(5, entity.getIso());
        stmt.bindLong(6, entity.getAperture());
        stmt.bindLong(7, entity.getExposureCompensation());
        stmt.bindLong(8, entity.getShutterSpeed());
        stmt.bindLong(9, entity.getImageWidth());
        stmt.bindLong(10, entity.getImageHeight());
        stmt.bindDouble(11, entity.getOpticalFormat());
        stmt.bindDouble(12, entity.getOpticalFormatHeight());
 
        String imgType = entity.getImgType();
        if (imgType != null) {
            stmt.bindString(13, imgType);
        }
        stmt.bindDouble(14, entity.getSensorHeight());
        stmt.bindDouble(15, entity.getSensorWidth());
        stmt.bindDouble(16, entity.getMinInterval());
        stmt.bindLong(17, entity.getIsCustom() ? 1L: 0L);
        stmt.bindLong(18, entity.getIsCustomSelected() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FlyCamera entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        java.util.Date createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindLong(2, createTime.getTime());
        }
        stmt.bindDouble(3, entity.getFocalLength());
        stmt.bindString(4, entity.getName());
        stmt.bindLong(5, entity.getIso());
        stmt.bindLong(6, entity.getAperture());
        stmt.bindLong(7, entity.getExposureCompensation());
        stmt.bindLong(8, entity.getShutterSpeed());
        stmt.bindLong(9, entity.getImageWidth());
        stmt.bindLong(10, entity.getImageHeight());
        stmt.bindDouble(11, entity.getOpticalFormat());
        stmt.bindDouble(12, entity.getOpticalFormatHeight());
 
        String imgType = entity.getImgType();
        if (imgType != null) {
            stmt.bindString(13, imgType);
        }
        stmt.bindDouble(14, entity.getSensorHeight());
        stmt.bindDouble(15, entity.getSensorWidth());
        stmt.bindDouble(16, entity.getMinInterval());
        stmt.bindLong(17, entity.getIsCustom() ? 1L: 0L);
        stmt.bindLong(18, entity.getIsCustomSelected() ? 1L: 0L);
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public FlyCamera readEntity(Cursor cursor, int offset) {
        FlyCamera entity = new FlyCamera( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // createTime
            cursor.getFloat(offset + 2), // focalLength
            cursor.getString(offset + 3), // name
            cursor.getInt(offset + 4), // iso
            cursor.getInt(offset + 5), // aperture
            cursor.getInt(offset + 6), // exposureCompensation
            cursor.getInt(offset + 7), // shutterSpeed
            cursor.getInt(offset + 8), // imageWidth
            cursor.getInt(offset + 9), // imageHeight
            cursor.getFloat(offset + 10), // opticalFormat
            cursor.getFloat(offset + 11), // opticalFormatHeight
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // imgType
            cursor.getFloat(offset + 13), // sensorHeight
            cursor.getFloat(offset + 14), // sensorWidth
            cursor.getFloat(offset + 15), // minInterval
            cursor.getShort(offset + 16) != 0, // isCustom
            cursor.getShort(offset + 17) != 0 // isCustomSelected
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FlyCamera entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setCreateTime(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setFocalLength(cursor.getFloat(offset + 2));
        entity.setName(cursor.getString(offset + 3));
        entity.setIso(cursor.getInt(offset + 4));
        entity.setAperture(cursor.getInt(offset + 5));
        entity.setExposureCompensation(cursor.getInt(offset + 6));
        entity.setShutterSpeed(cursor.getInt(offset + 7));
        entity.setImageWidth(cursor.getInt(offset + 8));
        entity.setImageHeight(cursor.getInt(offset + 9));
        entity.setOpticalFormat(cursor.getFloat(offset + 10));
        entity.setOpticalFormatHeight(cursor.getFloat(offset + 11));
        entity.setImgType(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSensorHeight(cursor.getFloat(offset + 13));
        entity.setSensorWidth(cursor.getFloat(offset + 14));
        entity.setMinInterval(cursor.getFloat(offset + 15));
        entity.setIsCustom(cursor.getShort(offset + 16) != 0);
        entity.setIsCustomSelected(cursor.getShort(offset + 17) != 0);
     }
    
    @Override
    protected final String updateKeyAfterInsert(FlyCamera entity, long rowId) {
        return entity.getId();
    }
    
    @Override
    public String getKey(FlyCamera entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FlyCamera entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
