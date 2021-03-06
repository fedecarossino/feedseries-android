package com.login;


import static com.wabila.app.CommonUtilities.SENDER_ID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.menu.AndroidTabAndListView;
import com.wabila.app.ConnectionDetector;
import com.wabila.app.R;
import com.wabila.app.ServerUtilities;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class AndroidLogin extends Activity implements OnClickListener {
	
	Button ok,back,exit;
	TextView result,registerScreen, recoveryScreen;
	SharedPreferences pref;
	EditText pword,uname;
	int status;
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        
		if(pref.getString("email", null) != null && !GCMRegistrar.getRegistrationId(this).equals("")){
//        if(pref.getString("email", null) != null){
			Intent i = new Intent(getApplicationContext(), AndroidTabAndListView.class);
			startActivity(i);
			finish();
		}else{
	        setContentView(R.layout.main_login);
	        recoveryScreen = (TextView) findViewById(R.id.link_to_recovery);
	        recoveryScreen.setOnClickListener(this);
	        
	        registerScreen = (TextView) findViewById(R.id.link_to_register);
	        registerScreen.setOnClickListener(this);
	        // Login button clicked
	        ok = (Button)findViewById(R.id.btn_login);
	        ok.setOnClickListener(this);
	        pword = (EditText)findViewById(R.id.txt_password);
	        uname = (EditText)findViewById(R.id.txt_username);
	        
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	        
	        result = (TextView)findViewById(R.id.lbl_result);
		}
        
    }
    
    class Login extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AndroidLogin.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			try {
				postLoginData();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			if (status != 200){
				result.setText("Incorrect password and/or email"); 
			}
			pDialog.dismiss();
		}
		
		
    }
    
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void postLoginData() throws IllegalStateException, JSONException {
    	
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        

        try {
            // Add user name and password
        	
        	String username = uname.getText().toString();
        	String password = pword.getText().toString();
        	
        	HttpGet httpget = new HttpGet("http://feedseries.herokuapp.com/getUser?email="+username+"&pass="+password);
        	
            // Execute HTTP Post Request
            Log.w("SENCIDE", "Execute HTTP Post Request");
            HttpResponse response = httpclient.execute(httpget);
            
            int str = response.getStatusLine().getStatusCode();
            Log.w("SENCIDE", String.valueOf(str));
            status = str;
            if(str == 200)
            {
            	Log.w("SENCIDE", "TRUE");
            	String email = getEmail(inputStreamToString(response.getEntity().getContent()).toString());
//            	pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            	Editor editor = pref.edit();
            	
            	editor.putString("email", email);
            	editor.commit();
            	String redId = registerGCM();
//            	String redId = "df";
            	if(redId != null && !redId.equals("")){
            		Intent i = new Intent(getApplicationContext(), AndroidTabAndListView.class);
            		startActivity(i);
            		finish();
            	}
            	
            }

        } catch (ClientProtocolException e) {
        	e.printStackTrace();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    } 
    
    private String registerGCM(){
    	GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);
        
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, SENDER_ID);
            
        } else {
                 // Try to register again, but not in the UI thread.
                 // It's also necessary to cancel the thread onDestroy(),
                 // hence the use of AsyncTask instead of a raw thread.
                 final Context context = this;
                 mRegisterTask = new AsyncTask<Void, Void, Void>() {

                     @Override
                     protected Void doInBackground(Void... params) {
                         boolean registered =
                                 ServerUtilities.register(context, regId, pref.getString("email", null));
                         // At this point all attempts to register with the app
                         // server failed, so we need to unregister the device
                         // from GCM - the app will try to register again when
                         // it is restarted. Note that GCM will send an
                         // unregistered callback upon completion, but
                         // GCMIntentService.onUnregistered() will ignore it.
                         if (!registered) {
                             GCMRegistrar.unregister(context);
                         }
                         return null;
                     }

                     @Override
                     protected void onPostExecute(Void result) {
                         mRegisterTask = null;
                     }

                 };
                 mRegisterTask.execute(null, null, null);
        }
        return GCMRegistrar.getRegistrationId(this);
    }
    
    @SuppressWarnings("unused")
	private String getEmail(String response) throws JSONException{
    	JSONObject jsonObject = new JSONObject(response);
    	JSONObject data = jsonObject.getJSONObject("data");
    	return data.getString("email"); 
    }
    private StringBuilder inputStreamToString(InputStream is) {
    	String line = "";
    	StringBuilder total = new StringBuilder();
    	// Wrap a BufferedReader around the InputStream
    	BufferedReader rd = new BufferedReader(new InputStreamReader(is));
    	// Read response until the end
    	try {
			while ((line = rd.readLine()) != null) { 
				total.append(line); 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	// Return full string
    	return total;
    }

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) {
		if(view == ok){
			try {
				pword = (EditText)findViewById(R.id.txt_password);
		        uname = (EditText)findViewById(R.id.txt_username);
				ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
				Boolean isInternetPresent = cd.isConnectingToInternet();
				if(!pword.getText().toString().equals("") && !uname.getText().toString().equals("")){
					if(isInternetPresent){
						new Login().execute();
					}else{
						AlertDialog alertDialog = new AlertDialog.Builder(
		                        AndroidLogin.this).create();
				 
				        // Setting Dialog Title
				        alertDialog.setTitle(getString(R.string.error_internet));
				 
				        // Setting Dialog Message
				        alertDialog.setMessage(getString(R.string.error_internet_message));
				 
				        // Setting Icon to Dialog
				 
				        // Setting OK Button
				        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				                public void onClick(DialogInterface dialog, int which) {
				                // Write your code here to execute after dialog closed
				                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
				                }
				        });
				        alertDialog.show();
					}
				}else{
					result.setText("Email and password can not be null"); 
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(view == registerScreen){
    		Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
    		startActivity(i);
		}else if(view == recoveryScreen){
    		Intent i = new Intent(getApplicationContext(), RecoveryPassActivity.class);
    		startActivity(i);
		}
	}

}