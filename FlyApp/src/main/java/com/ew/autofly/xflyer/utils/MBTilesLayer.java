package com.ew.autofly.xflyer.utils;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.esri.android.map.TiledServiceLayer;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.ew.autofly.entity.LatLngInfo;

public class MBTilesLayer extends TiledServiceLayer {
	private SQLiteDatabase mapDb;
	private int mLevels = 0;
	public LatLngInfo latLngInfo;

	public MBTilesLayer(String path, LatLngInfo latLngInfo) {
		super(path);
		this.latLngInfo = latLngInfo;
		try {
			this.mapDb = SQLiteDatabase.openDatabase(path, null, 1);
		} catch (SQLException ex) {
			Log.e(getName(), ex.getMessage());
			throw ex;
		}
		double diffX = 0.0D;
		double diffY = 0.0D;

		if ((latLngInfo != null) && (latLngInfo.longitude != 0.0D)) {
			LatLngInfo gcj = CoordinateUtils.gps84_To_Gcj02(
					latLngInfo.latitude, latLngInfo.longitude);
			LatLngInfo pGCJ = CoordinateUtils.lonLatToMercator(gcj.longitude,
					gcj.latitude);
			LatLngInfo lt = CoordinateUtils.lonLatToMercator(
					latLngInfo.longitude, latLngInfo.latitude);

			diffX = pGCJ.longitude - lt.longitude;
			diffY = pGCJ.latitude - lt.latitude;
		} else {
			Cursor bounds = this.mapDb.rawQuery(
					"SELECT value FROM metadata WHERE name = 'bounds'", null);

			if (bounds.moveToFirst()) {
				String bs = bounds.getString(0);
				String[] ba = bs.split(",", 4);
				if (ba.length == 4) {
					double leftLon = Double.parseDouble(ba[0]);
					double topLat = Double.parseDouble(ba[3]);
					LatLngInfo gcj = CoordinateUtils.gps84_To_Gcj02(topLat,
							leftLon);
					LatLngInfo pGCJ = CoordinateUtils.lonLatToMercator(
							gcj.longitude, gcj.latitude);
					LatLngInfo lt = CoordinateUtils.lonLatToMercator(leftLon,
							topLat);
					this.latLngInfo = gcj;

					diffX = pGCJ.longitude - lt.longitude;
					diffY = pGCJ.latitude - lt.latitude;
				}
			}
		}

		Cursor maxLevelCur = this.mapDb.rawQuery(
				"SELECT MAX(zoom_level) AS max_zoom FROM tiles", null);
		if (maxLevelCur.moveToFirst()) {
			this.mLevels = maxLevelCur.getInt(0);
		}

		Log.i("TAG", "Max levels = " + Integer.toString(this.mLevels));

		double[] resolution = new double[this.mLevels];
		double[] scale = new double[this.mLevels];
		for (int i = 0; i < this.mLevels; i++) {
			resolution[i] = (156543.03392800014D / Math.pow(2.0D, i));

			scale[i] = (591657527.591555D / Math.pow(2.0D, i));
		}

		TiledServiceLayer.TileInfo ti = new TiledServiceLayer.TileInfo(
				new Point(-20037508.342787001D + diffX,
						20037508.342787001D + diffY), scale, resolution,
				this.mLevels, 96, 256, 256);

		setFullExtent(new Envelope(-20037508.342787001D, -20037508.342787001D,
				20037508.342787001D, 20037508.342787001D));
		setDefaultSpatialReference(SpatialReference.create(3857));
		setTileInfo(ti);
		setInitialExtent(getFullExtent());

		initLayer();
	}

	protected byte[] getTile(int level, int col, int row) throws Exception {
		int nRows = 1 << level;
		int tmsRow = nRows - 1 - row;

		Cursor imageCur = this.mapDb.rawQuery(
				"SELECT tile_data FROM tiles WHERE zoom_level = "
						+ Integer.toString(level) + " AND tile_column = "
						+ Integer.toString(col) + " AND tile_row = "
						+ Integer.toString(tmsRow), null);

		if (imageCur.moveToFirst()) {
			return imageCur.getBlob(0);
		}
		return null;
	}
}