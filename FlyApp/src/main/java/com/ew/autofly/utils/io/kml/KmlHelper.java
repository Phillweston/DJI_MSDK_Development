package com.ew.autofly.utils.io.kml;

import android.content.Context;
import android.util.Log;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.ew.autofly.entity.Tower;
import com.ew.autofly.entity.geometry.GeoLine;
import com.ew.autofly.entity.geometry.GeoPoint;
import com.ew.autofly.entity.geometry.GeoPolygon;
import com.ew.autofly.utils.IOUtils;
import com.flycloud.autofly.base.util.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class KmlHelper {

    public final static int TYPE_POINT = 1;
    public final static int TYPE_LINE = 2;
    public final static int TYPE_POLYGON = 3;

    private List<GeoPoint> mGeoPoints = new ArrayList<>();
    private List<GeoLine> mGeoLines = new ArrayList<>();
    private List<GeoPolygon> mGeoPolygons = new ArrayList<>();

    public List<GeoPoint> getGeoPoints() {
        return mGeoPoints;
    }

    public List<GeoLine> getGeoLines() {
        return mGeoLines;
    }

    public List<GeoPolygon> getGeoPolygons() {
        return mGeoPolygons;
    }

    public void loadKml(String filePath) {
        mGeoPoints.clear();
        mGeoLines.clear();
        mGeoPolygons.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            MyKmlHandler handler = new MyKmlHandler();
            reader.setContentHandler(handler);
            InputSource is = new InputSource();
            is.setByteStream(new FileInputStream(file));
            is.setEncoding("utf-8");
            reader.parse(is);
        } catch (ParserConfigurationException e) {
            Log.e("KmlHelper", e.getMessage());
        } catch (SAXException e) {
            Log.e("KmlHelper", e.getMessage());
        } catch (IOException e) {
            Log.e("KmlHelper", e.getMessage());
        }
    }

    private class MyKmlHandler extends DefaultHandler {
        private int type = TYPE_POINT;
        private String loadValue = "";
        private boolean bMultiGeometry = false;

        private String name = "";
        private String description = "";
        private String coordinates = "";
        private String reserve1 = "";

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            String nameVal = localName;
            if (nameVal.equalsIgnoreCase("")) {
                nameVal = qName;
            }
            loadValue = "";
            if (nameVal.equalsIgnoreCase("Point")) {
                type = TYPE_POINT;
            } else if (nameVal.equalsIgnoreCase("LineString")) {
                type = TYPE_LINE;
            } else if (nameVal.equalsIgnoreCase("Polygon")) {
                type = TYPE_POLYGON;
            } else if (nameVal.equalsIgnoreCase("Placemark")) {
                name = "";
                description = "";
                coordinates = "";
            } else if (nameVal.equalsIgnoreCase("MultiGeometry")) {
                bMultiGeometry = true;
            } else if (nameVal.equalsIgnoreCase("Reserve_1")) {
                reserve1 = "";
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            String nameVal = localName;
            if (nameVal.equalsIgnoreCase("")) {
                nameVal = qName;
            }
            if (nameVal.equalsIgnoreCase("name")) {
                name = loadValue;
            } else if (nameVal.equalsIgnoreCase("description")) {
                description = loadValue;
            } else if (nameVal.equalsIgnoreCase("coordinates")) {

                coordinates = loadValue;

                String[] tmp;
                if (type == TYPE_POINT) {//点
                    tmp = coordinates.split(",");
                    Point point = toPoint(tmp);
                    if (point != null) {
                        GeoPoint geoPoint = new GeoPoint();
                        geoPoint.setPoint(point);
                        geoPoint.setName(name);
                        geoPoint.setGid(StringUtils.newGUID());
                        geoPoint.setDescription(description);
                        mGeoPoints.add(geoPoint);
                    }
                } else if (type == TYPE_LINE) {//线
                    coordinates = coordinates.replace(" ", "\n").replace("\t", "");
                    tmp = coordinates.split("\n");

                    PointCollection pointCollection = new PointCollection(SpatialReferences.getWgs84());

                    for (String str : tmp) {
                        if (StringUtils.isEmptyOrNull(str)) {
                            continue;
                        }
                        String[] tmp1 = str.trim().split(",");
                        Point point = toPoint(tmp1);
                        if (point != null) {
                            pointCollection.add(point);
                        }
                    }
                    Polyline polyline = new Polyline(pointCollection);
                    GeoLine geoLine = new GeoLine();
                    geoLine.setPolyline(polyline);
                    geoLine.setName(name);
                    geoLine.setGid(StringUtils.newGUID());
                    mGeoLines.add(geoLine);

                } else if (type == TYPE_POLYGON) {//面
                    coordinates = coordinates.replace(" ", "\n").replace("\t", "");
                    tmp = coordinates.split("\n");

                    PointCollection pointCollection = new PointCollection(SpatialReferences.getWgs84());

                    for (String str : tmp) {
                        if (StringUtils.isEmptyOrNull(str)) {
                            continue;
                        }
                        String[] tmp1 = str.trim().split(",");
                        Point point = toPoint(tmp1);
                        if (point != null) {
                            pointCollection.add(point);
                        }
                    }
                    Polygon polygon = new Polygon(pointCollection);
                    GeoPolygon geoPolygon = new GeoPolygon();
                    geoPolygon.setPolygon(polygon);
                    geoPolygon.setName(name);
                    geoPolygon.setGid(StringUtils.newGUID());
                    mGeoPolygons.add(geoPolygon);
                }

            } else if (nameVal.equalsIgnoreCase("Reserve_1")) {
                reserve1 = loadValue;
                int size = mGeoPoints.size();
                if (size > 0) {
                    mGeoPoints.get(size - 1).setReserve1(reserve1);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            loadValue += new String(ch, start, length);
        }
    }

    private Point toPoint(String[] tmp) {

        Point point = null;

        try {
            if (tmp.length > 1) {

                double longitude = Double.parseDouble(tmp[0]);
                double latitude = Double.parseDouble(tmp[1]);
                double altitude = 0;
                if (tmp.length > 2) {
                    altitude = Double.parseDouble(tmp[2]);
                }
                point = new Point(longitude, latitude, altitude, SpatialReferences.getWgs84());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return point;
    }


    public static boolean saveTowerKml(Context context, String filePath, List<Tower> towerList) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < towerList.size(); i++) {
            sb.append("\n");
            sb.append("<Placemark>").append("\n");
            sb.append("<name>").append(towerList.get(i).getTowerNo()).append("</name>").append("\n");
            sb.append("<description>").append(towerList.get(i).getDescription()).append("</description>").append("\n");

            sb.append("<Point>").append("\n");

            sb.append("<coordinates>").append(towerList.get(i).getLongitude()).append(",")
                    .append(towerList.get(i).getLatitude()).append(",0</coordinates>").append("\n");
            sb.append("</Point>").append("\n");
            sb.append("</Placemark>");
        }
        String template = IOUtils.getFromAssets(context, "point_template.kml");
        template = String.format(template, sb.toString());

        return IOUtils.writeTxtFile(filePath, template);
    }
}
