package com.ew.autofly.mode.linepatrol.point.ui.presentation

import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyAreaPoint
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyCamera
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import dji.common.mission.waypoint.WaypointActionType
import java.util.*
import kotlin.collections.ArrayList

class PointInfoEditPresenter(private val view: PointInfoEditView, repository: Repository) : BasePresenter(repository) {

    val pTask: FlyTask
        get() {
            return repository.getTaskOnly(flyTask.pId)
        }

    lateinit var flyTask: FlyTask

    lateinit var cameraList: List<FlyCamera>

    override fun onCreate() {

    }

    fun getTask(taskId: String) {
        doAsync {
            flyTask = repository.getTask(taskId)
            flyTask.flyAreaPoints = ArrayList()
            flyTask.flyAreaPoints.add(FlyAreaPoint())
            view.onGetTaskFinish()
        }
    }

    fun saveRangeSpeed(speed: Int) {
        if (flyTask.speed.toInt() == speed) {
            return
        }
        doAsyncWidthTaskUpdate(pTask) {
            val toFloat = speed.toFloat()
            flyTask.speed = toFloat
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.speed = speed.toDouble()

            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)

//            super.updateTaskNotSave(flyTask.pId)
        }
    }

    fun saveHeight(height: Int = 0) {
        if (flyTask.flyHeight.toInt() == height) {
            return
        }
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.flyHeight = height.toFloat()
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.altitude = height.toFloat()

            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)

//            super.updateTaskNotSave(flyTask.pId)
        }
    }

    fun saveYaw(yaw: Int = 0) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.yaw = yaw
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.yaw = yaw

            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)

//            super.updateTaskNotSave(flyTask.pId)
        }
    }

    fun savePitch(pitch: Int) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.pitch = pitch
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.pitch = pitch

            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)

//            super.updateTaskNotSave(flyTask.pId)
        }
    }

    fun saveLongitude(longitude: Double) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.longitude = longitude
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.longitude = longitude
            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)

//            super.updateTaskNotSave(flyTask.pId)
        }
    }

    fun saveLatitude(latitude: Double) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.latitude = latitude
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.latitude = latitude
            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)
        }
    }

    fun addAction(actionType: Int) {
        val flyAreaPoint = flyTask.flyAreaPoints[0]
        val flyPointActions = flyAreaPoint.flyPointActions
        val flyPointAction = FlyPointAction()
        flyPointAction.id = UUID.randomUUID().toString()
        flyPointAction.taskId = flyTask.id
        flyPointAction.pTaskId = flyTask.pId
        flyPointAction.pointId = flyAreaPoint.id
        flyPointAction.action = actionType
        if (actionType == WaypointActionType.STAY.value()) {
            flyPointAction.actionName = "悬停"
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
        view.onAddActionFinish()
    }

    fun deleteCurrentTask() {
        doAsyncWidthTaskUpdate(pTask) {
            repository.removeTask(flyTask.id)
            view.onDeleteCurrentTask()
            view.sendTaskUpdate(flyTask.pId)
        }
    }

    fun actionItemChange(position: Int, action: Int, value: Float) {
        doAsyncWidthTaskUpdate(pTask) {
            val flyPointActions = flyTask.flyAreaPoints[0].flyPointActions
            val flyPointAction = flyPointActions[position]
            if (action == WaypointActionType.STAY.value()) {
                flyPointAction.staySecond = value.toInt()
            } else if (action == WaypointActionType.ROTATE_AIRCRAFT.value()) {
                flyPointAction.rotateAircraft = value.toInt()
            } else if (action == WaypointActionType.GIMBAL_PITCH.value()) {
                flyPointAction.gimbalPitch = value.toInt()
            }
            repository.saveFlyPointAction(flyPointAction)
            view.onActionItemChangeFinish()
            view.sendTaskUpdate(flyTask.pId)
        }
    }

    fun deleteItem(position: Int) {
        val flyPointActions = flyTask.flyAreaPoints[0].flyPointActions
        flyPointActions.removeAt(position)
        view.onDeleteItemFinish()
    }

    fun updateTaskLatitude(latitude: Double) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.latitude = latitude
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.latitude = latitude
            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)
        }
    }

    fun updateTaskLongitude(longitude: Double) {
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.longitude = longitude
            repository.saveTask(flyTask)

            val flyAreaPoint = flyTask.flyAreaPoints[0]
            flyAreaPoint.longitude = longitude
            repository.saveTaskPoint(flyAreaPoint)

            view.sendTaskUpdate(flyTask.pId)
        }
    }

    fun saveFollowSpeed(checked: Boolean) {
        if (flyTask.followSpeed == checked) {
            return
        }
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.followSpeed = checked
            repository.saveTask(flyTask)
        }
    }

    fun saveFollowHeight(checked: Boolean) {
        if (flyTask.followHeight == checked) {
            return
        }
        doAsyncWidthTaskUpdate(pTask) {
            flyTask.followHeight = checked
            repository.saveTask(flyTask)
        }
    }

    fun getFlyAreaPoint() {
        doAsync {
            val first = flyTask.flyAreaPoints.first()
            first.flyPointActions.clear()
            first.flyPointActions.addAll(repository.getFlyPointActions(first.id))
            view.onGetFlyAreaPointFinish()
        }
    }

}