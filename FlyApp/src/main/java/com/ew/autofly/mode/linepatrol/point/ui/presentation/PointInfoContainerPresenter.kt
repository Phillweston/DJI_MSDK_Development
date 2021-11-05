package com.ew.autofly.mode.linepatrol.point.ui.presentation

import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask

class PointInfoContainerPresenter(val view: PointInfoContainerView, repository: Repository) : BasePresenter(repository) {

    var selectedFlyAreaPoint: FlyAreaPoint? = null

    lateinit var currentFlyTask: FlyTask

    override fun onCreate() {
    }

    fun getTaskInfo(taskId: String) {
        doAsync {
            currentFlyTask = repository.getTask(taskId)
            view.getTaskInfoFinish()
        }
    }

}
