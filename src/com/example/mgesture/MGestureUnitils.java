package com.example.mgesture;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.R.string;
import android.content.Context;
import android.gesture.GestureUtils;
import android.util.ArrayMap;
import android.util.Log;
public class MGestureUnitils {
	private ArrayList<MGesture> mGesturesContainer=null;
	String filePathString;
	public MGestureUnitils(String filePathString){
		this.filePathString=filePathString;
	}
	public void addMGesture(MGesture mGesture,String mGestureName) throws FileNotFoundException, ClassNotFoundException, IOException{
		if (mGesturesContainer==null) {
			load();
		}
		if (mGesturesContainer==null) {
			mGesturesContainer=new ArrayList<MGesture>();
		}
		mGesture.setGestureName(mGestureName);
		mGesturesContainer.add(mGesture);
	}
	
	
/**
 * 	
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-2 下午6:09:34
 * @param mGesture
 * @return
 */
public MGesture recogniseGeMGesture(MGesture mGesture ){
	if (mGesturesContainer==null||mGesturesContainer.size()==0||mGesture==null) {
		return null;
	}
for (MGesture mGetureInContainer :mGesturesContainer) {
	if (mGestureIsEqual(mGetureInContainer,mGesture)) {
		return mGetureInContainer;
	}
}
	return null;
}
/**
 * 	
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-2 下午6:09:27
 * @param mGesture
 * @param gestureName
 */
public void saveGesture(MGesture mGesture,String gestureName){
	mGesture.setGestureName(gestureName);
	//调用getGesturesFromFile(),将mGesture添加进去
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-2 下午10:50:37
 * @param filePathString
 * @return
 */
public boolean save() {
    if (mGesturesContainer==null||mGesturesContainer.size()==0) {
    	return false;
	}
    final File file =new File(filePathString);

    final File parentFile = file.getParentFile();
    if (!parentFile.exists()) {
        if (!parentFile.mkdirs()) {
            return false;
        }
    }

    boolean result = false;
    try {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();//        
//        吧唧吧唧
        write(file,mGesturesContainer);//暂时形态
        result = true;
    } catch (FileNotFoundException e) {
        Log.d("GestureUnitils.save(String filePathString)", "Could not save the gesture library in " +filePathString, e);
    } catch (IOException e) {
        Log.d("GestureUnitils.save(String filePathString)", "Could not save the gesture library in " + filePathString, e);
    }

    return result;
}

public  ArrayList<MGesture> load() throws FileNotFoundException, IOException, ClassNotFoundException{

	final File file =new File(filePathString);
    if (file.exists() && file.canRead()) {
        read( file);
    }
    else {
    	
		Log.e("zgm", "文件不存在或者不可读");
		return null;
	}
    return mGesturesContainer;
}
	



public  void write(File file,ArrayList<MGesture> mGesturesContainer) throws IOException {
	FileOutputStream out = new FileOutputStream(file);
	ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
	try {
		for (MGesture mGesture : mGesturesContainer) {
			objectOutputStream.writeObject(mGesture);
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		objectOutputStream.flush();
		out.flush();
		objectOutputStream.close();
		out.close();

	}
}

public ArrayList<MGesture> read(File file) throws StreamCorruptedException, IOException, ClassNotFoundException{
	InputStream in = new FileInputStream(file);
	
	ObjectInputStream objectInputStream = new ObjectInputStream(in);
	try
			{
		MGesture   mGesture= (MGesture) objectInputStream.readObject();
		if (mGesture!=null) {
			if (mGesturesContainer==null) {
				mGesturesContainer=new ArrayList<MGesture>();
			}
			mGesturesContainer.clear();
			mGesturesContainer.add(mGesture);
		}
		while (true) {
			mGesture= (MGesture) objectInputStream.readObject();
			mGesturesContainer.add(mGesture);
		}
	} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}finally{
				objectInputStream.close();
				in.close();
			}
	return mGesturesContainer;
}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-2 下午6:09:49
 * @return
 */
public ArrayList<MGesture> getGesturesFromFile(){
	
	
	
	
	return null;
	
	
}	

/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-2 下午5:25:32
 * @param mGesture1
 * @param mGesture2
 * @return
 */
private boolean mGestureIsEqual(MGesture mGesture1,MGesture mGesture2){
if (mGesture1.getStrokeCount()!=mGesture2.getStrokeCount()) {//手势的笔划数不相等
	return false;
}
for (int i = 0; i < mGesture1.getStrokeCount(); i++) {
	byte[] mGesure1StrokeTrend=mGesture1.getMGseture().get(i).getStrokeTrend();//mGesture1
	Log.e("zgm", "190106:mGesture1.name="+mGesture1.getGestureName()+"   index:"+mGesture1.getMGseture().get(i).index);
	byte[] mGesure2StrokeTrend=mGesture2.getMGseture().get(i).getStrokeTrend();//mGesture2
	for (int j = 0; j < mGesure2StrokeTrend.length; j++) {
		Log.e("zgm", "190106:mGesture2.trend："+mGesure2StrokeTrend[j]);
	}
	Log.e("zgm", "190106:mGesture2.name="+mGesture2.getGestureName()+"   index:"+mGesture2.getMGseture().get(i).index);
	if (mGesure1StrokeTrend.length!=mGesure2StrokeTrend.length) {//手势的某笔划的变化趋势变化次数不相等
		return false;
	}
	for (int j = 0; j < mGesure2StrokeTrend.length; j++) {
		if (mGesure1StrokeTrend[j]!=mGesure2StrokeTrend[j]) {//手势的某笔划的具体的莫一个变化趋势不相等
			return false;
		}
	}
}
return true;	
}
}
