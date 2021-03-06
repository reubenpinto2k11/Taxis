package com.pranav.driverApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	GoogleCloudMessaging gcm;
	Context context;
	String regId;
	
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";

	
	//private static final String[] DUMMY_CREDENTIALS = new String[] {
		//	"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		
		SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
		
Button b=(Button) findViewById(R.id.bckbutton);
b.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View arg0) {
		finish();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		//System.exit(1);
		
	}
});
		
		//check for already logged in
		if(!spref.getString("username", "XYZZY").equals("XYZZY")) //usr already logged in. show register activity
		{
			Intent a = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(a);
		}

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		TextView newusr=(TextView) findViewById(R.id.textView1);
		
		newusr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			Intent x= new Intent(LoginActivity.this,RegisterDriver.class);
			startActivity(x);
			}
			
			});
		
		
		
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.TestID

			
			String password=mPassword;
			try
			{
			 MessageDigest md = MessageDigest.getInstance("MD5");
		        md.update(password.getBytes());
		 
		        byte byteData[] = md.digest();
		 
		        //convert the byte to hex format method 1
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < byteData.length; i++) {
		         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		        }
		 
		        System.out.println("Digest(in hex format):: " + sb.toString());
		        password=sb.toString();

		        SharedPreferences prefs = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
				SharedPreferences.Editor   editor = prefs.edit();
		        editor.putString("username",mEmail);
				editor.putString("password",password);
				editor.commit();

			}
			catch(Exception e)
    		{
    			Log.d("Taxiapp",e.toString());
    		}
    	

			//register GCM
			

			
			 
			   try
			   {
				   
				   
				   String x=registerGCM();
			    regId = registerGCM();
			    String url = "http://testapp1pranav.appspot.com/auth?type=driver&name="+mEmail+"&pass="+password+"&regId="+x;
				   
				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		 
				// optional default is GET
				con.setRequestMethod("GET");
		 
				//add request header
				con.setRequestProperty("User-Agent", "Mozilla/5.0");
		 
				int responseCode = con.getResponseCode();

				System.out.println("Response Code : " + responseCode);
		 
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
		 
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				
				String resp= response.toString();
				
				if(mEmail.equals(resp))
				{
					
					SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

					//store username in sharedpref
				     //spref is sharedpreferences
				      Editor editor = spref.edit();
				      editor.putString("username", mEmail);
				      editor.putString("password", password);
				      editor.commit();

					
					Intent a = new Intent(LoginActivity.this, RegisterActivity.class);
					startActivity(a);
					return true; //passwords match
				}
				else
				{
					Intent i=new Intent(LoginActivity.this,LoginActivity.class);
					startActivity(i);
				}
			   }
			   
			   catch(Exception e)
	    		{
	    			Log.d("Taxiapp",e.toString());
	    		}
				
				
		        			


			// TODO: register the new account here.
		return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
				
				
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

	}
	
	
	
	
	
	//GCM code
	
	
	
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			/*Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();*/
			Log.i(TAG, "Registration Already Available.  RegId:"+ regId);
			
			
		}
		return regId;
	}
	
	
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}
	
	
	private static int getAppVersion(Context context) {

		//TODO:give proper app id. no change sto current version.
			return 1;
			

	}
	
	
	
	
	
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
					
					
					//send to server from here itself using auth request 
					
					
					
					 try
					   {
						   
						// send regid 
							SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
							String username=spref.getString("username","");
							String password=spref.getString("password", "");

						   
					    String url = "http://testapp1pranav.appspot.com/auth?type=driver&name="+username+"&pass="+password+"&regId="+regId;
						   
						URL obj = new URL(url);
						HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				 
						// optional default is GET
						con.setRequestMethod("GET");
				 
						//add request header
						con.setRequestProperty("User-Agent", "Mozilla/5.0");
				 
						int responseCode = con.getResponseCode();

						System.out.println("Response Code : " + responseCode);
				 
						BufferedReader in = new BufferedReader(
						        new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();
				 
						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						
						

					   }
					   
					   catch(Exception e)
			    		{
			    			Log.d("Taxiapp",e.toString());
			    		}
					
					
					
					
					
					
					
					
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				
				
				
				
				
				
				return msg;
				
			}

			@Override
			protected void onPostExecute(String msg) {
		
				Log.i(TAG, "Registered With GCM Server."+ msg);
			}
		}.execute(null, null, null);
	}
	
	
	
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
	
}

