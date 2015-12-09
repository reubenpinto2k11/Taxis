package com.pranav.driverApp;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterDriver extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.android_register);
		final EditText et1=(EditText) findViewById(R.id.editText1);
		final EditText et2=(EditText) findViewById(R.id.editText2);
		final EditText et3=(EditText) findViewById(R.id.editText3);
		final EditText et4=(EditText) findViewById(R.id.editText4);
		final EditText et5=(EditText) findViewById(R.id.editText5);
		final EditText et6=(EditText) findViewById(R.id.editText6);
		final EditText et7=(EditText) findViewById(R.id.editText7);
		final Button b1=(Button) findViewById(R.id.bckbutton);
		final Button b2=(Button) findViewById(R.id.button2);
		
		b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				View fv=null;
				if((et1.getText().toString().equals(""))||(et2.getText().toString().equals(""))||(et3.getText().toString().equals(""))||(et4.getText().toString().equals(""))||(et5.getText().toString().equals(""))||(et6.getText().toString().equals(""))||(et7.getText().toString().equals("")))
				{
					Toast toast= Toast.makeText(RegisterDriver.this,"please enter all the details", 5000);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
				else
				{
					if (et1.getText().toString().matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
							+"\\@"+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
							+"("+"\\." 
							+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" 
							+")+") && et1.getText().length() > 0)
				            {
								
				            	Toast toast= Toast.makeText(RegisterDriver.this,et1.getText()+" is valid", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
				            }
				            else
				            {
				            	Toast toast= Toast.makeText(RegisterDriver.this,et1.getText()+" Invalid. Please Re-enter Email.", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								fv=et1;
								fv.requestFocus();
				            }
					if(et2.getText().toString().equals(""))
					{

						Toast toast= Toast.makeText(RegisterDriver.this,"Enter Value into the name field", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						fv=et2;
						fv.requestFocus();					
					
					}
					else 
					{
						if(et3.getText().toString().equals(""))
						{

							Toast toast= Toast.makeText(RegisterDriver.this,"Enter Value into the password field", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							fv=et3;
							fv.requestFocus();					
						
						}
					
					else
					if(et3.getText().length()<4)
					{
						Toast toast= Toast.makeText(RegisterDriver.this,"Password too short", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						fv=et3;
						fv.requestFocus();
					}

					if(et3.getText().toString().equals(et4.getText().toString()))
					{						
						String password=et3.getText().toString();
						MessageDigest md;
						try {
							md = MessageDigest.getInstance("MD5");
							md.update(password.getBytes());
							byte byteData[] = md.digest();
							
							//convert the byte to hex format method 1
					        StringBuffer sb = new StringBuffer();
					        for (int i = 0; i < byteData.length; i++) {
					         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
					        }
					        
					        //getting details in local variable to send
					        String usnm=et2.getText().toString();
					        String email=et1.getText().toString();
					        String pass=sb.toString();
					        String tt=et5.getText().toString();
					        String tn=et6.getText().toString();
					        String num=et7.getText().toString();
					     
					        //sending to server
					        		               
			            String url="http://testapp1pranav.appspot.com/register?type=driver&name="+usnm+"&email="+email+"&pass="+pass+"&mob="+num+"&t_type="+tt+"&t_num="+tn;  
			            SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);
						Editor editor = spref.edit();
						editor.putString("url", url);
						editor.commit();



						Thread t1 = new Thread()
						{
							public void run()
							{

								//send to server
								try
								{
									SharedPreferences spref = getSharedPreferences("com.pranav.driverapp", Context.MODE_PRIVATE);

									String url = spref.getString("url", "");

									URL obj = new URL(url);
									HttpURLConnection con = (HttpURLConnection) obj.openConnection();
								//	urlConnection.setRequestProperty("connection", "close")
									con.setRequestProperty("connection", "close");

									// optional default is GET
									con.setRequestMethod("GET");

									//add request header
									con.setRequestProperty("User-Agent", "Mozilla/5.0");

									int responseCode = con.getResponseCode();

									if(responseCode!=200)
									{	Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
									showToast("Problem Contacting server, Please try again");
									}
									
									if(responseCode==200)
									{	Log.e("Driverapp", "ERROR: Server responded with "+responseCode);
									showToast("Registration successful. Please login");
									Intent i= new Intent(RegisterDriver.this,LoginActivity.class);
									startActivity(i);
									}
								
								}
								catch(Exception e)
								{
									Log.e("Driverapp", e.toString());
									showToast("Problem Contacting server, Please try again");

								}
							}
						};
						t1.start();

						 
					       		        }
					        catch(Exception ex)
					        {}    
					}
					else
					{
					Toast toast= Toast.makeText(RegisterDriver.this,"PASSWORDS DO NOT MATCH", 5000);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					}
					}
				}
				
				
			}
		});
		b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				android.os.Process.killProcess(android.os.Process.myPid());
				finishActivity(0);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(RegisterDriver.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}


}
