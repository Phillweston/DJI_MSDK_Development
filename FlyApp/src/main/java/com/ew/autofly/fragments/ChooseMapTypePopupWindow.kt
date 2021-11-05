package com.ew.autofly.fragments

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.dji.mapkit.core.maps.DJIMap
import com.ew.autofly.R

class ChooseMapTypePopupWindow(context: Context) : PopupWindow(context) {

    lateinit var chooseMapTypeClickListener: ChooseMapTypeClickListener

    interface ChooseMapTypeClickListener {

        fun onClick(map: Int)

    }

    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(context.resources.getColor(R.color.white) ?: 0))
        val contentView: View = LayoutInflater.from(context).inflate(R.layout.popup_choose_map_type, null, false)
        val layoutMapDefault = contentView.findViewById<LinearLayout>(R.id.layout_map_default)
        val layoutMapSatellite = contentView.findViewById<LinearLayout>(R.id.layout_map_satellite)
        layoutMapDefault.setOnClickListener {
            chooseMapTypeClickListener.onClick(DJIMap.MAP_TYPE_NORMAL)
        }
        layoutMapSatellite.setOnClickListener {
            chooseMapTypeClickListener.onClick(DJIMap.MAP_TYPE_HYBRID)
        }
        setContentView(contentView)
    }


}