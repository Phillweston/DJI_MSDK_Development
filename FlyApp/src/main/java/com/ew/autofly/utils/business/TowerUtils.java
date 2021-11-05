package com.ew.autofly.utils.business;

import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.utils.coordinate.LocationCoordinateUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TowerUtils {

    /**
     * 存在相邻杆塔相同位置
     *
     * @param list
     * @return
     */
    public static boolean isExistAdjacentEqualPosition(List<Tower> list) {
        Tower tower = null;
        Iterator<Tower> iterator = list.iterator();
        while (iterator.hasNext()) {
            Tower next = iterator.next();
            if (tower != null) {
                if (tower.isEqualPosition(next)) {
                    return true;
                }
            }
            tower = next;
        }
        return false;
    }


    public static String getAutoIncreaseTowerNo(String previousTowerNo) {

        if (previousTowerNo == null) {
            return "";
        }

        try {

            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(previousTowerNo);

            String s_num = m.replaceAll("").trim();

            int num = Integer.parseInt(s_num);
            num++;

            int s_num_length = s_num.length();

            String result_num = String.format("%0" + s_num_length + "d", num);

            String letterRegEx = "[^A-Za-z]";
            Pattern letterP = Pattern.compile(letterRegEx);
            Matcher letterM = letterP.matcher(previousTowerNo);

            String s_letter = letterM.replaceAll("").trim();

            return s_letter + result_num;

        } catch (Exception e) {
            return "";
        }
    }
}
