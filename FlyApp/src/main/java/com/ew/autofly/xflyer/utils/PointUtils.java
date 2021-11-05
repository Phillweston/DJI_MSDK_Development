package com.ew.autofly.xflyer.utils;

import android.graphics.Point;

public class PointUtils {
	public static Point GetBPoint(Point aPoint, Point center, double angle) {
		double k = Math.toRadians(angle);
		double x2 = (aPoint.x - center.x) * Math.cos(k) - (aPoint.y - center.y)
				* Math.sin(k) + center.x;
		double y2 = (aPoint.x - center.x) * Math.sin(k) + (aPoint.y - center.y)
				* Math.cos(k) + center.y;

		return new Point(Double.valueOf(x2).intValue(), Double.valueOf(y2)
				.intValue());
	}

	public static double getDegree(Point A, Point B, Point C) {
		double ab = getDistince(A, B);
		double ac = getDistince(A, C);
		double bc = getDistince(B, C);

		double hd = Math.acos((Math.pow(ab, 2.0D) + Math.pow(ac, 2.0D) - Math
				.pow(bc, 2.0D)) / (2.0D * ab * ac));
		return Math.toDegrees(hd);
	}

	public static double getDegreeWithX(Point A, Point B) {
		Point A1 = new Point(A.x - B.x, A.y - B.y);
		Point B1 = new Point(0, 0);
		Point C1 = new Point(0, A1.y);
		if (A1.y == 0)
			return 0.0D;
		return getDegree(B1, A1, C1);
	}

	public static double getDistince(Point A, Point B) {
		return Math.sqrt(Math.pow(A.x - B.x, 2.0D) + Math.pow(A.y - B.y, 2.0D));
	}

	public static Point getCenterPoint(Point A, Point B) {
		return new Point((A.x + B.x) / 2, (A.y + B.y) / 2);
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
		double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
				- c.x * b.y - a.x * c.y) / 2.0D);

		return result;
	}
}