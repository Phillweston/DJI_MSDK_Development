package com.travijuu.numberpicker.library.listener

import android.view.View
import android.widget.EditText
import com.travijuu.numberpicker.library.NumberPicker

/**
 * Created by travijuu on 03/06/17.
 */
class DefaultOnFocusChangeListener(var layout: NumberPicker) : View.OnFocusChangeListener {
    override fun onFocusChange(v: View, hasFocus: Boolean) {
        val editText = v as EditText
        val toFloat = if (layout.symbol.isNullOrBlank()) {
            editText.text.toString().toFloat()
        } else {
            editText.text.toString().substringBeforeLast(layout.symbol).takeIf { it.isNotEmpty() }?.toFloat()?:0f
        }
        layout.value = toFloat
        layout.refresh(hasFocus)
    }
}