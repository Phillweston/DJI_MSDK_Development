package com.ew.autofly.mode.linepatrol.point.ui.fragment

import android.os.Bundle
import com.ew.autofly.R
import com.ew.autofly.entity.AirRouteParameter
import com.ew.autofly.entity.LatLngInfo
import com.ew.autofly.interfaces.OnSettingDialogClickListener
import com.ew.autofly.mode.linepatrol.TouchPointChangeListener
import com.ew.autofly.mode.linepatrol.point.ui.base.BasePresenterFragment
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoContainerPresenter
import com.ew.autofly.mode.linepatrol.point.ui.presentation.PointInfoContainerView
import com.ew.autofly.mode.linepatrol.widget.dialog.LinePatrolSettingFragment
import kotlinx.android.synthetic.main.fragment_point_container.*
import java.util.ArrayList

class PointInfoContainerFragment : BasePresenterFragment(), PointInfoContainerView,
    TouchPointChangeListener {

    private val taskId by lazy { arguments?.getString("taskId") ?: "" }

    override val presenter by lazy { PointInfoContainerPresenter(this, repository) }

    lateinit var act: OnSettingDialogClickListener

    override var layoutId = R.layout.fragment_point_container

    override fun initializeUI(savedInstanceState: Bundle?) {
        presenter.getTaskInfo(taskId)
    }

    override fun registerListeners() {
        image_basic_info.setOnClickListener {
            val beginTransaction = childFragmentManager.beginTransaction()
            childFragmentManager.fragments.forEach {
                beginTransaction.hide(it)
            }
            beginTransaction.show(pointInfoBasicFragment)
            beginTransaction.commit()
        }
        image_fly_point.setOnClickListener {
            val beginTransaction = childFragmentManager.beginTransaction()
            childFragmentManager.fragments.forEach {
                beginTransaction.hide(it)
            }
            beginTransaction.show(pointChildInfoFragment)
            beginTransaction.commit()
        }
    }

    private val pointInfoBasicFragment by lazy {
        LinePatrolSettingFragment.newInstance(
            this.arguments,
            act
        )
    }

    private val pointChildInfoFragment by lazy {
        PointChildInfoContainerFragment.newInstance()
    }

    private fun switchFragment() {
        val beginTransaction = childFragmentManager.beginTransaction()
        beginTransaction.add(R.id.container, pointChildInfoFragment, "pointChildInfoFragment")
        beginTransaction.add(R.id.container, pointInfoBasicFragment, "pointInfoBasicFragment")
        beginTransaction.hide(pointChildInfoFragment)
        beginTransaction.show(pointInfoBasicFragment)
        beginTransaction.commit()
    }

    override fun updateChildTaskInfo(dragTaskId: String?) {
        pointChildInfoFragment.updateChildTaskInfo(dragTaskId)
    }

    override fun onAddActionFinish() {
        pointChildInfoFragment.onAddActionFinish()
    }

    fun addChildTaskInfo(addIndex: Int, childTaskId: String) {
        pointChildInfoFragment.addChildTaskInfo(addIndex, childTaskId)
    }

    override fun getTaskInfoFinish() {
        switchFragment()
    }

    fun setSelectedFlyAreaPoint(flyAreaPoint: FlyAreaPoint) {
        presenter.selectedFlyAreaPoint = flyAreaPoint
        pointChildInfoFragment.updateHighlightFlyTask(flyAreaPoint.targetId)
    }

    fun deleteSelectedTask(flyAreaPoint: FlyAreaPoint?) {
        if (flyAreaPoint != null) {
            pointChildInfoFragment.deleteChildTask(flyAreaPoint)
        } else {
            presenter.selectedFlyAreaPoint?.let { pointChildInfoFragment.deleteChildTask(it) }
        }
    }

    companion object {

        fun newInstance(
            taskId: String,
            latitude: Double,
            longitude: Double,
            bundle: Bundle, act: OnSettingDialogClickListener
        ): PointInfoContainerFragment {
            val fragment = PointInfoContainerFragment()
            fragment.act = act
            fragment.arguments = Bundle().apply {
                putString("taskId", taskId)
                putDouble("latitude", latitude)
                putDouble("longitude", longitude)
                this.putAll(bundle)
            }
            return fragment;
        }

    }

    override fun onTouchPointChange(mLinePointReverseMer: ArrayList<LatLngInfo>?) {
        pointChildInfoFragment.onTouchPointChange(mLinePointReverseMer)
    }

    fun refreshChildInfo(
        mLinePointMer: ArrayList<LatLngInfo>,
        airRoutePara: AirRouteParameter
    ) {
        pointChildInfoFragment.refreshChildInfo(mLinePointMer,airRoutePara)
    }

}