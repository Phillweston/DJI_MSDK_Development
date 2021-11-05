package com.ew.autofly.xflyer.utils;

import android.graphics.Point;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.ew.autofly.entity.AirRouteParameter;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.entity.MarkerPosition;
import java.util.Hashtable;

public class GDMapHelperUtils {
	public static Hashtable<String, Object> FindTouchPosition(
			Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPosition,
			Hashtable<CommonConstants.MarkerFlag, Marker> htMarker,
			Point touchPoint, Projection projection) {
		Point lt = projection
				.toScreenLocation(new LatLng(
						((MarkerPosition) htMarkerPosition
								.get(CommonConstants.MarkerFlag.LeftTop))
								.getLatitude(),
						((MarkerPosition) htMarkerPosition
								.get(CommonConstants.MarkerFlag.LeftTop))
								.getLongitude()));

		Point rb = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.RightBottom))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.RightBottom))
						.getLongitude()));

		Point rt = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.RightTop))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.RightTop))
						.getLongitude()));

		Point lb = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.LeftBottom))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.LeftBottom))
						.getLongitude()));

		Point ct = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterTop))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterTop))
						.getLongitude()));

		Point cr = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterRight))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterRight))
						.getLongitude()));

		Point cb = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterBottom))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterBottom))
						.getLongitude()));

		Point cl = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterLeft))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.CenterLeft))
						.getLongitude()));

		Point mc = projection.toScreenLocation(new LatLng(
				((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.MoveCenter))
						.getLatitude(), ((MarkerPosition) htMarkerPosition
						.get(CommonConstants.MarkerFlag.MoveCenter))
						.getLongitude()));

		boolean regionEdit = false;
		Marker touchMarker = null;
		double dLT = PointUtils.getDistince(lt, touchPoint);
		double dRB = PointUtils.getDistince(rb, touchPoint);
		double dRT = PointUtils.getDistince(rt, touchPoint);
		double dLB = PointUtils.getDistince(lb, touchPoint);
		double dCT = PointUtils.getDistince(ct, touchPoint);
		double dCR = PointUtils.getDistince(cr, touchPoint);
		double dCB = PointUtils.getDistince(cb, touchPoint);
		double dCL = PointUtils.getDistince(cl, touchPoint);
		double dMC = PointUtils.getDistince(mc, touchPoint);

		double mcImageSize = ((MarkerPosition) htMarkerPosition
				.get(CommonConstants.MarkerFlag.MoveCenter)).getImageWidth();

		if (PointUtils.isInTriangle(ct, cb, cl, touchPoint)) {
			if (dMC <= mcImageSize / 2.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.MoveCenter);
				regionEdit = true;
			} else if (dCT <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterTop);
				regionEdit = true;
			} else if (dCB <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterBottom);
				regionEdit = true;
			} else if (dCL <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterLeft);
				regionEdit = true;
			} else {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.RotationLeft);
				regionEdit = true;
			}
		} else if (PointUtils.isInTriangle(ct, cb, cr, touchPoint)) {
			if (dMC <= mcImageSize / 2.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.MoveCenter);
				regionEdit = true;
			} else if (dCT <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterTop);
				regionEdit = true;
			} else if (dCB <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterBottom);
				regionEdit = true;
			} else if (dCR <= 40.0D) {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.CenterRight);
				regionEdit = true;
			} else {
				touchMarker = (Marker) htMarker
						.get(CommonConstants.MarkerFlag.RotationRight);
				regionEdit = true;
			}

		} else if (dCT <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.CenterTop);
			regionEdit = true;
		} else if (dCB <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.CenterBottom);
			regionEdit = true;
		} else if (dCR <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.CenterRight);
			regionEdit = true;
		} else if (dCL <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.CenterLeft);
			regionEdit = true;
		} else if (dLT <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.LeftTop);
			regionEdit = true;
		} else if (dLB <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.LeftBottom);
			regionEdit = true;
		} else if (dRT <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.RightTop);
			regionEdit = true;
		} else if (dRB <= 40.0D) {
			touchMarker = (Marker) htMarker
					.get(CommonConstants.MarkerFlag.RightBottom);
			regionEdit = true;
		}

		Hashtable htResult = new Hashtable();
		htResult.put("RegionEdit", Boolean.valueOf(regionEdit));
		htResult.put("TouchMarker", touchMarker);
		return htResult;
	}

	public static Hashtable<String, Object> findNextPoint(Projection projection, Point pLB, Point pRB, Point pLT, Point pRT, LatLng homePoint, AirRouteParameter airRoutePara)
  {
    double width = LatLngUtils.getDistance(projection.fromScreenLocation(pLB).latitude, projection.fromScreenLocation(pLB).longitude, projection.fromScreenLocation(pRB).latitude, projection.fromScreenLocation(pRB).longitude);

    double height = LatLngUtils.getDistance(projection.fromScreenLocation(pLB).latitude, projection.fromScreenLocation(pLB).longitude, projection.fromScreenLocation(pLT).latitude, projection.fromScreenLocation(pLT).longitude);

    boolean blnCW = width > height;

    LatLng startPoint = projection.fromScreenLocation(pLB);
    CommonConstants.MarkerFlag mfPoint = CommonConstants.MarkerFlag.LeftBottom;
    double minDistince = 0.0D;
    if (homePoint != null)
    {
      minDistince = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, startPoint.latitude, startPoint.longitude);

      double dRB = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, projection.fromScreenLocation(pRB).latitude, projection.fromScreenLocation(pRB).longitude);

      double dLT = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, projection.fromScreenLocation(pLT).latitude, projection.fromScreenLocation(pLT).longitude);

      double dRT = LatLngUtils.getDistance(homePoint.latitude, homePoint.longitude, projection.fromScreenLocation(pRT).latitude, projection.fromScreenLocation(pRT).longitude);

      if (dRB < minDistince) {
        minDistince = dRB;
        startPoint = projection.fromScreenLocation(pRB);
        mfPoint = CommonConstants.MarkerFlag.RightBottom;
      }
      if (dLT < minDistince) {
        minDistince = dLT;
        startPoint = projection.fromScreenLocation(pLT);
        mfPoint = CommonConstants.MarkerFlag.LeftTop;
      }
      if (dRT < minDistince) {
        minDistince = dRT;
        startPoint = projection.fromScreenLocation(pRT);
        mfPoint = CommonConstants.MarkerFlag.RightTop;
      }
    }

    if (airRoutePara.isShortDirection()) {
      blnCW = !blnCW;
    }
    airRoutePara.setAirZoneWidth(blnCW ? width : height);
    airRoutePara.setAirZoneHeight(!blnCW ? width : height);

    airRoutePara.setGotoTaskDistance(minDistince);

    double routeAngle = 0.0D;

    double angleB = 0.0D;


    switch(mfPoint) {
    case LeftTop:
      if (blnCW) {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pLT).latitude, projection.fromScreenLocation(pLT).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle + 90.0D;
      } else {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pRB).latitude, projection.fromScreenLocation(pRB).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle - 90.0D;
      }
      break;
    case RightTop:
      if (blnCW) {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pRT).latitude, projection.fromScreenLocation(pRT).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle - 90.0D;
      } else {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pLB).latitude, projection.fromScreenLocation(pLB).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle + 90.0D;
      }
      break;
    case RightBottom:
      if (blnCW) {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pLB).latitude, projection.fromScreenLocation(pLB).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle - 90.0D;
      } else {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pRT).latitude, projection.fromScreenLocation(pRT).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle + 90.0D;
      }
      break;
    case LeftBottom:
      if (blnCW) {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pRB).latitude, projection.fromScreenLocation(pRB).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle + 90.0D;
      } else {
        routeAngle = LatLngUtils.getABAngle(projection.fromScreenLocation(pLT).latitude, projection.fromScreenLocation(pLT).longitude, startPoint.latitude, startPoint.longitude);

        angleB = routeAngle - 90.0D;
      }
      break;
    }

    LatLngInfo nextPoint = LatLngUtils.getStartPoint(startPoint.latitude, startPoint.longitude, airRoutePara.getAvailableWidth(), airRoutePara.getAvailableHeight(), routeAngle, angleB);

    Hashtable htResult = new Hashtable();
    htResult.put("NextPoint", nextPoint);
    htResult.put("RouteAngle", Double.valueOf(routeAngle));
    htResult.put("AngleB", Double.valueOf(angleB));

    return htResult;
  }

	public static Hashtable<String, Object> RecalculatePoint(AMap aMap, Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPosition, Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPositionDrag, CommonConstants.MarkerFlag mf, Point StartPoint, Point newPosition)
  {
    Hashtable htResult = new Hashtable();
    Projection projection = aMap.getProjection();

    Point lt = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftTop)).getLongitude()));
    Point rb = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.RightBottom)).getLongitude()));
    Point rt = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.RightTop)).getLongitude()));
    Point lb = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.LeftBottom)).getLongitude()));
    Point ct = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterTop)).getLongitude()));
    Point cr = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterRight)).getLongitude()));
    Point cb = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterBottom)).getLongitude()));
    Point cl = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.CenterLeft)).getLongitude()));
    Point mc = projection.toScreenLocation(new LatLng((htMarkerPosition.get(CommonConstants.MarkerFlag.MoveCenter)).getLatitude(), (htMarkerPosition.get(CommonConstants.MarkerFlag.MoveCenter)).getLongitude()));

    double dAngle;
    double dDiagonalAngle;
    int diffX;
    int diffY;
    switch(mf) {
    case RightBottom:
      if (!CheckDistince(htMarkerPositionDrag, projection, rb, CommonConstants.MarkerFlag.RightTop))
        return null;
      if (!CheckDistince(htMarkerPositionDrag, projection, rb, CommonConstants.MarkerFlag.LeftBottom))
        return null;
      if (lb.y == rb.y) {
        diffX = newPosition.x - lt.x;
        diffY = newPosition.y - lt.y;
        rt.y += diffY;
        lb.x += diffX;
      } else {
        ct = PointUtils.getCenterPoint(newPosition, rb);
        dAngle = PointUtils.getDegree(rb, ct, rt);
        dDiagonalAngle = 180.0D - 2.0D * dAngle;
        rt = PointUtils.GetBPoint(rb, ct, -1.0D * dDiagonalAngle);
        lb = PointUtils.GetBPoint(newPosition, ct, -1.0D * dDiagonalAngle);
      }
      lt = newPosition;
      break;
    case LeftBottom:
      if (!CheckDistince(htMarkerPositionDrag, projection, lb, CommonConstants.MarkerFlag.LeftTop))
        return null;
      if (!CheckDistince(htMarkerPositionDrag, projection, lb, CommonConstants.MarkerFlag.RightBottom))
        return null;
      if (lb.y == rb.y) {
        diffX = newPosition.x - rt.x;
        diffY = newPosition.y - rt.y;
        lt.y += diffY;
        rb.x += diffX;
      } else {
        ct = PointUtils.getCenterPoint(newPosition, lb);
        dAngle = PointUtils.getDegree(lb, ct, lt);
        dDiagonalAngle = 180.0D - 2.0D * dAngle;
        lt = PointUtils.GetBPoint(lb, ct, dDiagonalAngle);
        rb = PointUtils.GetBPoint(newPosition, ct, dDiagonalAngle);
      }
      rt = newPosition;
      break;
    case RightTop:
      if (!CheckDistince(htMarkerPositionDrag, projection, lt, CommonConstants.MarkerFlag.LeftBottom))
        return null;
      if (!CheckDistince(htMarkerPositionDrag, projection, lt, CommonConstants.MarkerFlag.RightTop))
        return null;
      if (lt.y == rt.y) {
        diffX = newPosition.x - rb.x;
        diffY = newPosition.y - rb.y;
        lb.y += diffY;
        rt.x += diffX;
      } else {
        ct = PointUtils.getCenterPoint(newPosition, lt);
        dAngle = PointUtils.getDegree(lt, ct, lb);
        dDiagonalAngle = 180.0D - 2.0D * dAngle;
        lb = PointUtils.GetBPoint(lt, ct, -1.0D * dDiagonalAngle);
        rt = PointUtils.GetBPoint(newPosition, ct, -1.0D * dDiagonalAngle);
      }
      rb = newPosition;
      break;
    case LeftTop:
      if (!CheckDistince(htMarkerPositionDrag, projection, rt, CommonConstants.MarkerFlag.RightBottom))
        return null;
      if (!CheckDistince(htMarkerPositionDrag, projection, rt, CommonConstants.MarkerFlag.LeftTop))
        return null;
      if (lt.y == rt.y) {
        diffX = newPosition.x - lb.x;
        diffY = newPosition.y - lb.y;
        rb.y += diffY;
        lt.x += diffX;
      } else {
        ct = PointUtils.getCenterPoint(newPosition, rt);
        dAngle = PointUtils.getDegree(rt, ct, rb);
        dDiagonalAngle = 180.0D - 2.0D * dAngle;
        rb = PointUtils.GetBPoint(rt, ct, dDiagonalAngle);
        lt = PointUtils.GetBPoint(newPosition, ct, dDiagonalAngle);
      }
      lb = newPosition;
      break;
    case CenterTop:
      if (!CheckDistince(htMarkerPositionDrag, projection, cb, CommonConstants.MarkerFlag.CenterTop)) {
        return null;
      }
      if ((lb.y == rb.y) || (lb.x == rb.x)) {
        diffX = newPosition.x - ct.x;
        diffY = newPosition.y - ct.y;
        rt.y += diffY;
        lt.y += diffY;
      }
      else {
        double dDistance = PointUtils.getDistince(cb, newPosition);
        double dMoveAngle = PointUtils.getDegree(cb, ct, newPosition);
        double dOldDistance = PointUtils.getDistince(lb, lt);
        if (dMoveAngle > 90.0D)
          dMoveAngle = 90.0D;
        double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
        if (dOldDistance > 0.0D) {
          lb.x += Double.valueOf((lt.x - lb.x) * dNewDistance / dOldDistance).intValue();
          lb.y += Double.valueOf((lt.y - lb.y) * dNewDistance / dOldDistance).intValue();

          rb.x += Double.valueOf((rt.x - rb.x) * dNewDistance / dOldDistance).intValue();
          rb.y += Double.valueOf((rt.y - rb.y) * dNewDistance / dOldDistance).intValue();
        } else {
          lt.x = lb.x;
          lt.y = lb.y;

          rt.x = rb.x;
          rt.y = rb.y;
        }
      }
      break;
    case CenterRight:
      if (!CheckDistince(htMarkerPositionDrag, projection, cl, CommonConstants.MarkerFlag.CenterRight)) {
        return null;
      }
      if (lb.y == rb.y) {
        diffX = newPosition.x - cr.x;
        diffY = newPosition.y - cr.y;
        rt.x += diffX;
        rb.x += diffX;
      }
      else {
        double dDistance = PointUtils.getDistince(cl, newPosition);
        double dMoveAngle = PointUtils.getDegree(cl, cr, newPosition);
        double dOldDistance = PointUtils.getDistince(lb, rb);
        if (dMoveAngle > 90.0D)
          dMoveAngle = 90.0D;
        double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
        if (dOldDistance > 0.0D) {
          lb.x += Double.valueOf((rb.x - lb.x) * dNewDistance / dOldDistance).intValue();
          lb.y += Double.valueOf((rb.y - lb.y) * dNewDistance / dOldDistance).intValue();

          lt.x += Double.valueOf((rt.x - lt.x) * dNewDistance / dOldDistance).intValue();
          lt.y += Double.valueOf((rt.y - lt.y) * dNewDistance / dOldDistance).intValue();
        } else {
          rb.x = lb.x;
          rb.y = lb.y;

          rt.x = lt.x;
          rt.y = lt.y;
        }
      }
      break;
    case CenterBottom:
      if (!CheckDistince(htMarkerPositionDrag, projection, ct, CommonConstants.MarkerFlag.CenterBottom))
        return null;
      if (lb.y == rb.y) {
        diffX = newPosition.x - cb.x;
        diffY = newPosition.y - cb.y;
        lb.y += diffY;
        rb.y += diffY;
      }
      else {
        double dDistance = PointUtils.getDistince(ct, newPosition);
        double dMoveAngle = PointUtils.getDegree(ct, cb, newPosition);
        double dOldDistance = PointUtils.getDistince(lb, lt);
        if (dMoveAngle > 90.0D)
          dMoveAngle = 90.0D;
        double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
        if (dOldDistance > 0.0D) {
          lt.x += Double.valueOf((lb.x - lt.x) * dNewDistance / dOldDistance).intValue();
          lt.y += Double.valueOf((lb.y - lt.y) * dNewDistance / dOldDistance).intValue();

          rt.x += Double.valueOf((rb.x - rt.x) * dNewDistance / dOldDistance).intValue();
          rt.y += Double.valueOf((rb.y - rt.y) * dNewDistance / dOldDistance).intValue();
        } else {
          rb.x = rt.x;
          rb.y = rt.y;

          lb.x = lt.x;
          lb.y = lt.y;
        }
      }
      break;
    case CenterLeft:
      if (!CheckDistince(htMarkerPositionDrag, projection, cr, CommonConstants.MarkerFlag.CenterLeft))
        return null;
      if (lb.y == rb.y) {
        diffX = newPosition.x - cl.x;
        diffY = newPosition.y - cl.y;
        lb.x += diffX;
        lt.x += diffX;
      }
      else {
        double dDistance = PointUtils.getDistince(cr, newPosition);
        double dMoveAngle = PointUtils.getDegree(cr, cl, newPosition);
        double dOldDistance = PointUtils.getDistince(rt, lt);
        if (dMoveAngle > 90.0D)
          dMoveAngle = 90.0D;
        double dNewDistance = dDistance * Math.cos(Math.toRadians(dMoveAngle));
        if (dOldDistance > 0.0D) {
          rt.x += Double.valueOf((lt.x - rt.x) * dNewDistance / dOldDistance).intValue();
          rt.y += Double.valueOf((lt.y - rt.y) * dNewDistance / dOldDistance).intValue();

          rb.x += Double.valueOf((lb.x - rb.x) * dNewDistance / dOldDistance).intValue();
          rb.y += Double.valueOf((lb.y - rb.y) * dNewDistance / dOldDistance).intValue();
        } else {
          lt.x = rt.x;
          lt.y = rt.y;

          lb.x = lt.x;
          lb.y = lt.y;
        }
      }
      break;
    case MoveCenter:
      diffX = newPosition.x - mc.x;
      diffY = newPosition.y - mc.y;
      rt.x += diffX;
      rt.y += diffY;
      rb.x += diffX;
      rb.y += diffY;

      lb.x += diffX;
      lb.y += diffY;
      lt.x += diffX;
      lt.y += diffY;
      break;
    case RotationLeft:
    case RotationRight:
      double dCurrentRotation = LatLngUtils.getABAngle(projection.fromScreenLocation(newPosition).latitude, projection.fromScreenLocation(newPosition).longitude, projection.fromScreenLocation(mc).latitude, projection.fromScreenLocation(mc).longitude);

      double dOldRotation = LatLngUtils.getABAngle(projection.fromScreenLocation(StartPoint).latitude, projection.fromScreenLocation(StartPoint).longitude, projection.fromScreenLocation(mc).latitude, projection.fromScreenLocation(mc).longitude);

      float markerAngle = Double.valueOf(270.0D - dCurrentRotation).floatValue();
      if (mf == CommonConstants.MarkerFlag.RotationRight) {
        markerAngle = Double.valueOf(90.0D - dCurrentRotation).floatValue();
      }
      htResult.put("RotationAngle", Float.valueOf(Double.valueOf(dCurrentRotation - dOldRotation).floatValue()));
      htResult.put("MarkerAngle", Float.valueOf(markerAngle));
      break;
    }

    htResult.put("LT", lt);
    htResult.put("RB", rb);
    htResult.put("RT", rt);
    htResult.put("LB", lb);
    htResult.put("MC", mc);

    return htResult;
  }

	private static boolean CheckDistince(
			Hashtable<CommonConstants.MarkerFlag, MarkerPosition> htMarkerPositionDrag,
			Projection projection, Point oldPosition,
			CommonConstants.MarkerFlag dragFlag) {
		MarkerPosition mpCenterBottom = (MarkerPosition) htMarkerPositionDrag
				.get(dragFlag);
		if (mpCenterBottom != null) {
			double dCurrentDistince = PointUtils.getDistince(oldPosition,
					projection.toScreenLocation(new LatLng(mpCenterBottom
							.getLatitude(), mpCenterBottom.getLongitude())));

			if (dCurrentDistince < 80.0D) {
				return false;
			}
		}
		return true;
	}
}