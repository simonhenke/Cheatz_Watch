package activities;

import sensors.OnSwipeTouchListener;

import com.example.mywatch.R;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import androidElements.InfinitePagerAdapter;
import androidElements.MainPagerAdapter;

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
