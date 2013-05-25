package com.menu;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.fedorvlasov.lazylist.MyShowActivity;
import com.fedorvlasov.lazylist.NewsActivity;
import com.fedorvlasov.lazylist.ShowActivity;
import com.google.android.gcm.demo.app.R;

public class AndroidTabAndListView extends TabActivity {
	// TabSpec Names
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainshows);
        
        TabHost tabHost = getTabHost();
        
        // Profile Tab
        TabSpec profileSpec = tabHost.newTabSpec("News");
        profileSpec.setIndicator("News", getResources().getDrawable(R.drawable.icon_profile));
        Intent profileIntent = new Intent(this, NewsActivity.class);
        profileSpec.setContent(profileIntent);

        // Outbox Tab
        TabSpec outboxSpec = tabHost.newTabSpec("My Shows");
        outboxSpec.setIndicator("My Shows", getResources().getDrawable(R.drawable.icon_outbox));
        Intent outboxIntent = new Intent(this, MyShowActivity.class);
        outboxSpec.setContent(outboxIntent);

        // Inbox Tab
        TabSpec inboxSpec = tabHost.newTabSpec("Shows");
        inboxSpec.setIndicator("Shows", getResources().getDrawable(R.drawable.icon_inbox));
        Intent inboxIntent = new Intent(this, ShowActivity.class);
        inboxSpec.setContent(inboxIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(profileSpec); // Adding Profile tab
        tabHost.addTab(outboxSpec); // Adding Outbox tab
        tabHost.addTab(inboxSpec); // Adding Inbox tab
    }
}