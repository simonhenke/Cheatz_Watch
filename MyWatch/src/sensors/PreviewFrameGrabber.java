package sensors;

import activities.CameraCalibration;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by David on 13.06.2015.
 */
public class PreviewFrameGrabber implements Camera.PreviewCallback {

    private Context context;
    private int frameCounter;
    private int darkerFrameCounter;
    private ImageProcessor processor;
    private TextView textView;

    int test;

    public PreviewFrameGrabber(Context context, TextView textView){
        this.context = context;
        this.textView = textView;
        this.processor = new ImageProcessor();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        frameCounter++;
        //Convert Threshold
        Integer threshold = new Integer(CameraCalibration.getProgressChanged());
        float thresholdPercentage = threshold.floatValue() / 100;

        //Check for darkness
        if (processor.darkerThanThreshold(data, thresholdPercentage, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height)) {
            darkerFrameCounter++;
            //If 15 out of 60 frames are darker than the threshold -> fire Event
            if (darkerFrameCounter >= 20 && frameCounter <= 60){
                darkerFrameCounter = 0;
                frameCounter = 0;
                //feuer Event "ProfKommtModus"
                textView.setText("PROF KOMMT!!!");
                test++;
                Log.d("PROF_KOMMT", "CALLED FOR THE " + test + "TIME");
            }
            textView.setText("darker");
        } else {
            textView.setText("brighter");
        }

        if (frameCounter > 60){
            frameCounter = 0;
        }

       // Log.d("FRAME_COUNTER", "FrameCounter: " + frameCounter);
       // Log.d("FRAME_COUNTER_DARK", "Dark Frames : " + darkerFrameCounter);

    }

}
