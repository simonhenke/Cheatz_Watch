package com.example.mywatch.activities;

import com.example.mywatch.R;
import com.example.mywatch.R.layout;
import com.example.mywatch.androidElements.InfinitePagerAdapter;
import com.example.mywatch.androidElements.MainPagerAdapter;
import com.example.mywatch.sensors.OnSwipeTouchListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class Explanation extends Activity{

	private OnSwipeTouchListener swipeListener;
	RelativeLayout myLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_explanation);	
		myLayout = (RelativeLayout)findViewById(R.id.explainLayout);
		
		myLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
		    public void onSwipeRight() {
		    	finish();
		    }
		});
		
		myLayout.setBackgroundResource(R.drawable.explaincheatz);
		
	
	}
	
	
}
