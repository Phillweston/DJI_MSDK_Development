package com.ew.autofly.mode.linepatrol.point.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.ew.autofly.R
import dji.common.mission.waypoint.WaypointActionType


class EditFlyActionPopupWindow(context: Context?) : PopupWindow(context) {

    private var text_action_stay: TextView
    private var text_action_start_take_photo: TextView
    private var text_action_start_record: TextView
    private var text_action_stop_record: TextView
    private var text_action_gimbal_pitch: TextView
    private var text_action_rotate_aircraft: TextView

    lateinit var flyActionClickListener: FlyActionClickListener

    interface FlyActionClickListener {
        fun onActionClick(actionType: Int)
    }

    init {
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(context?.resources?.getColor(R.color.white) ?: 0))
        val contentView: View = LayoutInflater.from(context).inflate(R.layout.popup_edit_fly_action, null, false)
        text_action_stay = contentView.findViewById(R.id.text_action_stay)
        text_action_stay.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.STAY.value())
        }
        text_action_start_take_photo = contentView.findViewById(R.id.text_action_start_take_photo)
        text_action_start_take_photo.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.START_TAKE_PHOTO.value())
        }
        text_action_start_record = contentView.findViewById(R.id.text_action_start_record)
        text_action_start_record.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.START_RECORD.value())
        }
        text_action_stop_record = contentView.findViewById(R.id.text_action_stop_record)
        text_action_stop_record.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.STOP_RECORD.value())
        }
        text_action_gimbal_pitch = contentView.findViewById(R.id.text_action_gimbal_pitch)
        text_action_gimbal_pitch.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.GIMBAL_PITCH.value())
        }
        text_action_rotate_aircraft = contentView.findViewById(R.id.text_action_rotate_aircraft)
        text_action_rotate_aircraft.setOnClickListener {
            flyActionClickListener.onActionClick(WaypointActionType.ROTATE_AIRCRAFT.value())
        }
        setContentView(contentView)
    }


}