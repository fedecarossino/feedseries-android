package com.fedorvlasov.lazylist;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.demo.app.R;
import com.login.AndroidLogin;
import com.menu.JSONParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowDescription extends Activity implements OnClickListener{
	
	Button seguir;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private static final String HOOK_URL = "http://feedseries.herokuapp.com/newUserShow";
	private String email = "";
	private String showId = "";
	SharedPreferences pref;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.show_descrip);
        
        TextView title=(TextView)findViewById(R.id.descriptitle);
        TextView desc=(TextView)findViewById(R.id.description);
        ImageView image=(ImageView)findViewById(R.id.imageshowdescrip);
        
        seguir = (Button)findViewById(R.id.buttonseguir);
        seguir.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        String json=b.getString("JSONArray");
        JSONObject jsonObject = new JSONObject();
        try {
			jsonObject = new JSONObject(json);
			showId = jsonObject.getString("showId");
			pref = getApplicationContext().getSharedPreferences("MyPref", 0);
			email = pref.getString("email", null);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			title.setText(jsonObject.getString("showTitle"));
			desc.setText(jsonObject.getString("showOverview"));
			
			ImageLoader imgLoader = new ImageLoader(getApplicationContext());
			imgLoader.DisplayImage(jsonObject.getString("banner"), image);
//			image.setImageURI(null);
//			image.setImageURI(Uri.parse(jsonObject.getString("banner")));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
    class seguir extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShowDescription.this);
			pDialog.setMessage("Saving info ...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		protected String doInBackground(String... args) {
			try {
				postSeguir();
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
    
    private void postSeguir(){
    	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    	params.add(new BasicNameValuePair("showId", showId));
    	params.add(new BasicNameValuePair("email", email));
    	jsonParser.makeHttpRequest(HOOK_URL, "POST",
				params);
    	
//    	Editor editor = pref.edit();
//    	editor.putString("nuevaSerie", "true");
//    	editor.commit();
    }

	@Override
	public void onClick(View view) {
		if(view == seguir){
			try {
				new seguir().execute();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
