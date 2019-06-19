package com.example.mgesture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.R.integer;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class MGestureStroke  implements Serializable{
	private int gesturesTrendCount;//后面初始化代码块中有默认值
	private final static byte  X_DIMENSION=0;
	private final static byte  Y_DIMENSION=1;
	private final static byte  Z_DIMENSION=2;
	static final float TOUCH_TOLERANCE = 1;

	
	public int index;//后面初始化代码块中有默认值	
//	private MGestureTrend[] gestureTrendsContainer;
//	public MGestureTrend mGestureTrend;//存放手势数据变化趋势的容器类
	private ArrayList<MGesturePoint> mGesturePoints=new ArrayList<MGesturePoint>();//存放原始笔迹数据
	private  transient   RectF boundBoxRect=new RectF();
    private float length=0;
    private transient  Path mCachedPath;
    
	private byte[] gesturesTrend;//表示手势变化趋势的byte数组，new byte[x]，这里的x确定了最多可以支持手势变化次数
	private int[] trendChangePlaceIndex;
    
	/*
	 * 初始化代码块，在构造函数之前运行，这里初始化的值可以在构造函数中重新赋值
	 */	
	{
		gesturesTrendCount=30;//gesturesTrendLength默认值为30;
		index=0;//index=0默认值为0	
		trendChangePlaceIndex=new int[gesturesTrendCount];
		gesturesTrend=new byte[gesturesTrendCount];
		for (byte  element:gesturesTrend) {
			element=0;
		}

	}
	/**
	 * 
	 * @param mGesturePoints
	 */
	public MGestureStroke(ArrayList<MGesturePoint> mGesturePoints){
		if (mGesturePoints.size()<1) {
			this.mGesturePoints=mGesturePoints;
		}else {
			int startindex=0;
			this.mGesturePoints.add(mGesturePoints.get(0));
			double delt=2;
			for (int i = 1; i < mGesturePoints.size(); i++) {
				if (Math.abs(mGesturePoints.get(i).x-mGesturePoints.get(startindex).x)<delt&&Math.abs(mGesturePoints.get(i).y-mGesturePoints.get(startindex).y)<delt) {
				continue;	
				}
				startindex=i;
				this.mGesturePoints.add(mGesturePoints.get(startindex));
			}
		}
//		gestureTrendsContainer=new MGesture.MGestureTrend[gesturesTrendLength];
        getTrends();
		smoothTrend();
	}
	 /**
	 * 
	 * @param mGesturePoints
	 * @param gestureChangeCount
	 */
	public MGestureStroke(ArrayList<MGesturePoint> mGesturePoints,int gestureChangeCount){
		if (mGesturePoints.size()<1) {
			this.mGesturePoints=mGesturePoints;
		}else {
			int startindex=0;
			this.mGesturePoints.add(mGesturePoints.get(0));
			double delt=2;
			for (int i = 1; i < mGesturePoints.size(); i++) {
				if (Math.abs(mGesturePoints.get(i).x-mGesturePoints.get(startindex).x)<delt&&Math.abs(mGesturePoints.get(i).y-mGesturePoints.get(startindex).y)<delt) {
				continue;	
				}
				startindex=i;
				this.mGesturePoints.add(mGesturePoints.get(i));
			}
		}
		this.gesturesTrendCount=gestureChangeCount;
//		gestureTrendsContainer=new MGesture.MGestureTrend[gesturesTrendLength];		
		getTrends();
		smoothTrend();
	}	
	/**
	 * 
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午5:03:26
	 * @param mGesturePoints
	 */
	private void   getTrends(){
		
		if (mGesturePoints.size()<2) {
			return ;
		}
		 index=0;
		MGesturePoint lastMGesturePoint=mGesturePoints.get(0);
		MGesturePoint currentMGesturePoint;
		GestureTrend[] tempTrend=new GestureTrend[3];
		GestureTrend[] currentTrend=new GestureTrend[3];
		currentTrend[0]=currentTrend[1]=currentTrend[2]=null;
	
//		int trendChangeCount=0;
		for (int i = 1; i < mGesturePoints.size(); i++) {
			currentMGesturePoint=mGesturePoints.get(i);
			tempTrend[0]=getTrend(lastMGesturePoint.x, currentMGesturePoint.x);
			tempTrend[1]=getTrend(lastMGesturePoint.y, currentMGesturePoint.y);
			lastMGesturePoint=currentMGesturePoint;
				if (index==0) {
					Log.e("zgm", "1801061925：index："+index);
					currentTrend[0]=tempTrend[0];//x
					currentTrend[1]=tempTrend[1];//y
					setGestureTrend(CoordinateDimension.X,currentTrend[0]);
					setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
					trendChangePlaceIndex[index]=i;
					if (index<gesturesTrend.length) {
						index++;
					}
					continue;
				}else {
					Log.e("zgm", "1801061925：tempTrend[0]："+tempTrend[0].getValue());
					Log.e("zgm", "1801061925：currentTrend[0]："+currentTrend[0].getValue());
					Log.e("zgm", "1801061925：tempTrend[1]："+tempTrend[1].getValue());
					Log.e("zgm", "1801061925：currentTrend[1]："+currentTrend[1].getValue());
					
					if ((tempTrend[0].getValue()!=currentTrend[0].getValue())&&(tempTrend[1].getValue()!=currentTrend[1].getValue())) {//x和y的变化趋势都改变
						Log.e("zgm", "1801061925：x,y都变化了：x:的判断"+(tempTrend[0].getValue()!=currentTrend[0].getValue()));
						
						currentTrend[0]=tempTrend[0];//x
						currentTrend[1]=tempTrend[1];//y
						setGestureTrend(CoordinateDimension.X,currentTrend[0]);
						setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
						trendChangePlaceIndex[index]=i;
						if (index<gesturesTrend.length) {
							index++;
						}
						continue;	
					}
					if ((tempTrend[0].getValue()!=currentTrend[0].getValue())&&(tempTrend[1].getValue()==currentTrend[1].getValue())) {//x趋势改变，y趋势没有改变
						Log.e("zgm", "1801061925：x变化了，y没有变化：x:的判断"+(tempTrend[0].getValue()!=currentTrend[0].getValue()));
						currentTrend[0]=tempTrend[0];//x
//						currentTrend[1]=tempTrend[1];//y不用更新
						setGestureTrend(CoordinateDimension.X,currentTrend[0]);
						setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
						trendChangePlaceIndex[index]=i;
						if (index<gesturesTrend.length) {
							index++;
						}
						continue;	
					}
					if ((tempTrend[0].getValue()==currentTrend[0].getValue())&&(tempTrend[1].getValue()!=currentTrend[1].getValue())) {//y趋势改变，x趋势没有改变
						Log.e("zgm", "1801061925：x没有变化，y有变化：y:的判断"+(tempTrend[1].getValue()!=currentTrend[1].getValue()));
//						currentTrend[0]=tempTrend[0];//x不用更新
						currentTrend[1]=tempTrend[1];//y
						setGestureTrend(CoordinateDimension.X,currentTrend[0]);
						setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
						trendChangePlaceIndex[index]=i;
						if (index<gesturesTrend.length) {
							index++;
						}
						continue;	
					}
				}
		}
		trendChangePlaceIndex[index]=mGesturePoints.size();//将最后的点的索引加进来
		
	}
	/**
	 * 
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午5:03:50
	 * @param last
	 * @param current
	 * @return
	 */
	private GestureTrend getTrend(float last,float current){
		GestureTrend gestureTrend=null;
		float delt=(float) 0.1;
		if (current-last>delt) {//增加的趋势
			gestureTrend=GestureTrend.increase;
			return gestureTrend;
		}
		if (current-last<-delt) {//减少的趋势
			gestureTrend=GestureTrend.decrease;
			return gestureTrend;
		}
		if ((current-last>-delt)&&(current-last<delt)) {//不变的趋势
			gestureTrend=GestureTrend.even;
			return gestureTrend;
		}
		return gestureTrend;
	}

	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2018-12-28 上午10:40:18
	 * @param cD
	 * @param trend
	 * @param index
	 */
	 public void  setGestureTrend(CoordinateDimension cD,GestureTrend trend){
		 if (index>=gesturesTrend.length||index<0) {
			 Log.e("setGestureTrend", new Date().getTime()+"--setGesturesTrend:index的值超越了gestureTrend的长度，正确的长度为0~"+gesturesTrend.length);
			return;
		}
		 switch (cD.getValue()) {
		case X_DIMENSION:
			gesturesTrend[index]&=(255-3);
			gesturesTrend[index]+=trend.getValue();
			break;
		case Y_DIMENSION:
			gesturesTrend[index]&=(255-12);
			gesturesTrend[index]+=(byte) (trend.getValue()<<2);
			break;
		case Z_DIMENSION:
			gesturesTrend[index]&=(255-48);
			gesturesTrend[index]+=(byte) (trend.getValue()<<4);
			break;
		default:
			Log.e("setGestureTrend", new Date().getTime()+"--setGesturesTrend:输入的坐标系维度不存在，正确的只有三种：X_DIMENSION，Y_DIMENSION，Z_DIMENSION");
			break;
		}
		 
	 }

	 public double[] getTrendLength(){
		 if (index<1) {
			return null;
		}
		 double[] trendLength=new double[index];
		 MGesturePoint lastGesturePoint = null;
		 MGesturePoint currentGesturePoint;
		 
		 for (int i = 0; i < trendLength.length; i++) {
			 trendLength[i]=0;
			 for (int j =trendChangePlaceIndex[i]; j < trendChangePlaceIndex[i+1]; j++) {
				if (j ==trendChangePlaceIndex[i]) {
					lastGesturePoint=mGesturePoints.get(j);
					continue;
				}
				currentGesturePoint=mGesturePoints.get(j);
				double deltx=currentGesturePoint.x-lastGesturePoint.x;
				double delty=currentGesturePoint.y-lastGesturePoint.y;
				trendLength[i]+= Math.hypot(deltx, delty);
			}
		}
		return trendLength;
	 }
	 public void smoothTrend(){
		 double[] trendLength=getTrendLength();

		 
		 double maxTrendLength=Double.MIN_VALUE;
		 double secTrendLength=Double.MIN_VALUE;
		 for (int i = 0; i < trendLength.length; i++) {
			if (trendLength[i]>maxTrendLength) {
				secTrendLength=maxTrendLength;
				maxTrendLength=trendLength[i];
			}
		}
		double trendLenghToRemove=0;//防止出现变化趋势太长的线段
		if (secTrendLength!=Double.MIN_VALUE) {
			if ((secTrendLength/(maxTrendLength-secTrendLength))<0.25) {
				trendLenghToRemove=maxTrendLength;
			}
		}
//		求平均值
	double TrendLengthSum=0;
	int tempCount=0;
		 for (int i = 0; i < trendLength.length; i++) {
			if (trendLength[i]!=trendLenghToRemove) {
				TrendLengthSum+=trendLength[i];
				tempCount++;
			}
		}
		 double trendLengthAverage=0;
		 if (tempCount!=0) {
			 trendLengthAverage=TrendLengthSum/tempCount;
		} 
		 
		 byte[]  tempTrend=new byte[trendLength.length];
		 int[] temptrendChangePlaceIndex=new int[trendLength.length+1];
		 tempCount=0;
		 for (int i = 0; i < trendLength.length; i++) {
			if (trendLength[i]>trendLengthAverage*0.25) {
				tempTrend[tempCount]=gesturesTrend[i];
				temptrendChangePlaceIndex[tempCount]=trendChangePlaceIndex[i];
				tempCount++;
			}
		} 
		 temptrendChangePlaceIndex[tempCount]=trendChangePlaceIndex[trendLength.length];
		 
		 trendChangePlaceIndex=temptrendChangePlaceIndex;
		 index=tempCount;
		 gesturesTrend=new byte[index];
		 for (int i = 0; i < gesturesTrend.length; i++) {
			 gesturesTrend[i]=tempTrend[i];
		}
		
	 }
	 
	 
	 
	 
	 
		/**
		 *	 
		 * @author： nkxm
		 * @name:  
		 * @description ：
		 * @parameter:
		 * @parameter:
		 * @return:
		 * @date：2018-12-28 上午10:50:59
		 * @param cD
		 * @param trends
		 * @return
		 */
	 public byte getCoordinateDimensionTrend(CoordinateDimension cD, byte CoordinateTrends){
		 
		 switch (cD.getValue()) {
		case X_DIMENSION:
			CoordinateTrends=(byte) ((byte)3&CoordinateTrends);
			break;
		case Y_DIMENSION:
			CoordinateTrends=(byte) ((byte)12&CoordinateTrends);
			break;
		case Z_DIMENSION:
			CoordinateTrends=(byte) ((byte)48&CoordinateTrends);
			break;
		default:
			Log.e("getCoordinateDimensionTrend", new Date().getTime()+"--getCoordinateDimensionTrend:输入的坐标系维度不存在，正确的只有三种：X_DIMENSION，Y_DIMENSION，Z_DIMENSION");
			break;
		}
		 
		return CoordinateTrends;
		 
	 }	


	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @date：2019-1-1 下午5:00:16
	 */
		
		public enum GestureTrend{
			//int强制转化为byte，直接取低8位，如果第8位(从1开始计数)是1，那么byte是负值，否则是正值
			increase("增加",(byte)0),//增加的状态
			decrease("减少",(byte)1),//减少的状态
			even("持平",(byte)2);    //不变化的状态
			private byte value;
			private String trend;
			GestureTrend(String trend,byte value){
				this.trend=trend;
				this.value=value;
			}
			public byte getValue(){
				return this.value;
			}
			}
		/**
		 * 
		 * @author： nkxm
		 * @name:  
		 * @description ：
		 * @date：2019-1-1 下午5:02:20
		 */
		public enum CoordinateDimension{
			//int强制转化为byte，直接取低8位，如果第8位(从1开始计数)是1，那么byte是负值，否则是正值
			X("X坐标维度",X_DIMENSION),//X坐标维度
			Y("Y坐标维度",Y_DIMENSION),//Y坐标维度
			Z("Z坐标维度",Z_DIMENSION);    //Z坐标维度
			private byte value;
			private String coordinateDimension;//坐标维度
			CoordinateDimension(String cD,byte value){
				this.coordinateDimension=cD;
				this.value=value;
			}
		public byte getValue(){
			return this.value;
		}
		
		}
	/**
     * @author： nkxm
     * @name:  
     * @description ：
     * @parameter:
     * @parameter:
     * @return:
     * @date：2019-1-1 下午4:59:47
     * @param mGesturePoints2
     */
	private void setGestureBoundBoxRect(ArrayList<MGesturePoint> mGesturePoints2){
		boundBoxRect.setEmpty();//将原来的边界框清空防止影响
		for (MGesturePoint mGesturePoint : mGesturePoints2) {
			if (boundBoxRect.isEmpty()) {
				boundBoxRect.left=mGesturePoint.x;
				boundBoxRect.top=mGesturePoint.y;
				boundBoxRect.right=mGesturePoint.x;
				boundBoxRect.bottom=mGesturePoint.y;
			}
			boundBoxRect.union(mGesturePoint.x,mGesturePoint.y);
		}
	}
	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午4:59:40
	 * @param mGesturePoints
	 * @param gestureTrendChangeTimes
	 */
	private void setGestureBoundBoxRect(ArrayList<MGesturePoint> mGesturePoints,int gestureTrendChangeTimes){
		if (gestureTrendChangeTimes>index) {
			setGestureBoundBoxRect(mGesturePoints);
		}
		int tempIndex=trendChangePlaceIndex[gestureTrendChangeTimes-1];
		MGesturePoint tempMGesturePoint;
		boundBoxRect.setEmpty();
		
		for (int i = 0; i < tempIndex; i++) {
			tempMGesturePoint=mGesturePoints.get(i);
			if (boundBoxRect.isEmpty()) {
				boundBoxRect.left=tempMGesturePoint.x;
				boundBoxRect.top=tempMGesturePoint.y;
				boundBoxRect.right=tempMGesturePoint.x;
				boundBoxRect.bottom=tempMGesturePoint.y;
			}
			boundBoxRect.union(tempMGesturePoint.x,tempMGesturePoint.y);
		}
	}

	public float getGestureLength(){
		if (length!=0||mGesturePoints==null) {
			return length;
		}
		if (mGesturePoints==null) {
			
		}
		MGesturePoint lastGesturePoint=mGesturePoints.get(0);
		for (MGesturePoint mGesturePoint : mGesturePoints) {
			length+= Math.hypot(mGesturePoint.x - lastGesturePoint.x, mGesturePoint.y - lastGesturePoint.y);	
			lastGesturePoint=mGesturePoint;
		}
		return length;
	}
   
	
	private void makePath() {
     
        final int count = mGesturePoints.size();

        Path path = null;

        float mX = 0;
        float mY = 0;

        for (int i = 0; i < count; i ++) {
            float x = mGesturePoints.get(i).x;
            float y = mGesturePoints.get(i).y;
            if (path == null) {
                path = new Path();
                path.moveTo(x, y);
                mX = x;
                mY = y;
            } else {
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);//画贝塞尔曲线
                    mX = x;
                    mY = y;
                }
            }
        }

        mCachedPath = path;
    }

	private void makePath( int gestureTrendChangeTimes) {
		if (gestureTrendChangeTimes>index) {
			makePath();
		}
        final int count = mGesturePoints.size();

        Path path = null;

        float mX = 0;
        float mY = 0;

        for (int i = 0; i < gestureTrendChangeTimes; i ++) {
            float x = mGesturePoints.get(i).x;
            float y = mGesturePoints.get(i).y;
            if (path == null) {
                path = new Path();
                path.moveTo(x, y);
                mX = x;
                mY = y;
            } else {
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);//画贝塞尔曲线
                    mX = x;
                    mY = y;
                }
            }
        }
        mCachedPath = path;
    }

    /**
     * Draws the stroke with a given canvas and paint.
     * 
     * @param canvas
     */
    void draw(Canvas canvas, Paint paint) {
        if (mCachedPath == null) {
            makePath();
        }

        canvas.drawPath(mCachedPath, paint);
    }	
 
    public Path getPath() {
    	if (mCachedPath!= null) {
    		clearPath();
		}
    	makePath();
        return mCachedPath;
    }
    public Path getPath(int gestureTrendChangeTimes) {
    	if (mCachedPath!= null) {
    		clearPath();
		}
            makePath(gestureTrendChangeTimes);
        return mCachedPath;
    }

    /**
     * Converts the stroke to a Path of a given number of points.
     * 
     * @param width the width of the bounding box of the target path
     * @param height the height of the bounding box of the target path
     * @param numSample the number of points needed
     * 
     * @return the path
     */
    public Path toPath(float width, float height, int numSample) {
        final ArrayList<MGesturePoint> pts = temporalSampling(numSample);
        final RectF rect =getStrokeBoundBoxRect();

        translate(pts, -rect.left, -rect.top);
        
        float sx = width / rect.width();
        float sy = height / rect.height();
        float scale = sx > sy ? sy : sx;
        scale(pts, scale, scale);

        float mX = 0;
        float mY = 0;

        Path path = null;

        final int count = pts.size();

        for (int i = 0; i < count; i ++) {
            float x = pts.get(i).x;
            float y = pts.get(i).y;
            if (path == null) {
                path = new Path();
                path.moveTo(x, y);
                mX = x;
                mY = y;
            } else {
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
            }
        }

        return path;
    }
    
    public  ArrayList<MGesturePoint> temporalSampling(int numPoints) {
        final float increment =length / (numPoints - 1);
//        int vectorLength = numPoints * 2;
//        float[] vector = new float[vectorLength];
        ArrayList<MGesturePoint> temporalSampled=new ArrayList<MGesturePoint>(numPoints);
        float distanceSoFar = 0;
//        float[] pts = stroke.points;
        float lstPointX = mGesturePoints.get(0).x;
        float lstPointY = mGesturePoints.get(0).y;
        long  lstPointTimestamp=mGesturePoints.get(0).timestamp;
        int index = 0;
        float currentPointX = Float.MIN_VALUE;
        float currentPointY = Float.MIN_VALUE;
        long  currentPointTimestamp=Long.MIN_VALUE;
        temporalSampled.add(new MGesturePoint(lstPointX,lstPointY, lstPointTimestamp));
        index++;
        /*
         * 上面对采样后的笔迹进行了时间的保留，后面的所有点的时间戳都是lstPointTimestamp=mGesturePoints.get(0).timestamp;
         */
        int i = 0;
        int count = mGesturePoints.size();
        while (i < count) {
            if (currentPointX == Float.MIN_VALUE) {
                i++;
                if (i >= count) {
                    break;
                }
                currentPointX = mGesturePoints.get(i).x;
                currentPointY = mGesturePoints.get(i).y;
                currentPointTimestamp=mGesturePoints.get(i).timestamp;
            }
            float deltaX = currentPointX - lstPointX;
            float deltaY = currentPointY - lstPointY;
            float distance = (float) Math.hypot(deltaX, deltaY);//正常返回sqrt(x^2+y^2)
            if (distanceSoFar + distance >= increment) {
                float ratio = (increment - distanceSoFar) / distance;
                float nx = lstPointX + ratio * deltaX;
                float ny = lstPointY + ratio * deltaY;
                temporalSampled.add(new MGesturePoint(nx,ny, lstPointTimestamp));//这里并没有更新时间
                index++;
                lstPointX = nx;
                lstPointY = ny;
                distanceSoFar = 0;
            } else {
                lstPointX = currentPointX;
                lstPointY = currentPointY;
                currentPointX = Float.MIN_VALUE;
                currentPointY = Float.MIN_VALUE;
                distanceSoFar += distance;
            }
        }

        for (i = index; i < numPoints; i ++) {
        	temporalSampled.add(new MGesturePoint(lstPointX,lstPointY, lstPointTimestamp));
        }
        return temporalSampled;
    }
    
    static ArrayList<MGesturePoint> translate(ArrayList<MGesturePoint> points, float dx, float dy) {
        int size = points.size();
        for (int i = 0; i < size; i ++) {
            points.get(i).x += dx;
            points.get(i).y+=dy;
        }
        return points;
    } 
    static ArrayList<MGesturePoint> scale(ArrayList<MGesturePoint> points, float sx, float sy) {
    	int size = points.size();
        for (int i = 0; i < size; i ++) {
        	 points.get(i).x *= sx;
        	 points.get(i).y*= sy;
        }
        return points;
    }
    /**
     * Invalidates the cached path that is used to render the stroke.
     */
    public void clearPath() {
        if (mCachedPath != null) mCachedPath.rewind();
    }	
	
	/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-1 下午9:24:35
 * @return
 */
	public RectF getStrokeBoundBoxRect(){
		setGestureBoundBoxRect(mGesturePoints);//浪费性能，却节省了一个Rect对象，值不值得?
		return boundBoxRect;
	}
	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2019-1-1 下午9:24:41
	 * @param gestureTrendChangeTimes
	 * @return
	 */
	public RectF getStrokeBoundBoxRect(int gestureTrendChangeTimes){
		setGestureBoundBoxRect(mGesturePoints,gestureTrendChangeTimes);//浪费性能，却节省了一个Rect对象，值不值得?
		return boundBoxRect;
	}	
	
	 public byte[] getStrokeTrend(){

		 return gesturesTrend;
	 }

	 public void clear(){
		 mGesturePoints.clear();
		 gesturesTrendCount=0;
		 length=0;
		 for (int i = 0; i < gesturesTrend.length; i++) {
			 gesturesTrend[i]=0;
		}
		 boundBoxRect.setEmpty();
		 index=0;
		 mCachedPath.rewind();
	 }
	
	
}
