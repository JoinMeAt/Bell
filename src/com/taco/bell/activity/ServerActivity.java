package com.taco.bell.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.taco.bell.R;
import com.taco.bell.Server;
import com.taco.bell.ServiceRequest;
import com.taco.bell.Station;
import com.taco.bell.notification.Notification;
import com.taco.bell.rest.RestMethodFactory;
import com.taco.bell.rest.RestMethod;
import com.taco.bell.swipedismiss.SwipeDismissListViewTouchListener;
import com.taco.bell.swipedismiss.SwipeDismissListViewTouchListener.DismissCallbacks;
import com.taco.bell.util.Constants;
import com.taco.bell.util.GCMUtilities;
import com.taco.bell.util.Logger;
import com.taco.bell.util.Serializer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class ServerActivity extends Activity {
	protected static final String TAG = "ServerActivity";
	
	ListView listView;
	StationAdapter adapter;
	
	IntentFilter gcmFilter;
	BroadcastReceiver gcmReceiver;
	Timer T = new Timer();
	TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
        	if( !adapter.hasServiceRequests() ) return;
        	
        	int firstView = listView.getFirstVisiblePosition();
        	int lastView = listView.getLastVisiblePosition();
        	
			long localTime = DateTime.now(DateTimeZone.UTC).getMillis();
			
			// do any visible views need to be updated?
			for( int i = firstView; i <= lastView; i++ ) {
				if( adapter.viewShouldUpdate(i, localTime)) {
					notifiyDataSetChanged();
					break;
				}
			}		    				
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_server);
		
		listView = (ListView) findViewById(R.id.station_list);
		ArrayList<Station> stations = new ArrayList<Station>();
		
		Random rand = new Random();
		for( int i = 0; i < 16; i++ ) {
			stations.add(new Station(i+1, rand.nextInt(100),1));
		}
		Collections.sort(stations);
		
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
                                	long id = adapter.removeRequest(position);
                                	Server me = (Server) Constants.ME;
                                	RestMethod method = RestMethodFactory.CompleteRequest(me.getServerID(), id);
                                	new CompleteRequestTask().execute(method);
                                }
                            	adapter.sort();
                            	notifiyDataSetChanged();
                            }

							@Override
							public boolean canDismiss(int position) {
								return adapter.getItem(position).hasServiceRequest();
							}
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
        

		T.scheduleAtFixedRate(timerTask, 1000, 1000);
		

        if( Constants.GCM_ID == null )
        	GCMUtilities.registerBackground(this);
	}
	
	protected void notifiyDataSetChanged() {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		        adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if( gcmFilter == null ) {
	        gcmFilter = new IntentFilter();
	        gcmFilter.addAction(Constants.GCM_MESSAGE_RX);
        }
		
		if( gcmReceiver == null )
			gcmReceiver =  new GcmBroadcastReceiver();

        registerReceiver(gcmReceiver, gcmFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(gcmReceiver);
		T.cancel();
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.setClass(ServerActivity.this, LoginActivity.class);
		startActivity(i);
		finish();
	}

	private class GcmBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			/* process GCM message */
			String newMessage = intent.getExtras().getString(Constants.GCM_MESSAGE);
			
			Notification msg = (Notification) Serializer.deserialize(newMessage, Notification.class);
			
			switch(msg.getId()) {
			case Notification.Types.SERVICE_REQUEST:
				ServiceRequest sr = (ServiceRequest) Serializer.deserialize(msg.getMessage(), ServiceRequest.class);
				if( adapter.addRequest(sr) ) {
					notifiyDataSetChanged();
				}
				break;
			default:
				Logger.d(TAG, "Unknown notification received: " + msg.getId());
			}
		}
	}	
	
	private class CompleteRequestTask extends AsyncTask<RestMethod, Void, String> {

		@Override
		protected String doInBackground(RestMethod... params) {
			String response = null;
			
			try {
				response = params[0].execute();
			} catch( Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}
	}
}
