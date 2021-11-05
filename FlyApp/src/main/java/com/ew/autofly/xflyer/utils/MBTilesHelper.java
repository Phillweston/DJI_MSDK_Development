package com.ew.autofly.xflyer.utils;

import android.util.Log;
import com.ew.autofly.entity.LatLngInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class MBTilesHelper {
	private double north;
	private double south;
	private double east;
	private double west;
	private LatLngInfo latLngInfo = new LatLngInfo();

	public LatLngInfo readMBTileKml(String path) {
		String kmlPath = path.substring(0, path.lastIndexOf('.')) + ".kml";
		File file = new File(kmlPath);
		if (file.exists()) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(false);
			try {
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				MySaxHandler handler = new MySaxHandler(this.latLngInfo);
				reader.setContentHandler(handler);
				InputSource is = new InputSource();
				is.setByteStream(new FileInputStream(file));
				is.setEncoding("utf-8");
				reader.parse(is);
			} catch (ParserConfigurationException e) {
				Log.e("KmlLoader", e.getMessage());
			} catch (SAXException e) {
				Log.e("KmlLoader", e.getMessage());
			} catch (IOException e) {
				Log.e("KmlLoader", e.getMessage());
			} catch (Exception e) {
				Log.e("MBTilesHelper", e.getMessage());
			}
		}
		return this.latLngInfo;
	}

	private class MySaxHandler extends DefaultHandler {
		private LatLngInfo latLngInfo;
		private StringBuffer content = new StringBuffer();
		private String preTag = null;

		public MySaxHandler(LatLngInfo latLngInfo) {
			this.latLngInfo = latLngInfo;
		}

		public void startDocument() throws SAXException {
			super.startDocument();
		}

		public void endDocument() throws SAXException {
			super.endDocument();
		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			try {
				if (this.preTag != null) {
					this.content.append(ch, start, length);
				}
			} catch (Exception localException) {
			}
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			this.content.delete(0, this.content.length());
			super.startElement(uri, localName, qName, attributes);
			String nameVal = localName;
			if (nameVal.equalsIgnoreCase("")) {
				nameVal = qName;
			}

			this.preTag = nameVal;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			super.endElement(uri, localName, qName);

			String nameVal = localName;
			if (nameVal.equalsIgnoreCase("")) {
				nameVal = qName;
			}
			try {
				if ("north".equals(nameVal)) {
					MBTilesHelper.this.north = Double.parseDouble(this.content
							.toString());
					if (MBTilesHelper.this.south > 0.0D)
						this.latLngInfo.latitude = ((MBTilesHelper.this.north + MBTilesHelper.this.south) / 2.0D);
				} else if ("west".equals(nameVal)) {
					MBTilesHelper.this.west = Double.parseDouble(this.content
							.toString());
					if (MBTilesHelper.this.east > 0.0D)
						this.latLngInfo.longitude = ((MBTilesHelper.this.west + MBTilesHelper.this.east) / 2.0D);
				} else if ("east".equals(nameVal)) {
					MBTilesHelper.this.east = Double.parseDouble(this.content
							.toString());
					if (MBTilesHelper.this.west > 0.0D)
						this.latLngInfo.longitude = ((MBTilesHelper.this.west + MBTilesHelper.this.east) / 2.0D);
				} else if ("south".equals(nameVal)) {
					MBTilesHelper.this.south = Double.parseDouble(this.content
							.toString());
					if (MBTilesHelper.this.north > 0.0D)
						this.latLngInfo.latitude = ((MBTilesHelper.this.north + MBTilesHelper.this.south) / 2.0D);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			this.preTag = null;
		}
	}
}