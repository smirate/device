package com.evixar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import net.enswer.ear.EARAccessInfo;
import net.enswer.ear.EARConfig;
import net.enswer.ear.EARErrorCode;
import net.enswer.ear.EARQueryMode;
import net.enswer.ear.EARRecognizer;
import net.enswer.ear.EARServerClientAccessInfo;

public class EARSDK {
	
	private boolean isRunning;
	
	private EARRecorder mRecorder;
	private EARRecognizer mRecognizer;
	
	private EARHandler mHandler = new EARHandler();
	private Handler resultHandler;
	private Handler errHandler;
	
	public EARSDK(String liveappkey, String liveaccesskey, Context context, Handler resulthandler, Handler errhandler) {
		
		resultHandler = resulthandler;
		errHandler = errhandler;
		
		isRunning = false;
		
		mRecognizer = new EARRecognizer();
		mRecorder = new EARRecorder();
		
		int validCount = 0;
		if(liveappkey!=null && liveaccesskey!=null) validCount++;
		
		EARAccessInfo[] accessInfos = new EARAccessInfo[validCount];
		validCount = 0;
		if(liveappkey!=null && liveaccesskey!=null){
			accessInfos[validCount] = (EARServerClientAccessInfo)new EARServerClientAccessInfo(liveappkey, liveaccesskey);
			validCount++;
		}
		
		EARConfig config = new EARConfig();
		config.queryMode = EARQueryMode.EAR_MODE_CONTINUOUS;
		config.queryInterval = 2;
		
		EARErrorCode result = mRecognizer.initialize(accessInfos, config, context);
		if(result != EARErrorCode.EAR_SUCCESS){
			
			Message errmsg = new Message();
			errmsg.obj = result;
			errHandler.sendMessage(errmsg);
			
		}else{
			
			mRecognizer.clearAudioBuffer();
			
		}
	}
	
	public void release() {
		
		mRecorder.stop();
		if(isRunning) stopRecognizing();
		
		mRecognizer.release();
	}
	
	public void startRecognizing() {
		if(!isRunning){
			isRunning = true;
			
			mRecognizer.clearAudioBuffer();
			
			mRecorder.start(mRecognizer, true);
			
			EARErrorCode code = mRecognizer.start(mHandler);
			if(code != EARErrorCode.EAR_SUCCESS){
				
				Message errmsg = new Message();
				errmsg.obj = code;
				errHandler.sendMessage(errmsg);
				
				mRecorder.stop();
				isRunning = false;
			}
		}
	}
	
	public void stopRecognizing() {
		if(isRunning){
			
			mRecorder.stop();
			
			EARErrorCode code = mRecognizer.stop();
			
			isRunning = false;
			
			if(code != EARErrorCode.EAR_SUCCESS){
				
				Message errmsg = new Message();
				errmsg.obj = code;
				errHandler.sendMessage(errmsg);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private class EARHandler extends Handler {
		@Override
		public void handleMessage(Message msg){
			Message sendmsg = new Message();
			sendmsg.obj = msg.obj;
			resultHandler.sendMessage(sendmsg);
		}
	}
}
