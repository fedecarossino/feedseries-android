package com.menu;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.fedorvlasov.lazylist.MyShowActivity;
import com.fedorvlasov.lazylist.NewsActivity;
import com.fedorvlasov.lazylist.ShowActivity;
import com.wabila.app.R;

public class AndroidTabAndListView extends TabActivity {
	// TabSpec Names
	TabHost tabHost;
	SharedPreferences pref;
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainshows);
        
        tabHost = getTabHost();
        
        // Profile Tab
        TabSpec profileSpec = tabHost.newTabSpec("News");
        profileSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_news_large));
        Intent profileIntent = new Intent(this, NewsActivity.class);
        profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        profileSpec.setContent(profileIntent);

        // Outbox Tab
        TabSpec outboxSpec = tabHost.newTabSpec("My Shows");
        outboxSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_my_series_large));
        Intent outboxIntent = new Intent(this, MyShowActivity.class);
        outboxIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        outboxSpec.setContent(outboxIntent);

        // Inbox Tab
        TabSpec inboxSpec = tabHost.newTabSpec("Shows");
        inboxSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_series_large));
        Intent inboxIntent = new Intent(this, ShowActivity.class);
//        inboxIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        inboxSpec.setContent(inboxIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(profileSpec); // Adding Profile tab
        tabHost.addTab(outboxSpec); // Adding Outbox tab
        tabHost.addTab(inboxSpec); // Adding Inbox tab
        
        tabHost.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String arg0) {
//				pref = getApplicationContext().getSharedPreferences("MyPref", 0);
//				String newShow = pref.getString("nuevaSerie", null);
//				if(newShow != null && arg0.contains("My Shows")){
//					
//					LocalActivityManager manager = getLocalActivityManager();
//					String currentTag = tabHost.getCurrentTabTag();
//					manager.destroyActivity(currentTag, true);
//					manager.startActivity(currentTag, new Intent(tabHost.getContext(), MyShowActivity.class));
//					
//					Editor editor = pref.edit();
//					editor.putString("nuevaSerie", null);
//					editor.commit();
//				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            imm.hideSoftInputFromWindow(tabHost.getApplicationWindowToken(), 0);
			}
        	
        });
    }
}