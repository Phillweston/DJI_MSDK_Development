package com.ew.autofly.fragments;

import com.ew.autofly.entity.Tower;

import java.util.List;



public interface IBaseTowerView {


    void showImportTowerDialog();


    void showChoseTowerDialog();


    void showTowerDetailDialog(Tower tower);


    void showUpdaterTowerNoDialog(Tower tower);


    void importTowerGridLine();


    void clearTowerGridLine();


    void refreshDrawTowerGriLines();


    void refreshDrawTowerGriLinesAltitude();


    void refreshAddTowerGridLine(boolean isMoveToCenter);


    void repaintTaskPathByTower();

    
    void drawTowerGridLine(List<Tower> towerList, boolean isMoveToCenter) throws Throwable;

    
    void drawTowerGridLineAltitude(List<Tower> towerList);


    void saveNewTowerGridLine(String lineName, String voltage, String groupName);


    void saveUpdateTowerGridLine(String lineName);


}
