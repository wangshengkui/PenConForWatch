package com.example.pencon;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;

import com.example.readAndSave.SmartPenPage;

import android.R.string;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.AsyncTask;

public class DrawFromFileTask extends AsyncTask<Void, Integer, Long> {
	private ProgressDialog mDialog;
	private File mFile;
	private DrawView drawView;
	private Context mContext;
	private String fileName;
	private SmartPenPage smartPenPage=null;
	MainActivity activity;
	/**
	 * 
	 * @param filename 从该文件中读取数据(笔迹点序列)
	 * @param drawView 要绘图的画布
	 */
	public DrawFromFileTask(String fileName,DrawView drawView,MainActivity activity ) {
		// TODO Auto-generated constructor stub
		super();
		this.drawView=drawView;
		this.activity=activity;
//		this.mFile=new File("/sdcard/xyz/"+filenames);
		this.fileName=fileName;
/*		if(context!=null){
			mDialog = new ProgressDialog(context);
			mContext = context;
		}
		else{
			mDialog = null;
		}*/
		
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		activity.doSomeworkIsOK=false;
/*		if(mDialog!=null){
			mDialog.setTitle("Downloading...");
			mDialog.setMessage(mFile.getName());
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					cancel(true);
				}
			});
			mDialog.show();
		}*/
	}
	@Override
	protected Long doInBackground(Void... params) {
		// TODO Auto-generated method stub
		if (fileName==null) {
			return null;
		}
		android.util.Log.e("zgm", "0417：fileName："+fileName);
		if (fileName.contains("001")) {
			drawView.paint.setColor(Color.RED);
			
//			smartPenPage=activity.getfromFile("/sdcard/xyz/"+filename,filename);
//			activity.drawsmartpenpoints(smartPenPage);
		}else {
			drawView.paint.setColor(Color.BLACK);
		}
		smartPenPage=activity.getfromFile("/sdcard/xyz/"+fileName,fileName);
	
		activity.drawsmartpenpoints(smartPenPage);
//		drawView.paint.setColor(Color.BLACK);

		return null;

	}
	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		//super.onProgressUpdate(values);
		if(mDialog==null)
			return;
		if(values.length>1){
			int contentLength = values[1];
			if(contentLength==-1){
				mDialog.setIndeterminate(true);
			}
			else{
				mDialog.setMax(contentLength);
			}
		}
		else{
			mDialog.setProgress(values[0].intValue());
		}
	}
	@Override
	protected void onPostExecute(Long result) {
		// TODO Auto-generated method stub
		//super.onPostExecute(result);
		drawView.paint.setColor(Color.BLACK);
		activity.doSomeworkIsOK=true;
		if(mDialog!=null&&mDialog.isShowing()){
			mDialog.dismiss();
		}
		if(isCancelled())
			return;
//		((MainActivity)mContext).showUnzipDialog();
	}
}
