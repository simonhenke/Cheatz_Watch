package Sensors;

public interface AudioLevelListener {

	public void onVeryLoudSoundDetected();	
	
	public void onLevelChanged(double value);
}
