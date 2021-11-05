package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ew.autofly.R
import com.ew.autofly.entity.LatLngInfo

class PointTaskListAdapter : ListAdapter<LatLngInfo, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    interface RecyclerTaskClickListener {
        fun showTaskTypeDialog()
        fun showKMLDialog()
        fun clickTaskItem(task: LatLngInfo)
    }

    lateinit var recyclerTaskClickListener: RecyclerTaskClickListener

    var highlightIndex: Int = 0
        set(value) {
            field = value
            this@PointTaskListAdapter.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LatLngInfoViewHolder(View.inflate(parent.context, R.layout.layout_circle_task_list_title, null))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as LatLngInfoViewHolder
        if (highlightIndex == position) {
            holder.layoutBottomLine.visibility = View.VISIBLE
        } else {
            holder.layoutBottomLine.visibility = View.INVISIBLE
        }
        val task = getItem(position)
        holder.textTaskName.text = "航点${position+1}"
        holder.itemView.setOnClickListener {
            recyclerTaskClickListener.clickTaskItem(task)
        }
    }

    companion object {

        val TAG = PointTaskListAdapter::class.simpleName

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LatLngInfo>() {

            override fun areItemsTheSame(oldItem: LatLngInfo, newItem: LatLngInfo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LatLngInfo, newItem: LatLngInfo): Boolean {
                return oldItem.id == newItem.id
            }

        }

    }
}

class LatLngInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textTaskName: TextView by lazy { itemView.findViewById<TextView>(R.id.text_task_name) }
    val layoutBottomLine: LinearLayout by lazy { itemView.findViewById<LinearLayout>(R.id.layout_bottom_line) }
}
