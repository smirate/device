package com.evixar;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import net.enswer.ear.EARRecognizer;
import net.enswer.ear.EARErrorCode;

public class EARRecorder implements Runnable {

    private static final int RECORDER_BUFFER_SIZE_UNIT = 3200;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT; // 16bit
    private Thread mThread; 
    private AudioRecord mRecorder;
    private int mBufferSize = 0;
    private EARRecognizer mRecognizer;
    private boolean mUseHighPriority = false;
    
    @SuppressLint("NewApi")
    public boolean start(EARRecognizer recognizer, boolean useHighPriority) {
        stop();
        
        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 2;
        
        if (bufferSize == AudioRecord.ERROR_BAD_VALUE || bufferSize == AudioRecord.ERROR) {
            return false;
        }
        
        mBufferSize = RECORDER_BUFFER_SIZE_UNIT;
        while (mBufferSize < bufferSize) {
            mBufferSize += RECORDER_BUFFER_SIZE_UNIT;
        }
        
        //SONY Z1�΍�
        if(Build.MODEL.equalsIgnoreCase("SO-01F") ||
        		Build.MODEL.equalsIgnoreCase("SO-02F") ||
        		Build.MODEL.equalsIgnoreCase("SOL22") ||
        		Build.MODEL.equalsIgnoreCase("SOL23") ||
        		Build.MODEL.equalsIgnoreCase("SOL24") ||
        		Build.MODEL.equalsIgnoreCase("SO-01F") )
        {
        	mRecorder = new AudioRecord(
                    MediaRecorder.AudioSource.CAMCORDER,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, mBufferSize);
        }
        else
        {
        	mRecorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, mBufferSize);
        }

        if (mRecorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
            stop();
            return false;
        }

        mRecorder.startRecording();
        
        mRecognizer = recognizer;
        mThread = new Thread(this);
        mThread.start();
        
        mUseHighPriority = useHighPriority;
        
        return true;
    }
    
    public void stop() {
        if (mThread != null) {
            mThread.interrupt();
            try {
                mThread.join();
            } catch (InterruptedException e) {}
            mThread = null;
        }
        
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        mBufferSize = 0;
        mRecognizer = null;
        mUseHighPriority = false;
    }

    @Override
    public void run() {
        if (mUseHighPriority) {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        }
        
        byte[] audioBuffer = new byte[mBufferSize];
        int read;
        EARErrorCode result;
        
        while (true) {
            if (mThread.isInterrupted()) break;
            
            if (mRecorder == null) break;
            
            read = mRecorder.read(audioBuffer, 0, mBufferSize);
            
            if (mThread.isInterrupted()) break;
            
            result = mRecognizer.appendAudioBuffer(audioBuffer, read);
            
            if (result != EARErrorCode.EAR_SUCCESS) break;

            if (mThread.isInterrupted()) break;
        }
    }
     
}
