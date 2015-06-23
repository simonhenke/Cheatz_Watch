package sensors;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class AudioLevelDispatcher {
	
	private AudioLevelListener listener;
	private Thread recorderThread;
	private AudioRecord audio;
	private int sampleRate;
	private int minimumReactionValue;
	private int bufferSize;
	private int lastLevel;
	private boolean threadStopped;	
	private boolean releaseTimeActive;
	
	public AudioLevelDispatcher(Context context){		
		SharedPreferences sharedPref = context.getSharedPreferences("CheatzCalibration", Context.MODE_PRIVATE);
		minimumReactionValue  = sharedPref.getInt("blowReactionValue", 100);
		sampleRate = 8000;
	}
	
	public void startRecording()
	{	
		try {
			if(audio != null){
				audio.release();
				audio = null;
			}			
			threadStopped = false;			
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
		threadStopped = true;	
	}
	
	public void setListener(AudioLevelListener listener)
	{
		this.listener = listener;
	}
	
	public void setMinimumReactionValue(int minLevel)
	{
		minimumReactionValue = minLevel;
	}
	
	
	private void readAudioBuffer() {
		 
	    try {
	        short[] buffer = new short[bufferSize];
	 
	        int bufferReadResult = 1;
	 
	        if (audio != null) {
	 
	            // Sense the voice...
	            bufferReadResult = audio.read(buffer, 0, bufferSize);
	            int sumLevel = 0;
	            for (int i = 0; i < bufferReadResult; i++) {
	                sumLevel += buffer[i];
	            }
	            lastLevel = (int) Math.abs((sumLevel / bufferReadResult));
	            
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
			        listener.onLevelChanged(lastLevel);
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
					 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
					 releaseTimeActive = false;
				 }
			 }).start();
		 }		 
	};
	
}
