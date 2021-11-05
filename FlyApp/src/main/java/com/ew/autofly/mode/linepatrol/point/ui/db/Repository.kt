package com.ew.autofly.mode.linepatrol.point.ui.db

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyCamera
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import java.util.ArrayList

interface Repository {
    fun getTaskOnly(pId: String?): FlyTask
    fun getTask(taskId: String): FlyTask
    fun getFlyAreaPointByTaskId(taskId: String): List<FlyAreaPoint>
    fun saveTask(flyTask: FlyTask)
    fun saveTaskPoint(flyAreaPoint: FlyAreaPoint?)
    fun saveFlyPointAction(flyPointAction: FlyPointAction)
    fun removeTask(id: String?)
    fun deleteFlyPointAction(id: String?)
    fun getFlyPointActions(id: String?): List<FlyPointAction>
    fun getCameraList(): List<FlyCamera>
    fun getChildTaskList(id: String): List<FlyTask>
    fun saveTaskList(childTaskList: List<FlyTask>)
    fun saveTaskPoints(id: String?, flyAreaPoints: ArrayList<FlyAreaPoint>?)
    fun deleteFlyPointByActionAndPointId(action: Int, id: String?)
}