package com.ew.autofly.constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AirLineConstant {


    public static final String DIR_AIRLINE_DATA = "AirLineData";

    public static final String DIR_AIRLINE_TYPE_PLAN = "Plan";
    public static final String DIR_AIRLINE_TYPE_STUDY = "Study";
    public static final String DIR_AIRLINE_TOWER_MODEL = DIR_AIRLINE_DATA + File.separator + "TowerModel";

    public static final String DIR_AIRLINE_DATA_AIRROUTE = "AirRoute";
    public static final String DIR_AIRLINE_DATA_ZIP = "Zip";
    public static final String DIR_AIRLINE_DATA_TEMP = "Temp";
    public static final String DIR_AIRLINE_DATA_EXPORT = "Export";

    public static final String DIR_AIRLINE_MODE_NOTRTK = "NotRtk";

    public static void createFolder(String root) {
        for (String dir : airLineChildDir()) {
            File d = new File(root + dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }

        for (String dir : airLineNoRtkChildDir()) {
            File d = new File(root + dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }
        File d = new File(root + DIR_AIRLINE_TOWER_MODEL);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    private static List<String> airLineChildDir() {
        List<String> dir = new ArrayList<>();
        List<String> parentDirList = airLineTypeDir();
        for (String parentDir : parentDirList) {
            List<String> childDirList = getChildDir(parentDir);
            dir.addAll(childDirList);
        }
        return dir;
    }

    private static List<String> airLineNoRtkChildDir() {

        List<String> dir = new ArrayList<>();
        List<String> parentDirList = airLineTypeDir();
        for (String parentDir : parentDirList) {
            List<String> childDirList = getChildDir(parentDir + File.separator + DIR_AIRLINE_MODE_NOTRTK);
            dir.addAll(childDirList);
        }
        return dir;
    }

    private static List<String> airLineTypeDir() {
        List<String> dir = new ArrayList<>();
        dir.add(DIR_AIRLINE_DATA + File.separator + DIR_AIRLINE_TYPE_PLAN);
        dir.add(DIR_AIRLINE_DATA + File.separator + DIR_AIRLINE_TYPE_STUDY);
        return dir;
    }

    private static List<String> getChildDir(String parentDir) {
        List<String> dir = new ArrayList<>();
        dir.add(parentDir + File.separator + DIR_AIRLINE_DATA_AIRROUTE);
        dir.add(parentDir + File.separator + DIR_AIRLINE_DATA_ZIP);
        dir.add(parentDir + File.separator + DIR_AIRLINE_DATA_TEMP);
        dir.add(parentDir + File.separator + DIR_AIRLINE_DATA_EXPORT);
        return dir;
    }


}
