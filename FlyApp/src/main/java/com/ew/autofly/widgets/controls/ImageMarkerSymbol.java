package com.ew.autofly.widgets.controls;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.esri.core.symbol.PictureMarkerSymbol;


public class ImageMarkerSymbol extends PictureMarkerSymbol {

    public ImageMarkerSymbol(Context context, Drawable drawable){
        super(context,drawable);
    }

    public ImageMarkerSymbol(Drawable drawable) {
        super(drawable);
    }

    public void setWidth(float width){
        super.setWidth(width);
    }

    public void setHeight(float height){
        super.setHeight(height);
    }
}
