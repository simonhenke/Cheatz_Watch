package com.example.mywatch;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioLevelDispatcher {
	
	private AudioLevelListener listener;
	MediaRecorder recorder;
	Thread recorderThread;
	AudioRecord audio;
	int sampleRate = 8000;
	int minimumReactionValue = 25;
	int checkTime = 100;
	int bufferSize;
	double lastLevel;
	boolean threadStopped;	
	boolean releaseTimeActive;
	
	public AudioLevelDispatcher(Context context){		
			
	}
	public void startRecording()
	{	
		try {
			if(audio != null){
				audio.release();
				audio = null;
			}			
			threadStopped = false;				
			recorder = new MediaRecorder();
		    bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
		            AudioFormat.ENCODING_PCM_16BIT);    
		    audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
		                AudioFormat.CHANNEL_IN_MONO,
		                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
		} catch (Exception e) {
		    android.util.Log.e("TrackingFlow", "Exception", e);
		}
		if(audio != null && audio.getState() == AudioRecord.STATE_INITIALIZED){
			audio.startRecording();
			recorderThread = new Thread(audioLevelDetectLoop);
			recorderThread.start();
		}	
	}
	
	public void stopRecording()
	{
		if(audio != null){
			audio.stop();
			audio.release();
		}		
		recorderThread.interrupt();
		Log.e("ONPAUSE","----");
		threadStopped = true;	
	}
	
	public void setListener(AudioLevelListener listener)
	{
		this.listener = listener;
	}
	
	private void readAudioBuffer() {
		 
	    try {
	        short[] buffer = new short[bufferSize];
	 
	        int bufferReadResult = 1;
	 
	        if (audio != null) {
	 
	            // Sense the voice...
	            bufferReadResult = audio.read(buffer, 0, bufferSize);
	            double sumLevel = 0;
	            for (int i = 0; i < bufferReadResult; i++) {
	                sumLevel += buffer[i];
	            }
	            lastLevel = Math.abs((sumLevel / bufferReadResult));
	        }
	 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	private Runnable audioLevelDetectLoop = new Runnable() {
		
		 public synchronized void run() {
		        while(recorderThread != null && !threadStopped){
			        //Let's make the thread sleep for a the approximate sampling time
			        try{Thread.sleep(50);}catch(InterruptedException ie){ie.printStackTrace();}
			        readAudioBuffer();//After this call we can get the last value assigned to the lastLevel variable		       				           
			        if(lastLevel > minimumReactionValue && !releaseTimeActive){
			        	activateReleaseTime();
			        	listener.onVeryLoudSoundDetected();		        	
			        }
			        	
		        }
		    }
		 public void activateReleaseTime()
		 {
			
			 new Thread(new Runnable() {
				 public void run() {
					 releaseTimeActive = true;
					 Log.e("test","test1");
					 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					 releaseTimeActive = false;
					 Log.e("test","test2");
				 }
			 }).start();
		 }
		 
	};
	
}
