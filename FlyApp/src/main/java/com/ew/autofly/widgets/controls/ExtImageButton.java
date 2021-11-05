package com.ew.autofly.widgets.controls;

import com.ew.autofly.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExtImageButton extends LinearLayout {
	private ImageButton imgBtnIcon;
	private TextView tvIconText;
	private LinearLayout linear;
	private Object data;

	public ExtImageButton(Context context) {
		this(context, null);
	}

	public ExtImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.ctrl_imagebutton, this, true);
		imgBtnIcon = (ImageButton) findViewById(R.id.imgBtnIcon);
		tvIconText = (TextView) findViewById(R.id.tvIconText);
		linear = (LinearLayout) findViewById(R.id.extImgBtnId);
		
		imgBtnIcon.setClickable(false);
		tvIconText.setFocusable(false);
	}

	
	public void setImageResource(int resId) {
		imgBtnIcon.setImageResource(resId);
	}

	
	public void setImageBitmap(Bitmap bm) {
		imgBtnIcon.setImageBitmap(bm);
	}

	
	public void setTextViewText(String text) {
		tvIconText.setText(text);
	}

	public void setTextViewColor(int color) {
		tvIconText.setTextColor(color);
	}

	/**
	 * 
	 * @param orientation
	 */
	public void setImageOrientation(int orientation) {
		linear.setOrientation(orientation);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
