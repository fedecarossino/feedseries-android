package com.menu;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.R;
import com.login.AndroidLogin;

public class ProfileActivity extends Activity implements OnClickListener{
	// All xml labels
	TextView txtEmail;
	Button ok;
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();
	
	// Profile json object
	JSONObject profile;
	
	// Profile JSON url
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		txtEmail = (TextView) findViewById(R.id.email);
		
        // Login button clicked
        ok = (Button)findViewById(R.id.btn_logout);
        ok.setOnClickListener(this);
		
        // Loading Profile in Background Thread
        new LoadProfile().execute();
	}

	/**
	 * Background Async Task to Load profile by making HTTP Request
	 * */
	class LoadProfile extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ProfileActivity.this);
			pDialog.setMessage("Loading profile ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Profile JSON
		 * */
		protected String doInBackground(String... args) {
//			// Building Parameters
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			
//			// getting JSON string from URL
//			JSONObject json = jsonParser.makeHttpRequest(PROFILE_URL, "GET",
//					params);
//
//			// Check your log cat for JSON reponse
//			Log.d("Profile JSON: ", json.toString());
//
//			try {
//				// profile json object
//				profile = json.getJSONObject(TAG_PROFILE);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
					
					// displaying all data in textview
					txtEmail.setText("Email: " + pref.getString("email", null).toLowerCase());
					
				}
			});

		}

	}
	
	private void logout(){
    	SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    	Editor editor = pref.edit();
    	
    	editor.putString("email", null);
    	editor.commit();
    	GCMRegistrar.unregister(getBaseContext());
    	Intent i = new Intent(getApplicationContext(), AndroidLogin.class);
		startActivity(i);
		finish();
	}

	@Override
	public void onClick(View view) {
		if(view == ok){
			try {
				logout();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
