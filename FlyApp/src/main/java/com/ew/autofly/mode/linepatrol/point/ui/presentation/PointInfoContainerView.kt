package com.ew.autofly.mode.linepatrol.point.ui.presentation

interface PointInfoContainerView : BaseView {

    fun getTaskInfoFinish()

    fun updateChildTaskInfo(dragTaskId: String? = null)

    fun onAddActionFinish()

}
