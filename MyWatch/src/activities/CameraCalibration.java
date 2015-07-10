package activities;

import java.util.List;

import sensors.CameraPreview;
import sensors.OnSwipeTouchListener;
import sensors.PreviewFrameGrabber;

import com.example.mywatch.R;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;






public class CameraCalibration extends Activity {

	private Camera mCamera;
    private CameraPreview mCameraPreview;
    private TextView myAwesomeTextView;
    private TextView seekbarText;
    private SeekBar thresholdControl;
    private int camThreshold;
    private SharedPreferences.Editor editor;
    private FrameLayout preview;
    private OnSwipeTouchListener swipeListener;
    private RelativeLayout myLayout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_calibration);

        SharedPreferences sharedPref = this.getSharedPreferences("CheatzCalibrationCam",Context.MODE_PRIVATE);
        camThreshold = sharedPref.getInt("camThreshold", 25);
        editor = sharedPref.edit();
        
		myLayout = (RelativeLayout)findViewById(R.id.layoutCam);

		myLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeRight() {
				finish();
			}
		});
        
        //DebugText
        myAwesomeTextView = (TextView)findViewById(R.id.textview);
        seekbarText = (TextView) findViewById(R.id.seekbartext);
        seekbarText.setText("Threshold: " + camThreshold);
        //Slider init
        thresholdControl = (SeekBar) findViewById(R.id.seekbar);
        thresholdControl.setProgress(camThreshold);
        
        thresholdControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                camThreshold = progress;
                seekbarText.setText("Threshold: " + camThreshold);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            	editor.putInt("camThreshold", camThreshold);
        		editor.commit();
            }
        });

    }
    
    public void initCam()
    {
    	  mCamera = getCameraInstance();
          Camera.Parameters parameters = mCamera.getParameters();
          
          List<int[]> fpsranges = parameters.getSupportedPreviewFpsRange();  
          int[] fpsrange = fpsranges.get(0);  
          parameters.setPreviewFpsRange(fpsrange[0], fpsrange[1]); 
          mCamera.setParameters(parameters);
      
          mCamera.setPreviewCallback(new PreviewFrameGrabber(getApplicationContext(), myAwesomeTextView));

          //Preview init
          mCameraPreview = new CameraPreview(this, mCamera);
          //Adding the Preview to the Layout
          preview = (FrameLayout) findViewById(R.id.camera_preview);
          preview.addView(mCameraPreview);
    }
    

    /**
     * Save method to open the Camera
     *
     * @return camera
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // cannot get camera or does not exist
        }
        return camera;
    }

 
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	initCam();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	  preview.removeView(mCameraPreview);
    }
    
    
}
