package com.shevanel.cranked;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class CrankedTabs extends TabActivity {

	private View createTabView(String text, final int paramInt2,
			final int paramInt3) {

		View localView = LayoutInflater.from(this).inflate(R.layout.tab, null);
		((TextView) localView.findViewById(R.id.tab_title)).setText(text);
		ImageView localImageView = (ImageView) localView
				.findViewById(R.id.tab_icon);

		StateListDrawable localStateListDrawable = new StateListDrawable();

		int[] arrayOfInt1 = new int[1];
		arrayOfInt1[0] = 16842913;
		localStateListDrawable.addState(arrayOfInt1, getResources()
				.getDrawable(R.drawable.ic_menu_chart));
		int[] arrayOfInt2 = new int[1];
		arrayOfInt2[0] = 16842919;
		localStateListDrawable.addState(arrayOfInt2, getResources()
				.getDrawable(R.drawable.ic_menu_chart));
		int[] arrayOfInt3 = new int[1];
		arrayOfInt3[0] = 16842908;
		localStateListDrawable.addState(arrayOfInt3, getResources()
				.getDrawable(R.drawable.ic_menu_chart));

		int[] arrayOfInt4 = new int[3];
		arrayOfInt4[0] = -16842913;
		arrayOfInt4[1] = -16842919;
		arrayOfInt4[2] = -16842908;

		localStateListDrawable.addState(arrayOfInt4, getResources()
				.getDrawable(R.drawable.ic_menu_database));

		localImageView.setImageDrawable(localStateListDrawable);

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

		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, MainActivity.class);
		initTab("new", "NEW RIDE", 1, 1, intent);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, SavedRidesActivity.class);
		initTab("saved", "SAVED RIDES", 1, 1, intent);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		TabHost tabHost = getTabHost(); // The activity TabHost

		outState.putString("tab", tabHost.getCurrentTabTag());
	}

}