package com.fedorvlasov.lazylist;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.fedorvlasov.lazylist.NewsActivity.LoadShows;
import com.google.android.gcm.demo.app.ConnectionDetector;
import com.google.android.gcm.demo.app.R;
import com.menu.JSONParser;
import com.menu.ProfileActivity;

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
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		
		if(isInternetPresent){
			new LoadShows().execute();
		}else{
			showAlert();
		}
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	    		Intent i = new Intent(getApplicationContext(), MyShowDescription.class);
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
//	    		startActivity(i);
	    	    startActivityForResult(i, 1);
			}
		});	
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {     
    	  super.onActivityResult(requestCode, resultCode, data);
    	  new LoadShows().execute();
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
    
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch (item.getItemId())
        {
        case R.id.menu_bookmark:
        	Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        	startActivity(i);
//    		finish();
        	// Single menu item is selected do something
        	// Ex: launching new activity/screen or show alert message
//            Toast.makeText(AndroidMenusActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
            return true;
        case R.id.menu_save:
            ImageLoader cache = new ImageLoader(getApplicationContext());
            cache.clearCache();
            
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        	Editor editor = pref.edit();
        	
        	editor.putString("email", null);
        	editor.commit();
        	
        	finish();
        	System.exit(0);
//        	Toast.makeText(AndroidMenusActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @SuppressWarnings("deprecation")
	private void showAlert() {
		AlertDialog alertDialog = new AlertDialog.Builder(
                MyShowActivity.this).create();
 
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