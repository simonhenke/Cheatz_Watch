package com.example.mywatch;



import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends Activity {

	TextView text;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		String x = Environment.getRootDirectory().toString();
		Log.d("Files2", "PathX: " + x);
		
		File[] files = getFilesDir().listFiles();
		files = Environment.getRootDirectory().listFiles();
		if(files != null){
			Log.d("STATUS",""+files.length);
			for(File f: files)
			{
				//Log.e("FileName", f.getAbsolutePath());
			}
		}else{
			Log.d("STATUS","failed: its null");
		}
		
		
		// ----------- 
		
		
		
	}
	
	
	
	
	
	
	public void openCreateFile(View view) {
	    Intent intent = new Intent(MainActivity.this, TestActivity.class);
	    MainActivity.this.startActivity(intent);
	}
	
	public void openChooseFile(View view) {
	    Intent intent = new Intent(MainActivity.this, ChooseFile.class);
	    MainActivity.this.startActivity(intent);
	}
	
	
	
	
}
