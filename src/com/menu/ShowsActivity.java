package com.menu;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.android.gcm.demo.app.R;

public class ShowsActivity extends ListActivity {
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> showsList;


	private static final String SHOWS_EPISODES_URL = "http://feedseries.herokuapp.com/getEpisodes";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shows_list);
		
		// Hashmap for ListView
        showsList = new ArrayList<HashMap<String, String>>();
 
        // Loading INBOX in Background Thread
        new LoadInbox().execute();
	}

	/**
	 * Background Async Task to Load all INBOX messages by making HTTP Request
	 * */
	class LoadInbox extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShowsActivity.this);
			pDialog.setMessage("Loading Inbox ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			JSONObject json = jsonParser.makeHttpRequest(SHOWS_EPISODES_URL, "GET",
					params);

			Log.d("Inbox JSON: ", json.toString());

			try {
				JSONArray data = json.getJSONArray("data");
				for (int i = 0; i < data.length(); i++) {
					JSONObject c = data.getJSONObject(i);

					String showTitle = c.getString("showTitle");
					String title = c.getString("title");
					String id = c.getString("id").split("-")[1];
					String firstAired = c.getString("firstAired");
					String banner = c.getString("banner");

					HashMap<String, String> map = new HashMap<String, String>();

					map.put("image", banner);
					map.put("showTitle", showTitle);
					map.put("title", title);
					map.put("firstAired", firstAired);
					map.put("id", id);

					showsList.add(map);
					
//					ImageView il = (ImageView) findViewById(R.id.imageid);
//
//					try {
//					        URL url = new URL(banner);
//					        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//					        connection.setDoInput(true);
//					        connection.connect();
//					        InputStream input = connection.getInputStream();
//					        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//					        il.setImageBitmap(myBitmap);
//					    } catch (IOException e) {
//					        e.printStackTrace();
//					    }
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							ShowsActivity.this, showsList,
							R.layout.shows_list_item, new String[] { "image", "showTitle", "title", "firstAired", "id" },
							new int[] {R.id.imageid, R.id.showtitle, R.id.title, R.id.firstaired, R.id.number});
					setListAdapter(adapter);
				}
			});

		}

	}
}
