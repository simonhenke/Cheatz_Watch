package sensors;

public interface AudioLevelListener {

	public void onVeryLoudSoundDetected();	
	
	public void onLevelChanged(double value);
}
