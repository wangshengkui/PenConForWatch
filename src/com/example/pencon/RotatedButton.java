package com.example.pencon;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

public class RotatedButton extends Button{

	public RotatedButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

    public RotatedButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RotatedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        // 坐标需要转换，因为默认情况下Button中的文字居中显示
        // 这里需要让文字在底部显示
    	

        canvas.rotate(90, getMeasuredWidth()/2, getMeasuredHeight()/2);
        super.onDraw(canvas);
        
    }


}
