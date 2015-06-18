package com.example.mywatch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class FontChooser extends Activity {
	
	String content;
	int delayTime;
	int fontSize;
	int fontMinimum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.font_chooser);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    content = extras.getString("content");
		    delayTime = extras.getInt("delay");
		}		
		
		
		SeekBar seek;
		final TextView fontExample;
		
		seek = (SeekBar)findViewById(R.id.seekBar1);
		fontExample = (TextView)findViewById(R.id.fontSize);
		
		fontMinimum = 8;
		seek.setMax(40-fontMinimum);
		
		seek.setProgress(seek.getMax()/2);
		fontExample.setText(""+(seek.getProgress()+fontMinimum));
		fontExample.setTextSize(TypedValue.COMPLEX_UNIT_SP, (seek.getProgress()+fontMinimum));
		fontSize = (seek.getProgress()+fontMinimum);
		
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {           
		    }       
		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {         
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 	    	 
		    	progress = progress+fontMinimum;
		    	
		    	fontExample.setText(""+progress);	
		    	fontExample.setTextSize(TypedValue.COMPLEX_UNIT_SP,progress);
		        fontSize = progress;		       
		    }       
		});             
	}
	
	public void nextActivity(View view)
	{
		Intent intent = new Intent(FontChooser.this, CheatAnimation.class);
		intent.putExtra("content", content);
		intent.putExtra("delay",delayTime);
		intent.putExtra("fontSize",fontSize);
		FontChooser.this.startActivity(intent);   
		
		
	}
}
