package com.ew.autofly.mode.linepatrol.point.ui.db

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyCamera
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import java.util.ArrayList

class RepositoryImpl(private val local: LocalDataSource) : Repository {

    override fun getTaskOnly(pId: String?): FlyTask {
        val flyTask = FlyTask()
        return flyTask
    }

    override fun getTask(taskId: String): FlyTask {
        val flyTask = FlyTask()
        flyTask.id = "task1"
        for (i in 0..10) {
            val element = FlyTask()
            element.id = "$i"
            element.setTaskName("task$i")
            flyTask.flyTaskItemList.add(element)
        }
        return flyTask
    }

    override fun getFlyAreaPointByTaskId(taskId: String): List<FlyAreaPoint> {
        return emptyList()
    }

    override fun saveTask(flyTask: FlyTask) {

    }

    override fun saveTaskPoint(flyAreaPoint: FlyAreaPoint?) {
        TODO("Not yet implemented")
    }

    override fun saveFlyPointAction(flyPointAction: FlyPointAction) {
        local.saveFlyPointAction(flyPointAction)
    }

    override fun removeTask(id: String?) {
        TODO("Not yet implemented")
    }

    override fun deleteFlyPointAction(id: String?) {
        TODO("Not yet implemented")
    }

    override fun getFlyPointActions(id: String?): List<FlyPointAction> {
        return ArrayList()
    }

    override fun getCameraList(): List<FlyCamera> {
        TODO("Not yet implemented")
    }

    override fun getChildTaskList(taskId: String): List<FlyTask> {
        val childTaskList = local.getChildTaskList(taskId)
        childTaskList.forEach {
            it.flyAreaPoints = getFlyAreaPointByTaskId(it.id) as ArrayList<FlyAreaPoint>?
        }
        return childTaskList
    }

    override fun saveTaskList(childTaskList: List<FlyTask>) {
        TODO("Not yet implemented")
    }

    override fun saveTaskPoints(id: String?, flyAreaPoints: ArrayList<FlyAreaPoint>?) {
        TODO("Not yet implemented")
    }

    override fun deleteFlyPointByActionAndPointId(action: Int, id: String?) {
        TODO("Not yet implemented")
    }
}