package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ew.autofly.R
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.widget.CustomFlySeekBarLayout
import dji.common.mission.waypoint.WaypointActionType

class ActionListAdapter : ListAdapter<FlyPointAction, RecyclerView.ViewHolder>(DIFF_CALLBACK) {




    lateinit var itemDeleteListener: ItemDeleteListener

    lateinit var itemChangeListener: ItemChangeListener

    interface ItemDeleteListener {

        fun onItemDelete(position: Int)

    }

    interface ItemChangeListener {

        fun onItemChange(position: Int, action: Int, value: Float)

    }

    companion object {

        val TAG = ActionListAdapter::class.simpleName

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FlyPointAction>() {

            override fun areItemsTheSame(oldItem: FlyPointAction, newItem: FlyPointAction): Boolean {
                return oldItem.action == newItem.action
            }

            override fun areContentsTheSame(oldItem: FlyPointAction, newItem: FlyPointAction): Boolean {
                return oldItem.id == newItem.id
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getAction()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == WaypointActionType.ROTATE_AIRCRAFT.value()) {//飞行器航角
            return ActionRotateAircraftViewHolder(View.inflate(parent.context, R.layout.layout_fly_action_rotate_aircraft, null))
        } else if (viewType == WaypointActionType.STAY.value()) {//悬停
            return ActionStayViewHolder(View.inflate(parent.context, R.layout.layout_fly_action_stay, null))
        } else if (viewType == WaypointActionType.GIMBAL_PITCH.value()) {//云台俯仰角
            return ActionGimbalPitchViewHolder(View.inflate(parent.context, R.layout.layout_fly_action_gimbal_pitch, null))
        }
        return ActionViewHolder(View.inflate(parent.context, R.layout.layout_fly_action, null))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as BaseDeleteViewHolder
        val item = getItem(position)
        if (holder.itemViewType == WaypointActionType.STAY.value()) {
            holder as ActionStayViewHolder
            holder.rangeBar.setProgress(item.staySecond.toFloat())
            holder.rangeBar.customFlySeekBarChangeListener = object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {

                override fun onValueChange(value: Float) {
                    itemChangeListener.onItemChange(position, WaypointActionType.STAY.value(), value)
                }

            }
        } else if (holder.itemViewType == WaypointActionType.ROTATE_AIRCRAFT.value()) {
            holder as ActionRotateAircraftViewHolder
            holder.rangeBar.setProgress(item.rotateAircraft.toFloat())
            holder.rangeBar.customFlySeekBarChangeListener = object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {

                override fun onValueChange(value: Float) {
                    itemChangeListener.onItemChange(position, WaypointActionType.ROTATE_AIRCRAFT.value(), value)
                }

            }
        } else if (holder.itemViewType == WaypointActionType.GIMBAL_PITCH.value()) {
            holder as ActionGimbalPitchViewHolder
            holder.rangeBar.setProgress(item.gimbalPitch.toFloat())
            holder.rangeBar.customFlySeekBarChangeListener = object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {

                override fun onValueChange(value: Float) {
                    itemChangeListener.onItemChange(position, WaypointActionType.GIMBAL_PITCH.value(), value)
                }

            }
        } else {
            holder as ActionViewHolder
            holder.textTaskName.text = item.actionName
        }
        holder.btnDelete.setOnClickListener {
            itemDeleteListener.onItemDelete(position)
        }
    }

}

open class BaseDeleteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val btnDelete: TextView by lazy { itemView.findViewById<TextView>(R.id.btn_delete) }
}

class ActionViewHolder(itemView: View) : BaseDeleteViewHolder(itemView) {
    val textTaskName: TextView by lazy { itemView.findViewById<TextView>(R.id.text_task_name) }
}

class ActionRotateAircraftViewHolder(itemView: View) : BaseDeleteViewHolder(itemView) {
    val rangeBar: CustomFlySeekBarLayout by lazy { itemView.findViewById<CustomFlySeekBarLayout>(R.id.fly_range) }
}

class ActionGimbalPitchViewHolder(itemView: View) : BaseDeleteViewHolder(itemView) {
    val rangeBar: CustomFlySeekBarLayout by lazy { itemView.findViewById<CustomFlySeekBarLayout>(R.id.fly_range) }
}

class ActionStayViewHolder(itemView: View) : BaseDeleteViewHolder(itemView) {
    val rangeBar: CustomFlySeekBarLayout by lazy { itemView.findViewById<CustomFlySeekBarLayout>(R.id.fly_range) }
}
