package com.fedorvlasov.lazylist;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.menu.JSONParser;
import com.wabila.app.R;

public class MyShowDescription extends Activity implements OnClickListener{
	
	Button seguir;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private static final String MY_SHOWS_DELELTE_URL = "http://feedseries.herokuapp.com/deleteUserShow";
	private String email = "";
	private String showId = "";
	SharedPreferences pref;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.my_show_descrip);
        
        TextView title=(TextView)findViewById(R.id.descriptitleMyShow);
        TextView desc=(TextView)findViewById(R.id.descriptionMyshow);
        ImageView image=(ImageView)findViewById(R.id.imageshowdescripMyShow);
        
        seguir = (Button)findViewById(R.id.buttonseguirMyShow);
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
			title.setText(jsonObject.getString("title"));
			desc.setText(jsonObject.getString("overview"));
			
			ImageLoader imgLoader = new ImageLoader(getApplicationContext());
			imgLoader.DisplayImage(jsonObject.getString("banner"), image);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
    class seguir extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyShowDescription.this);
			pDialog.setMessage("Saving info ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
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
    	jsonParser.makeHttpRequest(MY_SHOWS_DELELTE_URL+"?email="+email+"&showId="+showId, "DELETE",
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
