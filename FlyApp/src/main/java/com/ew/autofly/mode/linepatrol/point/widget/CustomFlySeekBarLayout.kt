package com.ew.autofly.mode.linepatrol.point.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.ew.autofly.R
import com.ew.autofly.widgets.CustomBar
import com.ew.autofly.widgets.CustomSeekbar.BubbleSeekBar
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar

class CustomFlySeekBarLayout : LinearLayout, BubbleSeekBar.OnProgressChangedListener {

    interface CustomFlySeekBarChangeListener {

        fun onValueChange(value: Float)

    }

    lateinit var customFlySeekBarChangeListener: CustomFlySeekBarChangeListener

    lateinit var range: CustomBar

    lateinit var edit: EditText

    lateinit var text_left: TextView

    lateinit var text_right: TextView

    private var seekBarMode = 0

    private var minProgress = 0f

    private var maxProgress = 0f

    private var defaultValue = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
        init()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        try {
            val t = context.obtainStyledAttributes(attrs, R.styleable.CustomFlySeekBarLayout)
//            seekBarMode = t.getInt(R.styleable.RangeSeekBar_rsb_mode, RangeSeekBar.SEEKBAR_MODE_RANGE)
            minProgress = t.getFloat(R.styleable.CustomFlySeekBarLayout_rsb_min, 0f)
            maxProgress = t.getFloat(R.styleable.CustomFlySeekBarLayout_rsb_max, 100f)
            defaultValue = t.getFloat(R.styleable.CustomFlySeekBarLayout_rsb_default, 100f)
//            gravity = t.getInt(R.styleable.CustomFlySeekBarLayout_rsb_gravity, RangeSeekBar.Gravity.TOP)
            t.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    fun init() {
        LayoutInflater.from(context).inflate(R.layout.custom_layout_fly_parameter, this)
        range = findViewById(R.id.range)
        range.bubbleSeekBar.configBuilder
            .max(maxProgress)
            .min(minProgress)
            .progress(1f)
            .sectionCount((maxProgress - minProgress).toInt())
            .hideBubble()
            .build()
        range.bubbleSeekBar.onProgressChangedListener = this
//        range.setRange(minProgress, maxProgress, 1f)
//        range.setOnRangeChangedListener(this)
        edit = findViewById(R.id.edit)
        text_left = findViewById(R.id.text_left)
        text_right = findViewById(R.id.text_right)
        text_left.text = minProgress.toInt().toString()
        text_right.text = maxProgress.toInt().toString()
        edit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                val value = edit.text.toString().toFloat()
//                range.setProgress(value)
                customFlySeekBarChangeListener.onValueChange(value)
            }
            false
        }
//        range.setProgress(5.0f)
        edit.setText(defaultValue.toString())
//        customFlySeekBarChangeListener.onValueChange(edit.text.toString().toFloat())
    }

    fun setProgress(progress: Float) {
        range.bubbleSeekBar.setProgress(progress)
        edit.setText(progress.toInt().toString())
    }

//    override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//
//    }
//
//    override fun onRangeChanged(
//        view: RangeSeekBar?,
//        leftValue: Float,
//        rightValue: Float,
//        isFromUser: Boolean
//    ) {
//        edit.setText(leftValue.toInt().toString())
//    }
//
//    override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
//        customFlySeekBarChangeListener.onValueChange(edit.text.toString().toFloat())
//    }

    override fun onProgressChanged(progress: Int, progressFloat: Float) {
        edit.setText(progress.toString())
    }

    override fun getProgressOnActionUp(progress: Int, progressFloat: Float) {

    }

    override fun getProgressOnFinally(progress: Int, progressFloat: Float) {
        customFlySeekBarChangeListener.onValueChange(edit.text.toString().toFloat())
    }


}