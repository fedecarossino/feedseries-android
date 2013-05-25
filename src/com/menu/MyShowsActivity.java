package com.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.android.gcm.demo.app.R;

public class MyShowsActivity extends ListActivity {
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jsonParser = new JSONParser();

	ArrayList<HashMap<String, String>> myShowsList;

	// Outbox JSON url
	private static final String MY_SHOWS_URL = "http://feedseries.herokuapp.com/getEpisodesByUser?email=fede@gmail.com&limit=10&offset=0";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_shows_list);
		
		// Hashmap for ListView
        myShowsList = new ArrayList<HashMap<String, String>>();
 
        // Loading OUTBOX in Background Thread
        new LoadOutbox().execute();
	}

	/**
	 * Background Async Task to Load all OUTBOX messages by making HTTP Request
	 * */
	class LoadOutbox extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyShowsActivity.this);
			pDialog.setMessage("Loading Outbox ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Outbox JSON
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(MY_SHOWS_URL, "GET",
					params);

			// Check your log cat for JSON reponse
			Log.d("Outbox JSON: ", json.toString());

			try {
				JSONArray data = json.getJSONArray("data");
				// looping through All messages
				for (int i = 0; i < data.length(); i++) {
					JSONObject c = data.getJSONObject(i);

					// Storing each json item in variable
					String showTitle = c.getString("showTitle");
					String title = c.getString("title");
					String id = c.getString("id").split("-")[1];
					String firstAired = c.getString("firstAired");
					

					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put("showTitle", showTitle);
					map.put("title", title);
					map.put("firstAired", firstAired);
					map.put("id", id);

					// adding HashList to ArrayList
					myShowsList.add(map);
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
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							MyShowsActivity.this, myShowsList,
							R.layout.my_shows_list_item, new String[] { "showTitle", "title", "firstAired", "id"  },
							new int[] {R.id.showtitle, R.id.title, R.id.firstaired, R.id.number});
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}