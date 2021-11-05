package com.ew.autofly.struct.presenter.interfaces;

import android.content.Context;

import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.io.file.FileInfo;

import java.util.List;


public interface IBaseTowerMapPresenter extends IBaseMapPresenter{

    /**
     * 将kml文件转换为杆塔集
     * @param fileInfo
     * @return
     */
    List<Tower> loadTowerListByKml(FileInfo fileInfo);

    /**
     * 将excel文件转换为杆塔集
     * @param fileInfo
     * @return
     */
    List<Tower> loadTowerListByExcel(FileInfo fileInfo);

    /**
     *
     * 保存杆塔集到kmL文件
     * @param context
     * @param towerList
     * @param lineName 线路名称
     * @param isNewFile
     * @return
     */
    boolean saveTowerListToKml(Context context, List<Tower> towerList, String lineName, boolean isNewFile);

    /**
     *
     *保存杆塔集到excel文件
     * @param context
     * @param towerList
     * @param lineName 线路名称
     * @param isNewFile
     * @return
     */
    boolean saveTowerListToExcel(Context context, List<Tower> towerList, String lineName, boolean isNewFile);
}
