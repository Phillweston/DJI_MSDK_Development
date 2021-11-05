package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.os.Bundle
import android.view.Gravity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ew.autofly.R
import com.ew.autofly.entity.AirRouteParameter
import com.ew.autofly.mode.linepatrol.point.ui.base.BasePresenterFragment
import com.ew.autofly.mode.linepatrol.point.ui.fragment.PointChildInfoContainerFragment.Companion.TASK_ID
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointChildInfoView
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoEditPresenter
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoEditView
import com.ew.autofly.mode.linepatrol.point.widget.CustomFlySeekBarLayout
import com.ew.autofly.mode.linepatrol.point.widget.EditFlyActionPopupWindow
import com.flycloud.autofly.base.util.SysUtils
import kotlinx.android.synthetic.main.fragment_point_info_edit.*
import java.util.ArrayList

class PointInfoEditFragment : BasePresenterFragment(), PointInfoEditView {

    lateinit var pointChildInfoView: PointChildInfoView

    private val taskId by lazy { arguments?.getString(TASK_ID) ?: "" }

    private val airRoutePara by lazy { arguments?.getSerializable("airRoutePara") as AirRouteParameter }

    override val presenter by lazy { PointInfoEditPresenter(this, repository) }

    override var layoutId = R.layout.fragment_point_info_edit

    override fun initializeUI(savedInstanceState: Bundle?) {
        presenter.getTask(taskId)
    }

    private val actionListAdapter by lazy { ActionListAdapter() }

    override fun onGetTaskFinish() {
        runOnUiThread {
            val flyTask = presenter.flyTask
            val flySpeed = airRoutePara.flySpeed.toFloat()
            val altitude = airRoutePara.altitude.toFloat()
            range_speed.setProgress(flySpeed)
            range_height.setProgress(altitude)
//            val yaw = flyTask.flyAreaPoints.first().yaw.toFloat()
//            range_yaw.setProgress(yaw)
            range_pitch.setProgress(flyTask.pitch.toFloat())
            recycler_action_list.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recycler_action_list.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            recycler_action_list.adapter = actionListAdapter
            actionListAdapter.submitList(flyTask.flyAreaPoints[0].flyPointActions)
        }
    }

    override fun registerListeners() {
        actionListAdapter.itemChangeListener = object : ActionListAdapter.ItemChangeListener {

            override fun onItemChange(position: Int, action: Int, value: Float) {
                presenter.actionItemChange(position, action, value)
            }

        }

        actionListAdapter.itemDeleteListener = object : ActionListAdapter.ItemDeleteListener {

            override fun onItemDelete(position: Int) {
                presenter.deleteItem(position)
            }

        }
//        btn_delete.setOnClickListener {
//            deleteCurrentTask()
//        }
        layout_add_action.setOnClickListener {

            val height = SysUtils.getScreenHeight(context)
            val width = SysUtils.getScreenWidth(context)
            val popupWindow = EditFlyActionPopupWindow(requireContext())
            popupWindow.height = height
            popupWindow.width = SysUtils.dp2px(requireContext(), 200)
            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            val decorView = requireActivity().window.decorView
            popupWindow.showAtLocation(decorView, Gravity.START, (width - popupWindow.width) / 2, 0)
            popupWindow.flyActionClickListener =
                object : EditFlyActionPopupWindow.FlyActionClickListener {

                    override fun onActionClick(actionType: Int) {
                        presenter.addAction(actionType)
                    }

                }
        }
        range_speed.customFlySeekBarChangeListener =
            object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {
                override fun onValueChange(value: Float) {
                    onFlyParamChange.flyParamChange("speed", value)
                }

            }
        range_height.customFlySeekBarChangeListener =
            object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {

                override fun onValueChange(value: Float) {
                    onFlyParamChange.flyParamChange("height", value)
                }

            }
        range_yaw.customFlySeekBarChangeListener =
            object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {
                override fun onValueChange(value: Float) {
                    onFlyParamChange.flyParamChange("yaw", value)
                }

            }
        range_pitch.customFlySeekBarChangeListener =
            object : CustomFlySeekBarLayout.CustomFlySeekBarChangeListener {
                override fun onValueChange(value: Float) {
//                    presenter.savePitch(value.toInt())
                    onFlyParamChange.flyParamChange("pitch", value)
                }

            }
    }

    override fun onDeleteItemFinish() {
        runOnUiThread {
            val flyAreaPoints = presenter.flyTask.flyAreaPoints
            actionListAdapter.notifyDataSetChanged()
            onFlyAreaPointChange.flyAreaPointChange(flyAreaPoints)
        }
    }

    override fun onActionItemChangeFinish() {

    }

    override fun onAddActionFinish() {
        val flyAreaPoints = presenter.flyTask.flyAreaPoints
        actionListAdapter.notifyDataSetChanged()
        onFlyAreaPointChange.flyAreaPointChange(flyAreaPoints)
        println("flyAreaPoints = ${flyAreaPoints}")
    }

    override fun onGetFlyAreaPointFinish() {
        runOnUiThread {
            actionListAdapter.notifyDataSetChanged()
        }
    }

    override fun onDeleteCurrentTask() {
//        pointChildInfoView.onChildDeleteFinish(presenter.flyTask)
    }

    fun updateTaskInfo() {
        presenter.getTask(taskId)
    }

    fun deleteCurrentTask() {
        presenter.deleteCurrentTask()
    }

    public interface OnFlyParamChange {

        fun flyParamChange(type: String, value: Float)

    }

    public interface OnFlyAreaPointChange {

        fun flyAreaPointChange(flyAreaPoints: ArrayList<FlyAreaPoint>)

    }

    lateinit var onFlyAreaPointChange: OnFlyAreaPointChange

    lateinit var onFlyParamChange: OnFlyParamChange


    companion object {

        fun newInstance(
            pointChildInfoView: PointChildInfoView,
            taskId: String, airRoutePara: AirRouteParameter?,
            onFlyAreaPointChange: OnFlyAreaPointChange, onFlyParamChange: OnFlyParamChange
        ): PointInfoEditFragment {
            val pointInfoEditFragment = PointInfoEditFragment()
            pointInfoEditFragment.onFlyAreaPointChange = onFlyAreaPointChange
            pointInfoEditFragment.onFlyParamChange = onFlyParamChange
            pointInfoEditFragment.pointChildInfoView = pointChildInfoView
            pointInfoEditFragment.arguments = Bundle().apply {
                putString("taskId", taskId)
                putSerializable("airRoutePara", airRoutePara)
            }
            return pointInfoEditFragment
        }

    }

}