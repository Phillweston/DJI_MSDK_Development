package com.ew.autofly.mode.linepatrol.point.ui.presentation

import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import com.ew.autofly.utils.Bearing
import dji.common.mission.waypoint.WaypointActionType
import java.util.*

class PointInfoAdvancedPresenter(private val view: PointInfoAdvancedView, repository: Repository) : BasePresenter(repository) {

    var currentFlyAreaPoint: FlyAreaPoint? = null

    lateinit var flyTask: FlyTask

    override fun onCreate() {

    }

    fun updateAutoFlightSpeed(value: Float) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.autoFlightSpeed = value
            repository.saveTask(flyTask)
            view.sendTaskUpdate(flyTask.id)
        }
    }

    fun getTaskInfo(taskId: String) {
        doAsync {
            flyTask = repository.getTask(taskId)
            flyTask.flyPointActions = repository.getFlyPointActions(taskId) as ArrayList<FlyPointAction>?
            view.getTaskInfoFinish()
        }
    }

    fun saveRangeSpeed(speed: Int = 0) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.speed = speed.toFloat()
            repository.saveTask(flyTask)
            updateChildRangeSpeed(speed)
            view.onSaveRangeSpeedFinish(speed)
            view.sendTaskUpdate(flyTask.id)
        }
    }

    private fun updateChildRangeSpeed(speed: Int) {
        val childTaskList = repository.getChildTaskList(flyTask.id)
        childTaskList.forEachIndexed { index, flyTask ->
            if (flyTask.followSpeed) {
                flyTask.speed = speed.toFloat()
            }
        }
        repository.saveTaskList(childTaskList)
    }

    fun saveHeight(height: Int = 0) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.flyHeight = height.toFloat()
            repository.saveTask(flyTask)
            updateChildRangeHeight(height)
            view.onSaveRangeHeightFinish(height)
            view.sendTaskUpdate(flyTask.id)
        }
    }

    private fun updateChildRangeHeight(height: Int) {
        val childTaskList = repository.getChildTaskList(flyTask.id)
        childTaskList.forEachIndexed { index, flyTask ->
            if (flyTask.followHeight) {
                val flyAreaPoint = flyTask.flyAreaPoints[0]
                flyAreaPoint.altitude = height.toFloat()
                flyTask.flyHeight = height.toFloat()
                repository.saveTaskPoints(flyTask.id, flyTask.flyAreaPoints)
            }
        }
        repository.saveTaskList(childTaskList)
    }

    fun saveYawSetting(position: Int) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.pYawSetting = position
            if (position == 0) {//沿线方向
                val taskList = repository.getChildTaskList(flyTask.id)
                for (i in 1 until taskList.size) {
                    val loc1 = taskList[i].flyAreaPoints[0]
                    val flyTask1 = taskList[i - 1]
                    val loc2 = flyTask1.flyAreaPoints[0]
                    val angle = Bearing.computeAzimuth(loc1, loc2)
                    loc2.yaw = angle.toInt()
                    repository.saveTaskPoints(flyTask1.id, flyTask1.flyAreaPoints)
                }
            } else if (position == 1) {//手动控制
            } else if (position == 2) {//依照每个航点设置
                val taskList = repository.getChildTaskList(flyTask.id)
                for (i in 1..taskList.size) {
                    val flyTask1 = taskList[i - 1]
                    val loc2 = flyTask1.flyAreaPoints[0]
                    loc2.yaw = 0
                    repository.saveTaskPoints(flyTask1.id, flyTask1.flyAreaPoints)
                }
            }

            repository.saveTask(flyTask)
            view.onSaveYawSetting()

            view.sendTaskUpdate(flyTask.id)

