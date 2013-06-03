package com.fedorvlasov.lazylist;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gcm.demo.app.R;
import com.menu.JSONParser;

public class MyShowActivity extends Activity {
    
    private static final String MY_SHOWS_URL = "http://feedseries.herokuapp.com/getEpisodesByUser?limit=10&offset=0";
    private static final String MY_SHOWS_DELELTE_URL = "http://feedseries.herokuapp.com/deleteUserShow";
	ListView list;
	String showIdDelete = "";
	private ProgressDialog pDialog;
    MyShowLazyAdapter adapter;
    // Creating JSON Parser object
 	JSONParser jsonParser = new JSONParser();
 	JSONObject json = new JSONObject();
 	
 	Button b;
    
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_list_delete);
        
        list=(ListView)findViewById(R.id.list);
        new LoadShows().execute();
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	    		Intent i = new Intent(getApplicationContext(), ShowDescription.class);
	    		Bundle b = new Bundle();    
	    		JSONObject data = null;
				try {
					data = (JSONObject) json.getJSONArray("data").get(position);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	    b.putString("JSONArray",data.toString());
	    	    i.putExtras(b);
	    		startActivity(i);

			}
		});	
        
    }
    private void setLazyAdapter() throws JSONException{
    	SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		String email = pref.getString("email", null);
    	adapter=new MyShowLazyAdapter(this, json.getJSONArray("data"), email);
    }
    private void setLoadLazyAdapter() throws JSONException{
    	new LoadShows().execute();
    }
    @Override
    public void onDestroy()
    {
        list.setAdapter(null);
        super.onDestroy();
    }
    
    public OnClickListener listener=new OnClickListener(){
        @Override
        public void onClick(View arg0) {
            adapter.imageLoader.clearCache();
            adapter.notifyDataSetChanged();
        }
    };
    
    class LoadShows extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyShowActivity.this);
			pDialog.setMessage("Loading shows ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
    	
		@Override
		protected String doInBackground(String... arg0) {
			SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
			String email = pref.getString("email", null);
	        json = jsonParser.makeHttpRequest(MY_SHOWS_URL+"&email="+email, "GET",
					null);
			Log.d("Outbox JSON: ", json.toString());
			
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			try {
				setLazyAdapter();
				list.setAdapter(adapter);
				adapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pDialog.dismiss();
		}
    }

	public void DeleteUserShow(String email, String showId) {
		showIdDelete = showId;
		new userShowDelete().execute(email);
		
	}
	
	
    class userShowDelete extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... email) {
			
	        json = jsonParser.makeHttpRequest(MY_SHOWS_DELELTE_URL+"?email="+email[0]+"&showId="+showIdDelete, "DELETE",
					null);
			Log.d("Outbox JSON: ", json.toString());
			return null;
		}
		protected void onPostExecute(String file_url) {
			try {
				setLoadLazyAdapter();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}