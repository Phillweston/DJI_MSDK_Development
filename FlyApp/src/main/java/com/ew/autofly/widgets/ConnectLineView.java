package com.ew.autofly.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.ew.autofly.R;
import com.ew.autofly.utils.Tools;

public class ConnectLineView extends View {
	/*
	 * 0 连接中 1 连接成功 -1 连接失败 2 传输图片
	 */
	private int state = -1;
	private int lineWidth = 10;
	private int speed = 300;
	private int position = -1;

	private Bitmap pictureBitmap;
	private int picturePosition = -1;
	
	private Paint paint;
	public ConnectLineView(Context context) {
		this(context, null);
	}

	public ConnectLineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ConnectLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		pictureBitmap = Tools.zoomImage(BitmapFactory.decodeResource(getResources(),R.drawable.picture, null),30,30,1);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ConnectLineView, defStyle, 0);
		int n = a.getIndexCount();  
        for (int i = 0; i < n; i++)  
        {  
            int attr = a.getIndex(i);  
            switch (attr)  
            {  
            case R.styleable.ConnectLineView_lineWidth:  
                lineWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(  
                        TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));  
                break;  
            case R.styleable.ConnectLineView_speed:  
                speed = a.getInt(attr, 200);
                break;  
            }  
        }  
        
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(lineWidth);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					if(state==0){
						position++;
					}
					if(state == 2){
						picturePosition++;
					}
					postInvalidate();
					 try  
	                    {  
	                        Thread.sleep(speed);  
	                    } catch (InterruptedException e)  
	                    {  
	                        e.printStackTrace();  
	                    }  
				}
			}
		}).start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		int circleNum = (width - 10) / (lineWidth + 10);
		int pictureNum = width/(pictureBitmap.getWidth());
		switch (state) {
		case 1:
			 paint.setColor(getResources().getColor(R.color.connectline_green));

			for (int i = 1; i <= circleNum; i++) {
				if (width - (i * (lineWidth + 10)) < lineWidth + 10) {
					continue;
				}
				canvas.drawCircle(i * (lineWidth + 10), height / 2, lineWidth / 2,
						paint);
			}
			break;
		case 0:
			paint.setColor(getResources().getColor(R.color.connectline_gary));
			for (int i = 1; i <= circleNum; i++) {
				if (width - (i * (lineWidth + 10)) < lineWidth + 10) {
					if(position>circleNum){
						position=-1;
					}
					continue;
				}
				if(position==i){
					paint.setColor(getResources().getColor(R.color.connectline_green));
				}else if(position-1>=0&&i==position-1){
					paint.setColor(getResources().getColor(R.color.connectline_green));
				}else if(position-1<0 && i ==circleNum){
					paint.setColor(getResources().getColor(R.color.connectline_green));
				}else if(position+1<=circleNum && i==position+1){
					paint.setColor(getResources().getColor(R.color.connectline_green));
				}else if(position+1>circleNum && i ==0){
					paint.setColor(getResources().getColor(R.color.connectline_green));
				}else{
					paint.setColor(getResources().getColor(R.color.connectline_gary));
				}
				canvas.drawCircle(i * (lineWidth + 10), height / 2, lineWidth / 2,
						paint);
				if(position>circleNum){
					position=-1;
				}
			}
			break;
		case -1:



//


			paint.setColor(getResources().getColor(R.color.connectline_gary));
			for (int i = 1; i <= circleNum; i++) {
				if (width - (i * (lineWidth + 10)) < lineWidth + 10) {
					continue;
				}
				canvas.drawCircle(i * (lineWidth + 10), height / 2, lineWidth / 2,
						paint);
			}
			break;
			case 2:
				paint.setColor(getResources().getColor(R.color.connectline_green));
				for (int i = 1; i <= circleNum; i++) {
					if (width - (i * (lineWidth + 10)) < lineWidth) {
						continue;
					}
					canvas.drawCircle(i * (lineWidth + 10), height / 2, lineWidth / 2,
							paint);
				}
				for (int i = 0; i <= pictureNum; i++) {
					if (width - picturePosition*pictureBitmap.getWidth() < pictureBitmap.getWidth()) {
						if(picturePosition>pictureNum){
							picturePosition=-1;
						}
						continue;
					}
					if(i==picturePosition){
						canvas.drawBitmap(pictureBitmap, picturePosition*pictureBitmap.getWidth(),10, paint);
					}
					if(picturePosition>pictureNum){
						picturePosition=-1;
					}
				}
				break;

		}

	}
	
	public void setState(int state) {
		this.state = state;
	}

}
