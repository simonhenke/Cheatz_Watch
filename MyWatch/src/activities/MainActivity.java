package activities;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import com.example.mywatch.R;

import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	Button openFile;
	Button settings;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//openFile = (Button) findViewById(R.id.buttonOpenFile);
		//settings = (Button) findViewById(R.id.buttonSettings);
		
	}	

	public void openFileClicked(View view) {
	    Intent intent = new Intent(MainActivity.this, ChooseFile.class);
	    MainActivity.this.startActivity(intent);
	}
	
	public void settingsClicked(View view) {
	    Intent intent = new Intent(MainActivity.this, Settings.class);
	    MainActivity.this.startActivity(intent);
	}

}
