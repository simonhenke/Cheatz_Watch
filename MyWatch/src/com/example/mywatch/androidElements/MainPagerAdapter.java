package com.example.mywatch.androidElements;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MainPagerAdapter extends PagerAdapter
{
	 // This holds all the currently displayable views, in order from left to right.
	  private ArrayList<View> views = new ArrayList<View>();

	  @Override
	  public int getItemPosition (Object object)
	  {
	    int index = views.indexOf (object);
	    if (index == -1)
	      return POSITION_NONE;
	    else
	      return index;
	  }

	  @Override
	  public Object instantiateItem (ViewGroup container, int position)
	  {
	    View v = views.get (position);
	    container.addView (v);
	    return v;
	  }

	  @Override
	  public void destroyItem (ViewGroup container, int position, Object object)
	  {
	    container.removeView (views.get (position));
	  }

	  @Override
	  public int getCount ()
	  {
	    return views.size();
	  }

	  @Override
	  public boolean isViewFromObject (View view, Object object)
	  {
	    return view == object;
	  }

	  public int addView (View v)
	  {
	    return addView (v, views.size());
	  }

	  public int addView (View v, int position)
	  {
	    views.add (position, v);
	    return position;
	  }

	  public int removeView (ViewPager pager, View v)
	  {
	    return removeView (pager, views.indexOf (v));
	  }

	  public int removeView (ViewPager pager, int position)
	  {
	    pager.setAdapter (null);
	    views.remove (position);
	    pager.setAdapter (this);

	    return position;
	  }

	  public View getView (int position)
	  {
	    return views.get (position);
	  }
}










