package com.taco.bell.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.taco.bell.R;
import com.taco.bell.Station;
import com.taco.bell.swipedismiss.SwipeDismissListViewTouchListener;
import com.taco.bell.swipedismiss.SwipeDismissListViewTouchListener.DismissCallbacks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServerActivity extends Activity {
	
	ListView listView;
	StationAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_server);
		
		listView = (ListView) findViewById(R.id.station_list);
		ArrayList<Station> stations = new ArrayList<Station>();
		
		Random rand = new Random();
		for( int i = 0; i < 4; i++ ) {
			stations.add(new Station(i+1, rand.nextInt(100),1));
		}
		
		adapter = new StationAdapter(this, R.layout.layout_station, stations);
		listView.setAdapter(adapter);
		
		// Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new DismissCallbacks() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
//                                    adapter.remove(adapter.getItem(position));
                                	adapter.getItem(position).removeServiceRequest();
                                	adapter.sort();
                                }
                                adapter.notifyDataSetChanged();
                            }

							@Override
							public boolean canDismiss(int position) {
								return true;
							}
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
		
		
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
