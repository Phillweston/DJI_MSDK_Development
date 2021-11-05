package com.ew.autofly.mode.linepatrol.point.ui.presentation

import android.util.Log
import com.ew.autofly.mode.linepatrol.point.ui.db.Repository
import com.ew.autofly.mode.linepatrol.point.ui.model.FlyTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class BasePresenter(val repository: Repository) {

    abstract fun onCreate()

    open fun onDestroy() {
    }

    fun doAsyncWidthTaskUpdate(flyTask: FlyTask, f: (FlyTask) -> Unit) {
        fixedThreadPool.execute {
            try {
                val task = repository.getTask(flyTask.id)
                task.save = false
                repository.saveTask(task)
                f(task)
            } catch (e: Throwable) {
                Log.e("doAsync", "错误消息", e)
            }
        }
    }



    fun doAsync(f: () -> Unit) {
        fixedThreadPool.execute {
            try {
                f()
            } catch (e: Throwable) {
                Log.e("doAsync", "错误消息", e)
            }
        }
    }

    companion object {

        val fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(10)

    }

}
