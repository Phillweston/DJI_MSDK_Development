package com.ew.autofly.struct.presenter;

import android.content.Context;

import com.ew.autofly.constant.AppConstant;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.entity.geometry.GeoPoint;
import com.ew.autofly.struct.presenter.interfaces.IBaseTowerMapPresenter;
import com.ew.autofly.struct.view.IBaseTowerMapView;
import com.ew.autofly.utils.IOUtils;
import com.ew.autofly.utils.io.excel.ExcelHelper;
import com.ew.autofly.utils.io.excel.JxlExcelHelper;
import com.ew.autofly.utils.io.file.FileInfo;
import com.ew.autofly.utils.io.file.FileUtils;
import com.ew.autofly.utils.io.kml.KmlHelper;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseTowerMapPresenterImpl<V extends IBaseTowerMapView>
        extends BaseMapPresenterImpl<V> implements IBaseTowerMapPresenter {

    /**
     * Tower子类，扩展功能
     *
     * @return
     */
    protected Tower subTowerClass(Tower tower) {
        return null;
    }


    @Override
    public List<Tower> loadTowerListByKml(FileInfo fileInfo) {
        List<Tower> towers = new ArrayList<>();
        KmlHelper loader = new KmlHelper();
        loader.loadKml(fileInfo.getFilePath());
        List<GeoPoint> geoPoints = loader.getGeoPoints();

        for (GeoPoint geoPoint : geoPoints) {
            Tower tower = new Tower();
            tower.setLongitude(geoPoint.getPoint().getX());
            tower.setLatitude(geoPoint.getPoint().getY());
            tower.setAltitude((float) geoPoint.getPoint().getZ());
            tower.setTowerId(geoPoint.getGid());
            tower.setTowerNo(geoPoint.getName());
            tower.setGridLineName(fileInfo.getFileNameWithoutSuffix());
            tower.setDescription(geoPoint.getDescription());

            try {
                String reserve1 = geoPoint.getReserve1();
                float towerTopAltitude = Float.valueOf(reserve1);
                tower.setTopAltitude(towerTopAltitude);
            } catch (Exception e) {

            }

            Tower subTower = subTowerClass(tower);
            if (subTower != null) {
                towers.add(subTower);
            } else {
                towers.add(tower);
            }
        }
        return towers;
    }

    @Override
    public List<Tower> loadTowerListByExcel(FileInfo fileInfo) {
        List<Tower> towers = new ArrayList<>();
        List<Tower> loadTowers = ExcelHelper.loadTowers(fileInfo.getFilePath());
        if (loadTowers != null) {
            for (Tower tower : loadTowers) {
                Tower subTower = subTowerClass(tower);
                if (subTower != null) {
                    towers.add(subTower);
                } else {
                    towers.add(tower);
                }
            }
        }
        return towers;
    }

    @Override
    public synchronized boolean saveTowerListToKml(Context context, List<Tower> towerList, String lineName, boolean isNewFile) {

        String path = IOUtils.getRootStoragePath(context) + AppConstant.DIR_TOWER_KML +
                File.separator + lineName + ".kml";

        if (!checkCreateNewFile(path, isNewFile)) {
            return false;
        }

        return KmlHelper.saveTowerKml(context, path, towerList);
    }

    @Override
    public synchronized boolean saveTowerListToExcel(Context context, List<Tower> towerList, String lineName, boolean isNewFile) {

        String path = IOUtils.getRootStoragePath(context) + AppConstant.DIR_TOWER_EXCEL +
                File.separator + lineName + ".xls";

        if (!checkCreateNewFile(path, isNewFile)) {
            return false;
        }

        String[] fieldName = new String[]{"gridLineName", "voltage", "manageGroup", "towerNo", "longitude", "latitude", "towerAltitude", "altitude"};
        String[] title = new String[]{"线路名称", "电压等级", "管理班组", "杆塔编号", "经度", "纬度", "杆塔高度", "海拔高度"};

        try {
            JxlExcelHelper excelHelper = JxlExcelHelper.getInstance(path);
            excelHelper.writeExcel(Tower.class, towerList, fieldName, title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean checkCreateNewFile(String path, boolean isNewFile) {
        if (isNewFile && FileUtils.checkFileExist(path)) {
            showToastDialog("已存在相同命名的文件，请重新命名");
            return false;
        }

        if (FileUtils.checkFileExist(path)) {
            boolean isDeleteSuccess = IOUtils.delete(path);
            if (!isDeleteSuccess) {
                return false;
            }
        }
        return true;
    }
}
