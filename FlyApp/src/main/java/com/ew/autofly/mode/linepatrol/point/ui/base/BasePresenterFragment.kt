package com.ew.autofly.mode.linepatrol.point.ui.base

import android.os.Bundle
import android.view.View
import com.ew.autofly.application.DJIApplication
import com.ew.autofly.application.EWApplication
import com.ew.autofly.mode.linepatrol.point.ui.presentation.BasePresenter
import com.ew.autofly.mode.linepatrol.point.ui.presentation.BaseView

abstract class BasePresenterFragment : BaseFragment(), BaseView {

    protected val repository by lazy { (requireContext().applicationContext as EWApplication).repository }

    abstract val presenter: BasePresenter?

    override fun onDestroyView() {
        presenter?.onDestroy()
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }

    fun runOnUiThread(function: () -> Unit) {
        activity?.runOnUiThread(function)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.onCreate()
    }

    override fun sendTaskUpdate(taskId:String){
//        val intent = Intent(MapFragment.ON_TASK_LOAD)
//        intent.putExtra("id", taskId)
//        requireContext().sendBroadcast(intent)
    }


}