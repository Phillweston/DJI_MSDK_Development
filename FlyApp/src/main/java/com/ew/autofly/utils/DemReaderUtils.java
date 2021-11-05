package com.ew.autofly.utils;

import android.content.Context;

import com.esri.core.geometry.Point;
import com.ew.autofly.entity.Bound;
import com.ew.autofly.entity.LatLngInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Set;


public class DemReaderUtils {
    private static DemReaderUtils mInstance = null;
    protected LogUtilsOld log = null;

    private final double NO_Z_VALUE=Double.MAX_VALUE;

    /**
     * Key:file path;
     * value:dem infos;
     */
    private HashMap<String, DemInfo> mDemInfos;

    private class DemInfo {
        private int mFileType = 0;
        private int mVersion = 0;
        private double mUndefineOffset = 0;
        private int mScale = 0;
        private double mXllcorner = 0;
        private double mYllcorner = 0;
        private int mRows = 0;
        private int mCols = 0;
        private double mCellsize = 0;

        private int mNoDataValue = 0;
        private int mMaxValue = 0;
        private int mMinValue = 0;

        private long mFileLength = 0;

        private Bound mBound = null;

        private RandomAccessFile mRandomFile = null;

        private void resetVars() {
            if (mRandomFile != null) {
                try {
                    mRandomFile.close();
                } catch (IOException e) {
                    log.e(e.getMessage());
                }
            }
            mRandomFile = null;
            mFileLength = 0;
            mFileType = 0;
            mVersion = 0;
            mUndefineOffset = 0;
            mScale = 0;
            mXllcorner = 0;
            mYllcorner = 0;
            mRows = 0;
            mCols = 0;
            mCellsize = 0;
            mNoDataValue = 0;
            mMaxValue = 0;
            mMinValue = 0;

            mBound = null;
        }
    }

    private DemReaderUtils(Context context) {
        if (mDemInfos == null) {
            mDemInfos = new HashMap<String, DemInfo>();
        }
        log = LogUtilsOld.getInstance(context);
        log.setTag("DemReaderUtils");
    }

