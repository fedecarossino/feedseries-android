package com.fedorvlasov.lazylist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.menu.JSONParser;
import com.wabila.app.R;

public class MyShowLazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private JSONArray data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    private String userEmail = "";
    JSONParser jsonParser = new JSONParser();
    
    private Activity context;
    
    private static final String MY_SHOWS_DELELTE_URL = "http://feedseries.herokuapp.com/deleteUserShow";
    
    public MyShowLazyAdapter(Activity a, JSONArray d, String email) {
        activity = a;
        data=d;
        userEmail = email;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.length();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.show_list_row_delete, null);

        try {
	        JSONObject jsonData = (JSONObject) data.get(position);
	        TextView text=(TextView)vi.findViewById(R.id.title);
//	        TextView season = (TextView) vi.findViewById(R.id.seasonMyShow);
	        TextView artist = (TextView) vi.findViewById(R.id.episodeTitle);
	        RatingBar rating = (RatingBar) vi.findViewById(R.id.ratingBarMyShow);
	        ImageView image=(ImageView)vi.findViewById(R.id.list_image);
	        TextView firstAired = (TextView) vi.findViewById(R.id.firstAiredMyShow);
	        
	        Float rat = Float.parseFloat(jsonData.getString("rating"))/100*5;
	        
	        rating.setRating(rat);
	        firstAired.setText(jsonData.getString("firstAired"));
	        text.setText(jsonData.getString("showTitle")+" - "+jsonData.getString("number")+"x"+jsonData.getString("season"));
	        artist.setText(jsonData.getString("title"));
//	        season.setText(jsonData.getString("number")+"x"+jsonData.getString("season"));
	        
			imageLoader.DisplayImage(jsonData.getString("poster"), image);
			
//			ImageButton b=(ImageButton)vi.findViewById(R.id.buttonnoseguir);
//			b.setTag(jsonData.getString("showId"));
//			b.setOnClickListener(new OnClickListener() {
//
//	    	   @Override
//	    	    public void onClick(View arg0) {
//	    		    String showId = arg0.findViewById(R.id.buttonnoseguir).getTag().toString();
//	    		    MyShowActivity fede = (MyShowActivity) activity;
//	    		    fede.DeleteUserShow(userEmail, showId);
////	    		    new userShowDelete().execute(userEmail, showId);
//
//	    	    }
//		    });
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return vi;
    }
    
    class userShowDelete extends AsyncTask<String, String, String> {

    	
		@Override
		protected String doInBackground(String... dataAsync) {
			
	        JSONObject json = jsonParser.makeHttpRequest(MY_SHOWS_DELELTE_URL+"?email="+dataAsync[0]+"&showId="+dataAsync[1], "DELETE",
					null);
			Log.d("Outbox JSON: ", json.toString());
			
			return null;
		}
		

    }
    
    
}