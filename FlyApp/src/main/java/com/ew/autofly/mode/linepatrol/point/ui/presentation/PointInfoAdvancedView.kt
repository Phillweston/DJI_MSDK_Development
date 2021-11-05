package com.ew.autofly.mode.linepatrol.point.ui.presentation

interface PointInfoAdvancedView : BaseView {

    fun getTaskInfoFinish()

    fun onSaveYawSetting()

    fun onAddActionFinish()

    fun onActionItemChangeFinish()

    fun onDeleteItemFinish()

    fun onSaveRangeSpeedFinish(speed: Int)

    fun onSaveRangeHeightFinish(height: Int)

}