package com.fedorvlasov.lazylist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.menu.JSONParser;
import com.menu.ProfileActivity;
import com.wabila.app.ConnectionDetector;
import com.wabila.app.R;
import com.wabila.app.ServerUtilities;

public class NewsActivity extends Activity {
	    
	    private static final String NEWS_URL = "http://feedseries.herokuapp.com/getMessages";
	    private static final String MESSAGE_DELELTE_URL = "http://feedseries.herokuapp.com/messageDeleteToUser";
		ListView list;
		private ProgressDialog pDialog;
		int offset = 0;
		int limit = 10;
	    NewsLazyAdapter adapter;
	    String messageId;
	    // Creating JSON Parser object
	 	JSONParser jsonParser = new JSONParser();
	 	JSONObject json = new JSONObject();
	 	Button btnShoeMore;
	 	AsyncTask<Void, Void, Void> mRegisterTask;
	    
	    	
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.news_list_delete);
	        
	        list=(ListView)findViewById(R.id.list);

	        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
			Boolean isInternetPresent = cd.isConnectingToInternet();
			
			if(isInternetPresent){
				new LoadShows().execute();
			}else{
				showAlert();
			}
//	        Button b=(Button)findViewById(R.id.button1);
//	        b.setOnClickListener(listener);
	        
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
			
	        // Click event for single list row
	        list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					JSONObject aux;
					try {
						aux = (JSONObject) json.getJSONArray("data").get(position);
						messageId = aux.getString("messageId");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewsActivity.this);
			        // Setting Dialog Title
			        alertDialog.setTitle(R.string.title_delete_message);
			        // Setting Dialog Message
			        alertDialog.setMessage(R.string.text_message_alert);
			 
			        // Setting Positive "Yes" Button
			        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
			            	new messageDelete().execute(messageId);
			            }
			        });
			 
			        // Setting Negative "NO" Button
			        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            dialog.cancel();
			            }
			        });
			        alertDialog.show();
//		    		Intent i = new Intent(getApplicationContext(), MyShowDescription.class);
//		    		Bundle b = new Bundle();    
//		    		JSONObject data = null;
//					try {
//						data = (JSONObject) json.getJSONArray("data").get(position);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//		    	    b.putString("JSONArray",data.toString());
//		    	    i.putExtras(b);
//		    		startActivity(i);

				}
			});		
//	        list.setOnScrollListener(new OnScrollListener() {
//
//				@Override
//			    public void onScroll(AbsListView view, int firstVisibleItem, 
//			            int visibleItemCount, int totalItemCount) {
//			            //Check if the last view is visible
//			            if (++firstVisibleItem + visibleItemCount > totalItemCount) {
//			                offset = offset + limit;
//			                
//			            }
//			        }
//
//				@Override
//				public void onScrollStateChanged(AbsListView view, int scrollState) {
//					// TODO Auto-generated method stub
//					
//				}
//
//	        });
	        
	    }
	    @SuppressWarnings("deprecation")
		private void showAlert() {
			AlertDialog alertDialog = new AlertDialog.Builder(
                    NewsActivity.this).create();
	 
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
		private void setLoadLazyAdapter() throws JSONException{
	    	new LoadShows().execute();
	    }
	    private void setLazyAdapter() throws JSONException{
	    	adapter=new NewsLazyAdapter(this, json.getJSONArray("data"));
//	    	adapter.notifyDataSetChanged();
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
				pDialog.setMessage("Loading messages ...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
	    	
			@Override
			protected String doInBackground(String... arg0) {
				SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
				
				if(offset > 1){
					try {
						JSONArray jsonarray = json.getJSONArray("data");
						JSONArray jsonData = jsonParser.makeHttpRequest(NEWS_URL+"?limit="+limit+"&offset="+offset+"&email="+pref.getString("email", null), "GET",
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
					json = jsonParser.makeHttpRequest(NEWS_URL+"?limit="+limit+"&offset="+offset+"&email="+pref.getString("email", null), "GET",
							null);
				}
				
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
	    
		public void DeleteUserMessage(String messageId) {
			new messageDelete().execute(messageId);
			
		}
		
		
	    class messageDelete extends AsyncTask<String, String, String> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(NewsActivity.this);
				pDialog.setMessage("Deleting message...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
			@Override
			protected String doInBackground(String... message) {
				
		        json = jsonParser.makeHttpRequest(MESSAGE_DELELTE_URL+"?messageId="+message[0], "DELETE",
						null);
		        offset=0;
				Log.d("Outbox JSON: ", json.toString());
				return null;
			}
			protected void onPostExecute(String file_url) {
				try {
					pDialog.dismiss();
					setLoadLazyAdapter();
					list.setAdapter(adapter);
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
//	    		finish();
	        	// Single menu item is selected do something
	        	// Ex: launching new activity/screen or show alert message
//	            Toast.makeText(AndroidMenusActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.menu_save:
	            ImageLoader cache = new ImageLoader(getApplicationContext());
	            cache.clearCache();
	            
	            GCMRegistrar.checkDevice(this);
	            GCMRegistrar.checkManifest(this);
	            
	            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
	            
	            final String regId = GCMRegistrar.getRegistrationId(this);
	            final String emailunregister = pref.getString("email", null);
	            mRegisterTask = new AsyncTask<Void, Void, Void>() {
	            	 protected Void doInBackground(Void... params) {
	            		 ServerUtilities.unregister(getApplicationContext(), regId, emailunregister);
	            		 return null;
	            		 
	            	 }
                     protected void onPostExecute(Void result) {
                         mRegisterTask = null;
                         finish();
         	        	 System.exit(0);
                     }
	            };
	            mRegisterTask.execute(null, null, null);
	            
	        	Editor editor = pref.edit();
	        	
	        	editor.putString("email", null);
	        	editor.commit();

	        	
//	        	Toast.makeText(AndroidMenusActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	}
