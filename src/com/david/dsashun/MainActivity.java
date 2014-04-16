package com.david.dsashun;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	public static final String TAG= MainActivity.class.getSimpleName();
	public static final int TAKE_PHOTO_REQUEST=0;
	public static final int TAKE_VIDEO_REQUEST=1;
	public static final int PICK_PHOTO_REQUEST=2;
	public static final int PICK_VIDEO_REQUEST=3;
	
	public static final int MEDIA_TYPE_IMAGE=4;
	public static final int MEDIA_TYPE_VIDEO=5;
	public static final int FILE_SIZE_LIMIT=1024*1024*10; //Limiting to 10 MB
	
	protected  Uri mMediaUri;
	
	protected DialogInterface.OnClickListener mDialogListener =new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			//determining what should be done when a selection is made on the camera dialog box
			switch (which) {
			case 0: //Take a picture
				Intent takePhotoIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				mMediaUri =getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
				if (mMediaUri== null) {
					//display an error
					Toast.makeText(MainActivity.this,R.string.error_external_storage, Toast.LENGTH_LONG).show();
				}
				else
					takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
				startActivityForResult(takePhotoIntent,TAKE_PHOTO_REQUEST);
				break;
			case 1: //Take a video
				Intent  videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				mMediaUri =getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
				if (mMediaUri== null) {
					//display an error
					Toast.makeText(MainActivity.this,R.string.error_external_storage, Toast.LENGTH_LONG).show();
				}
				else
						videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
						//setting how long a video can be , we set ours to 60secs. We wanted to set it high but due to the limitations from 
						//parse.com we decided to limit it to 60secs.
						videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
						//setting the quality of our video, parse.com limits us to only 10MB so setting it to 1 will not help our app
						//unless we subscribe to a paid membership
						videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); //0=Lowest quality
						startActivityForResult(videoIntent,TAKE_VIDEO_REQUEST);
				break;
			case 2: //Choose Picture
				
				Intent choosePhotoIntent= new Intent(Intent.ACTION_GET_CONTENT);
				choosePhotoIntent.setType("image/*");
				startActivityForResult(choosePhotoIntent,PICK_PHOTO_REQUEST);
				break;
			case 3: //Choose video;
				Intent chooseVideoIntent= new Intent(Intent.ACTION_GET_CONTENT);
				chooseVideoIntent.setType("video/*");
				Toast.makeText(MainActivity.this, R.string.video_file_size_warning, Toast.LENGTH_LONG).show();
				startActivityForResult(chooseVideoIntent,PICK_VIDEO_REQUEST);
			}
		}

		private Uri getOutputMediaFileUri(int mediaType) {
			// To be safe, you should check that the SDCard is mounted 
			// using Envrionment.getExternalStorageState() before doing this.
			if (isExternalStorageAvailable()) {
				// get the Uri
				
				//1. Get the external storage directory
				
					String appName = MainActivity.this.getString(R.string.app_name);
					File mediaStorageDir= new File(
						Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);
				//2. Create our subdirectory
					if(! mediaStorageDir.exists()) {
						if (! mediaStorageDir.mkdirs()) {
							Log.e(TAG, "Failed to create directory");
						}
					}
				//3. create filename
				//4.create the file
					File mediaFile;
					Date now =new Date();
					String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(now);
					String path=mediaStorageDir.getPath()+ File.separator;
					
					if(mediaType==MEDIA_TYPE_IMAGE) {
						mediaFile =new File(path+ "IMG_"+timestamp+".jpg");
						
					}
					else if (mediaType==MEDIA_TYPE_VIDEO) {
						mediaFile =new File(path+ "VID_"+timestamp+".mp4");
					}
					else {
						return null;
					}
				//5. Return file's URI
						return Uri.fromFile(mediaFile);
			}
			else {
				return null;
			}
		} 
		private boolean isExternalStorageAvailable() {
			String state = Environment.getExternalStorageState();
			if(state.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			}
			else
				return false;
		}
	};
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		//Analytics for tracking app usage
		ParseAnalytics.trackAppOpened(getIntent());
		
		ParseUser currentUser= ParseUser.getCurrentUser();
		if(currentUser==null) {
		navigateToLogin();
		}
		else {
			Log.i(TAG,currentUser.getUsername());
		}
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			
			if(resultCode == RESULT_OK) {
				// Picking from gallery
				if (requestCode == PICK_PHOTO_REQUEST || requestCode == PICK_VIDEO_REQUEST) {
					
					if (data== null) {
						Toast.makeText(this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
					}
					
					//Means  intent has data
					else {
						mMediaUri =data.getData();
					}
					//Checking the size of the video
					if(requestCode == PICK_VIDEO_REQUEST) {
						//make sure the file is less than 10MB
						int fileSize=0;
						//used to stream a file byte by byte
						InputStream inputStream=null;
						try{
							inputStream = getContentResolver().openInputStream(mMediaUri);
							fileSize= inputStream.available();
							}
						catch (FileNotFoundException e) {
							Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
									return;
							}
						catch (IOException e) {
							Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
								return;
						}
						finally {//closing the inputstream to avoid memory leaks
							try {
								inputStream.close();
							} catch (IOException e) {
								// left blank on purpose
							}
						} if (fileSize>=FILE_SIZE_LIMIT) {
							
							Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
								
							return;
						}
					}
				}	
					else {
						// add to gallery
						Intent mediaScanIntent =new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						mediaScanIntent.setData(mMediaUri);
						sendBroadcast(mediaScanIntent);
				}
				
				Intent recipientsIntent =new Intent(this, RecipientsActivity.class);
				recipientsIntent.setData(mMediaUri);
				
				String fileType;
				if (requestCode==PICK_PHOTO_REQUEST ||requestCode==TAKE_PHOTO_REQUEST) {
					
					fileType= ParseConstants.TYPE_IMAGE;
				}else {
					fileType= ParseConstants.TYPE_VIDEO;

				} recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
				startActivity(recipientsIntent);
			}
			else if (resultCode != RESULT_CANCELED) {
				Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
			}
		}
	private void navigateToLogin() {
		Intent intent = new Intent (this, LoginActivity.class);
		//create new intent class so 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//clear history so main activity or inbox  cannot be seen
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@SuppressWarnings("unused")
	@Override
	//putting the logout button in the menu 
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId	=	item.getItemId();
		
		
		switch (itemId) {
		//logging out the current user using the parse.com method
		case R.id.action_logout :
			 ParseUser.logOut();
		//navigate to the login screen after logout 
			ParseUser currentUser = ParseUser.getCurrentUser(); 
		case
		
		R.id.action_edit_friends:
			Intent intent =new  Intent(this, EditFriendsActivity.class);
			startActivity(intent);
			//setting dialog box for the camera, this brings up a dialog box which will list available options
		case R.id.action_camera:
			
		AlertDialog.Builder builder =new AlertDialog.Builder(this);
		builder.setItems(R.array.camera_choices, mDialogListener);
		AlertDialog dialog = builder.create();
		dialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	
	
}
