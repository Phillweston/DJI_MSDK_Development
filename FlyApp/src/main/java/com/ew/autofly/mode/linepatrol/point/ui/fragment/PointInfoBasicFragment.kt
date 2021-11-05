package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.os.Bundle
import com.ew.autofly.R
import com.ew.autofly.entity.AirRouteParameter
import com.ew.autofly.interfaces.OnSettingDialogClickListener
import com.ew.autofly.mode.linepatrol.point.ui.base.BasePresenterFragment
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoBasicPresenter
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoBasicView
import com.ew.autofly.widgets.ActionEditText
import kotlinx.android.synthetic.main.fragment_point_info_basic.*

class PointInfoBasicFragment : BasePresenterFragment(), PointInfoBasicView,
    OnSettingDialogClickListener {

    private val taskId by lazy { arguments?.getString("taskId") ?: "" }

    override val presenter by lazy { PointInfoBasicPresenter(this, repository) }

    override fun getTaskInfoFinish() {
        runOnUiThread {
            setTask()
        }
    }

    override var layoutId = R.layout.fragment_point_info_basic

    override fun initializeUI(savedInstanceState: Bundle?) {
        presenter.getCameraList()
        presenter.getTaskInfo(taskId)
        setLocation()
    }

    override fun onGetCameraListFinish() {
        runOnUiThread {
        }
    }

    override fun registerListeners() {
        text_task_name.simpleTextWatchListener = object : ActionEditText.SimpleTextWatchListener {

            override fun afterTextChanged(value: String) {
                presenter.updateTaskName(value)
            }

        }
    }

    private fun setTask() {
        text_task_name.setText(presenter.flyTask.taskName)
    }

    fun saveTask(): Boolean {
        return false
    }

    fun setLocation() {
    }

    companion object {

        fun newInstance(flyTask: FlyTask, latitude: Double, longitude: Double): PointInfoBasicFragment {
            val pointInfoEditFragment = PointInfoBasicFragment()
            pointInfoEditFragment.arguments = Bundle().apply {
                putString("taskId", flyTask.id)
                putDouble("latitude", latitude)
                putDouble("longitude", longitude)
            }
            return pointInfoEditFragment
        }

    }

    override fun onSettingDialogConfirm(tag: String?, obj: Any?, params: AirRouteParameter?) {

    }

}