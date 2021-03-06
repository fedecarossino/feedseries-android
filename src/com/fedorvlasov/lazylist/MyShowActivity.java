package com.fedorvlasov.lazylist;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.menu.JSONParser;
import com.wabila.app.ConnectionDetector;
import com.wabila.app.R;

public class MyShowActivity extends Activity {
    
    private static final String MY_SHOWS_URL = "http://feedseries.herokuapp.com/getEpisodesByUser";
    private static final String MY_SHOWS_URL_SEARCH = "http://feedseries.herokuapp.com/getEpisodesByShow?title=";
    private static final String MY_SHOWS_DELELTE_URL = "http://feedseries.herokuapp.com/deleteUserShow";
	ListView list;
	String showIdDelete = "";
	private ProgressDialog pDialog;
    MyShowLazyAdapter adapter;
    EditText inputSearch;
    // Creating JSON Parser object
 	JSONParser jsonParser = new JSONParser();
 	JSONObject json = new JSONObject();
 	InputMethodManager keyboard;
	int offset = 0;
	int limit = 10;
	Button btnShoeMore;
 	
 	Button b;
    
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_list_delete);
        
        inputSearch = (EditText) findViewById(R.id.inputSearchMyShow);

    	keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
        
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
        
		// LoadMore button
		btnShoeMore = new Button(this);
		btnShoeMore.setText("Load More");
		btnShoeMore.setTextColor(Color.parseColor("#ff9800"));

		// Adding Load More button to lisview at bottom
		list.addFooterView(btnShoeMore);
		
		btnShoeMore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Starting a new async task
				offset=offset+limit;
		        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
				Boolean isInternetPresent = cd.isConnectingToInternet();
				
				if(isInternetPresent){
					new LoadShows().execute();
				}else{
					showAlert();
				}
			}
		});
        
        inputSearch.setOnKeyListener(new OnKeyListener() {
        	@Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                  // Perform action on key press
                	offset = 0;
                    ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            		Boolean isInternetPresent = cd.isConnectingToInternet();
            		
            		if(isInternetPresent){
            			new LoadShows().execute();
            		}else{
            			showAlert();
            		}
                	keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
                  return true;
                }
                return false;
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
			
			if(!inputSearch.getText().toString().equals("") && offset < 1){
				try {
					json = jsonParser.makeHttpRequest(MY_SHOWS_URL_SEARCH+URLEncoder.encode(inputSearch.getText().toString(), "utf-8")+"&limit="+limit+"&offset="+offset, "GET",
							null);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(!inputSearch.getText().toString().equals("") && offset > 1){
				try {
					JSONArray jsonarray = json.getJSONArray("data");
					JSONArray jsonData = jsonParser.makeHttpRequest(MY_SHOWS_URL_SEARCH+URLEncoder.encode(inputSearch.getText().toString(), "utf-8")+"&limit="+limit+"&offset="+offset, "GET",
							null).getJSONArray("data");
					for(int f = 0; f < jsonData.length(); f++){
						jsonarray.put(jsonData.get(f));
					}
					json = new JSONObject("data");
					json.put("data", jsonarray);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}else if(offset > 1){
				try {
					JSONArray jsonarray = json.getJSONArray("data");
					JSONArray jsonData = jsonParser.makeHttpRequest(MY_SHOWS_URL+"?limit="+limit+"&offset="+offset+"&email="+email, "GET",
							null).getJSONArray("data");
					for(int f = 0; f < jsonData.length(); f++){
						jsonarray.put(jsonData.get(f));
					}
					json = new JSONObject("data");
					json.put("data", jsonarray);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
			}else{
				json = jsonParser.makeHttpRequest(MY_SHOWS_URL+"?limit="+limit+"&offset="+offset+"&email="+email, "GET",
						null);
			}
			
			
			
			
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