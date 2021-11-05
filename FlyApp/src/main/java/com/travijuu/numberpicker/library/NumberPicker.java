package com.travijuu.numberpicker.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ew.autofly.R;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.LimitExceededListener;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.listener.ActionListener;
import com.travijuu.numberpicker.library.listener.DefaultOnEditorActionListener;
import com.travijuu.numberpicker.library.listener.DefaultOnFocusChangeListener;
import com.travijuu.numberpicker.library.listener.DefaultValueChangedListener;
import com.travijuu.numberpicker.library.listener.DefaultLimitExceededListener;


public class NumberPicker extends LinearLayout {


    private final int DEFAULT_MIN = 0;
    private final int DEFAULT_MAX = 999999;
    private final int DEFAULT_VALUE = 1;
    private final int DEFAULT_UNIT = 1;
    private final String DEFAULT_SYMBOL = "";
    private final int DEFAULT_LAYOUT = R.layout.number_picker_layout;
    private final boolean DEFAULT_FOCUSABLE = true;


    private int minValue;
    private int maxValue;
    private float unit;
    private String symbol;
    private String inputType;
    private float currentValue;
    private int layout;
    private boolean focusable;
    private boolean delay;


    private Context mContext;
    private Button decrementButton;
    private Button incrementButton;
    private EditText displayEditText;


    private LimitExceededListener limitExceededListener;
    private ValueChangedListener valueChangedListener;

    public NumberPicker(Context context) {
        super(context, null);
    }

    public NumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initialize(context, attrs);
    }

    public NumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberPicker, 0, 0);


        this.minValue = attributes.getInteger(R.styleable.NumberPicker_min, this.DEFAULT_MIN);
        this.maxValue = attributes.getInteger(R.styleable.NumberPicker_max, this.DEFAULT_MAX);
        this.currentValue = attributes.getInteger(R.styleable.NumberPicker_value, this.DEFAULT_VALUE);
        this.symbol = attributes.getString(R.styleable.NumberPicker_symbol);
        this.inputType = attributes.getString(R.styleable.NumberPicker_inputType);
        this.unit = attributes.getFloat(R.styleable.NumberPicker_unit, this.DEFAULT_UNIT);
        if (this.unit == 0.1) {
            this.inputType = "numberDecimal";
        }
        this.layout = attributes.getResourceId(R.styleable.NumberPicker_custom_layout, this.DEFAULT_LAYOUT);
        this.focusable = attributes.getBoolean(R.styleable.NumberPicker_focusable, this.DEFAULT_FOCUSABLE);
        this.delay = attributes.getBoolean(R.styleable.NumberPicker_delay, true);
        this.mContext = context;


        this.currentValue = this.currentValue > this.maxValue ? maxValue : currentValue;


        this.currentValue = this.currentValue < this.minValue ? minValue : currentValue;


        LayoutInflater.from(this.mContext).inflate(layout, this, true);


        this.decrementButton = findViewById(R.id.decrement);
        this.incrementButton = findViewById(R.id.increment);
        this.displayEditText = findViewById(R.id.display);

        if (this.inputType != null && inputType.equals("numberDecimal")) {
            this.displayEditText.setInputType(8194);
        } else if (this.inputType == null || inputType.equals("number")) {
            this.displayEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }


        this.incrementButton.setOnClickListener(new ActionListener(this, this.displayEditText, ActionEnum.INCREMENT));
        this.decrementButton.setOnClickListener(new ActionListener(this, this.displayEditText, ActionEnum.DECREMENT));


        this.setLimitExceededListener(new DefaultLimitExceededListener());

        this.setValueChangedListener(new DefaultValueChangedListener());

        this.setOnFocusChangeListener(new DefaultOnFocusChangeListener(this));

        this.setOnEditorActionListener(new DefaultOnEditorActionListener(this));





        this.setDisplayFocusable(this.focusable);


        this.refresh(false);
    }

    public EditText getDisplayEditText() {
        return displayEditText;
    }

    public void refresh(boolean hasFocus) {
        if (hasFocus) {
            if (displayEditText.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                this.displayEditText.setText(Integer.toString(((int) this.currentValue)));
            } else {
                this.displayEditText.setText(String.format("%.1f", this.currentValue));
            }
        } else {
            if (displayEditText.getInputType() == InputType.TYPE_CLASS_NUMBER) {
                this.displayEditText.setText(((int) this.currentValue) + symbol);
            } else {
                this.displayEditText.setText(String.format("%.1f", this.currentValue) + symbol);
            }
        }
    }

    public void clearFocus() {
        this.displayEditText.clearFocus();
    }

    public boolean valueIsAllowed(float value) {
        return (value >= this.minValue && value <= this.maxValue);
    }

    public void setMin(int value) {
        this.minValue = value;
    }

    public void setMax(int value) {
        this.maxValue = value;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public float getUnit() {
        return this.unit;
    }

    public int getMin() {
        return this.minValue;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getMax() {
        return this.maxValue;
    }

    public void setValue(float value) {
        if (!this.valueIsAllowed(value)) {
            this.limitExceededListener.limitExceeded(value < this.minValue ? this.minValue : this.maxValue, value);
            return;
        }

        this.currentValue = value;
        this.refresh(false);
    }

    public float getValue() {
        return this.currentValue;
    }

    public void setLimitExceededListener(LimitExceededListener limitExceededListener) {
        this.limitExceededListener = limitExceededListener;
    }

    public LimitExceededListener getLimitExceededListener() {
        return this.limitExceededListener;
    }

    public void setValueChangedListener(ValueChangedListener valueChangedListener) {
        this.valueChangedListener = valueChangedListener;
    }

    public ValueChangedListener getValueChangedListener() {
        return this.valueChangedListener;
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        this.displayEditText.setOnEditorActionListener(onEditorActionListener);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.displayEditText.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void setActionEnabled(ActionEnum action, boolean enabled) {
        if (action == ActionEnum.INCREMENT) {
            this.incrementButton.setEnabled(enabled);
        } else if (action == ActionEnum.DECREMENT) {
            this.decrementButton.setEnabled(enabled);
        }
    }

    public void setDisplayFocusable(boolean focusable) {
        this.displayEditText.setFocusable(focusable);


        if (focusable) {
            this.displayEditText.setFocusableInTouchMode(true);
        }
    }

    public void increment() {
        this.changeValueBy(this.unit);
    }

    public void increment(int unit) {
        this.changeValueBy(unit);
    }

    public void decrement() {
        this.changeValueBy(-this.unit);
    }

    public void decrement(int unit) {
        this.changeValueBy(-unit);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    valueChangedListener.valueChanged(getValue(), unit > 0 ? ActionEnum.INCREMENT : ActionEnum.DECREMENT);
                    break;
            }
        }
    };

    private void changeValueBy(float unit) {
        float oldValue = this.getValue();

        this.setValue(this.currentValue + unit);

        if (oldValue != this.getValue()) {
            if (delay) {
                handler.removeMessages(1000);
                Message msg = new Message();
                msg.what = 1000;
                handler.sendMessageDelayed(msg,600L);
            } else {
                valueChangedListener.valueChanged(getValue(), unit > 0 ? ActionEnum.INCREMENT : ActionEnum.DECREMENT);
            }
        }
    }
}
