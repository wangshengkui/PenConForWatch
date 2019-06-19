package com.example.pencon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import com.example.pencon.RecordingService.MyBinder;

/**
 * 开始录音的 DialogFragment
 *
 * Created by developerHaoz on 2017/8/12.
 */

public class RecordAudioDialogFragment extends DialogFragment {

    private static final String TAG = "RecordAudioDialogFragme";

    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;
   
    long timeWhenPaused = 0;
    public RecordingService mRecordingService=new RecordingService();
    public Intent intent; 
    public MyBinder recordService;
    private Button mFabRecord;
    private Chronometer mChronometerTime;
    private ImageView mIvClose;

    private OnAudioCancelListener mListener;
	private final ServiceConnection recordConnection=new ServiceConnection() {
		
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

    public static RecordAudioDialogFragment newInstance() {
        RecordAudioDialogFragment dialogFragment = new RecordAudioDialogFragment();
        Bundle bundle = new Bundle();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(getActivity(), RecordingService.class);
//      getActivity().bindService(intent,recordConnection,Service.BIND_AUTO_CREATE);
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       
    }

    @org.eclipse.jdt.annotation.NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_record_audio, null);
        initView(view);

        mFabRecord.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mFabRecord.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        mFabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity()
//                            , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1);
//                }else {
                    onRecord(mStartRecording);
                    mStartRecording = !mStartRecording;
//                }

            }
        });

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancel();
            }
        });

        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    private void initView(View view) {
        mChronometerTime = (Chronometer) view.findViewById(R.id.record_audio_chronometer_time);
        mFabRecord = (Button) view.findViewById(R.id.record_audio_fab_record);
        mIvClose = (ImageView) view.findViewById(R.id.record_audio_iv_close);
    }

    private void onRecord(boolean start) {
        if (start) {
            // start recording
            mFabRecord.setBackgroundResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "开始录音...", Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }

            //start Chronometer
            mChronometerTime.setBase(SystemClock.elapsedRealtime());
            mChronometerTime.start();
            //start RecordingService
            getActivity().startService(intent);
            if (getActivity().bindService(intent,recordConnection,Service.BIND_AUTO_CREATE)) {
				Log.e("zgm", "1217:绑定成功！");
			}else {
				Log.e("zgm", "1217:绑定失败！ "+getActivity().getClass().getName());
			}
//            
            
            //keep screen on while recording
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        } else {
            //stop recording
            mFabRecord.setBackgroundResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometerTime.stop();
            timeWhenPaused = 0;
            Toast.makeText(getActivity(), "录音结束...", Toast.LENGTH_SHORT).show();
            getActivity().unbindService(recordConnection);
            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void setOnCancelListener(OnAudioCancelListener listener) {

        this.mListener = listener;
    }



    public interface OnAudioCancelListener {
        void onCancel();
    }

	public String  getRecordFilePath(MyBinder recordService){
		String recordFilePath="";
		RecordingService	recordingService=recordService.getService();
		recordFilePath=recordingService.getMFilePath();
		return recordFilePath;
		
	}
	public long  getRecordFileElpased(MyBinder recordService){
		long recordFileElpased=0;
		RecordingService recordingService=recordService.getService();
		recordFileElpased=recordingService.getMFileElpased();
		return recordFileElpased;
		
	}
}
