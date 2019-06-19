package com.example.pencon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotatedTextView extends TextView {
    public RotatedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
		public RotatedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public RotatedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	} 
	
	@Override
	protected void onDraw(Canvas canvas) {
        //倾斜度45,上下左右居中
        canvas.rotate(90, getMeasuredWidth()/2, getMeasuredHeight()/2);
        super.onDraw(canvas);
    }


}