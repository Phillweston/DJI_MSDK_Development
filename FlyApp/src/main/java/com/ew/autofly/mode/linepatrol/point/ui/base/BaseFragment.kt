package com.ew.autofly.mode.linepatrol.point.ui.base

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.ew.autofly.BuildConfig
import com.ew.autofly.mode.linepatrol.point.ui.presentation.BaseView

abstract class BaseFragment : Fragment(), BaseView {

    abstract var layoutId: Int

    override val debug: Boolean = BuildConfig.DEBUG

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId,null)
    }

    fun toast(errorMessage: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun toast(id: Int) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), requireContext().resources.getString(id), Toast.LENGTH_SHORT).show()
        }
    }

    fun doAsync(function: () -> Unit){
        if (!isDetached) {
            try {
                AsyncTask.execute(function)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun showError(badResponse: Any) {
        requireActivity().runOnUiThread {
            context?.commonShowError(badResponse)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeUI(savedInstanceState)
        registerListeners()
    }

    abstract fun initializeUI(savedInstanceState: Bundle?)

    abstract fun registerListeners()
//
//    override fun onSaveInstanceState(outState: Bundle) {
//
//    }


}

fun Context?.commonShowError(badResponse: Any) {
    if (badResponse is String) {
        Toast.makeText(this, badResponse, Toast.LENGTH_SHORT).show()
    }
}

fun Activity.toast(badResponse: Any) {
    runOnUiThread {
        if (badResponse is String) {
            Toast.makeText(this, badResponse, Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Allows calls like
 *
 * `supportFragmentManager.inTransaction { add(...) }`
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commitNow()
}
