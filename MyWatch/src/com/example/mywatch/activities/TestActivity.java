package com.example.mywatch.activities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.mywatch.R;
import com.example.mywatch.R.id;
import com.example.mywatch.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TestActivity extends Activity {

EditText edFileName, edContent;
Button btnSave;
ListView listSavedFiles;

String[] SavedFiles;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
     // Intent intent = getIntent();
      Log.d("STATUS","IT WORKED");
      
      
      setContentView(R.layout.activity_test);
      
      edFileName = (EditText)findViewById(R.id.filename);
      edContent = (EditText)findViewById(R.id.content);
      btnSave = (Button)findViewById(R.id.save);
      listSavedFiles = (ListView)findViewById(R.id.list);
  
   ShowSavedFiles();
  
   btnSave.setOnClickListener(new Button.OnClickListener(){

  @Override
  public void onClick(View arg0) {
   // TODO Auto-generated method stub
   String fileName = edFileName.getText().toString();
   String content = edContent.getText().toString();
  
   FileOutputStream fos;
   try {
    fos = openFileOutput(fileName, Context.MODE_PRIVATE);
    fos.write(content.getBytes());
    fos.close();
   
    Toast.makeText(TestActivity.this,fileName + " saved",Toast.LENGTH_LONG).show();
   
   } catch (FileNotFoundException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
   }
  
   ShowSavedFiles();
  
  }});
  
  }

  void ShowSavedFiles(){
   SavedFiles = getApplicationContext().fileList();
   ArrayAdapter<String> adapter
   = new ArrayAdapter<String>(this,
     android.R.layout.simple_list_item_1,
     SavedFiles);

   listSavedFiles.setAdapter(adapter);
  }
}