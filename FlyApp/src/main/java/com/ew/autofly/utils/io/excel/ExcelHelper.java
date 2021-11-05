package com.ew.autofly.utils.io.excel;

import com.ew.autofly.entity.Tower;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ExcelHelper {


    @SuppressWarnings("deprecation")
    public static String getHSSFValue(HSSFCell cell) {
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }

    public static List<Tower> loadTowers(String filePath) {
        List<Tower> towerList = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists())
            return null;
        try {
            InputStream is = new FileInputStream(filePath);
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);

            for (int i = 0, sheetNum = hssfWorkbook.getNumberOfSheets(); i < sheetNum; i++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(i);
                if (hssfSheet == null) {
                    continue;
                }
                for (int j = 1, rowNum = hssfSheet.getLastRowNum(); j <= rowNum; j++) {
                    HSSFRow hssfRow = hssfSheet.getRow(j);
                    if (hssfRow == null) {
                        continue;
                    }
                    Tower tower = new Tower();
                    HSSFCell gridLineName = hssfRow.getCell(0);
                    if (gridLineName == null) {
                        continue;
                    }
                    tower.setGridLineName(getHSSFValue(gridLineName));

                    HSSFCell voltage = hssfRow.getCell(1);
                    if (voltage == null) {
                        continue;
                    }
                    tower.setVoltage(getHSSFValue(voltage));

                    HSSFCell manageGroup = hssfRow.getCell(2);
                    if (manageGroup == null) {
                        continue;
                    }
                    tower.setManageGroup(getHSSFValue(manageGroup));

                    HSSFCell towerNo = hssfRow.getCell(3);
                    if (towerNo == null) {
                        continue;
                    }
                    tower.setTowerNo(getHSSFValue(towerNo));

                    HSSFCell longitude = hssfRow.getCell(4);
                    if (longitude == null) {
                        continue;
                    }
                    tower.setLongitude(Double.parseDouble(getHSSFValue(longitude)));

                    HSSFCell latitude = hssfRow.getCell(5);
                    if (latitude == null) {
                        continue;
                    }
                    tower.setLatitude(Double.parseDouble(getHSSFValue(latitude)));

                    HSSFCell towerAltitude = hssfRow.getCell(6);
                    if (towerAltitude == null) {
                        continue;
                    }
                    tower.setTowerAltitude((int) Float.parseFloat(getHSSFValue(towerAltitude)));

                    HSSFCell altitude = hssfRow.getCell(7);
                    if (altitude == null) {
                        continue;
                    }
                    tower.setAltitude(Float.parseFloat(getHSSFValue(altitude)));
                    towerList.add(tower);
                }
            }
            is.close();
            hssfWorkbook.close();
        } catch (IOException e) {
        }
        return towerList;
    }
}