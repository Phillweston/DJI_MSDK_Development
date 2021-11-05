package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.os.Bundle
import com.ew.autofly.R
import com.ew.autofly.entity.AirRouteParameter
import com.ew.autofly.entity.LatLngInfo
import com.ew.autofly.mode.linepatrol.point.ui.base.BasePresenterFragment
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointChildInfoPresenter
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointChildInfoView
import kotlinx.android.synthetic.main.fragment_point_child_info.*
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList

class PointChildInfoContainerFragment : BasePresenterFragment(), PointChildInfoView {

    private val taskId by lazy { arguments?.getString(TASK_ID) ?: "" }

    override var layoutId = R.layout.fragment_point_child_info

    override val presenter by lazy { PointChildInfoPresenter(this, repository) }

    override fun initializeUI(savedInstanceState: Bundle?) {
//        presenter.getTaskChildInfo(taskId)
        initListAdapter()
    }

    override fun registerListeners() {

    }

    private fun addAllFlyTask() {
        if (mLinePointMer.isNotEmpty()) {
            val beginTransaction = childFragmentManager.beginTransaction()
            for (i in 0 until mLinePointMer.size) {
                val latLngInfo = mLinePointMer[i]
                val newInstance = PointInfoEditFragment.newInstance(
                    this,
                    latLngInfo.id,airRoutePara,
                    object : PointInfoEditFragment.OnFlyAreaPointChange {

                        override fun flyAreaPointChange(flyAreaPoints: ArrayList<FlyAreaPoint>) {
                            latLngInfo.flyTask.flyAreaPoints = flyAreaPoints
                        }

                    }, object : PointInfoEditFragment.OnFlyParamChange {
                        override fun flyParamChange(type: String, value: Float) {
                            when (type) {
                                "yaw" -> {
                                    latLngInfo.flyTask.yaw = value.toInt()
                                }
                                "pitch" -> {
                                    latLngInfo.flyTask.pitch = value.toInt()
                                }
                                "height" -> {
                                    latLngInfo.flyTask.flyHeight = value
                                }
                                "speed" -> {
                                    latLngInfo.flyTask.speed = value
                                }
                            }
                        }

                    })
                beginTransaction.add(R.id.circle_fragment_container, newInstance, latLngInfo.id)
                if (i == 0) {
                    beginTransaction.show(newInstance)
                } else {
                    beginTransaction.hide(newInstance)
                }
            }
            beginTransaction.commit()
        }
    }

    private val pointTaskListAdapter by lazy { PointTaskListAdapter() }

    private fun initListAdapter() {
        pointTaskListAdapter.recyclerTaskClickListener =
            object : PointTaskListAdapter.RecyclerTaskClickListener {
                override fun showTaskTypeDialog() {

                }

                override fun showKMLDialog() {
                }

                override fun clickTaskItem(flyTask: LatLngInfo) {
                    updateHighlightFlyTask(flyTask.id)
//                    sendHighlightIntent(flyTask.flyAreaPoints.first().id)
                }

            }
        taskCircleListView.adapter = pointTaskListAdapter
        pointTaskListAdapter.submitList(mLinePointMer)
    }

    private fun sendHighlightIntent(id: String) {
//        val intent = Intent(MapFragment.ON_UPDATE_SELECTED_POINT)
//        intent.putExtra("id", id)
//        requireContext().sendBroadcast(intent)
    }

    /**
     * 点击高亮任务
     */
    fun updateHighlightFlyTask(id: String) {
        pointTaskListAdapter.highlightIndex = mLinePointMer.indexOfFirst { it.id == id }
        val beginTransaction = childFragmentManager.beginTransaction()
        childFragmentManager.findFragmentByTag(id)?.let { it ->
            childFragmentManager.fragments.forEach {
                beginTransaction.hide(it)
            }
            beginTransaction.show(it).commit()
        }
    }

    fun onAddNewTaskFinish(newFlyTask: LatLngInfo) {
        val beginTransaction = childFragmentManager.beginTransaction()
        val newInstance = PointInfoEditFragment.newInstance(
            this,
            newFlyTask.id,this.airRoutePara,
            object : PointInfoEditFragment.OnFlyAreaPointChange {
                override fun flyAreaPointChange(flyAreaPoints: ArrayList<FlyAreaPoint>) {
                    newFlyTask.flyTask.flyAreaPoints = flyAreaPoints
                }

            },
            object : PointInfoEditFragment.OnFlyParamChange {
                override fun flyParamChange(type: String, value: Float) {
                    when (type) {
                        "yaw" -> {
                            newFlyTask.flyTask.yaw = value.toInt()
                        }
                        "pitch" -> {
                            newFlyTask.flyTask.pitch = value.toInt()
                        }
                        "height" -> {
                            newFlyTask.flyTask.flyHeight = value
                        }
                        "speed" -> {
                            newFlyTask.flyTask.speed = value
                        }
                    }
                }

            })
        childFragmentManager.fragments.forEach {
            beginTransaction.hide(it)
        }
        beginTransaction.add(R.id.circle_fragment_container, newInstance, newFlyTask.id)
            .show(newInstance)
        runOnUiThread {
            beginTransaction.commitNow()
        }
    }