    public static DemReaderUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DemReaderUtils.class) {
                if (mInstance == null) {
                    mInstance = new DemReaderUtils(context.getApplicationContext());

                }
            }
        }
        return mInstance;
    }

    /**
     * 设置加载目录,获取所有tra文件的坐标范围
     *
     * @param fileDir
     */
    public void addDemFilePath(String fileDir) {
        if (mDemInfos!=null){
            mDemInfos.clear();
        }
        File dir = new File(fileDir);
        if (!dir.exists()) {
            return;
        }
        if (dir.isDirectory()) {
            File[] fileArray = dir.listFiles();
            if (null != fileArray) {
                for (int i = 0; i < fileArray.length; i++) {
                    String file = fileArray[i].getAbsolutePath();
                    if (!file.toLowerCase().endsWith("tra")) {
                        continue;
                    }
                    if (!mDemInfos.containsKey(file)) {
                        DemInfo info = readHeader(file);
                        if (info != null) {
                            mDemInfos.put(file, info);
                        }
                    }
                }
            }
        }
    }


    public void destroy() {
        if (mDemInfos != null) {
            Set<String> keys = mDemInfos.keySet();
            for (String key : keys) {
                mDemInfos.get(key).resetVars();
            }
            mDemInfos = null;
        }
        log = null;
        mInstance = null;
    }

    private DemInfo readHeader(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        echo("FilePath", fileName);
        DemInfo info = new DemInfo();
        try {
            info.mRandomFile = new RandomAccessFile(file, "r");
            info.mFileLength = info.mRandomFile.length();
            info.mRandomFile.seek(256);
            byte[] fileType = new byte[4];
            info.mRandomFile.read(fileType);
            info.mFileType = toInt(fileType);
            echo("FileType", info.mFileType);

            byte[] version = new byte[4];
            info.mRandomFile.read(version);
            info.mVersion = toInt(version);
            echo("Version", info.mVersion);

            byte[] undefineOffset = new byte[8];
            info.mRandomFile.read(undefineOffset);
            info.mUndefineOffset = toDouble(undefineOffset);
            echo("Undefine Offset", info.mUndefineOffset);

            byte[] scale = new byte[4];
            info.mRandomFile.read(scale);
            info.mScale = toInt(scale);
            echo("Scale", info.mScale);

            byte[] xllcorner = new byte[8];
            info.mRandomFile.read(xllcorner);
            info.mXllcorner = toDouble(xllcorner);
            echo("xllcorner", info.mXllcorner);

            byte[] yllcorner = new byte[8];
            info.mRandomFile.read(yllcorner);
            info.mYllcorner = toDouble(yllcorner);
            echo("yllcorner", info.mYllcorner);

            byte[] rows = new byte[4];
            info.mRandomFile.read(rows);
            info.mRows = toInt(rows);
            echo("Rows", info.mRows);

            byte[] cols = new byte[4];
            info.mRandomFile.read(cols);
            info.mCols = toInt(cols);
            echo("Cols", info.mCols);

            byte[] cellsize = new byte[8];
            info.mRandomFile.read(cellsize);
            info.mCellsize = toDouble(cellsize);
            echo("Cellsize", info.mCellsize);

            byte[] nodata = new byte[4];
            info.mRandomFile.read(nodata);
            info.mNoDataValue = toInt(nodata);
            echo("NoDataValue", info.mNoDataValue);

            info.mBound = new Bound(info.mXllcorner,
                    info.mYllcorner,
                    info.mXllcorner + info.mCellsize * info.mCols,
                    info.mYllcorner + info.mCellsize * info.mRows);
            return info;
        } catch (FileNotFoundException e) {
            log.e(e.getMessage());
            return null;
        } catch (IOException e) {
            log.e(e.getMessage());
            return null;
        }
    }

    public static double getLengthInWgs84(LatLngInfo p1, LatLngInfo p2) {
        double radLat1 = p1.latitude * Math.PI / 180.0;
        double radLat2 = p2.latitude * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double b = p1.longitude * Math.PI / 180.0 - p2.longitude * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static double getLengthInWgs84(Point p1, Point p2) {
        return getLengthInWgs84(new LatLngInfo(p1.getX(), p1.getY()), new LatLngInfo(p2.getX(), p2.getY()));
    }

    public boolean checkZValue(double zValue){
        return zValue!=NO_Z_VALUE;
    }

    public double getZValue(LatLngInfo point) {
        if (mDemInfos == null || mDemInfos.size() == 0) {
            return NO_Z_VALUE;
        }
        Set<String> keys = mDemInfos.keySet();
        DemInfo info = null;
        for (String key : keys) {
            if (mDemInfos.get(key).mBound.contain(point)) {
                info = mDemInfos.get(key);
                break;
            }
        }

        if (info == null) {
            return NO_Z_VALUE;
        }

        int col = (int) ((point.longitude - info.mXllcorner) / info.mCellsize);
        int row = info.mRows - (int) ((point.latitude - info.mYllcorner) / info.mCellsize);
        if (row > info.mRows || col > info.mCols) {
            return NO_Z_VALUE;
        }
        if (info.mRandomFile == null) {
            return NO_Z_VALUE;
        }
        try {
            int position = 312 + 4 * (row * info.mCols + col);
            if (position > info.mFileLength) {
                return NO_Z_VALUE;
            }
            info.mRandomFile.seek(position);
            byte[] z = new byte[4];
            info.mRandomFile.read(z);
            double alt = toInt(z) / 100.0d;
            if (alt == 0.0 || alt == -9999) {
                byte[] z1 = new byte[4];
                info.mRandomFile.read(z1);
                return toInt(z1) / 100.0d;
            }
            return alt;
        } catch (IOException e) {
            log.e(e.getMessage());
            return NO_Z_VALUE;
        }

    }

    private void echo(String tag, Object o) {
        log.i(tag + ": " + o);
    }

    private double toDouble(byte[] b) {
        long l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[1] << 8);
        l &= 0xffff;
        l |= ((long) b[2] << 16);
        l &= 0xffffff;
        l |= ((long) b[3] << 24);
        l &= 0xffffffffl;
        l |= ((long) b[4] << 32);
        l &= 0xffffffffffl;
        l |= ((long) b[5] << 40);
        l &= 0xffffffffffffl;
        l |= ((long) b[6] << 48);
        l &= 0xffffffffffffffl;
        l |= ((long) b[7] << 56);
        return Double.longBitsToDouble(l);
    }

    private int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }
}
