package com.ew.autofly.mode.linepatrol.point.ui.db

import com.ew.autofly.db.dao.DaoSession
import com.ew.autofly.db.dao.FlyTaskDao
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyPointAction
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask

class LocalDataSourceImpl(private val daoSession: DaoSession): LocalDataSource {

    override fun getChildTaskList(taskId: String): List<FlyTask> {
        return emptyList()
    }

    override fun saveFlyPointAction(flyPointAction: FlyPointAction) {
//        daoSession.flyPointActionDao.insertOrReplace(flyPointAction)
    }

}