package com.example.mywatch;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class EntryView extends TextView{

	private int deviceWidth;
	private int deviceHeight;
	private boolean isImage;
	
	public EntryView(Context context) {
		super(context);
		
		isImage = false;
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		Point size = new Point();
		display.getSize(size);
		deviceWidth = size.x;
		deviceHeight = size.y;	
	}
	
	public void setIsImage(){
		isImage = true;
	}
	
	public boolean isImage(){
		return isImage;
	}
	
	public String getContent()
	{
	    String text;
	    if(isImage)
	    	text = "image";
	    else
	    	text = getText().toString();
		return text;
	}
	
	public float getYPosition()
	{	
		float heightFactor = 0;
		heightFactor = getMeasuredHeight()*0.5f;
		return (deviceHeight == 0) ? 0 : (getY()+ heightFactor) / (float) deviceHeight;
	}

    public void setYPosition(float yFraction) 
    {   
    	float heightFactor = 0;  
    	heightFactor = getMeasuredHeight()*0.5f;
    	setY((deviceHeight > 0) ? (yFraction * deviceHeight-heightFactor) : 0);
    }    
    
    
    public float getXPosition()
	{
		return getX();
	}

    public void setXPosition(float xFraction) 
    {   
    	setX((deviceWidth > 0) ? (xFraction * deviceWidth) : 0);
    }   
}
