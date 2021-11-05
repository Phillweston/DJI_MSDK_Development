package com.ew.autofly.xflyer.utils;

import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.MarkerPosition;

import java.util.ArrayList;
import java.util.Hashtable;

public class GoogleMapHelperUtils {
    public static Hashtable<String, Object> FindTouchPosition(
            Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPosition,
            Hashtable<CommonConstants.MarkerFlag, Integer> htMarker, Point touchPoint, MapView mapView) {
        Point lt = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLatitude()));

        Point rb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLatitude()));

        Point rt = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLatitude()));

        Point lb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLatitude()));

        Point ct = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLatitude()));

        Point cr = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLatitude()));

        Point cb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLatitude()));

        Point cl = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLatitude()));





        boolean regionEdit = false;
        int touchMarker = 0;
        double dLT = ArcgisPointUtils.getDistance(lt, touchPoint);
        double dRB = ArcgisPointUtils.getDistance(rb, touchPoint);
        double dRT = ArcgisPointUtils.getDistance(rt, touchPoint);
        double dLB = ArcgisPointUtils.getDistance(lb, touchPoint);
        double dCT = ArcgisPointUtils.getDistance(ct, touchPoint);
        double dCR = ArcgisPointUtils.getDistance(cr, touchPoint);
        double dCB = ArcgisPointUtils.getDistance(cb, touchPoint);
        double dCL = ArcgisPointUtils.getDistance(cl, touchPoint);




        if (ArcgisPointUtils.isInTriangle(ct, cb, cl, touchPoint)) {




            if (dCT <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterTop)).intValue();
                regionEdit = true;
            } else if (dCB <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterBottom)).intValue();
                regionEdit = true;
            } else if (dCL <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterLeft)).intValue();
                regionEdit = true;
            }




        } else if (ArcgisPointUtils.isInTriangle(ct, cb, cr, touchPoint)) {




            if (dCT <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterTop)).intValue();
                regionEdit = true;
            } else if (dCB <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterBottom)).intValue();
                regionEdit = true;
            } else if (dCR <= 40.0D) {
                touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterRight)).intValue();
                regionEdit = true;
            }




        } else if (dCT <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterTop)).intValue();
            regionEdit = true;
        } else if (dCB <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterBottom)).intValue();
            regionEdit = true;
        } else if (dCR <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterRight)).intValue();
            regionEdit = true;
        } else if (dCL <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.CenterLeft)).intValue();
            regionEdit = true;
        } else if (dLT <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.LeftTop)).intValue();
            regionEdit = true;
        } else if (dLB <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.LeftBottom)).intValue();
            regionEdit = true;
        } else if (dRT <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.RightTop)).intValue();
            regionEdit = true;
        } else if (dRB <= 40.0D) {
            touchMarker = (htMarker.get(CommonConstants.MarkerFlag.RightBottom)).intValue();
            regionEdit = true;
        }

        Hashtable htResult = new Hashtable();
        htResult.put("RegionEdit", Boolean.valueOf(regionEdit));
        if (touchMarker != 0)
            htResult.put("TouchMarker", Integer.valueOf(touchMarker));
        return htResult;
    }

    /**
     *
     * @param mapView
     * @param pLB
     * @param pRB
     * @param pLT
     * @param pRT
     * @param homePoint  gps84
     * @param airRoutePara
     * @param tiltStep
     * @return
     */
    public static Hashtable<String, Object> findNextPoint(MapView mapView, Point pLB, Point pRB, Point pLT, Point pRT, LatLngInfo homePoint, AirRouteParameter airRoutePara, CommonConstants.TiltStep tiltStep) {
        Point pLBPoint = mapView.toMapPoint(pLB);
        Point pRBPoint = mapView.toMapPoint(pRB);
        Point pLTPoint = mapView.toMapPoint(pLT);
        Point pRTPoint = mapView.toMapPoint(pRT);

        LatLngInfo lb = CoordinateUtils.mapMercatorToGps84(pLBPoint.getX(), pLBPoint.getY());
        LatLngInfo rb = CoordinateUtils.mapMercatorToGps84(pRBPoint.getX(), pRBPoint.getY());
        LatLngInfo lt = CoordinateUtils.mapMercatorToGps84(pLTPoint.getX(), pLTPoint.getY());
        LatLngInfo rt = CoordinateUtils.mapMercatorToGps84(pRTPoint.getX(), pRTPoint.getY());

        double width = LatLngUtils.getDistance(lb.latitude, lb.longitude, rb.latitude, rb.longitude);

        double height = LatLngUtils.getDistance(lb.latitude, lb.longitude, lt.latitude, lt.longitude);

        boolean blnCW = width > height;

        LatLngInfo startPoint = lb;
        CommonConstants.MarkerFlag mfPoint = CommonConstants.MarkerFlag.LeftBottom;
        double minDistance = 0.0D;
        if (homePoint != null) {

            minDistance = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, startPoint.latitude, startPoint.longitude);

            double dRB = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, rb.latitude, rb.longitude);

            double dLT = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, lt.latitude, lt.longitude);

            double dRT = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, rt.latitude, rt.longitude);

            if (dRB < minDistance) {
                minDistance = dRB;
                startPoint = rb;
                mfPoint = CommonConstants.MarkerFlag.RightBottom;
            }
            if (dLT < minDistance) {
                minDistance = dLT;
                startPoint = lt;
                mfPoint = CommonConstants.MarkerFlag.LeftTop;
            }
            if (dRT < minDistance) {
                minDistance = dRT;
                startPoint = rt;
                mfPoint = CommonConstants.MarkerFlag.RightTop;
            }
        }

        if (airRoutePara.isShortDirection())
            blnCW = !blnCW;
        airRoutePara.setAirZoneWidth(blnCW ? width : height);
        airRoutePara.setAirZoneHeight(!blnCW ? width : height);
        airRoutePara.setGotoTaskDistance(minDistance);

        double routeAngle = 0.0D;
        double angleB = 0.0D;
        switch (mfPoint.ordinal()) {
            case 0://LeftTop
                if (blnCW) {
                    routeAngle = LatLngUtils.getABAngle(startPoint.latitude, startPoint.longitude, rt.latitude, rt.longitude) - 90;
                    angleB = routeAngle - 90.0D;
                } else {
                    routeAngle = LatLngUtils.getABAngle(startPoint.latitude, startPoint.longitude, lb.latitude, lb.longitude) + 90;
                    angleB = routeAngle + 90.0D;
                }
                break;
            case 1://RightTop
                if (blnCW) {
                    routeAngle = LatLngUtils.getABAngle(startPoint.latitude, startPoint.longitude, lt.latitude, lt.longitude) + 90;
                    angleB = routeAngle + 90.0D;
                } else {
                    routeAngle = LatLngUtils.getABAngle(startPoint.latitude, startPoint.longitude, rb.latitude, rb.longitude) - 90;
                    angleB = routeAngle - 90.0D;
                }
                break;
            case 2://RightBottom
                if (blnCW) {
                    routeAngle = LatLngUtils.getABAngle(lb.latitude, lb.longitude, startPoint.latitude, startPoint.longitude) + 90;
                    angleB = routeAngle - 90.0D;
                } else {
                    routeAngle = LatLngUtils.getABAngle(rt.latitude, rt.longitude, startPoint.latitude, startPoint.longitude) - 90;
                    angleB = routeAngle + 90.0D;
                }
                break;
            case 3://LeftBottom
                if (blnCW) {
                    routeAngle = LatLngUtils.getABAngle(rb.latitude, rb.longitude, startPoint.latitude, startPoint.longitude) - 90;
                    angleB = routeAngle + 90.0D;
                } else {
                    routeAngle = LatLngUtils.getABAngle(lt.latitude, lt.longitude, startPoint.latitude, startPoint.longitude) + 90;
                    angleB = routeAngle - 90.0D;
                }
                break;
        }

        LatLngInfo nextPoint = LatLngUtils.getStartPoint(startPoint.latitude, startPoint.longitude, airRoutePara.getAvailableWidth(), airRoutePara.getAvailableHeight(), routeAngle, angleB);

        if ((tiltStep != null) && (tiltStep != CommonConstants.TiltStep.Step1)) {
            double distance = airRoutePara.getAltitude() * Math.tan(Math.toRadians(airRoutePara.getGimbalAngle() + 90));
            if ((tiltStep == CommonConstants.TiltStep.Step2) || (tiltStep == CommonConstants.TiltStep.Step4))
                nextPoint = LatLngUtils.getLatlngByDA(nextPoint.latitude, nextPoint.longitude, distance, angleB + 180.0D > 360.0D ? angleB - 180.0D : angleB + 180.0D);
            else
                nextPoint = LatLngUtils.getLatlngByDA(nextPoint.latitude, nextPoint.longitude, distance, angleB);
        }

        Hashtable htResult = new Hashtable();
        htResult.put("NextPoint", nextPoint);
        htResult.put("RouteAngle", Double.valueOf(routeAngle));
        htResult.put("AngleB", Double.valueOf(angleB));

        return htResult;
    }

    public static Hashtable<String, Object> RecalculatePoint(MapView mapView, Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPosition, Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPositionDrag, CommonConstants.MarkerFlag mf, Point StartPoint, Point newPosition) {
        Hashtable htResult = new Hashtable();
        Point lt = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLatitude()));
        Point rb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLatitude()));
        Point rt = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLatitude()));
        Point lb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLatitude()));
        Point ct = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLatitude()));
        Point cr = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLatitude()));
        Point cb = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLatitude()));
        Point cl = mapView.toScreenPoint(new Point(
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLongitude(),
                (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLatitude()));




        double dAngle, dDiagonalAngle, diffX, diffY;
        switch (mf.ordinal()) {
            case 0:
                if (!checkDistance(mapView, htMarkerPositionDrag, rb, CommonConstants.MarkerFlag.RightTop))
                    return null;
                if (!checkDistance(mapView, htMarkerPositionDrag, rb, CommonConstants.MarkerFlag.LeftBottom))
                    return null;
                if (lb.getY() == rb.getY()) {
                    diffX = newPosition.getX() - lt.getX();
                    diffY = newPosition.getY() - lt.getY();
                    rt.setY(rt.getY() + diffY);
                    lb.setX(lb.getX() + diffX);
                } else {
                    ct = ArcgisPointUtils.getCenterPoint(newPosition, rb);
                    dAngle = ArcgisPointUtils.getDegree(rb, ct, rt);
                    dDiagonalAngle = 180.0D - 2.0D * dAngle;
                    rt = ArcgisPointUtils.GetBPoint(rb, ct, -1.0D * dDiagonalAngle);
                    lb = ArcgisPointUtils.GetBPoint(newPosition, ct, -1.0D * dDiagonalAngle);
                }
                lt = newPosition;
                break;
            case 1:
                if (!checkDistance(mapView, htMarkerPositionDrag, lb, CommonConstants.MarkerFlag.LeftTop))
                    return null;
                if (!checkDistance(mapView, htMarkerPositionDrag, lb, CommonConstants.MarkerFlag.RightBottom))
                    return null;
                if (lb.getY() == rb.getY()) {
                    diffX = newPosition.getX() - rt.getX();
                    diffY = newPosition.getY() - rt.getY();
                    lt.setY(lt.getY() + diffY);
                    rb.setX(rb.getX() + diffX);
                } else {
                    ct = ArcgisPointUtils.getCenterPoint(newPosition, lb);
                    dAngle = ArcgisPointUtils.getDegree(lb, ct, lt);
                    dDiagonalAngle = 180.0D - 2.0D * dAngle;
                    lt = ArcgisPointUtils.GetBPoint(lb, ct, dDiagonalAngle);
                    rb = ArcgisPointUtils.GetBPoint(newPosition, ct, dDiagonalAngle);
                }
                rt = newPosition;
                break;
            case 2:
                if (!checkDistance(mapView, htMarkerPositionDrag, lt, CommonConstants.MarkerFlag.LeftBottom))
                    return null;
                if (!checkDistance(mapView, htMarkerPositionDrag, lt, CommonConstants.MarkerFlag.RightTop))
                    return null;
                if (lt.getY() == rt.getY()) {
                    diffX = newPosition.getX() - rb.getX();
                    diffY = newPosition.getY() - rb.getY();
                    lb.setY(lb.getY() + diffY);
                    rt.setX(rt.getX() + diffX);
                } else {
                    ct = ArcgisPointUtils.getCenterPoint(newPosition, lt);
                    dAngle = ArcgisPointUtils.getDegree(lt, ct, lb);
                    dDiagonalAngle = 180.0D - 2.0D * dAngle;
                    lb = ArcgisPointUtils.GetBPoint(lt, ct, -1.0D * dDiagonalAngle);
                    rt = ArcgisPointUtils.GetBPoint(newPosition, ct, -1.0D * dDiagonalAngle);
                }
                rb = newPosition;
                break;
            case 3:
                if (!checkDistance(mapView, htMarkerPositionDrag, rt, CommonConstants.MarkerFlag.RightBottom))
                    return null;
                if (!checkDistance(mapView, htMarkerPositionDrag, rt, CommonConstants.MarkerFlag.LeftTop))
                    return null;
                if (lt.getY() == rt.getY()) {
                    diffX = newPosition.getX() - lb.getX();
                    diffY = newPosition.getY() - lb.getY();
                    rb.setY(rb.getY() + diffY);
                    lt.setX(lt.getX() + diffX);
                } else {
                    ct = ArcgisPointUtils.getCenterPoint(newPosition, rt);
                    dAngle = ArcgisPointUtils.getDegree(rt, ct, rb);
                    dDiagonalAngle = 180.0D - 2.0D * dAngle;
                    rb = ArcgisPointUtils.GetBPoint(rt, ct, dDiagonalAngle);
                    lt = ArcgisPointUtils.GetBPoint(newPosition, ct, dDiagonalAngle);
                }
                lb = newPosition;
                break;
            case 4:
                if (!checkDistance(mapView, htMarkerPositionDrag, cb, CommonConstants.MarkerFlag.CenterTop)) {
                    return null;
                }
                if ((lb.getY() == rb.getY()) || (lb.getX() == rb.getX())) {
                    diffY = newPosition.getY() - ct.getY();
                    rt.setY(rt.getY() + diffY);
                    lt.setY(lt.getY() + diffY);
                } else {
                    double dDistance = ArcgisPointUtils.getDistance(cb, newPosition);
                    double dMoveAngle = ArcgisPointUtils.getDegree(cb, ct, newPosition);
                    double dOldDistance = ArcgisPointUtils.getDistance(lb, lt);
                    if (dMoveAngle > 90.0D)
                        dMoveAngle = 90.0D;
                    double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
                    if (dOldDistance > 0.0D) {
                        lt.setX(lb.getX() + (lt.getX() - lb.getX()) * dNewDistance / dOldDistance);
                        lt.setY(lb.getY() + (lt.getY() - lb.getY()) * dNewDistance / dOldDistance);

                        rt.setX(rb.getX() + (rt.getX() - rb.getX()) * dNewDistance / dOldDistance);
                        rt.setY(rb.getY() + (rt.getY() - rb.getY()) * dNewDistance / dOldDistance);
                    } else {
                        lt.setX(lb.getX());
                        lt.setY(lb.getY());

                        rt.setX(rb.getX());
                        rt.setY(rb.getY());
                    }
                }
                break;
            case 5:
                if (!checkDistance(mapView, htMarkerPositionDrag, cl, CommonConstants.MarkerFlag.CenterRight)) {
                    return null;
                }
                if (lb.getY() == rb.getY()) {
                    diffX = newPosition.getX() - cr.getX();
                    rt.setX(rt.getX() + diffX);
                    rb.setX(rb.getX() + diffX);
                } else {
                    double dDistance = ArcgisPointUtils.getDistance(cl, newPosition);
                    double dMoveAngle = ArcgisPointUtils.getDegree(cl, cr, newPosition);
                    double dOldDistance = ArcgisPointUtils.getDistance(lb, rb);
                    if (dMoveAngle > 90.0D)
                        dMoveAngle = 90.0D;
                    double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
                    if (dOldDistance > 0.0D) {
                        rb.setX(lb.getX() + (rb.getX() - lb.getX()) * dNewDistance / dOldDistance);
                        rb.setY(lb.getY() + (rb.getY() - lb.getY()) * dNewDistance / dOldDistance);

                        rt.setX(lt.getX() + (rt.getX() - lt.getX()) * dNewDistance / dOldDistance);
                        rt.setY(lt.getY() + (rt.getY() - lt.getY()) * dNewDistance / dOldDistance);
                    } else {
                        rb.setX(lb.getX());
                        rb.setY(lb.getY());

                        rt.setX(lt.getX());
                        rt.setY(lt.getY());
                    }
                }
                break;
            case 6:
                if (!checkDistance(mapView, htMarkerPositionDrag, ct, CommonConstants.MarkerFlag.CenterBottom))
                    return null;
                if (lb.getY() == rb.getY()) {
                    diffY = newPosition.getY() - cb.getY();
                    lb.setY(lb.getY() + diffY);
                    rb.setY(rb.getY() + diffY);
                } else {
                    double dDistance = ArcgisPointUtils.getDistance(ct, newPosition);
                    double dMoveAngle = ArcgisPointUtils.getDegree(ct, cb, newPosition);
                    double dOldDistance = ArcgisPointUtils.getDistance(lb, lt);
                    if (dMoveAngle > 90.0D)
                        dMoveAngle = 90.0D;
                    double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
                    if (dOldDistance > 0.0D) {
                        lb.setX(lt.getX() + (lb.getX() - lt.getX()) * dNewDistance / dOldDistance);
                        lb.setY(lt.getY() + (lb.getY() - lt.getY()) * dNewDistance / dOldDistance);

                        rb.setX(rt.getX() + (rb.getX() - rt.getX()) * dNewDistance / dOldDistance);
                        rb.setY(rt.getY() + (rb.getY() - rt.getY()) * dNewDistance / dOldDistance);
                    } else {
                        rb.setX(rt.getX());
                        rb.setY(rt.getY());

                        lb.setX(lt.getX());
                        lb.setY(lt.getY());
                    }
                }
                break;
            case 7:
                if (!checkDistance(mapView, htMarkerPositionDrag, cr, CommonConstants.MarkerFlag.CenterLeft))
                    return null;
                if (lb.getY() == rb.getY()) {
                    diffX = newPosition.getX() - cl.getX();
                    lb.setX(lb.getX() + diffX);
                    lt.setX(lt.getX() + diffX);
                } else {
                    double dDistance = ArcgisPointUtils.getDistance(cr, newPosition);
                    double dMoveAngle = ArcgisPointUtils.getDegree(cr, cl, newPosition);
                    double dOldDistance = ArcgisPointUtils.getDistance(rt, lt);
                    if (dMoveAngle > 90.0D)
                        dMoveAngle = 90.0D;
                    double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
                    if (dOldDistance > 0.0D) {
                        lt.setX(rt.getX() + (lt.getX() - rt.getX()) * dNewDistance / dOldDistance);
                        lt.setY(rt.getY() + (lt.getY() - rt.getY()) * dNewDistance / dOldDistance);

                        lb.setX(rb.getX() + (lb.getX() - rb.getX()) * dNewDistance / dOldDistance);
                        lb.setY(rb.getY() + (lb.getY() - rb.getY()) * dNewDistance / dOldDistance);
                    } else {
                        lt.setX(rt.getX());
                        lt.setY(rt.getY());

                        lb.setX(lt.getX());
                        lb.setY(lt.getY());
                    }
                }
                break;
            case 8:
                diffX = newPosition.getX() - StartPoint.getX();
                diffY = newPosition.getY() - StartPoint.getY();
                rt.setX(rt.getX() + diffX);
                rt.setY(rt.getY() + diffY);
                rb.setX(rb.getX() + diffX);
                rb.setY(rb.getY() + diffY);

                lb.setX(lb.getX() + diffX);
                lb.setY(lb.getY() + diffY);
                lt.setX(lt.getX() + diffX);
                lt.setY(lt.getY() + diffY);
                break;
            case 9:
            case 10:












                break;
        }
        htResult.put("LT", lt);
        htResult.put("RB", rb);
        htResult.put("RT", rt);
        htResult.put("LB", lb);

        return htResult;
    }

    private static boolean checkDistance(MapView mapView,
                                         Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPositionDrag,
                                         Point oldPosition,
                                         CommonConstants.MarkerFlag dragFlag) {
        MarkerPosition mpCenterBottom = htMarkerPositionDrag.get(dragFlag);
        if (mpCenterBottom != null) {
            double dCurrentDistance = ArcgisPointUtils.getDistance(oldPosition,
                    mapView.toScreenPoint(new Point(mpCenterBottom.getLongitude(),
                            mpCenterBottom.getLatitude())));
            if (dCurrentDistance < 80.0D)
                return false;
        }
        return true;
    }
}