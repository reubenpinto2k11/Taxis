package com.example.taxiclient;

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

public class Register extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.android_register);
		
		final EditText et1 = (EditText) findViewById(R.id.editText1);
		final EditText et2 = (EditText) findViewById(R.id.editText2);
		final EditText et3 = (EditText) findViewById(R.id.editText3);
		final EditText et4 = (EditText) findViewById(R.id.editText4);
		final EditText et5 = (EditText) findViewById(R.id.editText5);
		final Button b1=(Button) findViewById(R.id.button1);
		final Button b2=(Button) findViewById(R.id.buttonYes);
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View fv=null;
				
				
				
				if((et1.getText().toString().equals(""))||(et2.getText().toString().equals(""))||(et3.getText().toString().equals(""))||(et4.getText().toString().equals(""))||(et5.getText().toString().equals("")))
				{
					Toast toast= Toast.makeText(Register.this,"Enter all the fields", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					
				}
				else
				{
					if(et1.getText().toString().equals(""))
					{

						Toast toast= Toast.makeText(Register.this,"Enter Value into the name field", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						fv=et1;
						fv.requestFocus();					
					
					}
					else 
						if(et4.getText().toString().equals(""))
						{

							Toast toast= Toast.makeText(Register.this,"Enter Value into the password field", Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
							fv=et4;
							fv.requestFocus();					
						
						}
					
					else
					if(et4.getText().length()<4)
					{
						Toast toast= Toast.makeText(Register.this,"Password too short", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
						fv=et4;
						fv.requestFocus();
					}
					else
					{
					if (et2.getText().toString().matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
							+"\\@"+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}"
							+"("+"\\." 
							+"[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" 
							+")+") && et2.getText().length() > 0)
				            {
								
				            	Toast toast= Toast.makeText(Register.this,et2.getText()+" is valid", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
				            }
				            else
				            {
				            	Toast toast= Toast.makeText(Register.this,et2.getText()+" Invalid. Please Re-enter Email.", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
								fv=et2;
								fv.requestFocus();
				            }
					
					
					
					
					if((et4.getText()).toString().equals(et5.getText().toString()))
					{
					
					
						String password=et4.getText().toString();
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
					        Toast toast2 = Toast.makeText(Register.this, sb.toString(), Toast.LENGTH_SHORT);
					        toast2.setGravity(Gravity.CENTER, 0, 0);
							toast2.show();
					        
					        //getting details in local variable to send
					        String usnm=et1.getText().toString();
					        String email=et2.getText().toString();
					        String pass=sb.toString();
					        String phone=et3.getText().toString();					        
			
					        //sending to server
					        		               
			            String url="http://testapp1pranav.appspot.com/register?type=user&name="+usnm+"&email="+email+"&pass="+pass+"&phone="+phone;  
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
									{	Log.e("Driverapp", "Registration successful");
									showToast("Registration successful. Please Login");
									Intent i=new Intent(Register.this,LoginActivity.class);
									startActivity(i);
									finish();
									}
								
								}
								catch(Exception e)
								{
									Log.e("Driverapp", e.toString());

								}
							}
						};
						t1.start();
 
					        }            								
					        catch(Exception ex)
					        {}                                          			            
						 
//		
				else
					{
					Toast toast= Toast.makeText(Register.this,"PASSWORDS DO NOT  MATCH", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					fv=et4;
					fv.requestFocus();
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
			    //System.exit(0);
                finishActivity(0);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
	
	public void showToast(final String toast)
	{
	    runOnUiThread(new Runnable() {
	        public void run()
	        {
	            Toast.makeText(Register.this, toast, Toast.LENGTH_SHORT).show();
	        }
	    });
	}

}
