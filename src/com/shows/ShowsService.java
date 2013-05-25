package com.shows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gcm.demo.app.R;

public class ShowsService extends Activity implements OnClickListener{
	Button ok,back,exit;
	TextView result;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shows);
        
        // Login button clicked
        ok = (Button)findViewById(R.id.showtable);
        ok.setOnClickListener(this);
        
        result = (TextView)findViewById(R.id.showstext);
        
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        result.setText(pref.getString("email", null));
        
    }
    
	public void getShowsAPI(){
		// http get client
        	HttpClient client=new DefaultHttpClient();
        	HttpGet getRequest=new HttpGet();

        	try {
        		// construct a URI object
				getRequest.setURI(new URI("http://feedSeries.herokuapp.com/getShows"));
			} catch (URISyntaxException e) {
				Log.e("URISyntaxException", e.toString());
			}

			// buffer reader to read the response
        	BufferedReader in=null;
        	// the service response
        	HttpResponse response=null;
			try {
				// execute the request
				response = client.execute(getRequest);
			} catch (ClientProtocolException e) {
				Log.e("ClientProtocolException", e.toString());
			} catch (IOException e) {
				Log.e("IO exception", e.toString());
			}
        	try {
				in=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			} catch (IllegalStateException e) {
				Log.e("IllegalStateException", e.toString());
			} catch (IOException e) {
				Log.e("IO exception", e.toString());
			}
        	StringBuffer buff=new StringBuffer("");
        	String line="";
        	try {
				while((line=in.readLine())!=null)
				{
					buff.append(line);
				}
			} catch (IOException e) {
				Log.e("IO exception", e.toString());
			}

        	try {
				in.close();
			} catch (IOException e) {
				Log.e("IO exception", e.toString());
			}
        	// response, need to be parsed
        	Log.e("JSON: ", buff.toString());
        	result.setText(buff.toString());
	}

	@Override
	public void onClick(View view) {
		if(view == ok){
			getShowsAPI();
		}
		
	}
}
