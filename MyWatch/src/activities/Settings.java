package activities;

import sensors.OnSwipeTouchListener;

import com.example.mywatch.R;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Settings extends Activity {

	private OnSwipeTouchListener swipeListener;
	private LinearLayout myLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		myLayout = (LinearLayout)findViewById(R.id.layoutSettings);

		myLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeRight() {
				finish();
			}
		});
		/*
		for(int i=0; i<myLayout.getChildCount();i++)
		{
			myLayout.getChildAt(i).setOnTouchListener(new OnSwipeTouchListener(this) {
				public void onSwipeRight() {
					finish();
				}	
			});
		}*/
	}

	public void showInteractionsClicked(View view) {
	    Intent intent = new Intent(Settings.this, Explanation.class);
	    Settings.this.startActivity(intent);
	}
	
	public void calibrateMicClicked(View view) {
	    Intent intent = new Intent(Settings.this, SoundLevelCalibration.class);
	    Settings.this.startActivity(intent);
	}
	
	public void calibrateCamClicked(View view) {
	    Intent intent = new Intent(Settings.this, CameraCalibration.class);
	    Settings.this.startActivity(intent);
	}	
}
