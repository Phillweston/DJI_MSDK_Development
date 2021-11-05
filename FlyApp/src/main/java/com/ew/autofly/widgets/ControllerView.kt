package com.ew.autofly.widgets


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.ew.autofly.R

class ControllerView : FrameLayout, View.OnTouchListener {

    interface OnControllerViewListener {

        fun onAction(action: String)

    }

    lateinit var onControllerViewListener: OnControllerViewListener

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_controller, this)
        findViewById<ImageView>(R.id.btn_up).setOnTouchListener(this)
        findViewById<ImageView>(R.id.btn_down).setOnTouchListener(this)
        findViewById<ImageView>(R.id.btn_left).setOnTouchListener(this)
        findViewById<ImageView>(R.id.btn_right).setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.action.toByte()
        if (action.toInt() != MotionEvent.ACTION_DOWN && action.toInt() != MotionEvent.ACTION_UP) {
            return false
        }
        when (v.id) {
            R.id.btn_up -> {
                onControllerViewListener.onAction("up")
            }
            R.id.btn_down -> {
                onControllerViewListener.onAction("down")
            }
            R.id.btn_left -> {
                onControllerViewListener.onAction("left")
            }
            R.id.btn_right -> {
                onControllerViewListener.onAction("right")
            }
            else -> {
            }
        }
        return false
    }

    companion object {
        const val UP = "up"
        const val DOWN = "down"
        const val LEFT = "left"
        const val RIGHT = "right"
    }


}