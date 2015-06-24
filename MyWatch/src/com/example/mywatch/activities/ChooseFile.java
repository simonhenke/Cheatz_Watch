package com.example.mywatch.activities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.mywatch.R;
import com.example.mywatch.R.id;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseFile extends Activity {

	ListView listSavedFiles;

	String[] SavedFiles;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_file);

		listSavedFiles = (ListView)findViewById(R.id.list);
		ShowSavedFiles();
		
		listSavedFiles.setOnItemClickListener(itemClickListener);
		listSavedFiles.setOnItemLongClickListener(itemLongClickListener);

	}

	void ShowSavedFiles(){
		SavedFiles = getApplicationContext().fileList();
		ArrayAdapter<String> adapter
		= new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SavedFiles);

		listSavedFiles.setAdapter(adapter);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			String clickedFile = (String) parent.getItemAtPosition(position);
			String content = ReadContent(clickedFile);
			openChooseSpeed(content);
		}
	};
	
	OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        public boolean onItemLongClick(AdapterView<?> parent, View arg1,int position, long id) {
            Log.v("long clicked","pos: " + position);
            String clickedFile = (String) parent.getItemAtPosition(position);
            OpenFileDialog(clickedFile);
            return true;
        }
    }; 
	
 
    void OpenFileDialog(final String file)
    {
    	 //Create a custom Dialog
    	String content = ReadContent(file);
    	
        AlertDialog.Builder fileDialog 
        = new AlertDialog.Builder(ChooseFile.this);
        fileDialog.setTitle(file);
        
        TextView textContent = new TextView(ChooseFile.this);
        textContent.setText(content);
           LayoutParams textViewLayoutParams 
            = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
           textContent.setLayoutParams(textViewLayoutParams);
           
           fileDialog.setView(textContent);
           
           fileDialog.setPositiveButton("OK", null);
           
           //Delete file in Internal Storage
           OnClickListener DeleteListener = new OnClickListener(){
        	   @Override
        	   public void onClick(DialogInterface dialog, int which) {
        		   deleteFile(file);
        		   Toast.makeText(ChooseFile.this,file + " deleted",Toast.LENGTH_LONG).show();
        		   ShowSavedFiles();
        	   }
           };
           fileDialog.setNeutralButton("DELETE", DeleteListener);         
           fileDialog.show();
       }

	String ReadContent(String file){
		//Read file in Internal Storage
		FileInputStream fis;
		String content = "";
		try {
			fis = openFileInput(file);
			byte[] input = new byte[fis.available()];
			while (fis.read(input) != -1) {}
			content += new String(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace(); 
		}			
		return content;
	}
	
	public void openChooseSpeed(String content) {
	    Intent intent = new Intent(ChooseFile.this, SpeedChooser.class);
	    intent.putExtra("content", content);
	    ChooseFile.this.startActivity(intent);	    
	}
}