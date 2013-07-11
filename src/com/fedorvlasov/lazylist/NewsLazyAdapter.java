package com.fedorvlasov.lazylist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.menu.JSONParser;
import com.wabila.app.R;

public class NewsLazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private JSONArray data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    JSONParser jsonParser = new JSONParser();
    
    private Activity context;
    
    
    public NewsLazyAdapter(Activity a, JSONArray d) {
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
	        TextView text=(TextView)vi.findViewById(R.id.titleNews);
//	        TextView season = (TextView) vi.findViewById(R.id.seasonNews);
	        TextView artist = (TextView) vi.findViewById(R.id.episodeTitleNews);
	        RatingBar rating = (RatingBar) vi.findViewById(R.id.ratingBarNews);
	        ImageView image=(ImageView)vi.findViewById(R.id.list_imageNews);
	        TextView firstAired = (TextView) vi.findViewById(R.id.firstAiredNews);
	        
	        Float rat = Float.parseFloat(jsonData.getString("rating"))/100*5;
	        
	        rating.setRating(rat);
	        firstAired.setText(jsonData.getString("firstAired"));
	        text.setText(jsonData.getString("showTitle")+" - "+jsonData.getString("number")+"x"+jsonData.getString("season"));
	        artist.setText(jsonData.getString("title"));
//	        season.setText(jsonData.getString("number")+"x"+jsonData.getString("season"));
	        
			imageLoader.DisplayImage(jsonData.getString("poster"), image);
			
//			ImageButton b=(ImageButton)vi.findViewById(R.id.buttonnoseguirNews);
//			b.setTag(jsonData.getString("messageId"));
//			b.setOnClickListener(new OnClickListener() {
//
//	    	   @Override
//	    	    public void onClick(View arg0) {
//	    		    String messageId = arg0.findViewById(R.id.buttonnoseguirNews).getTag().toString();
//	    		    NewsActivity fede = (NewsActivity) activity;
//	    		    fede.DeleteUserMessage(messageId);
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
    
}