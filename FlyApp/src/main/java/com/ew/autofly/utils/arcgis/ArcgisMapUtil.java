package com.ew.autofly.utils.arcgis;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.TextSymbol;
import com.ew.autofly.entity.LatLngInfo;
import com.ew.autofly.widgets.controls.ImageMarkerSymbol;
import com.flycloud.autofly.base.util.SysUtils;

import static com.ew.autofly.xflyer.utils.CoordinateUtils.gps84ToMapMercator;
import static com.ew.autofly.xflyer.utils.CoordinateUtils.lonLatToMercator;



public class ArcgisMapUtil {


    private static final double MAP_SCALE_DIVISOR = 1.0E8;

    private static final int DEFAULT_MAP_SCALE_TIMES = 16;

    private static final float DEFAULT_DENSITY = 3.0F;


    private static double minMapScale = 0;

    /**
     * 是否已经初始化
     *
     * @return
     */
    public static boolean isInitialized() {
        return minMapScale != 0;
    }

    public static void initialize(double minScale) {
        minMapScale = minScale;
    }

    /**
     * 获取默认缩放比例
     *
     * @return
     */
    public static float getZoomScale() {
        return (float) (minMapScale / (Math.pow(2, DEFAULT_MAP_SCALE_TIMES)));
    }

    public static ImageMarkerSymbol getImageMarkerSymbol(Context context, int resId) {
        return getImageMarkerSymbol(context, resId, 0, 0);
    }

    public static ImageMarkerSymbol getImageMarkerSymbol(Context context, Drawable drawable) {
        return getImageMarkerSymbol(context, drawable, 0, 0);
    }

    /**
     * 以desnity:3.0(1920*1080)标准适配，最终效果是屏幕物理尺寸越大，图标显示越大（等比例）
     *
     * @param context
     * @param resId
     * @param width
     * @param height
     * @return
     */
    public static ImageMarkerSymbol getImageMarkerSymbol(Context context, int resId, float width, float height) {
        Drawable drawable = context.getResources().getDrawable(resId);
        return getImageMarkerSymbol(context, drawable, width, height);
    }

    /**
     * 以desnity:3.0(1920*1080)标准适配，最终效果是屏幕物理尺寸越大，图标显示越大（等比例）
     *
     * @param context
     * @param drawable
     * @param width
     * @param height
     * @return
     */
    public static ImageMarkerSymbol getImageMarkerSymbol(Context context, Drawable drawable, float width, float height) {
        float density = context.getResources().getDisplayMetrics().density;

        float densityFactor = DEFAULT_DENSITY / density;

        if (width == 0) {

            width = drawable.getIntrinsicWidth();
        } else {
            width = width / densityFactor;
        }
        if (height == 0) {
            height = drawable.getIntrinsicHeight();
        } else {
            height = height / densityFactor;
        }

        ImageMarkerSymbol symbol = new ImageMarkerSymbol(drawable);
        symbol.setWidth(width * getEqualRatioFactor(context));
        symbol.setHeight(height * getEqualRatioFactor(context));
        return symbol;
    }

    /**
     * 以desnity:3.0(1920*1080)标准适配，最终效果是不同物理尺寸的屏幕显示基本一样大
     *
     * @return
     */
    public static TextSymbol getTextSymbol(Context context, int size, String text, int color) {
        TextSymbol symbol = new TextSymbol((int) (size * getEqualFactor(context)), text, color);
        if (SysUtils.isSupportDroidSans())
            symbol.setFontFamily("DroidSansFallback.ttf");
        return symbol;
    }

    /**
     * 以desnity:3.0(1920*1080)标准适配，最终效果是不同物理尺寸的屏幕显示基本一样大
     *
     * @return
     */
    public static SimpleLineSymbol getSimpleLineSymbol(Context context, int color, float width) {
        return new SimpleLineSymbol(color, width * getEqualFactor(context));
    }

    /**
     * 等比例大小因子
     *
     * @param context
     * @return
     */
    public static float getEqualRatioFactor(Context context) {
        float density = context.getResources().getDisplayMetrics().density;

        float densityFactor = DEFAULT_DENSITY / density;

        float scaleFactor = (float) (MAP_SCALE_DIVISOR / minMapScale);
        float factor = density * scaleFactor * densityFactor * densityFactor;
        return factor;
    }

    /**
     * 相同大小因子
     *
     * @param context
     * @return
     */
    public static float getEqualFactor(Context context) {
        float density = context.getResources().getDisplayMetrics().density;

        float densityFactor = DEFAULT_DENSITY / density;

        float scaleFactor = (float) (MAP_SCALE_DIVISOR / minMapScale);
        float factor = density * scaleFactor * densityFactor;
        return factor;
    }

    /**
     * 更新标记到地图上(gps84坐标)
     *
     * @param context
     * @param layer
     * @param gps84
     * @param resId
     */
    public static void updateMarkerToMapByGps84(Context context, GraphicsLayer layer, LatLngInfo gps84, int resId) {
        LatLngInfo mercator = gps84ToMapMercator(gps84.longitude, gps84.latitude);
        updateMarkerToMapByMercator(context, layer, mercator, resId);
    }

    /**
     * (注：弃用，不建议使用)
     * 更新标记到地图上(gcj坐标)
     *
     * @param context
     * @param layer
     * @param gcj
     * @param resId
     */
    @Deprecated
    public static void updateMarkerToMapByGcj(Context context, GraphicsLayer layer, LatLngInfo gcj, int resId) {
        LatLngInfo mercator = lonLatToMercator(gcj.longitude, gcj.latitude);
        updateMarkerToMapByMercator(context, layer, mercator, resId);
    }

    /**
     * 更新标记到地图上（墨卡托坐标）
     *
     * @param context
     * @param layer
     * @param resId
     */
    public static void updateMarkerToMapByMercator(Context context, GraphicsLayer layer, LatLngInfo mercator, int resId) {
        if (layer == null) {
            return;
        }
        if (layer.getNumberOfGraphics() < 1) {
            ImageMarkerSymbol symbol = ArcgisMapUtil.getImageMarkerSymbol(context, resId);
            Graphic graphic = new Graphic(new Point(mercator.longitude, mercator.latitude), symbol);
            layer.addGraphic(graphic);
        } else {
            int id = layer.getGraphicIDs()[0];
            layer.updateGraphic(id, new Point(mercator.longitude, mercator.latitude));
        }
    }
}
