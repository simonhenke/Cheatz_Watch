package sensors;

import java.util.List;


import android.content.Context;
import android.hardware.Camera;
import android.view.View;
import android.widget.FrameLayout;

public class CameraEventDispatcher implements Camera.PreviewCallback {
	  private Context context;
	    private int frameCounter;
	    private int darkerFrameCounter;
	    private ImageProcessor processor;
		private CameraEventListener listener;
		private Camera camera;


	    public CameraEventDispatcher(Context context, FrameLayout preview){
	        this.context = context;
	        this.processor = new ImageProcessor();
	        this.camera = Camera.open();
	        Camera.Parameters parameters = camera.getParameters();

	        List<int[]> fpsranges = parameters.getSupportedPreviewFpsRange();  
	        int[] fpsrange = fpsranges.get(0);  
	        parameters.setPreviewFpsRange(fpsrange[0], fpsrange[1]); 
	        camera.setParameters(parameters);

	        camera.setPreviewCallback(this);

	        //Preview init
	        View mCameraPreview = new CameraPreview(context, camera);
	        //Adding the Preview to the Layout
	        
	        preview.addView(mCameraPreview);
	    }
	    
		public void setListener(CameraEventListener listener)
		{
			this.listener = listener;
		}

	    @Override
	    public void onPreviewFrame(byte[] data, Camera camera) {

	    	frameCounter++;
	    	int threshold =  context.getSharedPreferences("CheatzCalibrationCam",Context.MODE_PRIVATE).getInt("camThreshold", 25);
	    	float thresholdPercentage = threshold / 100;

	    	//Check for darkness
	    	if (processor.darkerThanThreshold(data, thresholdPercentage, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height)) {
	    		darkerFrameCounter++;
	    		//If 15 out of 60 frames are darker than the threshold -> fire Event
	    		if (darkerFrameCounter >= 20 && frameCounter <= 60){
	    			darkerFrameCounter = 0;
	    			frameCounter = 0;
	    			//trigger event
	    			listener.onCameraEvent();
	    		}
	    		
	    	} else {
	    		
	    	}

	    	if (frameCounter > 60){
	    		frameCounter = 0;
	    	}

	    }
}
