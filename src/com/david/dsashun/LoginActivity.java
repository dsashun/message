package com.david.dsashun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

@SuppressLint("NewApi")
public class LoginActivity extends Activity {
	protected EditText  mUsername;
	protected EditText  mPassword;
	protected Button  mLoginButton;
	//the m stands for member variable
	protected TextView mSignUpTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Progress
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		//Taking the id of the TextView and assigning it to the mSignup
		mSignUpTextView= (TextView) findViewById(R.id.signupText);
		//setting and on click listener to be able to detect that the text field has been touched
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//this declares that when this part of the activity is clicked activate the  Sign Up activity
				Intent intent= new Intent(LoginActivity.this,SignUpActivity.class);
				startActivity(intent);
			}
		});
		

		mUsername=(EditText) findViewById(R.id.usernameField);
		mPassword=(EditText) findViewById(R.id.passwordField);
		mLoginButton=(Button)findViewById(R.id.loginButton);
		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO Auto-generated method stub
				//getting data from the users input
				String username = 	mUsername.getText().toString();
				String password	=	mPassword.getText().toString();
				
				// taking out white spaces 
				username	=	username.trim();
				password	=	password.trim();
				
				if (username.isEmpty()||password.isEmpty()) {
					// Message to display when the check is right (user validation
					//this requires user interaction instead of a toast which can disappear 
					AlertDialog.Builder builder =new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message);
					builder.setTitle(R.string.login_error_title);
					builder.setPositiveButton(android.R.string.ok, null);
					
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					//progress is done here 
					setProgressBarIndeterminateVisibility(true);
					//Login
					ParseUser.logInInBackground(username, password, new LogInCallback() {

						@Override
						public void done(ParseUser user, ParseException e) {
							setProgressBarIndeterminateVisibility(false);

							if(e==null) {
								
								Intent intent = new Intent(LoginActivity.this,MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else {
								AlertDialog.Builder builder =new AlertDialog.Builder(LoginActivity.this);
								builder.setMessage(e.getMessage());
								builder.setTitle(R.string.login_error_title); 
								builder.setTitle(R.string.login_error_title);
								builder.setPositiveButton(android.R.string.ok, null);
								
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
						
					});
				
				}
			}
		});
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
