package activities;

import com.example.mywatch.R;
import com.example.mywatch.R.id;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SpeedChooser extends Activity {
	
	String content;
	int delayTime;
	int delayMinimum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.speed_chooser);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
		    content = extras.getString("content");
		}		
		
		
		SeekBar seek;
		final TextView seekValue;
		
		seek = (SeekBar)findViewById(R.id.seekBar1);
		seekValue = (TextView)findViewById(R.id.seekValue);

		delayMinimum = 800;
		seek.setMax(5000 - delayMinimum);
		seek.setProgress(seek.getMax()/2);
		seekValue.setText(""+(seek.getProgress()+delayMinimum));
		delayTime = (seek.getProgress()+delayMinimum);
		
		seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {       
		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {           
		    }       
		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {         
		    }       

		    @Override       
		    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
		    	// round the value to nearest 100
		    	int actualValue = Math.round(progress/100)*100;
		    	actualValue += delayMinimum;		    	
		        seekValue.setText(""+actualValue);		
		        delayTime = actualValue;
		    }       
		});             
	}
	
	public void nextActivity(View view)
	{
		Intent intent = new Intent(SpeedChooser.this, FontChooser.class);
		intent.putExtra("content", content);
		intent.putExtra("delay",delayTime);
		SpeedChooser.this.startActivity(intent);   
		
		
	}
}
