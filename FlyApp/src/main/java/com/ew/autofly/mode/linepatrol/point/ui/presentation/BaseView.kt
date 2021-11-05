package com.ew.autofly.mode.linepatrol.point.ui.presentation


interface BaseView {

    fun showError(badResponse: Any)

    val debug: Boolean

    fun toLogin() {

    }

    fun sendTaskUpdate(taskId:String) {

    }

}
