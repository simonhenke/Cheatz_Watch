package sensors;

public interface AudioLevelListener {

	public void onVeryLoudSoundDetected();	
	
	public void onLevelChanged(int value);
}
