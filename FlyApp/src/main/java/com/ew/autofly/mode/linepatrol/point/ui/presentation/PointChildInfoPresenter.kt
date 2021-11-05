package com.ew.autofly.mode.linepatrol.point.ui.presentation

import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask

class PointChildInfoPresenter(val view: PointChildInfoView, repository: Repository) : BasePresenter(repository) {

    lateinit var taskList: MutableList<FlyTask>

    override fun onCreate() {
    }

    private lateinit var taskId: String

    fun getTaskChildInfo(taskId: String) {
        doAsync {
            this.taskId = taskId
            taskList = repository.getChildTaskList(taskId).sortedBy { it.flyAreaPoints.first().createTime }.toMutableList()
            view.getTaskInfoFinish()
        }
    }

    fun addNewTask(addIndex: Int, taskId: String) {
//        val task = repository.getTask(taskId)
//        doAsyncWidthTaskUpdate(task) {
//            if (addIndex > 0) {
//                taskList.add(addIndex, task)
//                view.onAddNewTaskFinish(addIndex, task)
//            } else {
//                taskList.add(task)
//                view.onAddNewTaskFinish(taskList.size - 1, task)
//            }
//
////            super.updateTaskNotSave(taskId)
//        }

    }

}
