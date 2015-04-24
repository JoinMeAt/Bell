package com.taco.bell.activity;

import com.taco.bell.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ServerActivity extends Activity {
	
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_server);
		
		listView = (ListView) findViewById(R.id.station_list);
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	
}
