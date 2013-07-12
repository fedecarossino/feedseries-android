package com.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.menu.JSONParser;
import com.wabila.app.ConnectionDetector;
import com.wabila.app.R;


public class RecoveryPassActivity extends Activity implements OnClickListener{
	
	TextView loginScreen, email, result;
	Button register;
	private ProgressDialog pDialog;
	private static final String RECOVERY_URL = "http://feedseries.herokuapp.com/recoveryPass";//only email
	JSONParser jsonParser = new JSONParser();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.main_recovery);
        
        loginScreen = (TextView) findViewById(R.id.link_to_login_recovery);
        email = (TextView) findViewById(R.id.email_recovery);
        register = (Button) findViewById(R.id.btnRecovery);
        result = (TextView) findViewById(R.id.resultRecovery);
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
        
        loginScreen.setOnClickListener(this);
        register.setOnClickListener(this);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
    }

	public void onClick(View view) {
		result.setText(""); 
		if(view == loginScreen){
			finish();
		}else if (view == register){
			if(email.getText() != "" && email.getText() != null){
		        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
				Boolean isInternetPresent = cd.isConnectingToInternet();
				
				if(isInternetPresent){
					new postRecovery().execute();
				}else{
					showAlert();
				}
			}else{
				result.setText("Email can not be empty"); 
			}
		}
	}
	
	 class postRecovery extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RecoveryPassActivity.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			try {
		    	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		    	params.add(new BasicNameValuePair("email", email.getText().toString()));
		    	jsonParser.makeHttpRequest(RECOVERY_URL, "POST",
						params);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
    		finish();
		}
	 }
	
    
    @SuppressWarnings("deprecation")
	private void showAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(
                RecoveryPassActivity.this).create();
 
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
}
