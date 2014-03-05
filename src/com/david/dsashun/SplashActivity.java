package com.david.dsashun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
/**
 * 
 * 
 * @author David Sampong
 * @version 1.1
 * @date 2/13,2014
 * This is the splash screen for the Feeding Faith Ministries' Blog App
 *
 */
public class SplashActivity extends Activity {

	//Timer for the Splash Screen
	private static int splashTimeOut=4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        new Handler().postDelayed(new Runnable(){
        	
        	@Override
        	
        	public void run(){
        		
        		Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        		startActivity(intent);
        		
        		finish();
        	}
        	
        },splashTimeOut);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
   //     getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
    
}
