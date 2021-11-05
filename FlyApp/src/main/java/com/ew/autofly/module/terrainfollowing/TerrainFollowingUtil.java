package com.ew.autofly.module.terrainfollowing;

import android.content.Context;

import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.LocationCoordinate;
import com.ew.autofly.entity.WayPointTask;
import com.ew.autofly.utils.DemReaderUtils;
import com.ew.autofly.utils.DouglasPeuckerUtils;
import com.ew.autofly.xflyer.utils.LatLngUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



public class TerrainFollowingUtil {

    /**
     * 计算航点
     *
     * @param context
     * @param latLngInfoList
     * @return
     * @throws Exception
     */
    public static List<TerrainFollowCoordinate> calculate(Context context, List<WayPointTask> latLngInfoList, LatLngInfo homePoint, float altitude) throws Exception {

        List<TerrainFollowCoordinate> resultLocs = new ArrayList<>();

      
        final int disInterval = 5;

        DemReaderUtils demReader = DemReaderUtils.getInstance(context);
        double homeAltitude = demReader.getZValue(homePoint);
        if (!demReader.checkZValue(homeAltitude)) {
            return null;
        }

        for (int i = 0; i < latLngInfoList.size() - 1; i++) {

            List<TerrainFollowCoordinate> tfList = new ArrayList<>();

            LatLngInfo fromLocation = latLngInfoList.get(i).getPosition();
            LatLngInfo toLocation = latLngInfoList.get(i + 1).getPosition();

            float formHeading = (float) latLngInfoList.get(i).getHeadAngle();

            double distance = LatLngUtils.getDistance(fromLocation, toLocation);

            double fromY = demReader.getZValue(fromLocation);
            if (!demReader.checkZValue(fromY)) {
                return null;
            }
            double toY = demReader.getZValue(toLocation);
            if (!demReader.checkZValue(toY)) {
                return null;
            }

            List<DouglasPeuckerUtils.DPoint> allPoints = new ArrayList<>();
            allPoints.add(new DouglasPeuckerUtils.DPoint(0, fromY, 0));
            tfList.add(new TerrainFollowCoordinate(fromLocation.latitude, fromLocation.longitude, (float) fromY, formHeading, true));

            int count = (int) (distance / disInterval);
            if (count > 0) {
                double angle = LatLngUtils.getABAngle(fromLocation, toLocation);
                for (int j = 1; j <= count; j++) {
                    LatLngInfo pos = LatLngUtils.getLatlngByDA(fromLocation, disInterval * j, angle);
                    double y = demReader.getZValue(pos);
                    if (!demReader.checkZValue(y)) {
                        return null;
                    }
                    allPoints.add(new DouglasPeuckerUtils.DPoint(disInterval * j, y, j));
                    tfList.add(new TerrainFollowCoordinate(pos.latitude, pos.longitude, (float) y, 0, false));
                }
            }

            allPoints.add(new DouglasPeuckerUtils.DPoint(distance, toY, count + 1));
            tfList.add(new TerrainFollowCoordinate(toLocation.latitude, toLocation.longitude, (float) toY, 0, false));

          
            List<DouglasPeuckerUtils.DPoint> resultPoints = DouglasPeuckerUtils.compress(allPoints, (int) (altitude / 10));

            for (int n = 0; n < resultPoints.size(); n++) {

                if (i != 0 && n == 0) {
                    continue;
                }
              
                DouglasPeuckerUtils.DPoint resultPoint = resultPoints.get(n);
                TerrainFollowCoordinate loc = tfList.get(resultPoint.getIndex());
              
                loc.setAltitude((float) (loc.getAltitude() - homeAltitude + altitude));
                resultLocs.add(loc);
            }

        }

        return resultLocs;
    }

    public static class TerrainFollowCoordinate {
        private double latitude = 0.0D;
        private double longitude = 0.0D;
        private float altitude = 0.0F;
        private float heading;
        private boolean isCorner;

        public TerrainFollowCoordinate(double latitude, double longitude, float altitude, float heading, boolean isCorner) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.heading = heading;
            this.isCorner = isCorner;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public float getAltitude() {
            return altitude;
        }

        public void setAltitude(float altitude) {
            this.altitude = altitude;
        }

        public boolean isCorner() {
            return isCorner;
        }

        public void setCorner(boolean corner) {
            isCorner = corner;
        }

        public float getHeading() {
            return heading;
        }

        public void setHeading(float heading) {
            this.heading = heading;
        }
    }

}
