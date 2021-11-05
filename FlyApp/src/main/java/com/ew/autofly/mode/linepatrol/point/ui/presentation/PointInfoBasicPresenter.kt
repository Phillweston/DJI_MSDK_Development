package com.ew.autofly.mode.linepatrol.point.ui.presentation

import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyCamera
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask

class PointInfoBasicPresenter(private val view: PointInfoBasicView, repository: Repository) : BasePresenter(repository) {

    lateinit var flyTask: FlyTask

    lateinit var cameraList: List<FlyCamera>

    lateinit var cameraNameList: List<String>

    override fun onCreate() {

    }

    fun getCameraList() {
        doAsync {
            cameraList = repository.getCameraList();
            cameraNameList = cameraList.map { it.name.toString() }
            view.onGetCameraListFinish()
        }
    }

    fun getTaskInfo(taskId: String) {
        doAsync {
            flyTask = repository.getTask(taskId)
            view.getTaskInfoFinish()
        }
    }

    fun updateTaskName(toString: String) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.taskName = toString
            repository.saveTask(flyTask)

            view.sendTaskUpdate(flyTask.id)

//            super.updateTaskNotSave(flyTask.id)
        }
    }

}