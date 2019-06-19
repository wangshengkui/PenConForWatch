package com.example.readAndSave;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.util.Log;

import com.example.mgesture.MGesture;
import com.example.pencon.MainActivity;

public class SmartPenUnitils {
	
	
	public static SmartPenPage load(String filePathString) throws FileNotFoundException, IOException, ClassNotFoundException{

		final File file =new File(filePathString);
		SmartPenPage smartPenPage=null;
	    if (file.exists() && file.canRead()) {
	     smartPenPage=read( file);
	    }
	    else {
			System.out.println("文件不存在或者不可读");
		}
	    return smartPenPage;
	}
	private static SmartPenPage read(File file) throws StreamCorruptedException, IOException, ClassNotFoundException{
		InputStream in = new FileInputStream(file);
		
		ObjectInputStream objectInputStream = new ObjectInputStream(in);
		SmartPenPage   smartPenPage=null;
		try
				{
		   smartPenPage= (SmartPenPage) objectInputStream.readObject();
           

		} catch (EOFException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}finally{
					objectInputStream.close();
					in.close();
				}
		return smartPenPage;
	}
	
	
	public static boolean save(SmartPenPage smartPenPage  ) {
		if (smartPenPage==null) {
			return false;
		}
	    final  File file =new File("sdcard/-1/"+"NONE-"+MainActivity.studentNumber+"-"+smartPenPage.bookeId+"-"+smartPenPage.pageNumber+"-0.page");

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
	        write(file,smartPenPage);//暂时形态
	        result = true;
	    } catch (FileNotFoundException e) {
	        Log.d("GestureUnitils.save(String filePathString)", "Could not save the gesture library in " +file.toString(), e);
	    } catch (IOException e) {
	        Log.d("GestureUnitils.save(String filePathString)", "Could not save the gesture library in " +file.toString(), e);
	    }

	    return result;
	}
	private static  void write(File file,SmartPenPage smartPenPage) throws IOException {
		FileOutputStream out = null;
		ObjectOutputStream objectOutputStream = null;
		boolean isExist = file.exists();
		try {	
			if(isExist)out = new FileOutputStream(file,true);
			else out = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(out);
				objectOutputStream.writeObject(smartPenPage);
		} catch (Exception e) {
			Log.e("zgm", "0125:"+e);
			e.printStackTrace();
		} finally {
			objectOutputStream.flush();
			out.flush();
			objectOutputStream.close();
			out.close();

		}
	}
}
