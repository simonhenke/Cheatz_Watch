package activities;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import phoneAppData.Chapter;
import phoneAppData.Entry;
import phoneAppData.ImageEntry;
import phoneAppData.Sheet;
import phoneAppData.TextEntry;
import phoneAppUtil.IOCheatz;

import sensors.CameraEventDispatcher;
import sensors.CameraEventListener;
import sensors.CameraPreview;
import sensors.OnSwipeTouchListener;
import sensors.PreviewFrameGrabber;
import sensors.WristFlickDispatcher;
import sensors.WristFlickListener;

import com.example.mywatch.R;
import sensors.AudioLevelDispatcher;
import sensors.AudioLevelListener;

import data.EntryView;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidElements.FixedSpeedScroller;
import androidElements.InfinitePagerAdapter;

public class CheatAnimation extends Activity implements WristFlickListener,AudioLevelListener, CameraEventListener {

	private String path;
	private boolean runLoop = false;
	private boolean reverse = false;
	private boolean justSwitched = true;

	private LinkedList<LinkedList<EntryView>> chapters;

	private int currentChapterIndex = 0;
	private int[] chapterAnimSteps;
	// TODO: chapterAnimSteps bei Page-Wechsel anpassen

	//LinkedList<EntryView> entryViews;
	private RelativeLayout myLayout;
	private TextView stopIndicator;
	private int delayTime;
	private int animationTime;
	private int fontSize;

	private ListIterator<View> it;
	private WristFlickDispatcher sensor;
	private Vibrator vibe;
	private Thread myThread;
	private RunnableAnim animationThread = new RunnableAnim();
	private Handler handler = new Handler(){		  
		@Override
		public void handleMessage(Message msg) {
			animationStep();
		}
	};

	private ViewPager pager; 
	private InfinitePagerAdapter pagerAdapter;

	//---- Audio Recording
	private AudioLevelDispatcher audioLevelRecorder;
	private CameraEventDispatcher cameraDispatcher;
	//----
	private FrameLayout preview;


