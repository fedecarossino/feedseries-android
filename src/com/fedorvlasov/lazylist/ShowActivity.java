package com.fedorvlasov.lazylist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gcm.demo.app.R;
import com.menu.AndroidMenusActivity;
import com.menu.AndroidTabAndListView;
import com.menu.JSONParser;
import com.menu.MyShowsActivity;
import com.menu.ProfileActivity;

public class ShowActivity extends Activity {
    
    private static final String MY_SHOWS_URL = "http://feedseries.herokuapp.com/getEpisodes";
    private static final String MY_SHOWS_URL_SEARCH = "http://feedseries.herokuapp.com/getEpisodesByShow?title=";
	ListView list;
	private ProgressDialog pDialog;
	int offset = 0;
	int limit = 3;
    LazyAdapter adapter;
    // Creating JSON Parser object
 	JSONParser jsonParser = new JSONParser();
 	JSONObject json = new JSONObject();
 	EditText inputSearch;
 	InputMethodManager keyboard;
 	Button btnShoeMore;
    
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_list);
        
        list=(ListView)findViewById(R.id.list);
        inputSearch = (EditText) findViewById(R.id.inputSearchShow);

    	keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
        
        new LoadShows().execute();
        
//        Button b=(Button)findViewById(R.id.button1);
//        b.setOnClickListener(listener);
        
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
				new LoadShows().execute();
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
                	new LoadShows().execute();
                	keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
                  return true;
                }
                return false;
            }

        });
//        list.setOnScrollListener(new OnScrollListener() {
//
//			@Override
//		    public void onScroll(AbsListView view, int firstVisibleItem, 
//		            int visibleItemCount, int totalItemCount) {
//		            //Check if the last view is visible
//		            if (++firstVisibleItem + visibleItemCount > totalItemCount) {
//		                offset = offset + limit;
//		                
//		            }
//		        }
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// TODO Auto-generated method stub
//				offset = offset + limit;
//				
//			}
//
//        });
        
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
			pDialog = new ProgressDialog(ShowActivity.this);
			pDialog.setMessage("Loading shows ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
    	
		@Override
		protected String doInBackground(String... arg0) {
			if(!inputSearch.getText().toString().equals("") && offset < 1){
				json = jsonParser.makeHttpRequest(MY_SHOWS_URL_SEARCH+inputSearch.getText().toString()+"&limit="+limit+"&offset="+offset, "GET",
						null);
			}else if(!inputSearch.getText().toString().equals("") && offset > 1){
				try {
					JSONArray jsonarray = json.getJSONArray("data");
					JSONArray jsonData = jsonParser.makeHttpRequest(MY_SHOWS_URL_SEARCH+inputSearch.getText().toString()+"&limit="+limit+"&offset="+offset, "GET",
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
			}else if(offset > 1){
				try {
					JSONArray jsonarray = json.getJSONArray("data");
					JSONArray jsonData = jsonParser.makeHttpRequest(MY_SHOWS_URL+"?limit="+limit+"&offset="+offset, "GET",
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
				json = jsonParser.makeHttpRequest(MY_SHOWS_URL+"?limit="+limit+"&offset="+offset, "GET",
						null);
			}
			Log.d("Outbox JSON: ", json.toString());
			
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			try {
				setLazyAdapter();
				list.setAdapter(adapter);
				list.setSelection(offset);
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
}