package com.ew.autofly.db;



import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ew.autofly.db.dao.DaoMaster;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.internal.DaoConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBMigrationHelper {
    private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";

    public void onUpgrade(SQLiteDatabase db, Class<? extends AbstractDao<?, ?>>... daoClasses) {

        if (daoClasses.length < 1) {
            return;
        }

        Database database = new StandardDatabase(db);
        generateTempTables(database, daoClasses);
        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, true);


        restoreData(database, daoClasses);
    }

    private boolean dropAllTables(Database db, boolean b, Class<? extends AbstractDao<?, ?>>... daoClasses) {

        return reflectMethod(db, "dropTable", b, daoClasses);

    }

    private boolean createAllTables(Database db, boolean b, Class<? extends AbstractDao<?, ?>>... daoClasses) {

        return reflectMethod(db, "createTable", b, daoClasses);

    }

    
    private static boolean reflectMethod(Database db, String methodName, boolean isExists, @NonNull Class<? extends AbstractDao<?, ?>>... daoClasses) {

        boolean flag = true;

        for (Class cls : daoClasses) {

            try {

                Method method = cls.getDeclaredMethod(methodName, Database.class, boolean.class);
                method.invoke(null, db, isExists);

            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }

    private void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {

        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {

            try {
                DaoConfig daoConfig = new DaoConfig(db, daoClass);
                String divider = "";
                String tableName = daoConfig.tablename;
                String tempTableName = daoConfig.tablename.concat("_TEMP");
                ArrayList<String> properties = new ArrayList<>();
                List<String> columns = getColumns(db, tableName);

                StringBuilder dropTableStringBuilder = new StringBuilder();
                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");
                db.execSQL(dropTableStringBuilder.toString());

                StringBuilder createTableStringBuilder = new StringBuilder();
                createTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");

                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    if (columns.contains(columnName)) {
                        properties.add(columnName);
                        String type = null;
                        try {
                            type = getTypeByClass(daoConfig.properties[j].type);
                        } catch (Exception exception) {

                        }
                        createTableStringBuilder.append(divider).append(columnName).append(" ").append(type);
                        if (daoConfig.properties[j].primaryKey) {
                            createTableStringBuilder.append(" PRIMARY KEY");
                        }
                        divider = ",";
                    }
                }
                createTableStringBuilder.append(");");
                db.execSQL(createTableStringBuilder.toString());
                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tableName).append(";");
                db.execSQL(insertTableStringBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void restoreData(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {

        for (Class<? extends AbstractDao<?, ?>> daoClass : daoClasses) {

            try {
                DaoConfig daoConfig = new DaoConfig(db, daoClass);
                String tableName = daoConfig.tablename;
                String tempTableName = daoConfig.tablename.concat("_TEMP");
                ArrayList<String> properties = new ArrayList();
                List<String> columns = getColumns(db, tempTableName);
                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;

                    if (!columns.contains(columnName)) {
                        StringBuilder insertTableStringBuilder = new StringBuilder();
                        insertTableStringBuilder.append("ALTER TABLE ").append(tempTableName)
                                .append(" ADD COLUMN ").append(columnName).append(" ")
                                .append(getTypeByClass(daoConfig.properties[j].type));
                        db.execSQL(insertTableStringBuilder.toString());
                    }
                    properties.add(columnName);
                }

                StringBuilder insertTableStringBuilder = new StringBuilder();

                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";");

                StringBuilder dropTableStringBuilder = new StringBuilder();


                dropTableStringBuilder.append("DROP TABLE IF EXISTS ").append(tempTableName).append(";");





                db.execSQL(insertTableStringBuilder.toString());
                db.execSQL(dropTableStringBuilder.toString());

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    private String getTypeByClass(Class<?> type) throws Exception {
        if (type.equals(String.class)) {
            return "TEXT";
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return "INTEGER DEFAULT 0";
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            return "LONG DEFAULT 0";
        }
        if (type.equals(Boolean.class)) {
            return "BOOLEAN";
        }

        Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(type.toString()));
        throw exception;
    }

    private static List<String> getColumns(Database db, String tableName) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
            if (cursor != null) {
                columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
            }
        } catch (Exception e) {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return columns;
    }

}
