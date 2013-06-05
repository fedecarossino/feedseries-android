package com.login;

import static com.google.android.gcm.demo.app.CommonUtilities.SENDER_ID;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fedorvlasov.lazylist.ShowActivity;
import com.fedorvlasov.lazylist.ShowDescription;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gcm.demo.app.ConnectionDetector;
import com.google.android.gcm.demo.app.R;
import com.google.android.gcm.demo.app.ServerUtilities;
import com.menu.AndroidTabAndListView;
import com.menu.JSONParser;


public class RegistrationActivity extends Activity implements OnClickListener{
	
	TextView loginScreen, email, pass1, pass2, result;
	Button register;
	private ProgressDialog pDialog;
	private static final String REGISTER_URL = "http://feedseries.herokuapp.com/newUser";
	JSONParser jsonParser = new JSONParser();
	SharedPreferences pref;
	AsyncTask<Void, Void, Void> mRegisterTask;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.main_register);
        
        loginScreen = (TextView) findViewById(R.id.link_to_login);
        email = (TextView) findViewById(R.id.reg_email);
        pass1 = (TextView) findViewById(R.id.reg_password);
        pass2 = (TextView) findViewById(R.id.reg_password_two);
        result = (TextView) findViewById(R.id.resultRegister);
        register = (Button) findViewById(R.id.btnRegister);
        
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);
        imm.showSoftInput(pass1, InputMethodManager.SHOW_IMPLICIT);
        imm.showSoftInput(pass2, InputMethodManager.SHOW_IMPLICIT);
        
        loginScreen.setOnClickListener(this);
        register.setOnClickListener(this);
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
    }

	public void onClick(View view) {
		result.setText(""); 
		if(view == loginScreen){
			finish();
		}else if (view == register){
			if(pass1.getText().toString().equals(pass2.getText().toString()) && pass1.getText() != "" && pass1.getText() != null){
		        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
				Boolean isInternetPresent = cd.isConnectingToInternet();
				
				if(isInternetPresent){
					new postResgiter().execute();
				}else{
					showAlert();
				}
			}else{
				result.setText("Password Error"); 
			}
		}
	}
	
	 class postResgiter extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegistrationActivity.this);
			pDialog.setMessage("Loading ...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			try {
				register();
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
			pDialog.dismiss();
    		finish();
		}
	 }
	
    private void register() throws JSONException{
    	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    	params.add(new BasicNameValuePair("pass", pass1.getText().toString()));
    	params.add(new BasicNameValuePair("email", email.getText().toString()));
    	params.add(new BasicNameValuePair("userFace", ""));
    	JSONObject json = jsonParser.makeHttpRequest(REGISTER_URL, "POST",
				params);
    	if(json.getString("message").contains("correctamente")){
    		registerGCM();
    	}else if(json.getString("message").contains("existe")){
    		result.setText("El mail ya esta registrado"); 
    	}else{
        	result.setText("Error en el login"); 
    	}
    	
    }

	private void registerGCM() {
    	Log.w("SENCIDE", "TRUE");
    	pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    	Editor editor = pref.edit();
    	
    	editor.putString("email", email.getText().toString());
    	editor.commit();
//    	String redId = postGCM();
    	String redId = "df";
    	if(redId != null && !redId.equals("")){
    		Intent i = new Intent(getApplicationContext(), AndroidTabAndListView.class);
    		startActivity(i);
    		finish();
    	}
		
	}
	
    private String postGCM(){
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
    
    @SuppressWarnings("deprecation")
	private void showAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(
                RegistrationActivity.this).create();
 
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
