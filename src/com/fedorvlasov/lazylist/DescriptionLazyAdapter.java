package com.fedorvlasov.lazylist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fedorvlasov.lazylist.MyShowActivity.userShowDelete;
import com.google.android.gcm.demo.app.R;
import com.menu.JSONParser;

public class DescriptionLazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private JSONArray data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    JSONParser jsonParser = new JSONParser();
    
    private Activity context;
    
    
    public DescriptionLazyAdapter(Activity a, JSONArray d) {
        activity = a;
        data=d;
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
            vi = inflater.inflate(R.layout.news_list_row_delete, null);

        try {
	        JSONObject jsonData = (JSONObject) data.get(position);
	        TextView text=(TextView)vi.findViewById(R.id.title);
	        TextView season = (TextView) vi.findViewById(R.id.seasonMyShow);
	        TextView artist = (TextView) vi.findViewById(R.id.episodeTitle);
	        RatingBar rating = (RatingBar) vi.findViewById(R.id.ratingBarMyShow);
	        ImageView image=(ImageView)vi.findViewById(R.id.list_image);
	        TextView firstAired = (TextView) vi.findViewById(R.id.firstAiredMyShow);
	        
	        Float rat = Float.parseFloat(jsonData.getString("rating"))/100*5;
	        
	        rating.setRating(rat);
	        firstAired.setText(jsonData.getString("firstAired"));
	        text.setText(jsonData.getString("showTitle"));
	        artist.setText(jsonData.getString("title"));
	        season.setText(jsonData.getString("number")+"x"+jsonData.getString("season"));
	        
			imageLoader.DisplayImage(jsonData.getString("poster"), image);
			
			ImageButton b=(ImageButton)vi.findViewById(R.id.buttonnoseguir);
			b.setTag(jsonData.getString("messageId"));
			b.setOnClickListener(new OnClickListener() {

	    	   @Override
	    	    public void onClick(View arg0) {
	    		    String messageId = arg0.findViewById(R.id.buttonnoseguir).getTag().toString();
	    		    NewsActivity fede = (NewsActivity) activity;
	    		    fede.DeleteUserMessage(messageId);
//	    		    new userShowDelete().execute(userEmail, showId);

	    	    }
		    });
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return vi;
    }
    
}