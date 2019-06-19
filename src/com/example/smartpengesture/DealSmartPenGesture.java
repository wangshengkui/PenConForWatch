package com.example.smartpengesture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator.IsEmpty;

import com.example.mgesture.MGesture;
import com.example.mgesture.MGestureUnitils;
import com.example.pencon.MainActivity;
import com.example.pencon.MainActivity;
import com.example.pencon.ObjAndByte;
import com.example.pencon.R;
import com.example.pencon.RecordingService;
import com.example.pencon.RecordingService.MyBinder;
import com.example.readAndSave.SmartPenUnitils;
import com.example.pencon.StringMessage;
import com.example.pencon.UpLoad;
import com.google.common.collect.ArrayListMultimap;
import com.tqltech.tqlpencomm.Dot;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class DealSmartPenGesture {
	MainActivity activity;
    public MyBinder recordService;
    public Intent intent; 
    public int PageID=-1;
   public  ArrayList<Integer> tag=null;
   public String gestureFinalName="手势名称";
   public String  gestureResponce="响应方式";
    private ArrayList<Integer> itemsContainer=new ArrayList<Integer>();//布置作业的时候的题目容器
    private ParseXml parseXml=null;
    private volatile long lastItemTime=0; //老师最后一次选择题目的当前系统时间(正常是为了布置作业)
    public void setPageID( int PageID) {
    	this.PageID=PageID;
		if (parseXml==null) {
			parseXml=new ParseXml();
		}
	int parseXmlStatus=parseXml.setParseXml(PageID);
	switch (parseXmlStatus) {
	case 0:
		parseXml=null;
		break;
	case -1:
		parseXml=null;
		break;
	case 1:
		return;
	default:
		break;
	}	
	}
    public final ServiceConnection recordConnection=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			recordService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			recordService=(MyBinder) service;
		}
	};

	public void setDealSmartPenGesture(Context activity){
		this.activity=(MainActivity) activity;
        intent = new Intent(activity, RecordingService.class);
	}
	
	private ArrayListMultimap<String, GesturePlaceAndResource> gesturePlaceContainer= ArrayListMultimap.create(); // Book=100笔迹数据
	private float delt=15;//判断手势是否在同一个地方的手势边框的冗余量；
	

	public String recogniseSmartPenGesture(MGesture gesture ,MGestureUnitils mGestureUnitils){
		ArrayList<MGesture> mGesturesContainer = null;
		MGesture predictionMGesture=null;
		try {
			mGesturesContainer = mGestureUnitils.load();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (mGesturesContainer!=null) {
			Log.e("zgm", "1210：手势文件装载成功");
			predictionMGesture=mGestureUnitils.recogniseGeMGesture(gesture);
			
			if (predictionMGesture!=null) {
             String resultsString=predictionMGesture.getGestureName();
             return resultsString;
           
			}else {
				return null;
			}
	    }
		Log.e("zgm", "mGesturesContainer为空");
		return null;
	}
	public String recogniseSmartPenGesture(SmartPenGesture gesture){
//	GestureLibrary gestureLibrary=GestureLibraries.fromFile("/sdcard/zgmgesture");
	if (activity.gestureLibrary.load()) {
//		Log.e("zgm", "1210：手势文件装载成功");
	Set<String> aSet=activity.gestureLibrary.getGestureEntries();
/*	for (String string : aSet) {
		Log.e("zgm", "0113:"+string);
	}*/
/*	Gesture firstGesture=new Gesture();
	firstGesture.addStroke(gesture.getStrokes().get(0));*/
		ArrayList<Prediction> predictions=activity.gestureLibrary.recognize(gesture);
        ArrayList<GestureScore> gestureScores=new ArrayList<DealSmartPenGesture.GestureScore>();
        for (Prediction prediction :predictions) {
			if (prediction.score>2.5) {
				gestureScores.add(new GestureScore(prediction.name, prediction.score));
			}
		}
        String gestureName = getHightestScoreGesture(gestureScores);
        return gestureName;
	    }else {
			return null;
		}
	}

/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：手势识别主逻辑
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-24 下午4:08:41
 * @param currentSmartPenGesture
 */
	public void dealWithGesture(SmartPenGesture currentSmartPenGesture) {
		tag=getChirographyPositionInfo(currentSmartPenGesture,PageID);
		if(tag==null) {
			activity.runOnUIThread("警告", "模板文件读取错误");
		return ;
		
		}
//		Log.e("zgm", "tag0:"+tag.get(0)+"tag1:"+tag.get(1));
		String gestureName = null;
//		if (tag.get(1)==2) {//等于题号区
//			gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
//			activity.updateUsingInfo("题号区域动作",activity.ORDERSTATE);
//			if (gestureName==null) {
//				activity.updateUsingInfo("未识别的题号区域动作",activity.ORDERSTATE);
//				return;
//				
//			}
//			if (gestureName.equals("圈题")) {
//				activity.updateUsingInfo("圈题第"+tag.get(0)+"题",activity.ORDERSTATE);
//				return;
//			}
//			if (gestureName.equals("圈题结束")) {
//				activity.updateUsingInfo("圈题结束第"+tag.get(0)+"题",activity.ORDERSTATE);
//				return;
//			}
//		}
		
		

//		activity.updateUsingInfo("x坐标:" +averges[0] + "   " + "y坐标:" +averges[1],activity.PENPOINTINF);
		
		// TODO Auto-generated method stub
//		Log.i("zgm","0122：currentSmartPenGesture.getStrokesCount()"+currentSmartPenGesture.getStrokesCount());
		if (currentSmartPenGesture.getStrokesCount()==1) {
//			Log.e("zgm","0313:da'a" );
			if (isclick(currentSmartPenGesture)) {
//   tag=getSingleChirographyPositionInfo(currentSmartPenGesture,PageID);

 if(tag.size()>=2 && tag.get(1)==4)
 {
	  activity.ReadTiMu(tag);
	  gestureResponce="语音响应";
	  gestureFinalName="单击";
	  activity.intGestureCount++;
	  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
 }
  
return ;
}	

//float[] averges=getGestureCenture(currentSmartPenGesture);//求手势的中心点坐标
gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
Log.e("zgm", "gestureName:"+gestureName);
			if (gestureName==null) {
				activity.updateUsingInfo("未识别的手势",activity.PENSTROKECOUNT);
				gestureFinalName="未知手势";
				return;
			}
//			if (gestureName.equals("录音")&&tag.get(1)!=2) 
			if (gestureName.equals("录音") && tag.size()>=2){//不等于题号区
				getControlGestureBody(currentSmartPenGesture);
				Log.e("zgm", "gestureName:"+gestureName);
				activity.updateUsingInfo(gestureName,activity.PENSTROKECOUNT);
				  activity.intGestureCount++;
				  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
				String audioName="001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(0)+".mp3";
				Log.e("zgm", "audioName:"+audioName);
				activity.updateUsingInfo(audioName,activity.PENSTROKECOUNT);
				File file=new File("/sdcard/xyz/"+audioName);
				if (file.exists()) {
					Log.e("zgm", "音频文件存在");
					playAudio("/sdcard/xyz/"+audioName);
					return;
				}
				recordAudioGestureProcess(currentSmartPenGesture);
				return ;
			}
//			if (gestureName.equals("录音")||gestureName.equals("选题")) {//布置作业逻辑
//				if (parseXml!=null) {//这个需要提前设置PageId保证
////					float[] averges=getGestureCenture(currentSmartPenGesture);//求手势的中心点坐标
////					activity.updateUsingInfo("x坐标:" +averges[0] + "   " + "y坐标:" +averges[1],activity.PENPOINTINF);
////					activity.updateUsingInfo("未识别的手势",activity.ORDERSTATE);
//					ArrayList<Integer> areaArrayList=parseXml.getAreaInfo(averges[0], averges[1]);
//	                /*
//	                 * 获得画手势的题目区域
//	                 * areaArrayList.get(0):题目编号
//	                 * areaArrayList.get(1)：题目编号对应的题的区域编号，0代表题目编号所在区域
//	                 */
////					Log.e("zgm","0124:"+areaArrayList );
//					if (areaArrayList!=null) {//是在已经制作过模板的纸上画手势，且模板存在
//						if (areaArrayList.get(1)==0) {//是在题目编号处画手势
//							lastItemTime=System.currentTimeMillis();
//							if (itemsContainer.size()==0) {
//								new Thread(new Runnable() {
//									
//									@Override
//									public void run() {
//										while(System.currentTimeMillis()-lastItemTime<300000){
//											/*
//											 * 等待五分钟(300000),
//											 * 300000的时间下面布置完题目后依然要使用
//											 * 通过提前结束当前时间运行到这里来清空itemsContainer
//											 */
//										}
//										itemsContainer.clear();
//									}
//								});
//							}
//							itemsContainer.add(areaArrayList.get(0));
//							if (gestureName.equals("录音")) {
//								/*
//								 * 这里写发布题目的逻辑代码
//								 */
//								lastItemTime=lastItemTime-300000;//使上面的计时提前器结束
//								return;
//	 						}
//							activity.updateUsingInfo("选题中……",activity.ORDERSTATE);
//							return;
//						}
//					}
//				}
//				if (gestureName.equals("录音")) {
//					recordAudioGestureProcess(currentSmartPenGesture);
//					return ;
//				}
//				if (gestureName.equals("选题")) {
//					getGestureCenture(currentSmartPenGesture);
//					activity.updateUsingInfo("未在规定位置选题，",activity.ORDERSTATE);
//					//正常这里不应该进行处理直接返回
//					return;
//				}
//
//			}
			if (gestureName.equals("对")) {
				//正常这里不应该进行处理直接返回
				activity.updateUsingInfo("您画的是判题类手势:对",activity.ORDERSTATE);	
				gestureFinalName="对";
				gestureResponce="语音响应";
				  activity.intGestureCount++;
				  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
				return;
			}
			//手势只有一笔，且不是录音和选题，交给wsk处理
			return;
		}
		else {//手势笔画数大于1,对每一笔都进行识别,看是否有对号，录音符号
			SmartPenGesture tempGesture=new SmartPenGesture();
			int clickTimes=0;
			if (currentSmartPenGesture.getStrokesCount() == 2) {
//				ArrayList<Integer> tag = null;
				for (int i = 0; i < 2; i++) {
					tempGesture.SmartPenGestureClearAllStroke();
					tempGesture.SmartPenGestureClearmBoundingBox();
					tempGesture.addStroke(currentSmartPenGesture.getStrokes().get(i));// 将每一笔手势都重新放入临时手势中
					if (isclick(tempGesture)) {
						clickTimes++;
					}
				}
//				tag=getSingleChirographyPositionInfo(tempGesture, PageID);//如果根据题目读要求的话，需要这个tag,否则不需要
    
				if (clickTimes==2) {
					float[] a=getGestureCenture(currentSmartPenGesture);
					if (a[1]<20) {
					Log.e("zgm", "0415:双击页眉");
					  activity.intGestureCount++;
					  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					activity.showSound(R.raw.in);
					/**
					 * 双击页眉保存并上传代码
					 */
					SmartPenUnitils.save(activity.smartPenPage);
//					showSound(R.raw.in);

					new Thread(new Runnable() {				
						@Override
						public void run() {
							// TODO Auto-generated method stub
						boolean statsu=	UpLoad.uploadFile("http://118.24.109.3/Public/smartpen/upload.php","/sdcard/-1/"
									+ "NONE-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-0.page");	
						if (statsu) {
							activity.showSound(R.raw.upload_sucess);					
//							Toast.makeText(getBaseContext(), "上传成功", Toast.LENGTH_SHORT).show();
						}
						else {
							activity.showSound(R.raw.upload_fail);	
//							Toast.makeText(getBaseContext(), "上传失败", Toast.LENGTH_SHORT).show();
						}
						}
					}).start();

					return;
					}
//					activity.showSound(R.raw.sanweiyuyi);
					if(tag.size()>=2 && tag.get(1)==4)
					 {
						activity.readSanWeiYuYi(tag);
						gestureResponce="语音响应";
						gestureFinalName="双击";
					 }
					return;
				} 
			}
			
			
			
			
			int tempIndex=-1;
			int situation=-1;
			for (int i = 0; i < currentSmartPenGesture.getStrokesCount(); i++) {//拆分手势，进行单笔识别
				tempGesture.SmartPenGestureClearAllStroke();
				tempGesture.SmartPenGestureClearmBoundingBox();
				tempGesture.addStroke( currentSmartPenGesture.getStrokes().get(i));//将每一笔手势都重新放入临时手势中进行识别
				gestureName=recogniseSmartPenGesture(tempGesture);
				if (gestureName==null) {
					continue;
				}
				if (gestureName.equals("录音")) {
					situation=1;
					tempIndex=i;
					break;
				}
				if (gestureName.equals("对")) {
					situation=2;
//					tempIndex=i;
					break;
				}
			}
			
			switch (situation) {
			case 1://其中一笔是录音手势
//				重新封装剩下的笔画，用其他方法识别
				tempGesture.SmartPenGestureClearAllStroke();
				for (int i = 0; i <currentSmartPenGesture.getStrokesCount(); i++) {
					if (i==tempIndex) {
						continue;
					}
					tempGesture.addStroke(currentSmartPenGesture.getStrokes().get(i));
				}
				//将剩下的手势进行重新处理
				Bitmap bitmap=tempGesture.toBitmap(32, 32, 2, Color.WHITE);
			String simble=	matrix(bitmap);//概率小于0.6则返回null
			if (simble==null) {
				activity.updateUsingInfo("您画的是投票类手势：但我不知道选了什么",activity.ORDERSTATE);
				gestureFinalName="未知手势";
				return;
			}
			if (activity.groupstatus==9) {
				if (simble.equals("A")&&activity.groupLeader==1) {
					if (activity.correcting) {
						activity.correcting=false;
							activity.runOnUIThread("通知","退出了批改状态");
						activity.dismissAlertDialog(activity.builder);
						  activity.intGestureCount++;
						  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
						gestureFinalName="选A";
						gestureResponce="分享选项";
						return;
					}
					activity.updateUsingInfo("您画的是投票类手势：选"+simble,activity.ORDERSTATE);	
					gestureFinalName="选A";
					gestureResponce="分享选项";
					return;
				}
				if (simble.equalsIgnoreCase("C")&&activity.groupLeader==0) {
					if (activity.correcting) {
						activity.correcting=false;
							activity.runOnUIThread("通知","退出了批改状态");
						activity.dismissAlertDialog(activity.builder);
						gestureResponce="分享选项";
						gestureFinalName="选C";
						  activity.intGestureCount++;
						  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
						return;
					}
					activity.updateUsingInfo("您画的是投票类手势：选"+simble,activity.ORDERSTATE);	
					gestureResponce="分享选项";
					gestureFinalName="选C";
					return;					
				}
				{
					if (simble.equalsIgnoreCase("C")) {
						activity.updateUsingInfo("您要给组员："+activity.otherNameString+"批改作业",activity.ORDERSTATE);
						activity.correcting=true;
						gestureResponce="分享选项";
						gestureFinalName="选C";
						  activity.intGestureCount++;
						  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					}	
					if (simble.equals("A")) {
						activity.updateUsingInfo("您要给组长："+activity.otherNameString+"批改作业",activity.ORDERSTATE);	
						activity.correcting=true;
						gestureResponce="分享选项";
						gestureFinalName="选A";
						  activity.intGestureCount++;
						  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					}
					StringMessage mStringMessage = new StringMessage(activity.HOMEWORK,activity.mNameString+ "：要批改你的作业" ,activity.mNameString);
					 byte[] mbyte = ObjAndByte
								.ObjectToByte(mStringMessage);
					 activity.sendMessageManyTimes(mbyte);
					 return;
				}
				
				
			}else {
				  activity.intGestureCount++;
				  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
				activity.updateUsingInfo("您画的是投票类手势：选"+simble,activity.ORDERSTATE);
				gestureResponce="分享选项";
				gestureFinalName="选"+simble;
			}
				break;
			case 2://其中一笔是对
				switch (currentSmartPenGesture.getStrokesCount()) {
				case 2:
					gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
					if (gestureName!=null&&gestureName.equals("错")) {
						activity.updateUsingInfo("您画的是判题类手势:错",activity.ORDERSTATE);	
						gestureResponce="语音响应";
						gestureFinalName="错";
						  activity.intGestureCount++;
						  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
						return;
					}
					activity.updateUsingInfo("您画的是判题类手势:半对",activity.ORDERSTATE);
					gestureFinalName="半对";
					  activity.intGestureCount++;
					  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					return;
				case 3:
					activity.updateUsingInfo("您画的是判题类手势:半对2",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="半对2";
					  activity.intGestureCount++;
					  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					return;
				case 4:
					activity.updateUsingInfo("您画的是判题类手势:半对3",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="半对3";
					  activity.intGestureCount++;
					  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					return;
				default:
					gestureFinalName="未知手势";
					break;
				}
				activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
				gestureFinalName="未知手势";
				break;
			case -1:

				gestureName=recogniseSmartPenGesture(currentSmartPenGesture);
				if (gestureName!=null&&gestureName.equals("错")) {
					activity.updateUsingInfo("您画的是判题类手势:错",activity.ORDERSTATE);
					gestureResponce="语音响应";
					gestureFinalName="错";
					  activity.intGestureCount++;
					  activity.updateUsingInfo( activity.intGestureCount+"", activity.GESTURECOUNTER);
					return;
				}
/*				Bitmap mbitmap=currentSmartPenGesture.toBitmap(32, 32, 2, Color.WHITE);
				String msimble=	matrix(mbitmap);
				if (msimble==null) {
					if (currentSmartPenGesture.getStrokesCount()==2) {
						gestureName=recogniseSmartPenGesture(currentSmartPenGesture);

						
					}*/
//					activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
//				}
//				activity.updateUsingInfo("您画的手势是："+msimble,activity.ORDERSTATE);
				
				activity.updateUsingInfo("您画的手势我没有识别",activity.ORDERSTATE);
				gestureFinalName="未知手势";
				break;
			default:
				gestureFinalName="未知手势";
				break;
			}
		}
	}	
	
public String getHightestScoreGesture(ArrayList<GestureScore> arrayList){
	if (arrayList.size()==0) {
		return null;
	}
	double hightestScore=-5;
	String  gestureName=null;
	for (int i=0;i<arrayList.size();i++) {
		if (arrayList.get(i).getGestureScore()>hightestScore) {
			hightestScore=arrayList.get(i).getGestureScore();
			gestureName=arrayList.get(i).getGestureNmae();
		}
	}
	return gestureName;	
}	
private	 boolean isAlmostEqual(RectF rectF1,RectF rectF2){
	float centerPointX1=(rectF1.right+rectF1.left)/2;
	float centerPointY1=(rectF1.bottom+rectF1.top)/2;
	float centerPointX2=(rectF2.right+rectF2.left)/2;
	float centerPointY2=(rectF2.bottom+rectF2.top)/2;
/*	float leftDistance=Math.abs(rectF1.left-rectF2.left);
//	float topDistance=Math.abs(rectF1.top-rectF2.top);
//	float rightDistance=Math.abs(rectF1.right-rectF2.right);
	float bottomDistance=Math.abs(rectF1.bottom-rectF2.bottom);
	*/
	if (Math.abs(centerPointX1-centerPointX2)<delt&&Math.abs(centerPointY1-centerPointY2)<delt) {
		return true;
	}
	return false;	
}

public class GestureScore{
	String gestureName="";
	double gestureScore=0;
	public GestureScore(String gestureName, double score) {
		// TODO Auto-generated constructor stub
		this.gestureName=gestureName;
		this.gestureScore=score;
	}
	public String getGestureNmae() {
		return gestureName;
		
	}
	public double getGestureScore(){
		return gestureScore;
		
	}
}	

private class GesturePlaceAndResource{
	String gestureNameString="";
	String resourcePathString="";
	long    resourceElpased=0;
    final	RectF rectF;
	
	GesturePlaceAndResource(String gestureNameString,String resourcePathString,long resourceElpased,RectF rectF){
		this.gestureNameString=gestureNameString;
		this.resourcePathString=resourcePathString;
		this.resourceElpased=resourceElpased;//播放时长
		this.rectF=rectF;
	}
public String getGestureNameString(){
	return gestureNameString;
}
public String getResourcePath(){
	return resourcePathString;
}
public RectF getGesturePlace(){
	return this.rectF;
}

public long getresourceElpased(){
	return resourceElpased;
}
}

private void onRecord(boolean start) {
    if (start) {

//        Toast.makeText(activity, "开始录音...", Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() + "/penconNativeRecord  ");
        if (!folder.exists()) {
            //folder /SoundRecorder doesn't exist, create the folder
            folder.mkdir();
        }

        //start Chronometer
//        mChronometerTime.setBase(SystemClock.elapsedRealtime());
//        mChronometerTime.start();
        //start RecordingService
        activity.startService(intent);
        if (activity.bindService(intent,recordConnection,Service.BIND_AUTO_CREATE)) {
			Log.e("zgm", "1217:绑定成功！");
		}else {
			Log.e("zgm", "1217:绑定失败！ "+activity.getClass().getName());
		}
//        
        
        //keep screen on while recording
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    } else {
//        Toast.makeText(activity, "录音结束...", Toast.LENGTH_SHORT).show();
        activity.unbindService(recordConnection);
        activity.stopService(intent);
    }
}

public void playAudio(GesturePlaceAndResource gesturePlaceAndResource){
	activity.updateUsingInfo("播放相关资源"+gesturePlaceAndResource.getResourcePath(),activity.ORDERSTATE);
Log.e("zgm","1218：播放相关资源"+gesturePlaceAndResource.getResourcePath());
final MediaPlayer mMediaPlayer = new MediaPlayer();
try {
//    mMediaPlayer.setDataSource( Environment.getExternalStorageDirectory().getAbsolutePath()+"/SoundRecorder/" + "recordAudio"+ ".mp3");
	mMediaPlayer.setDataSource(gesturePlaceAndResource.getResourcePath());
	mMediaPlayer.prepare();

} catch (IOException e) {
    Log.e("zgm", "prepare() failed");
}
mMediaPlayer.start();
new Thread(new Runnable() {
	
	@Override
	public void run() {
		long tempStartTime=System.currentTimeMillis();
		while (System.currentTimeMillis()-tempStartTime<3*1000) {
			//空循环
		}
		activity.updateUsingInfo("留言播放结束",activity.ORDERSTATE);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        activity.isDealPenPoint=true;
	}
}).start();
return;
/*						com.example.pencon.RecordingItem recordingItem = new com.example.pencon.RecordingItem();
//播放相关资源
recordingItem.setLength((int) gesturePlaceAndResource.getresourceElpased());
recordingItem.setFilePath(gesturePlaceAndResource.getResourcePath());
com.example.pencon.PlaybackDialogFragment fragmentPlay = com.example.pencon.PlaybackDialogFragment.newInstance(recordingItem);
fragmentPlay.show(activity.getSupportFragmentManager(), com.example.pencon.PlaybackDialogFragment.class.getSimpleName());
*/	

}


public  void  playAudio(final String path){
	 final MediaPlayer mMediaPlayer = new MediaPlayer();
		try {
		    mMediaPlayer.setDataSource(path);
		    mMediaPlayer.prepare();

		} catch (IOException e) {
		    Log.e("zgm", "prepare() failed");
		}
		mMediaPlayer.start();
	new Thread(new Runnable() {
		
		@Override
		public void run() {
while(mMediaPlayer.isPlaying()) {
	try {
		Thread.sleep(1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
}
if (!mMediaPlayer.isPlaying()) {
	mMediaPlayer.release();
}



		}
	}).start();


	
}
public void recordAndio(final RectF boundingBox,boolean isjustforname){
	String gestureNameString="录音";
	GesturePlaceAndResource gesturePlaceAndResource;
//	 Log.e("zgm","/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3");
	 
	if (isjustforname) {
		
		 if(boundingBox!=null && tag.size()>=2){
			 RectF tempRectF=new RectF(boundingBox);
    		  gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,"/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3",2000,tempRectF);
    		  Log.e("zgm","/sdcard/xyz/001-"+activity.studentNumber+"-"+activity.gCurBookID+"-"+activity.gCurPageID+"-"+tag.get(1)+".mp3");
    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
return;
		 }		
	}
	activity.updateUsingInfo("请录音",activity.ORDERSTATE);
		onRecord(true);

	
	activity.updateUsingInfo("录音当中，录音3s，请留言",activity.ORDERSTATE);
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long temStartTime=System.currentTimeMillis();
			while (System.currentTimeMillis()-temStartTime<2*1000) {
			}
			onRecord(false);//结束录音
//			activity.showSound(R.raw.endrecord);
//			activity.showVibrator();// 震动
			String filePath=recordService.getService().getMFilePath();
			activity.soundPathString=filePath;
    		if (filePath==null) {
				activity.updateUsingInfo("没有录音",activity.ORDERSTATE);
				return;
				
			}
			 long elpased =recordService.getService().getMFileElpased();
			 String gestureNameString="录音";
			 if(boundingBox!=null){
				 RectF tempRectF=new RectF(boundingBox);
	    		 GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,tempRectF);
	    		 gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
 
				 
				 
			 }

    		 activity.updateUsingInfo("录音完成，音频文件路径："+filePath,activity.ORDERSTATE);
    		 activity.isDealPenPoint=true;
		}
	}).start();	
	
	
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-23 下午10:33:44
 * @param gestureName
 * @param gestureStroke
 * @return:返回的是点的序号（从零开始），因此返回值不会大于gestureStroke.points.length/2-1;
 */

public int  getTailIndex(String gestureName,GestureStroke gestureStroke){
	if (!gestureName.equals("录音")) {
		return -1;
	}
	if (gestureStroke.points.length<6) {
		return -1;
	}
	boolean first=true;
	boolean second=false,third=false;
	int index=-1;
	float x1,y1,x2,y2;
for (int i = 0; i < gestureStroke.points.length/2-2; i++) {
	x1=gestureStroke.points[i*2];
	y1=gestureStroke.points[i*2+1];
	x2=gestureStroke.points[(i+1)*2];
	y2=gestureStroke.points[(i+1)*2+1];
	if(first){
		if ((x2-x1)>0&&(y2-y1)<0) {
			first=false;
			second=true;
			third=false;
		}
	}
		if(second) {
			if ((x2-x1)<0&&(y2-y1)>0) {
				first=false;
				second=false;
				third=true;
			}
	}
		if (third) {
			if ((x2-x1)>0&&(y2-y1)<0) {
               index=i;
               return index;
			}
		
	}
}
	
	return -1;
	
}

public int  getTailIndex(String gestureName,ArrayList<Position> temContair){
	if (!gestureName.equals("录音")) {
		return -1;
	}
	if ( temContair.size()<8) {
		return -1;
	}
	boolean first=true;
	boolean second=false,third=false,forth=false;
	
	int index=-1;
	float x1,y1,x2,y2;
	float delt=(float) 0.3;//阈值

for (int i = 0; i < temContair.size()-1; i++) {
	x1=temContair.get(i).x;
	y1=temContair.get(i).y;
	x2=temContair.get(i+1).x;
	y2=temContair.get(i+1).y;
	if(first){
		if ((x2-x1)>delt&&(y2-y1)>delt) {//从左上到右下变化趋势
			first=false;
			second=true;
		}
	}
	if(second) {
		if ((x2-x1)>=delt&&(y2-y1)<=-delt) {//从左下到右上
			second=false;
			third=true;
			}				
	}
		if(third) {
			if ((x2-x1)<=-delt&&(y2-y1)>=-delt) {//从右上到左下

				third=false;
				forth=true;				
			}
	}
		if (forth) {
			if ((x2-x1)>delt&&(y2-y1)<delt) {//从左下到右上
               index=i;
               Log.i("zgm", "0615:"+index);
               return index;
			}		
	}
}	
Log.i("zgm", "0615:"+(-1));
	return -1;	
}



public RectF getBoundingBox(int endIndex,GestureStroke gestureStroke){
	if (endIndex<0||gestureStroke.points.length==0) {
		return null;
	}
	RectF boundingBox=new RectF();
	for (int i = 0;i < gestureStroke.points.length/2; i++) {
		if (i<=endIndex) {
			if (i==0) {
				boundingBox.left=gestureStroke.points[i*2];
				boundingBox.right=boundingBox.left;
				boundingBox.top=gestureStroke.points[i*2+1];
				boundingBox.bottom=boundingBox.top;
			}
			boundingBox.union(gestureStroke.points[i*2],gestureStroke.points[i*2+1]);
			continue;
		}
		break;
	}
	return boundingBox;
}
public RectF getBoundingBox(int endIndex,ArrayList<Position> temContaier){
	if (endIndex<0||temContaier.size()==0) {
		return null;
	}
	RectF boundingBox=new RectF();
	Dot tempDot;
	for (int i = 0;i < temContaier.size(); i++) {
		
		if (i<=endIndex) {
			if (i==0) {
				boundingBox.left=temContaier.get(i).x;
				boundingBox.right=temContaier.get(i).x;
				boundingBox.top=temContaier.get(i).y;
				boundingBox.bottom=temContaier.get(i).y;
				continue;
			}
			boundingBox.union(temContaier.get(i).x,temContaier.get(i).y);
			continue;
		}
		break;
	}
	return boundingBox;
}

public RectF getControlGestureBody(SmartPenGesture currentSmartPenGesture) {
	/*
	 * 注意录音手势应该只有一笔，这里就认为其只有一笔，所以下面的代码只适用于只有一笔的录音手势
	 * 
	 */
	float[] points=currentSmartPenGesture.getStrokes().get(0).points;
	ArrayList<Position> temContainer=new ArrayList<Position>();
for (int i = 0; i <points.length/2; i++) {
	temContainer.add(new Position(points[2*i], points[2*i+1]));
}
		    int endIndex= getTailIndex("录音",temContainer);
		    
		    if (endIndex==-1) {
		    	endIndex=temContainer.size()-1;
			}

		    RectF mainBoundingBox = getBoundingBox(endIndex,temContainer);
		/*
		 * 以上代码主要是获得录音手势去掉尾巴的的边界矩形
		 */
		    ArrayList<Position> tepmCountainer2=new ArrayList<Position>();
		    for (int i =0; i <endIndex; i++) {
		    	tepmCountainer2.add(temContainer.get(i));
		    }
		    float[] k_b=getKandB(tepmCountainer2);
//		    float temp =temContainer.get(temContainer.size()-1).x;
		    activity.updateInfo(k_b[0],activity.BODYSLOPE);//主体斜率		    
		    
//activity.drawRectF(mainBoundingBox);
		    activity.updateInfo((float) ((mainBoundingBox.left+mainBoundingBox.right)/2*1.524), activity.CENTERX);//中心点横坐标坐标
		    activity.updateInfo((float) ((mainBoundingBox.bottom+mainBoundingBox.top)/2*1.524), activity.CENTERY);//中心点垂直坐标坐标
		    float[] temlength1=getsignallength(endIndex,temContainer);
activity.updateInfo(temlength1[0], activity.TAILLENGTH);//控制符号尾巴长度
activity.updateInfo(temlength1[1], activity.PHYSICSTAILLENGTHPHYSICSTAILLENGTH);//控制符号尾巴长度
float[] temlength2=getsignallength(0,temContainer);
activity.updateInfo(temlength2[0], activity.GESTURELENGTH);//控制符号整体长度
activity.updateInfo(temlength2[1], activity.PHYSICSGESTURELENGTH);//控制符号整体长度
activity.updateInfo(mainBoundingBox.right-mainBoundingBox.left, activity.BOUNDINGBOXWIDTH);//控制符号主体边框宽
activity.updateInfo(mainBoundingBox.bottom-mainBoundingBox.top, activity.BOUNDINGBOXHEIGHT);//控制符号主体边框高
activity.updateInfo((temlength2[0]-temlength1[0])/(2*(mainBoundingBox.right-mainBoundingBox.left+mainBoundingBox.bottom-mainBoundingBox.top)), activity.BODYANDBODYBOXRATION);
activity.updateInfo(changeTimes(endIndex,temContainer), activity.TAILECHANGETIMES);//控制符号尾巴变化次数
activity.updateInfo(temContainer.size()-endIndex,activity.TAILPOINTCOUNTER);//控制符号尾巴变化次数
ArrayList<Position> tepmCountainer1=new ArrayList<Position>();
//int[] temIndexs=getLongestLineArea(endIndex,temContainer);
//for (int i = temIndexs[0]; i <temIndexs[1]+1; i++) {
	for (int i =endIndex; i <temContainer.size(); i++) {
	tepmCountainer1.add(temContainer.get(i));
}

float[] a_b=getKandB(tepmCountainer1);
activity.updateInfo(a_b[0],activity.TAILSLOPE);//控制符号尾巴变化次数
//activity.drawLine(temContainer.get(temIndexs[0]).x,temContainer.get(temIndexs[0]).y, temp, a_b[0]*temp+a_b[1]);
//activity.drawLine(temContainer.get(endIndex).x,temContainer.get(endIndex).y,temp,a_b[0]*temp+a_b[1]);

	return mainBoundingBox;
}

/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：还未进行调试(可能有bug,暂时还没有使用)，对点序列中的相邻两个点直接进行插值，使其x坐标连续(每一个整数都能对应一个x坐标).
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-20 上午11:26:58
 * @param gestureStroke
 * @return
 */

public void recordAudioGestureProcess(SmartPenGesture currentSmartPenGesture){
	if(activity==null){  return; }
	String gestureName="录音";
	/*
	 * 以下代码主要是获得录音手势去掉尾巴的的边界矩形
	 */
	RectF mainBoundingBox=null;
/*
 * 注意录音手势应该只有一笔，这里就认为其只有一笔，所以下面的代码只适用于只有一笔的录音手势
 */
/*	    int endIndex= getTailIndex( gestureName,currentSmartPenGesture.getStrokes().get(0));
	    if (endIndex==-1) {
	    	endIndex=currentSmartPenGesture.getStrokes().get(0).points.length/2-1;
		}*/
//	    mainBoundingBox=getBoundingBox(endIndex, currentSmartPenGesture.getStrokes().get(0));
	    mainBoundingBox=getControlGestureBody(currentSmartPenGesture);
	/*
	 * 以上代码主要是获得录音手势去掉尾巴的的边界矩形
	 */
/*			if(gesturePlaceContainer.get(gestureName).size()==0){
		//还没有录过音，开始第一个录音
	return;
	}else */
	{//代码块，匹配手势用
		
		activity.isDealPenPoint=false;
		Log.e("zgm", "1218:"+gestureName);
		List<GesturePlaceAndResource> a = gesturePlaceContainer.get(gestureName);
		if (a.size()>0) {
			Log.e("zgm","0108:"+a.size()+":"+a.get(0).rectF);	
		}

		for (GesturePlaceAndResource gesturePlaceAndResource : gesturePlaceContainer.get(gestureName)) {
			if (isAlmostEqual(mainBoundingBox,gesturePlaceAndResource.getGesturePlace())) {
				playAudio(gesturePlaceAndResource);
                  return;
				
			}
		}
	}
	
	{//代码块，没有匹配到手势，那么就开始录音相关的操作
	Log.e("zgm","1216：没有匹配到录音手势");
	activity.updateUsingInfo("没有匹配到录音手势,两秒之后听见滴的一声后请录音",activity.ORDERSTATE);

		recordAndio( mainBoundingBox,!activity.doSomeworkIsOK);

	
/*			activity.showSound(R.raw.recordstart);
	activity.showVibrator();// 震动
	long starttime=System.currentTimeMillis();
//	这里之所以写成死循环，是故意为了阻塞
	while(System.currentTimeMillis()-starttime<2000){
		//空循环等待
	}*/
	
//	activity.showSound(R.raw.startrecord);
//	activity.showVibrator();// 震动

	
/*            final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance();
    fragment.show(activity.getSupportFragmentManager(), RecordAudioDialogFragment.class.getSimpleName());
    fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
        @Override
        public void onCancel() {
            SharedPreferences sharePreferences = activity.getSharedPreferences("sp_name_audio", Service.MODE_PRIVATE);
//            final String filePath = sharePreferences.getString("audio_path", "");
            String filePath=fragment.getRecordFilePath(fragment.recordService);
            Log.e("zgm", "1223:filePath:"+filePath);
            long elpased = fragment.getRecordFileElpased(fragment.recordService);
            Log.e("zgm", "1223:elpased="+elpased);
            String gestureNameString="录音";
        	
			RecordingService.MyBinder myRecordBinder=(MyBinder) fragment.recordService;
			
			String resourcePathString=fragment.getRecordFilePath(myRecordBinder);
			if (filePath==null) {
				activity.runOnUIThread("没有录音");
				return;
				
			}
//			long  resourceElpased=fragment.getRecordFileElpased(myRecordBinder);
			Log.e("zgm","1223:filePath"+filePath+" elpased:" +
					elpased+" boundingBox:"+boundingBox);

			GesturePlaceAndResource gesturePlaceAndResource=new GesturePlaceAndResource(gestureNameString,filePath,elpased,boundingBox);
			gesturePlaceContainer.put(gestureNameString, gesturePlaceAndResource);//将手势和相关的信息加入gesturePlaceContainer中;
			activity.runOnUIThread("录音完成，音频文件路径："+filePath);
			fragment.dismiss();
        }
    });	
*/
	}//代码块完	
}

 public float[]  linearInterpolation(GestureStroke gestureStroke){
	ArrayList<Float> interpolatedPoints=new ArrayList<Float>();
	if (gestureStroke.points.length<4) {
		return gestureStroke.points;
	}
	int x1,y1,x2,y2;
	for (int i = 0; i < gestureStroke.points.length/2-1; i=i+1) {
		x1=Math.round(gestureStroke.points[i*2]);
		y1=Math.round(gestureStroke.points[i*2+1]);
		x2=Math.round(gestureStroke.points[(i+1)*2]);
		y2=Math.round(gestureStroke.points[(i+1)*2+1]);
		if (i==0) {
			interpolatedPoints.add((float) x1);
			interpolatedPoints.add((float) y1);
		}
		if (x1==x2&&y1==y2) {//情况1
			interpolatedPoints.add((float) x2);
			interpolatedPoints.add((float) y2);
			continue;
		}
		if (x1==x2&&y1!=y2) {//情况2
			int tempy=y1;
			if (tempy<y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy+1);
					tempy=tempy+1;
				}
				continue;
			}
			if (tempy>y2) {
				while(tempy<y2){
					interpolatedPoints.add((float) x1);
					interpolatedPoints.add((float) tempy-1);
					tempy=tempy-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1==y2) {//情况3
			int tempx=x1;
			if (tempx<x2) {
				while(tempx<x2){
					interpolatedPoints.add((float) tempx+1);
					interpolatedPoints.add((float) y1);
					tempx=tempx+1; 
				}
				continue;
			}
			if (tempx>x2) {
				while(tempx>x2){
					interpolatedPoints.add((float) tempx-1);
					interpolatedPoints.add((float) y1);
					tempx=tempx-1;
				}
				continue;
			}
		}
		if (x1!=x2&&y1!=y2) {//情况4
		float bx1x2 =y1*(x2-x1)-x1*(y2-y1);
		int tempx=x1;
		float tempy;
		if (tempx<x2) {
			while (tempx<x2) {
				tempy=((tempx+1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx+1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx+1;
			}
           continue;
		}
		if (tempx>x2) {
			while (tempx>x2) {
				tempy=((tempx-1)*(y2-y1)+bx1x2)/(x2-x1);
				interpolatedPoints.add((float) tempx-1);
				interpolatedPoints.add((float) y1);	
				tempx=tempx-1;
			}
           continue;
		}
		}
		
		
		
	}
	return null;
	
	
	
	
}         
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：求得手势的中心点坐标(所有笔画的中心)
 * @date：2019-1-23 下午9:05:28
 * @param currentSmartPenGesture
 * @return:返回手势的中心点坐标
 */
public float[] getGestureCenture(SmartPenGesture currentSmartPenGesture){
	float[] averges=new float[2];
	averges[0]=0;//averges[0]存放的是中心点x的坐标
	averges[1]=0;//averges[1]存放的是中心点y的坐标
	int counter=0;
	for (GestureStroke gStroke:currentSmartPenGesture.getStrokes()) {
		for (int i =0;i< gStroke.points.length/2; i++) {
			counter++;
			averges[0]=averges[0]+gStroke.points[2*i];
			averges[1]=averges[1]+gStroke.points[2*i+1];
		}
	}
	averges[0]=averges[0]/counter;
	averges[1]=averges[1]/counter;
	return averges;	
}

public double[][] layerout(double[][] a,double[][] b,double[][]c){
	double[][] d=new double[a.length][b[0].length];
	if(a[0].length==b.length) {
		for(int i=0;i<a.length;i++) {
			for(int j=0;j<b[0].length;j++) {
				for(int k=0;k<a[0].length;k++) {
					d[i][j]+=a[i][k]*b[k][j];
				}
		   }
	    }
    }
	if(d.length==c.length&&d[0].length==c[0].length) {
		for(int i=0;i<d.length;i++) {
			for(int j=0;j<d[0].length;j++) {
				d[i][j]=d[i][j]+c[i][j];
			}
		}
	}
	for(int i=0;i<c.length;i++) {
		d[i][0]=1.0/(1+1/Math.exp(d[i][0]));
	}
	return d;
}

public double[][] in_array(String string,int wigth,int height) throws IOException {
	BufferedReader in = new BufferedReader(new FileReader(string));
	double[][] arr = new double [height][wigth];
	String line = "";
	int row=0;
	while((line =in.readLine())!=null) {
		Log.i("di", "tmp"+row);
		String[] tmp = line.split(" ");
//   		Log.i("di", "tmp"+tmp[25]);
		for(int i=0;i<tmp.length;i++) {
//   			Log.i("di", "tmpi"+i);
			arr[row][i]= Double.valueOf(tmp[i]);
		}
		row++;
		
	}
	in.close();
	return arr;
}

private String matrix(Bitmap bitmap) {
	

	String picName = System.currentTimeMillis()+".jpg";
	File file = new File( "/sdcard/classify/"+picName);
	FileOutputStream out = null;
	try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
			System.out.println("___________保存__sd___下_______________________");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if(out!=null)
			{
			out.flush();
			out.close();
			}
		} catch (IOException e) {
			Log.e("zgm", ":"+e);
			e.printStackTrace();
		}
    double[][] arr=new double[1024][1];
    int r=0;
    for(int i=0;i<32;i++) {
    	for(int j=0;j<32;j++) {
    		Log.i("di", "pixel"+bitmap.getPixel(j, i));
    		if(bitmap.getPixel(j,i)==0) {
    			arr[r][0]=1;
    		}
    		else {
    			arr[r][0]=0;
    		}
//     		Log.i("di", "a"+r);
    		if(arr[r][0]==0) {
   // 		Log.i("di", "a"+r);
    		}
    		r++;
    		      		
    	}
    }
	if (!bitmap.isRecycled()) {
		bitmap.recycle();
    }
    try {
		double[][]w=in_array("/sdcard/classify/w.txt",28,5);
		double[][]b=in_array("/sdcard/classify/b.txt",1,5);
		double[][]w_h=in_array("/sdcard/classify/w_h.txt",1024,28);
		double[][]b_h=in_array("/sdcard/classify/b_h.txt",1,28);
		double[][] hid=layerout(w_h,arr,b_h);
		double[][] pre=layerout(w,hid,b);
		String[] a = {"A","B","C","D","E"};
		double max=0;
		
		for (int i=0;i<pre.length;i++) {
			Log.i("di", "type"+pre[i][0]);
			if(pre[i][0]>max) {
				max=pre[i][0];
			}		
		}
		if (max<0.9) {
			return null;	
		}
		for (int i=0;i<pre.length;i++) {				
			if(pre[i][0]==max) {

				//showInftTextView.setText("字母：" + a[i]);
				return a[i];

			}

		}
    }
		catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		Log.e("zgm", "读取参数矩阵失败:"+e);
		return null;
	}
	return null;
}

/**
 * 判断某个手势是否是单击
 * @param currentSmartPenGesture
 * @return true：是单击；false:不是单击
 */
public boolean isclick(SmartPenGesture currentSmartPenGesture) {
if (currentSmartPenGesture.getStrokesCount()!=1) {
	return false;
}
RectF rectF=currentSmartPenGesture.getGestureBoundBoxRect();
	if (Math.abs(rectF.right-rectF.left)<1&&Math.abs(rectF.bottom-rectF.top)<1) {
		return true;
	}
	
	return false;
	
	
}

//wsk 2019.1.26
//读题目和三维语义
/*public void ReadTiMu(int pageID)
{
	ArrayList<Integer> tag = new ArrayList<Integer>();
	
	tag = ReadQuestion.ReceiveQuestionDots(point_number1, point_number2, bihua, pageID);
	
	if(tag == null)
	{
		return;
	}
	
	else
	{
		//读题
		if(tag.get(1) == 0)
		{
			switch(tag.get(0))
			{
			case 1:showSound(R.raw.onetigan);
			break;
			case 2:showSound(R.raw.twotigan);
			break;
			case 3:showSound(R.raw.threetigan);
			break;
			case 4:showSound(R.raw.fourtigan);
			break;
			case 5:showSound(R.raw.fivetigan);
			break;
			case 6:showSound(R.raw.sixtigan);
			break;
			case 7:showSound(R.raw.seventigan);
			break;
			case 8:showSound(R.raw.eigthttigan);
			break;
			case 9:showSound(R.raw.ninetigan);
			break;
			default:break;
			}
		}
		
		else if(tag.get(1) == 1)
		{
			//读要求
			showSound(R.raw.sanweiyuyi);
		}
		
		else return;
	}
}
*/

public List<points> singleSmartPenGestureToPointsList(SmartPenGesture currentSmartPenGesture){
	List<points> points;
	
	
	return null;
	
}
/**
 * 
 * @param currentSmartPenGesture 一笔手势，多笔不做处理
 * @return: tag[2],tag=null:不在任何区域 ;tag[0]:题号；tag[1]:题号对应题目的某个区域
 */
public ArrayList<Integer> getChirographyPositionInfo(SmartPenGesture currentSmartPenGesture,int mPageID ) {
/*	if (currentSmartPenGesture.getStrokesCount()!=1) {//不是一笔，直接返回null
		return null;
	}*/
	float[] averages=getGestureCenture(currentSmartPenGesture);//averages[0]:x平均值;averages[1]:x平均值
	//得到区域-题干区
//	averages[0]=(float) (averages[0]/138.14*1519)+20;
//	averages[1]=(float) (averages[1]/194.296*2151)+100;
	
	averages[0]=(float) (averages[0]);
	averages[1]=(float) (averages[1]);
	
	ArrayList<Integer> tag=testxml.test( averages[0], averages[1],activity.gCurBookID,activity.gCurPageID);
	
	//wsk 2019.4.25
	//将返回来的tag转换为题号
	ArrayList<Integer> temp = new ArrayList<Integer>();
	if(tag == null)
	{
		return null;
	}
	
	else
	{
		if(tag.get(0) >1)
		{
			temp.add(HomeworkContnet(tag.get(0), activity.gCurBookID,activity.gCurPageID));
			temp.add(tag.get(1));
			temp.add(tag.get(2));
		}
		else
		{
			return tag;
		}
	}
	
	return temp;
}

static Document doc;
static String pagexml;
public int HomeworkContnet(int index,int BookID,int PageID)
{
	pagexml = "book_"+BookID+"_page_"+(PageID%20)+".xml";
	
	File file = new File("/sdcard/xml/" +pagexml);
	try 
	{
		doc = Jsoup.parse(file, "UTF-8");
	
	 }
	catch (IOException e) 
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	Elements element = doc.getElementsByTag("itemnumber");
	float tihao;
	
	if(index == 0 || index == 1)
	{
		return -1;
	}

	else 
	{
		tihao = Float.valueOf(element.get(index).text().toString());
	}
	
	return (int)tihao;
}

/**
 * 
* @Title: getsignallength 
* @Description: TODO(这里用一句话描述这个方法的作用)
* @param startIndex:开始的索引值
* @param temContainer存放码点坐标的容器
*  
*@return:float[][](返回类型) [0]码点坐标的长度，[1]纸上实际的长度
* @throws：异常描述
*
* @version: v1.0.0
* @author: lfgm
* @date: 2019年6月15日 上午11:12:25
 */
public float[] getsignallength(int startIndex,ArrayList<Position> temContainer) {
	float[] sum=new float[2];
	sum[0]=sum[1]=0;
//	float x=0,y=0,tmpx=0,tmpy=0,xk=(float) 1.3547,yk=(float) 1.3758;
	float x=0,y=0,tmpx=0,tmpy=0,xk=(float) 1.524,yk=(float) 1.524;
	for (int i = startIndex; i < temContainer.size(); i++) {
  	  if(i==startIndex) {
		  x=temContainer.get(i).x;
    	  y=temContainer.get(i).y;
    	  tmpx=x;
    	  tmpy=y;
    	  sum[0]=0;
    	  sum[1]=0;
	}else {
		  x=temContainer.get(i).x;
    	  y=temContainer.get(i).y;
       	  sum[0]+=Math.sqrt((x-tmpx)*(x-tmpx)+(y-tmpy)*(y-tmpy));
    	  sum[1]+=Math.sqrt((x-tmpx)*xk*(x-tmpx)*xk+(y-tmpy)*yk*(y-tmpy)*yk);
  	  tmpx=x;
  	  tmpy=y;
	  } 	
}
	return sum;
}	

public int changeTimes(int startIndex,ArrayList<Position> temContainer) {
	int changeTimes=0;
	float x0=0,y0=0,x1=0,y1=0;
	int lastStateX=-1;
	int currentStateX=-1;
	int lastStateY=-1;
	int currentStateY=-1;
	for (int i =startIndex; i < temContainer.size()-1; i++) {
		x0=temContainer.get(i).x;
		y0=temContainer.get(i).y;
		x1=temContainer.get(i+1).x;
		y1=temContainer.get(i+1).y;		
if (x1-x0>1) {//x增加
	currentStateX=0;
}
if (x1-x0<-1) {//x减少
	currentStateX=1;
}
if (x1-x0>-1&&x1-x0<1) {//x持平
	currentStateX=2;
}
if (y1-y0>1) {//x增加
	currentStateY=0;
}
if (y1-y0<-1) {//x减少
	currentStateY=1;
}
if (y1-y0>-1&&y1-y0<1) {//x持平
	currentStateY=2;
}
if(currentStateX!=lastStateX||currentStateY!=lastStateY) {
	changeTimes++;
	if (currentStateX!=lastStateX) {
		lastStateX=currentStateX;
	}
	if (currentStateY!=lastStateY) {
		lastStateY=currentStateY;
	}
}
	}	
	return changeTimes;	
}

public float[] getKandB(ArrayList<Position> posList){
	//List<Position>posList = new ArrayList();
	float[] result = new float[2];
	float k;
	float b;
	float x_y = 0;
	float x = 0;
	float y = 0;
	float x2 = 0;
	float x_mean;
	float y_mean;
	for(Position pos:posList){
		x_y+=pos.x*pos.y;
		x+=pos.x; 
		y+=pos.y;
		x2+=pos.x*pos.x;
		
	}
	x_mean=x/posList.size();
	y_mean=y/posList.size();
//	b=y_mean-x_mean;
	if(x2-x_mean*x_mean*posList.size()!=0){
	k=(x_y-x_mean*y_mean*posList.size())/(x2-x_mean*x_mean*posList.size());
	b=y_mean-x_mean*k;
	result[0]=k;//斜率
	result[1]=b;//截距
	}
	else{
		result[0]=Float.MAX_VALUE;//斜率
	    result[1]=0;//截距
	}
	return result;
	}

}
