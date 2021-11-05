package com.ew.autofly.struct.view;

import com.ew.autofly.entity.Tower;

import java.util.List;



public interface IBaseTowerMapView extends IBaseMapView{

    /**
     * 获取选中的杆塔
     * @return
     */
    List<Tower> getSelectedTowers();

  
    void showImportTowerDialog();

  
    void showChoseTowerDialog();

  
    void showTowerDetailDialog(Tower tower);

  
    void showUpdaterTowerNoDialog(Tower tower);

  
    void importTowerGridLine();

  
    void removeTowerGridLine();

    
    void clearTower();

  
    void clearTowerLayer();

  
    void refreshDrawTowerGriLines();

  
    void refreshAddTowerGridLine(boolean isMoveToCenter);

    
    void drawTowerGridLine(List<Tower> towerList) ;

    
    void drawTowerGridLineAltitude(List<Tower> towerList);

  
    void saveNewTowerGridLine(String lineName, String voltage, String groupName);

  
    void saveUpdateTowerGridLine(String lineName);

}
