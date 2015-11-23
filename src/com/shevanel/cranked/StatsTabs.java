package com.shevanel.cranked;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

public class StatsTabs extends TabActivity {

	private View createTabView(String text, int paramInt2, int paramInt3) {

		View localView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		((TextView) localView.findViewById(R.id.tab_title)).setText(text);
		// ImageView localImageView = (ImageView)
		// localView.findViewById(R.id.tab_icon);

		return localView;
	}

	protected void initTab(String paramString, String paramText, int paramInt2,
			int paramInt3, Intent paramIntent) {
		TabHost localTabHost = getTabHost(); // The activity TabHost
		localTabHost.addTab(localTabHost.newTabSpec(paramString)
				.setIndicator(createTabView(paramText, paramInt3, paramInt2))
				.setContent(paramIntent));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		getTabHost().getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		Bundle extras = getIntent().getExtras();
		long id = extras.getLong("identifier");

		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, RideSummaryActivity.class);
		intent.putExtra("identifier", id);
		initTab("stats", "SUMMARY", 1, 1, intent);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, RideMapActivity.class);
		intent.putExtra("identifier", id);
		initTab("map", "MAP", 1, 1, intent);

		intent = new Intent();
		// AbstractChart chart = new ElevationChart();
		// intent = chart.execute(StatsTabs.this, id);
		intent = new Intent().setClass(StatsTabs.this,
				RideElevationActivity.class);
		intent.putExtra("type", "line");
		intent.putExtra("identifier", id);
		initTab("elevation", "CHARTS", 1, 1, intent);

	}
}
