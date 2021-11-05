package com.ew.autofly.utils.io.excel;

import android.content.Context;

import com.ew.autofly.R;
import com.ew.autofly.entity.Tower;
import com.flycloud.autofly.base.util.StringUtils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class ExcelLoader {
    private Context mContext;
    private ArrayList<String> inserts = new ArrayList<>();
    private String fileId = "";

    public ExcelLoader(Context context) {
        this.mContext = context;
    }

    public void export03Excel(List<Tower> towerList) throws Exception {
        String[] columnNames = mContext.getResources().getStringArray(R.array.tower_column);
        int columnNum = columnNames.length;
        int size = towerList.size();
        HSSFWorkbook hwb = new HSSFWorkbook();
        Tower tower;
        HSSFSheet sheet = hwb.createSheet("sheet_test");
        HSSFRow firstRow = sheet.createRow(0);
        HSSFCell[] firstCell = new HSSFCell[columnNum];

        for (int i = 0; i < columnNum; i++) {
            firstCell[i] = firstRow.createCell(i);
            firstCell[i].setCellValue(new HSSFRichTextString(columnNames[i]));
        }
        for (int i = 0; i < size; i++) {
            HSSFRow row = sheet.createRow(i + 1);
            tower = towerList.get(i);
            for (int col = 0; col <= 4; col++) {
                HSSFCell lineNo = row.createCell(0);
                lineNo.setCellValue(tower.getGridLineName());
                HSSFCell voltage = row.createCell(1);
                voltage.setCellValue(tower.getVoltage());
                HSSFCell manageGroup = row.createCell(2);
                manageGroup.setCellValue(tower.getManageGroup());
                HSSFCell towerNo = row.createCell(3);
                towerNo.setCellValue(tower.getTowerNo());
                HSSFCell longitude = row.createCell(4);
                longitude.setCellValue(tower.getLongitude());
                HSSFCell latitude = row.createCell(5);
                latitude.setCellValue(tower.getLatitude());
                HSSFCell towerAltitude = row.createCell(7);
                towerAltitude.setCellValue(tower.getTowerAltitude());
            }
        }
        OutputStream outputStream = new FileOutputStream("backup.xls");
        hwb.write(outputStream);
        outputStream.close();
        hwb.close();
    }










//






























    public List<Tower> read03Excel(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Tower tower;
        List<Tower> list = new ArrayList<>();
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
                tower = new Tower();
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

                if (!StringUtils.isEmptyOrNull(fileId))
                    inserts.add("insert into points (GID, NAME, SYMBOL, STATE, FILEID, geometrys) values ('"
                            + StringUtils.newGUID() + "','" + tower.getTowerNo() + "','0','0','" + fileId + "'," +
                            "GeomFromText('POINT(" + tower.getLongitude() + " " + tower.getLatitude() + ")', 4326))");
                list.add(tower);
            }
        }
        hssfWorkbook.close();
        return list;
    }






















//





//





//





//





//





//
















    @SuppressWarnings("deprecation")
    private String getHSSFValue(HSSFCell cell) {
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return cell.getStringCellValue();
        }
    }











    public List<String> load03Excel(String filePath, String fileId) {
        this.fileId = fileId;
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
                    String name, lon, lat, alti;
                    if (hssfRow == null) {
                        continue;
                    }

                    HSSFCell towerNo = hssfRow.getCell(3);
                    if (towerNo == null) {
                        continue;
                    }
                    name = getHSSFValue(towerNo);

                    HSSFCell longitude = hssfRow.getCell(4);
                    if (longitude == null) {
                        continue;
                    }
                    lon = getHSSFValue(longitude);

                    HSSFCell latitude = hssfRow.getCell(5);
                    if (latitude == null) {
                        continue;
                    }
                    lat = getHSSFValue(latitude);

                    HSSFCell altitude = hssfRow.getCell(7);
                    if (altitude == null) {

                        alti = "0";
                    } else {
                        alti = getHSSFValue(altitude);
                    }

                    if (!StringUtils.isEmptyOrNull(fileId))
                        inserts.add("insert into points (GID, NAME, SYMBOL, STATE, FILEID, geometrys,altitude) values ('"
                                + StringUtils.newGUID() + "','" + name + "','0','0','" + fileId + "'," +
                                "GeomFromText('POINT(" + lon + " " + lat + ")', 4326)" + ",'" + alti + "')");
                }
            }
            is.close();
            hssfWorkbook.close();
        } catch (IOException e) {
        }
        return inserts;
    }









//











//





//





//





//












}