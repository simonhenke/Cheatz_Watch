
package activities;


import com.example.mywatch.R;

import sensors.AudioLevelDispatcher;
import sensors.AudioLevelListener;
import sensors.OnSwipeTouchListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SoundLevelCalibration extends Activity implements AudioLevelListener, OnSeekBarChangeListener {

	//Widgets
	private TextView tView,loudView;
	private ProgressBar progressBar;
	private SeekBar seekBar;
	
	private AudioLevelDispatcher audioLevelRecorder;
	private Handler handler = new Handler();
	private int maxProgress;
	private SharedPreferences.Editor editor;
	private OnSwipeTouchListener swipeListener;
	private RelativeLayout myLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sound_level_calibration);

		//initialize shared prefs
    	SharedPreferences sharedPref = this.getSharedPreferences("CheatzCalibration",Context.MODE_PRIVATE);
    	maxProgress = sharedPref.getInt("blowReactionValue", 100);
    	editor = sharedPref.edit();
    	
    	// init Layout
    	
		myLayout = (RelativeLayout)findViewById(R.id.layoutMic);
		myLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeRight() {
				finish();
			}
		});
    	
		
		//initialize variables
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		tView = (TextView) findViewById(R.id.soundtext); 
		loudView = (TextView) findViewById(R.id.loudtext);
		
		// Initializing Audio Stuff
		audioLevelRecorder = new AudioLevelDispatcher(this);
    	audioLevelRecorder.setListener(this);
    	audioLevelRecorder.startRecording();  
    	
    	seekBar.setOnSeekBarChangeListener(this);
    	
    	//set initial progress
    	seekBar.setProgress(maxProgress);
    	seekBar.setMax(200);
    	progressBar.setMax(maxProgress);
    	setMinimumReactionValue(maxProgress);
    	
    	
	}

	private void setMinimumReactionValue(int minLevel) {
		audioLevelRecorder.setMinimumReactionValue(minLevel);
	}


	@Override
	public void onLevelChanged(final int value) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				int val = value;
				if (val > maxProgress){
					val = maxProgress;
					loudView.setText("Event Triggered!");
				} else {
					loudView.setText("");
				}
				progressBar.setProgress(val);
				tView.setText(val + "/" + maxProgress);
			}
		});
		
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		maxProgress = progress;
		progressBar.setMax(maxProgress);
		setMinimumReactionValue(maxProgress);
	}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		editor.putInt("blowReactionValue", maxProgress);
		editor.commit();
	}


	@Override
	public void onVeryLoudSoundDetected() {}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	
}
