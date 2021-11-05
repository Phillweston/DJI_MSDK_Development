package com.travijuu.numberpicker.library;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.travijuu.numberpicker.library.Enums.ActionEnum;

import java.util.regex.Pattern;

/**
 * 功能描述：小数输入文本观察类
 *
 * @author (作者) edward（冯丰枫）
 * @link http://www.jianshu.com/u/f7176d6d53d2
 * 创建时间： 2018/3/12
 */

public class DecimalInputTextWatcher implements TextWatcher {
    
    private NumberPicker editText = null;

    
    private static final int DEFAULT_DECIMAL_DIGITS = 2;

    private int decimalDigits;
    private int integerDigits;

    private String symbol;


    public DecimalInputTextWatcher(NumberPicker numberPicker, int integerDigits, int decimalDigits,String symbol) {
        this.editText = numberPicker;
        if (integerDigits <= 0)
            throw new RuntimeException("integerDigits must > 0");
        if (decimalDigits <= 0)
            throw new RuntimeException("decimalDigits must > 0");
        this.integerDigits = integerDigits;
        this.decimalDigits = decimalDigits;
        this.symbol = symbol;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString();
        editText.getDisplayEditText().removeTextChangedListener(this);

        if (s.contains(".")) {
            if (integerDigits > 0) {
                editText.getDisplayEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits + decimalDigits + 1)});
            }
            if (s.length() - 1 - s.indexOf(".") > decimalDigits) {
                s = s.substring(0,
                        s.indexOf(".") + decimalDigits + 1);
                editable.replace(0, editable.length(), s.trim());
            }
        } else {
            if (integerDigits > 0) {
                editText.getDisplayEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(integerDigits + 1)});
                if (s.length() > integerDigits) {
                    s = s.substring(0, integerDigits);
                    editable.replace(0, editable.length(), s.trim());
                }
            }

        }
        if (s.trim().equals(".")) {
            s = "0" + s;
            editable.replace(0, editable.length(), s.trim());
        }
        if (s.startsWith("0")
                && s.trim().length() > 1) {
            if (!s.substring(1, 2).equals(".")) {
                editable.replace(0, editable.length(), "0");
            }
        }



//

//





//















//
//
//

//







        editText.getDisplayEditText().addTextChangedListener(this);

    }

}