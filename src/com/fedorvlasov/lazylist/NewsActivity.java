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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gcm.demo.app.R;
import com.menu.JSONParser;
import com.menu.ProfileActivity;

public class NewsActivity extends Activity {
	    
	    private static final String NEWS_URL = "http://feedseries.herokuapp.com/getMessages?limit=10&offset=0";
		ListView list;
		private ProgressDialog pDialog;
		int offset = 0;
		int limit = 3;
	    LazyAdapter adapter;
	    // Creating JSON Parser object
	 	JSONParser jsonParser = new JSONParser();
	 	JSONObject json = new JSONObject();
	    
	    	
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.show_list_delete);
	        
	        list=(ListView)findViewById(R.id.list);
	        new LoadShows().execute();
	        
//	        Button b=(Button)findViewById(R.id.button1);
//	        b.setOnClickListener(listener);
	        
	        // Click event for single list row
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
		    		finish();

				}
			});		
	        list.setOnScrollListener(new OnScrollListener() {

				@Override
			    public void onScroll(AbsListView view, int firstVisibleItem, 
			            int visibleItemCount, int totalItemCount) {
			            //Check if the last view is visible
			            if (++firstVisibleItem + visibleItemCount > totalItemCount) {
			                offset = offset + limit;
			                
			            }
			        }

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					
				}

	        });
	        
	    }
	    private void setLazyAdapter() throws JSONException{
	    	adapter=new LazyAdapter(this, json.getJSONArray("data"));
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
	            finish();
	        }
	    };
	    
	    class LoadShows extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(NewsActivity.this);
				pDialog.setMessage("Loading Outbox ...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
	    	
			@Override
			protected String doInBackground(String... arg0) {
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		        json = jsonParser.makeHttpRequest(NEWS_URL+"&email="+pref.getString("email", null), "GET",
						null);
				Log.d("Outbox JSON: ", json.toString());
				
				return null;
			}
			
			protected void onPostExecute(String file_url) {
				try {
					setLazyAdapter();
					list.setAdapter(adapter);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pDialog.dismiss();
			}
	    }
	    
	    /* Initiating Menu XML file (menu.xml) */
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu)
	    {
	        MenuInflater menuInflater = getMenuInflater();
	        menuInflater.inflate(R.layout.menu, menu);
	        return true;
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
	    		finish();
	        	// Single menu item is selected do something
	        	// Ex: launching new activity/screen or show alert message
//	            Toast.makeText(AndroidMenusActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.menu_save:
//	        	Toast.makeText(AndroidMenusActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.menu_search:
//	        	Toast.makeText(AndroidMenusActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	}
