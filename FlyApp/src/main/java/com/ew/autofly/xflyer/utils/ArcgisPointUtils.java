package com.ew.autofly.xflyer.utils;

import com.esri.core.geometry.Point;
import com.ew.autofly.entity.LatLngInfo;

public class ArcgisPointUtils {
	public static Point GetBPoint(Point aPoint, Point center, double angle) {
		double k = Math.toRadians(angle);
		double x2 = (aPoint.getX() - center.getX()) * Math.cos(k)
				- (aPoint.getY() - center.getY()) * Math.sin(k) + center.getX();
		double y2 = (aPoint.getX() - center.getX()) * Math.sin(k)
				+ (aPoint.getY() - center.getY()) * Math.cos(k) + center.getY();

		return new Point(x2, y2);
	}

	public static double getDegree(Point A, Point B, Point C) {
		double ab = getDistance(A, B);
		double ac = getDistance(A, C);
		double bc = getDistance(B, C);

		double hd = Math.acos((Math.pow(ab, 2.0D) + Math.pow(ac, 2.0D) - Math
				.pow(bc, 2.0D)) / (2.0D * ab * ac));
		return Math.toDegrees(hd);
	}

	public static double getDegreeWithX(Point A, Point B) {
		Point A1 = new Point(A.getX() - B.getX(), A.getY() - B.getY());
		Point B1 = new Point(0.0D, 0.0D);
		Point C1 = new Point(0.0D, A1.getY());
		if (A1.getY() == 0.0D)
			return 0.0D;
		return getDegree(B1, A1, C1);
	}

	public static double getDistance(Point A, Point B) {
		return Math.sqrt(Math.pow(A.getX() - B.getX(), 2.0D)
				+ Math.pow(A.getY() - B.getY(), 2.0D));
	}

	public static Point getCenterPoint(Point A, Point B) {
		return new Point((A.getX() + B.getX()) / 2.0D,
				(A.getY() + B.getY()) / 2.0D);
	}

	public static boolean isInTriangle(Point a, Point b, Point c, Point p) {
		double abc = triangleArea(a, b, c);
		double abp = triangleArea(a, b, p);
		double acp = triangleArea(a, c, p);
		double bcp = triangleArea(b, c, p);
		if (abc == abp + acp + bcp) {
			return true;
		}
		return false;
	}

	private static double triangleArea(Point a, Point b, Point c) {
		double result = Math.abs((a.getX() * b.getY() + b.getX() * c.getY()
				+ c.getX() * a.getY() - b.getX() * a.getY() - c.getX()
				* b.getY() - a.getX() * c.getY()) / 2.0D);

		return result;
	}

	public static LatLngInfo pointToLatLngInfo(Point p) {
		return new LatLngInfo(p.getY(), p.getX());
	}

	public static Point latLngInfoToPoint(LatLngInfo latLngInfo) {
		return new Point(latLngInfo.longitude, latLngInfo.latitude);
	}

	public static double calculatePolygonArea(double scale, Point[] vertex) {
		int pointNum = vertex.length;
		int i = 0;
		float temp = 0.0F;
		for (; i < pointNum - 1; i++) {
			temp = (float) (temp + det(vertex[0], vertex[i], vertex[(i + 1)]));
		}
		return temp / 2.0F * scale * scale;
	}

	private static double det(Point p0, Point p1, Point p2) {
		return (p1.getX() - p0.getX()) * (p2.getY() - p0.getY())
				- (p1.getY() - p0.getY()) * (p2.getX() - p0.getX());
	}
}