    fun onChildDeleteFinish(newFlyTask: LatLngInfo) {
        runOnUiThread {
            val beginTransaction = childFragmentManager.beginTransaction()
            val findFragmentById =
                childFragmentManager.findFragmentByTag(newFlyTask.id) ?: return@runOnUiThread
            beginTransaction.hide(findFragmentById)
            beginTransaction.remove(findFragmentById)
            val orNull =
                childFragmentManager.fragments.lastOrNull { it.tag.toString() != newFlyTask.id }
            if (orNull != null) {
                beginTransaction.show(orNull)
            }
            beginTransaction.commit()
            val taskList = mLinePointMer
            val index = taskList.indexOfFirst { it.id == newFlyTask.id }
            if (index > -1) {
                taskList.removeAt(index)
                val taskListSize = taskList.size
                pointTaskListAdapter.highlightIndex = taskListSize - 1
            }
        }
    }

    companion object {

        const val TASK_ID = "taskId"

        fun newInstance(): PointChildInfoContainerFragment {
            val circleInfoFragment = PointChildInfoContainerFragment()
            circleInfoFragment.arguments = Bundle().apply {
//                putString(TASK_ID, flyTask.id)
            }
            return circleInfoFragment
        }

    }

    override fun getTaskInfoFinish() {
        runOnUiThread {
            initListAdapter()
            if (mLinePointMer.isEmpty()) {
//                addFlyTask()
            } else {
                addAllFlyTask()
            }
        }
    }

    fun updateChildTaskInfo(dragTaskId: String?) {
        if (dragTaskId != null) {
            val pointInfoEditFragment =
                childFragmentManager.findFragmentByTag(dragTaskId) as PointInfoEditFragment
            pointInfoEditFragment.updateTaskInfo()
        } else {
            childFragmentManager.fragments.forEach {
                (it as PointInfoEditFragment).updateTaskInfo()
            }
        }
    }

    override fun onAddActionFinish() {
        val fragments = childFragmentManager.fragments
        fragments.forEach {
            it as PointInfoEditFragment
            it.onAddActionFinish()
        }
    }

    fun addChildTaskInfo(addIndex: Int, childTaskId: String) {
        presenter.addNewTask(addIndex, childTaskId)
    }

    fun deleteChildTask(flyAreaPoint: FlyAreaPoint) {
        val firstOrNull =
            childFragmentManager.fragments.firstOrNull { it.tag == flyAreaPoint.targetId }
        if (firstOrNull is PointInfoEditFragment) {
            firstOrNull.deleteCurrentTask()
        }
    }

    fun onTouchPointChange(mLinePointReverseMer: ArrayList<LatLngInfo>?) {
        val beginTransaction = childFragmentManager.beginTransaction()
//        childFragmentManager.fragments.forEach {
//            beginTransaction.remove(it)
//        }
        mLinePointReverseMer?.forEach {
//            beginTransaction.add()
        }
//        beginTransaction.commit()
    }

    private var mLinePointMer = CopyOnWriteArrayList<LatLngInfo>()

    private var airRoutePara: AirRouteParameter? = null

    @Synchronized
    fun refreshChildInfo(linePointMer: ArrayList<LatLngInfo>, airRoutePara: AirRouteParameter) {
        this.airRoutePara = airRoutePara
//        doAsync {
        val addPoint = mutableListOf<LatLngInfo>()
//        val removePoint = mutableListOf<LatLngInfo>()
        if (linePointMer.size > mLinePointMer.size) {
            //添加
            linePointMer.forEach { latLngInfo ->
                if (mLinePointMer.indexOfFirst { point -> latLngInfo.id == point.id } == -1) {
                    addPoint.add(latLngInfo)
                    mLinePointMer.add(latLngInfo)
                    onAddNewTaskFinish(latLngInfo)
                }
            }
        } else {
            //删除
            mLinePointMer.forEach { latLngInfo ->
                if (linePointMer.indexOfFirst { point -> latLngInfo.id == point.id } == -1) {
//                    removePoint.add(latLngInfo)
                    onChildDeleteFinish(latLngInfo)
                }
            }
        }
//            runOnUiThread {

        pointTaskListAdapter.highlightIndex = mLinePointMer.size - 1
//        pointTaskListAdapter.notifyDataSetChanged()
//            }
//        }
//        mLinePointMer.addAll(addPoint)
//        pointTaskListAdapter.notifyDataSetChanged()
//        println(linePointMer)
    }

}