	// --------------------------------------------------------------------------------

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cheat_animation);

		sensor = new WristFlickDispatcher(this);
		sensor.setListener(this);
		vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
		chapters = new LinkedList<LinkedList<EntryView>>();
		animationTime = 700;

		// Fetch extras from last activity
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			path  = extras.getString("path");
			delayTime = extras.getInt("delay");
			fontSize = extras.getInt("fontSize");
		}			

		myLayout = (RelativeLayout)findViewById(R.id.relativeLayout1);			
		stopIndicator = (TextView)findViewById(R.id.animStop);	



		myThread = new Thread(animationThread);
		myThread.start();	

		initViewPager();
		initChapters();	
		showChapterTitles();  	
		//initRecorder();

		audioLevelRecorder = new AudioLevelDispatcher(this);
		audioLevelRecorder.setListener(this);
		audioLevelRecorder.startRecording();  	

		preview = findViewById(R.id.camera_callback);

		cameraDispatcher = new CameraEventDispatcher(this, preview);
		cameraDispatcher.setListener(this);
	}

	public void showChapterTitles()
	{
		// Fade in chapter-titles
		int a = animationTime;
		animationTime = 1;
		for(int i=0; i<chapters.size();i++)
		{
			currentChapterIndex = i;
			animationStep();
		}
		currentChapterIndex = 0;
		animationTime = a;
	}

	public void onPause() {
		super.onPause();
		//Stop Animation
		animationThread.onPause();
		stopIndicator.setText("stopped");
		runLoop = false;	


		audioLevelRecorder.stopRecording();
	}

	public void onResume() {
		super.onResume();
		audioLevelRecorder.startRecording(); 
	}

	private void initViewPager()
	{	
		//---- PagerStuff ####
		pagerAdapter = new InfinitePagerAdapter();
		pager = (ViewPager) findViewById (R.id.pager);
		pager.setAdapter (pagerAdapter);

		pager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int state) {
			}
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				animationThread.onPause();
				stopIndicator.setText("stopped");
				runLoop = false;
			}

			public void onPageSelected(int position) {
				// Check if this is the page you want.
				currentChapterIndex = position%chapters.size();
				//Log.e("currentChapterIndex",""+currentChapterIndex);
			}
		});
		setViewPagerScrollSpeed();
		pager.setCurrentItem(pager.getChildCount() * InfinitePagerAdapter.LOOPS_COUNT / 2, false); // set current item in the adapter to middle		
	}

	private void setViewPagerScrollSpeed()
	{
		try {
			Field mScroller;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true); 
			FixedSpeedScroller scroller = new FixedSpeedScroller(pager.getContext(), new DecelerateInterpolator());
			mScroller.set(pager, scroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}

	private void initChapters()
	{
		OnSwipeTouchListener touchListener = new OnSwipeTouchListener(this) {
			public void onSwipeLeft() {/*changeChapter(true);*/}
			public void onSwipeRight() {/*changeChapter(false);*/}
			public void onLongClick(){reverseAnimation();}
			public void onShortClick(){startStopAnimation();}
		};	
		
		Sheet cheatSheet = IOCheatz.loadFromArchive(path);
		List<Chapter> chapterList = cheatSheet.getChapterList();

		//------------

		chapterAnimSteps = new int[chapters.size()];
		

		
		
		//iterate over chapters
		for(Chapter c: chapterList)
		{
			//create a new list for each chapter in the sheet
			LinkedList<EntryView> chapterView = new LinkedList<EntryView>();
			
			RelativeLayout rLayout = new RelativeLayout(this);
			rLayout.setLayoutParams(myLayout.getLayoutParams());
			rLayout.setOnTouchListener(touchListener);
			rLayout.setGravity(Gravity.CENTER);	

			//iterate over entries
			for(Entry en: c.getEntryList())
			{		
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);

				EntryView entry = new EntryView(this);
				//fill entryview with the entry
				if (en.getType() == Entry.Type.TEXT_ENTRY){
					//set the text of the view
					entry.setText( ((TextEntry) en).getText());
				} else if (en.getType() == Entry.Type.IMAGE_ENTRY) {
					//set the background image of the view
					entry.setIsImage();
					BitmapDrawable bmp = new BitmapDrawable(getResources(),((ImageEntry) en).getImage());
				}
				int length = entry.getText().length();
				if(length != 0){
					fontSize = 1000 / length;
				}
				fontSize = (fontSize < 15 ? 15: fontSize);
				fontSize = (fontSize > 30 ? 30: fontSize);


				entry.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
				// Underline the chapter name (first entry)
				if(en == c.getEntryList().get(0)){
					entry.setPaintFlags(entry.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
					entry.setTypeface(null, Typeface.BOLD);
				}

				entry.setAlpha(0);
				entry.setGravity(Gravity.CENTER);
				entry.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
				entry.setLayoutParams(params);	

				//add view to layout and entrylist
				rLayout.addView(entry);
				chapterView.add(entry);
			}
			//add list of entries to layout and chapterlist
			chapters.add(chapterView);
			addPage(rLayout);
		}

	}

	private void animationStep()
	{		
		LinkedList<EntryView> entryViews = new LinkedList<EntryView>(); 
		entryViews = chapters.get(currentChapterIndex);		
		int animStep = chapterAnimSteps[currentChapterIndex];

		String x = "List Order right now:";
		for(EntryView a: entryViews){
			x += a.getContent()+"|";
		}
		Log.d("list:",x);

		// ------ Determine Next, Previous and Previous2 Element ---------------
		EntryView next = null;
		EntryView previous = null;
		EntryView previous2 = null;		

		if(reverse){
			entryViews.addFirst(entryViews.pollLast());
			next = entryViews.getFirst();	
		}else{ // normal
			next = entryViews.pollFirst();	
		}

		// same for both					
		if(!(animStep<1))
			previous = entryViews.getLast(); 
		if(!(animStep<2))
			previous2 = entryViews.get(entryViews.size()-2); // 2nd last element
		if(!reverse)
			entryViews.addLast(next);	
		// ------------------------------------------------- #


		// In Reverse-Mode, an image should only fade in, when only 1 other entry is visible
		if(previous2 != null && reverse && previous2.isImage() && previous!= null){
			previous2 = null;
		}
		// If the previous (middle) element is an image, it is set to the previous2 element 
		// (which will be animated in). This is done because an image does not have a second moving step
		if(previous != null && previous.isImage()){
			previous2 = previous;
			if(!reverse)
				animStep = 0;				
			previous = null;			
		}

		// In reverse-mode, if an image fades out we need to make sure that the previous 
		// as well as the previous2 element are determined
		if(reverse && next.isImage() && previous2 != null && !previous2.isImage())
		{
			previous2 = entryViews.get(entryViews.size()-2);
		}

		// -------------------------- Animation Logic			
		// --- Define the animations

		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(next, "alpha", 0f, 1f);
		fadeIn.setDuration(animationTime);

		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(previous2, "alpha", 1f, 0f);
		fadeOut.setDuration(animationTime);

		ObjectAnimator fadeOut2 = ObjectAnimator.ofFloat(previous, "alpha", 1f, 0f);
		fadeOut.setDuration(animationTime);

		ObjectAnimator posMid = ObjectAnimator.ofFloat(next, "yPosition",1f,0.66f);
		posMid.setDuration(animationTime);

		ObjectAnimator posUp = ObjectAnimator.ofFloat(previous, "yPosition",0.66f, 0.33f);
		posUp.setDuration(animationTime);

		ObjectAnimator posOut = ObjectAnimator.ofFloat(previous2, "yPosition",0.33f, 0f);
		posOut.setDuration(animationTime);

		if(previous2 != null && previous2.isImage()){
			posOut.setFloatValues(0.5f,0.5f-0.33f);
		}

		if(next.isImage()){
			posMid.setFloatValues(0.5f+0.33f,0.5f);
		}

		//---  Apply the Animation

		// animate next (element from bottom pos 1 (or reverse)
		if(next != null){
			Log.e("next",next.getContent());
			if(reverse)	{
				posMid.reverse();
				fadeIn.reverse();
			}else{
				posMid.start();
				fadeIn.start();			
			}										
		}

		// animate previous (element from pos1 to pos2)
		if(previous != null){		
			Log.e("previous",previous.getContent());	
			if(reverse)
				posUp.reverse();
			else
				posUp.start();

			if(next.isImage() && !reverse)
				fadeOut2.start();
			else if(next.isImage() && reverse)
				fadeOut2.reverse();				
		}

		// animate previous2 (element from pos2 to top)
		if(previous2  != null){
			Log.e("previous2",previous2.getContent());
			if(reverse){
				posOut.reverse();
				fadeOut.reverse();
			}else{
				posOut.start();
				fadeOut.start();
			}
		}
		Log.e("---","-------");
		animStep++;
		chapterAnimSteps[currentChapterIndex] = animStep;


	} //-------------------------------



	public void startStopAnimation(){
		if(runLoop == true){
			animationThread.onPause();
			stopIndicator.setText("stopped");
			runLoop = false;
		}					
		else{
			animationThread.onResume();
			runLoop = true;	
			stopIndicator.setText("");
		}	
	}

	public void reverseAnimation() {	
		reverse = (reverse ? false: true);
		Log.e("->","long click");
		vibe.vibrate(100);
	}

	/**
	 * Method to handle the logic and animation for changing between chapters
	 * @param forwardOrBackwards value to determine the direction: 
	 * 0: Backwards
	 * 1: Forwards
	 */
	public void changeChapter(boolean forwards)
	{		
		// changing chapter does only make sense when there's more than one
		if(chapters.size()>1)
		{
			LinkedList<EntryView> currentChapter;
			LinkedList<EntryView> newChapter;


			if(forwards){
				if(currentChapterIndex+1 < chapters.size())
					currentChapterIndex++;
				else
					currentChapterIndex=0;
			}
			else{
				if(currentChapterIndex-1 >= 0)
					currentChapterIndex--;
				else
					currentChapterIndex = chapters.size()-1;
			}

		}
	}
	@Override
	public void onWristFlick() {		
		pager.setCurrentItem(pager.getCurrentItem() + 1);
	}


	public void addPage (View newPage)
	{
		int pageIndex = pagerAdapter.addView(newPage);
		pagerAdapter.notifyDataSetChanged();
		// You might want to make "newPage" the currently displayed page:
		//pager.setCurrentItem (pageIndex, true);
	}

	class RunnableAnim implements Runnable {
		private Object mPauseLock;
		private boolean mPaused;
		private boolean mFinished;

		public RunnableAnim() {
			mPauseLock = new Object();
			mPaused = true;
			mFinished = false;
		}

		public void run() {
			while (!mFinished) {	      
				synchronized (mPauseLock) {
					while (mPaused) {
						try {
							mPauseLock.wait();
						} catch (InterruptedException e) {
						}
					}
				}	            
				handler.sendMessage(handler.obtainMessage());
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}

		public void onPause() {
			synchronized (mPauseLock) {
				mPaused = true;
			}
		}

		public void onResume() {
			synchronized (mPauseLock) {
				mPaused = false;
				mPauseLock.notifyAll();
			}
		}  
	}

	@Override
	public void onVeryLoudSoundDetected() {
		//TODO show black screen
	}

	@Override
	public void onLevelChanged(int value) {


	}

	@Override
	public void onCameraEvent() {
		runOnUiThread(new Runnable() {
			public void run() {
				startStopAnimation();
			}
		});	

	}





	// --------------------------------------------
}