//            super.updateTaskNotSave(flyTask.id)
        }
    }

    fun savePitchSetting(position: Int) {
        doAsyncWidthTaskUpdate(flyTask) {
//            flyTask.isGimbalPitchRotationEnabled = position
            flyTask.pPitchSetting = position
            repository.saveTask(flyTask)
            view.onSaveYawSetting()

            view.sendTaskUpdate(flyTask.id)

//            super.updateTaskNotSave(flyTask.id)
        }
    }

    fun saveFinishAction(position: Int) {
        doAsyncWidthTaskUpdate(flyTask) {
            flyTask.finishAction = position
            repository.saveTask(flyTask)
            view.sendTaskUpdate(flyTask.id)
        }
    }

    fun addAction(actionType: Int) {
        doAsyncWidthTaskUpdate(flyTask) {
            val flyPointActions = flyTask.flyPointActions
            val flyPointAction = FlyPointAction()
            flyPointAction.id = UUID.randomUUID().toString()
            flyPointAction.taskId = flyTask.id
            flyPointAction.pTaskId = flyTask.id
            flyPointAction.pointId = flyTask.id//此处用taskId替代pointId
            flyPointAction.action = actionType
            if (actionType == WaypointActionType.STAY.value()) {
                flyPointAction.actionName = "悬停"
                flyPointAction.staySecond = 5
            } else if (actionType == WaypointActionType.ROTATE_AIRCRAFT.value()) {
                flyPointAction.actionName = "飞行器偏航角"
            } else if (actionType == WaypointActionType.START_TAKE_PHOTO.value()) {
                flyPointAction.actionName = "拍照"
            } else if (actionType == WaypointActionType.START_RECORD.value()) {
                flyPointAction.actionName = "开始录像"
            } else if (actionType == WaypointActionType.STOP_RECORD.value()) {
                flyPointAction.actionName = "停止录像"
            } else if (actionType == WaypointActionType.GIMBAL_PITCH.value()) {
                flyPointAction.actionName = "云台俯仰角"
            }
            flyPointActions.add(flyPointAction)
            repository.saveFlyPointAction(flyPointAction)

            val childTaskList = repository.getChildTaskList(flyTask.id)

            childTaskList.forEach {
                addChildAction(actionType, it)
            }

            view.onAddActionFinish()

            view.sendTaskUpdate(flyTask.id)

        }
    }

    private fun addChildAction(actionType: Int, flyTask: FlyTask) {
        flyTask.flyAreaPoints = repository.getFlyAreaPointByTaskId(flyTask.id) as ArrayList<FlyAreaPoint>
        flyTask.flyAreaPoints.forEach { flyAreaPoint ->
            val flyPointActions = flyAreaPoint.flyPointActions
            val flyPointAction = FlyPointAction()
            flyPointAction.id = UUID.randomUUID().toString()
            flyPointAction.taskId = flyTask.id
            flyPointAction.pTaskId = flyTask.pId
            flyPointAction.pointId = flyAreaPoint.id
            flyPointAction.action = actionType
            if (actionType == WaypointActionType.STAY.value()) {
                flyPointAction.actionName = "悬停"
                flyPointAction.staySecond = 5
            } else if (actionType == WaypointActionType.ROTATE_AIRCRAFT.value()) {
                flyPointAction.actionName = "云台俯仰角"
            } else if (actionType == WaypointActionType.START_TAKE_PHOTO.value()) {
                flyPointAction.actionName = "拍照"
            } else if (actionType == WaypointActionType.START_RECORD.value()) {
                flyPointAction.actionName = "开始录像"
            } else if (actionType == WaypointActionType.STOP_RECORD.value()) {
                flyPointAction.actionName = "停止录像"
            } else if (actionType == WaypointActionType.GIMBAL_PITCH.value()) {
                flyPointAction.actionName = "飞行器偏航角"
            }
            flyPointActions.add(flyPointAction)
            repository.saveFlyPointAction(flyPointAction)
        }
    }

    fun actionItemChange(position: Int, action: Int, value: Float) {
        doAsyncWidthTaskUpdate(flyTask) {
            val flyPointActions = flyTask.flyPointActions
            val flyPointAction = flyPointActions[position]
            if (action == WaypointActionType.STAY.value()) {
                flyPointAction.staySecond = value.toInt()
            } else if (action == WaypointActionType.ROTATE_AIRCRAFT.value()) {
                flyPointAction.rotateAircraft = value.toInt()
            } else if (action == WaypointActionType.GIMBAL_PITCH.value()) {
                flyPointAction.gimbalPitch = value.toInt()
            }
            repository.saveFlyPointAction(flyPointAction)

            val childTaskList = repository.getChildTaskList(flyTask.id)

            childTaskList.forEach {
                actionItemChildChange(it, action, value)
            }

            view.onActionItemChangeFinish()

            view.sendTaskUpdate(flyTask.id)

//            super.updateTaskNotSave(flyTask.id)
        }
    }

    private fun actionItemChildChange(flyTask: FlyTask, action: Int, value: Float) {
        flyTask.flyAreaPoints = repository.getFlyAreaPointByTaskId(flyTask.id) as ArrayList<FlyAreaPoint>
        flyTask.flyAreaPoints.forEach { flyAreaPoint ->
            val flyPointActions = flyAreaPoint.flyPointActions
            flyPointActions.forEach { flyPointAction ->
                if (action == WaypointActionType.STAY.value()) {
                    flyPointAction.staySecond = value.toInt()
                } else if (action == WaypointActionType.ROTATE_AIRCRAFT.value()) {
                    flyPointAction.rotateAircraft = value.toInt()
                } else if (action == WaypointActionType.GIMBAL_PITCH.value()) {
                    flyPointAction.gimbalPitch = value.toInt()
                }
                repository.saveFlyPointAction(flyPointAction)
            }
        }
    }

    fun deleteItem(position: Int) {
        doAsyncWidthTaskUpdate(flyTask) {
            val flyPointActions = flyTask.flyPointActions
            val removeAt = flyPointActions.removeAt(position)
            repository.deleteFlyPointAction(removeAt.id)

            val childTaskList = repository.getChildTaskList(flyTask.id)

            childTaskList.forEach {
                deleteChildItem(removeAt.action, it)
            }

            view.onDeleteItemFinish()

            view.sendTaskUpdate(flyTask.id)

        }
    }

    private fun deleteChildItem(action: Int, flyTask: FlyTask) {
        flyTask.flyAreaPoints = repository.getFlyAreaPointByTaskId(flyTask.id) as ArrayList<FlyAreaPoint>
        flyTask.flyAreaPoints.map {
            repository.deleteFlyPointByActionAndPointId(action, it.id)
        }
    }

}
