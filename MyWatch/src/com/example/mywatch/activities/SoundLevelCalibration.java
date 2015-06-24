package com.example.mywatch.activities;

import com.example.mywatch.R;
import com.example.mywatch.R.id;
import com.example.mywatch.R.layout;
import com.example.mywatch.R.menu;
import com.example.mywatch.sensors.AudioLevelDispatcher;
import com.example.mywatch.sensors.AudioLevelListener;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SoundLevelCalibration extends Activity implements AudioLevelListener {

	private AudioLevelDispatcher audioLevelRecorder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_level_calibration);
		
		// Initializing Audio Stuff
		audioLevelRecorder = new AudioLevelDispatcher(this);
    	audioLevelRecorder.setListener(this);
    	audioLevelRecorder.startRecording();  	
	}

	@Override
	public void onLevelChanged(double value) {
		// Luka Todo: 
		// Pegelveränderung Darstellen (z.B in Seekbar) und Einstellungsmöglichkeit für 
		// den mindestens benötigten Pegel für eine Interaktion
		
	}
	
	public void onPause() {
		super.onPause();
		audioLevelRecorder.stopRecording();
	}
	
	public void onResume() {
		super.onResume();
    	audioLevelRecorder.startRecording(); 
	} 
	
	@Override
	public void onVeryLoudSoundDetected() {
		// TODO Auto-generated method stub	
	}

	
}
