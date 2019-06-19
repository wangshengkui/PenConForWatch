package com.example.mgesture;

import java.io.Serializable;
import java.util.ArrayList;

import android.R.integer;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class MGesture implements Serializable{
	private String gestureName="";
	private ArrayList<MGestureStroke> mGestureContainer=null;
	private  int strokeCount=0;
	private transient RectF gestureBoundBoxRect=null;
    private static final int BITMAP_RENDERING_WIDTH = 2;

    private static final boolean BITMAP_RENDERING_ANTIALIAS = true;
    private static final boolean BITMAP_RENDERING_DITHER = true;
	
	public MGesture(MGestureStroke stroke){
		if (mGestureContainer==null) {
			mGestureContainer=new ArrayList<MGestureStroke>();
			strokeCount=0;
		}
		mGestureContainer.add(stroke);
		strokeCount++;
	}
	
    /**
     * 
     * @author： nkxm
     * @name:  
     * @description ：
     * @parameter:
     * @parameter:
     * @return:
     * @date：2019-1-1 下午9:32:19
     * @param mGesturePoints
     */
	public MGesture(ArrayList<MGesturePoint> mGesturePoints){
		if (mGestureContainer==null) {
			mGestureContainer=new ArrayList<MGestureStroke>();
			strokeCount=0;
		}
		mGestureContainer.add(new MGestureStroke(mGesturePoints));
		strokeCount++;
	}
	public void addMGestureStroke(ArrayList<MGesturePoint> mGesturePoints){
		mGestureContainer.add(new MGestureStroke(mGesturePoints));
		strokeCount++;
	}
	public void addMGestureStroke(MGestureStroke stroke){
		mGestureContainer.add(stroke);
		strokeCount++;
	}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-1 下午9:32:08
 * @return
 */
	public int getStrokeCount(){
		return strokeCount;
	}
	/**
	 * 
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午9:32:03
	 * @return
	 */
	public RectF getGestureBoundBoxRect(){
		if(mGestureContainer==null){
			return null;
		}
		if (gestureBoundBoxRect==null) {
			gestureBoundBoxRect=new RectF();
			gestureBoundBoxRect.setEmpty();
		}
		for (MGestureStroke stroke : mGestureContainer) {
			if (gestureBoundBoxRect.isEmpty()) {
				gestureBoundBoxRect=stroke.getStrokeBoundBoxRect();
			}
			gestureBoundBoxRect.union(stroke.getStrokeBoundBoxRect());
		}
		return gestureBoundBoxRect;
	}
	/**
	 * 
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午9:31:54
	 * @param strokesChangeTrendTimes
	 * @return
	 */
	public RectF getGestureBoundBoxRect(int[] strokesChangeTrendTimes){
		if (gestureBoundBoxRect==null) {
			gestureBoundBoxRect=new RectF();
			gestureBoundBoxRect.setEmpty();
		}
		for (int i=0;i<mGestureContainer.size();i++) {
			gestureBoundBoxRect.union(mGestureContainer.get(i).getStrokeBoundBoxRect(strokesChangeTrendTimes[i]));
		}
		return gestureBoundBoxRect;
	}
    /**
     * Creates a bitmap of the gesture with a transparent background.
     * 
     * @param width width of the target bitmap
     * @param height height of the target bitmap
     * @param edge the edge
     * @param numSample
     * @param color
     * @return the bitmap
     */
    public Bitmap toBitmap(int width, int height, int edge, int numSample, int color) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        canvas.translate(edge, edge);

        final Paint paint = new Paint();
        paint.setAntiAlias(BITMAP_RENDERING_ANTIALIAS);
        paint.setDither(BITMAP_RENDERING_DITHER);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(BITMAP_RENDERING_WIDTH);

        final ArrayList<MGestureStroke> strokes = mGestureContainer;
        final int count =  mGestureContainer.size();

        for (int i = 0; i < count; i++) {
            Path path = mGestureContainer.get(i).toPath(width - 2 * edge, height - 2 * edge, numSample);
            canvas.drawPath(path, paint);
        }

        return bitmap;
    }
    /**
     * Creates a bitmap of the gesture with a transparent background.
     * 
     * @param width
     * @param height
     * @param inset
     * @param color
     * @return the bitmap
     */
    public Bitmap toBitmap(int width, int height, int inset, int color) {
        final Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        final Paint paint = new Paint();
        paint.setAntiAlias(BITMAP_RENDERING_ANTIALIAS);
        paint.setDither(BITMAP_RENDERING_DITHER);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(BITMAP_RENDERING_WIDTH);

        final Path path = toPath();
        final RectF bounds = new RectF();
        path.computeBounds(bounds, true);

//        final float sx = (width - 2 * inset) / bounds.width();
//        final float sy = (height - 2 * inset) / bounds.height();
        final float sx = width/ (bounds.width()+2*inset);
        final float sy = height/ (bounds.height()+2*inset);
        final float scale = sx > sy ? sy : sx;
        paint.setStrokeWidth(2.0f / scale);

        path.offset(-bounds.left + (width - bounds.width() * scale) / 2.0f,
                -bounds.top + (height - bounds.height() * scale) / 2.0f);

        canvas.translate(inset, inset);
        canvas.scale(scale, scale);

        canvas.drawPath(path, paint);

        return bitmap;
    }	
	
    public Path toPath() {
        return toPath(null);
    }

    public Path toPath(Path path) {
        if (path == null) path = new Path();

        final ArrayList<MGestureStroke> strokes = mGestureContainer;
        final int count = strokes.size();

        for (int i = 0; i < count; i++) {
            path.addPath(strokes.get(i).getPath());
        }

        return path;
    }	
	
	
public void setGestureName(String gestureName){
	this.gestureName=gestureName;
}
public String getGestureName(){
	return gestureName;
}
public ArrayList<MGestureStroke> getMGseture(){
	return this.mGestureContainer;
}
public void clearMGestureStroke(){
	if(mGestureContainer!=null){}
	mGestureContainer.clear();
	 strokeCount=0;
	gestureBoundBoxRect.setEmpty();
	
}


}
