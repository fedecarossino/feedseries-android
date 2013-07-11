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

import com.wabila.app.R;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private JSONArray data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, JSONArray d) {
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
            vi = inflater.inflate(R.layout.show_list_row, null);

        try {
	        JSONObject jsonData = (JSONObject) data.get(position);
	        
	        TextView text=(TextView)vi.findViewById(R.id.title);
//	        TextView season = (TextView) vi.findViewById(R.id.duration);
	        ImageView image=(ImageView)vi.findViewById(R.id.list_image);
	        TextView artist = (TextView) vi.findViewById(R.id.artist);
	        TextView firstAires = (TextView) vi.findViewById(R.id.firstAired);
	        RatingBar rating = (RatingBar) vi.findViewById(R.id.ratingBarShow);
	        Float rat = Float.parseFloat(jsonData.getString("showRating"))/100*5;
	        rating.setRating(rat);

	        firstAires.setText("Season "+jsonData.getString("season"));
	        artist.setText("Episode "+ jsonData.getString("number"));
	        text.setText(jsonData.getString("showTitle")+" - "+jsonData.getString("number")+"x"+jsonData.getString("season"));
//	        season.setText(jsonData.getString("number")+"x"+jsonData.getString("season"));
			imageLoader.DisplayImage(jsonData.getString("poster"), image);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return vi;
    }
}