package com.david.dsashun;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class DsashunApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		  Parse.initialize(this, "bSuJgVIYpQHfFyA6Wk9dDLRN2c9bXPb9GqvwQgHx", "ZkE08Bh5r6MExbUONDH7IZqPAsxx9HK6qaxNGlhb");
		
		  
		  }
	
}
