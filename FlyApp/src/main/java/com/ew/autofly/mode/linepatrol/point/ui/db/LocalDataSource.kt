package com.ew.autofly.mode.linepatrol.point.ui.db

import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask

interface LocalDataSource {

    fun getChildTaskList(taskId: String): List<FlyTask>

    fun saveFlyPointAction(flyPointAction: FlyPointAction)

}