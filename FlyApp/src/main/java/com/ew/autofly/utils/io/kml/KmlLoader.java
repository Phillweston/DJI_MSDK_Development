package com.ew.autofly.utils.io.kml;

import android.graphics.Color;
import android.util.Log;

import com.esri.core.geometry.Polyline;
import com.ew.autofly.utils.StringUtils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


@Deprecated
public class KmlLoader {
    private ArrayList<Polyline> mLoadLines = null;

    public ArrayList<Polyline> getLoadLines() {
        return mLoadLines;
    }

    public ArrayList<String> loadKml(String filePath, String fileId) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        try {
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            MyKmlHandler handler = new MyKmlHandler(fileId);
            reader.setContentHandler(handler);
            InputSource is = new InputSource();
            is.setByteStream(new FileInputStream(file));
            is.setEncoding("utf-8");
            reader.parse(is);
            mLoadLines = handler.getPolylines();
            return handler.getInserts();
        } catch (ParserConfigurationException e) {
            Log.e("KmlLoader", e.getMessage());
        } catch (SAXException e) {
            Log.e("KmlLoader", e.getMessage());
        } catch (IOException e) {
            Log.e("KmlLoader", e.getMessage());
        }
        return null;
    }

    private class MyKmlHandler extends DefaultHandler {
        private int type = 1;
        private String loadValue = "";
        private boolean bMultiGeometry = false;
        
        private ArrayList<String> insertSqlList;
        private String name = "";
        private String description = "";
        private String altitude="0";
        private String coordinates = "";
        private String fileId = "";
        private ArrayList<String> mLineCoordinates = new ArrayList<>();

        public MyKmlHandler(String fileId) {
            super();
            this.fileId = fileId;
        }

        public ArrayList<Polyline> getPolylines() {
            ArrayList<Polyline> polylineList = new ArrayList<>();
            if (mLineCoordinates == null || mLineCoordinates.size() == 0) {
                return polylineList;
            }
            for (String str : mLineCoordinates) {
                String[] points = str.split(",");
                if (points.length < 2) {
                    continue;
                }
                Polyline line = new Polyline();
                for (int i = 0; i < points.length; i++) {
                    String[] xy = points[i].split(" ");
                    double x = Double.parseDouble(xy[0]);
                    double y = Double.parseDouble(xy[1]);
                    if (i % 2 == 0) {
                        line.startPath(x, y);
                    } else {
                        line.lineTo(x, y);
                        if (bMultiGeometry)
                            polylineList.add(line);
                    }
                }
                if (!bMultiGeometry)
                    polylineList.add(line);
            }
            return polylineList;
        }

        public ArrayList<String> getInserts() {
            return insertSqlList;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            insertSqlList = new ArrayList<>();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            String nameVal = localName;
            if (nameVal.equalsIgnoreCase("")) {
                nameVal = qName;
            }
            loadValue = "";
            if (nameVal.equalsIgnoreCase("Point")) {
                type = 1;
            } else if (nameVal.equalsIgnoreCase("LineString")) {
                type = 2;
            } else if (nameVal.equalsIgnoreCase("Polygon")) {
                type = 3;
            } else if (nameVal.equalsIgnoreCase("Placemark")) {
                name = "";
                description = "";
                altitude="0";
                coordinates = "";
            } else if (nameVal.equalsIgnoreCase("MultiGeometry")) {
                bMultiGeometry = true;
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
                if (type == 1) {//点
                    tmp = coordinates.split(",");
                    if (tmp.length > 1) {
                        if (tmp.length>2){
                            altitude=tmp[2];
                        }
                        if (!StringUtils.isEmptyOrNull(fileId))
                            insertSqlList.add("insert into points (GID, NAME, SYMBOL, STATE, FILEID, geometrys,description,altitude) values ('"
                                    + StringUtils.newGUID() + "','" + name + "','0','0','" + fileId + "'," +
                                    "GeomFromText('POINT(" + tmp[0] + " " + tmp[1] + ")', 4326)" + ",'" + description +"','" + altitude + "')");
                    }
                } else if (type == 2) {//线
                    coordinates = coordinates.replace(" ", "\n").replace("\t", "");
                    tmp = coordinates.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (String str : tmp) {
                        if (StringUtils.isEmptyOrNull(str)) {
                            continue;
                        }
                        String[] tmp1 = str.trim().split(",");
                        try {
                            sb.append(tmp1[0] + " " + tmp1[1] + ",");
                        } catch (Exception ex) {
                            Log.e("KmlLoader", ex.getMessage());
                        }
                    }
                    String coords = sb.toString();
                    int len = coords.length();
                    if (len > 0) {
                        coords = coords.substring(0, len - 1);
                        mLineCoordinates.add(coords);
                        if (!StringUtils.isEmptyOrNull(fileId)) {
                            if (bMultiGeometry) {
                                insertSqlList.add("insert into POLYLINES (GID, NAME, LINECOLOR,LINEWIDTH, STATE, FILEID, geometrys,description) values " + "('" + StringUtils.newGUID() + "','" + name + "','" + Color.parseColor("yellow") + "','1','1','" + fileId + "'," + "MultiLineStringFromText('MULTILINESTRING((" + coords + "))', 4326)" + ",'" + description + "')");
                            } else
                                insertSqlList.add("insert into POLYLINES (GID, NAME, LINECOLOR,LINEWIDTH, STATE, FILEID, geometrys,description) values " + "('" + StringUtils.newGUID() + "','" + name + "','" + Color.parseColor("white") + "','2','0','" + fileId + "'," + "MultiLineStringFromText('MULTILINESTRING((" + coords + "))', 4326)" + ",'" + description + "')");
                        }
                    }
                } else if (type == 3) {//面
                    coordinates = coordinates.replace(" ", "\n").replace("\t", "");
                    tmp = coordinates.split("\n");
                    StringBuilder sb = new StringBuilder();
                    for (String t : tmp) {
                        if (StringUtils.isEmptyOrNull(t)) {
                            continue;
                        }
                        String[] tmp1 = t.trim().split(",");
                        sb.append(tmp1[0] + " " + tmp1[1] + ",");
                    }
                    String coords = sb.toString();
                    if (coords.length() > 0) {
                        coords = coords.substring(0, coords.length() - 1);
                        if (!StringUtils.isEmptyOrNull(fileId))
                            insertSqlList.add("insert into POLYGONS (GID, NAME, LINECOLOR,FILLCOLOR, STATE, FILEID, geometrys,description)" +
                                    " values " +
                                    "('" + StringUtils.newGUID() + "','" + name + "','" + Color.parseColor("#00ff00")
                                    + "','" + Color.parseColor("#8000ff00") + "','0','" + fileId + "'," +
                                    "GeomFromText('POLYGON((" + coords + "))', 4326)" + ",'" + description + "')");
                    }
                }

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            loadValue += new String(ch, start, length);
        }
    }
}