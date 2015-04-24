package com.taco.bell.activity;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.taco.bell.R;
import com.taco.bell.ServiceRequest;
import com.taco.bell.Station;
import com.taco.bell.util.Constants;
import com.taco.bell.util.TimeFormatter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationAdapter extends ArrayAdapter<Station> {
	LayoutInflater inflator = null;
	int layoutID;
	List<Station> stations;
	Context context;
	int requestCount;
	
	public StationAdapter(Context context, int resource, List<Station> objects) {
		super(context, resource, objects);
		this.context = context;
		layoutID = resource;
		stations = objects;
		requestCount = 0;
		for( Station s : objects ) {
			if( s.hasServiceRequest() )
				requestCount++;
		}
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return stations.size();
	}
	
	@Override
	public long getItemId(int position) {
		return stations.get(position).getStationID();
	}
	
	@Override
	public void add(Station station) {
		stations.add(station);
		if( station.hasServiceRequest() )
			requestCount++;
	}

	@Override
	public void insert(Station station, int index) {
		stations.add(index, station);
		if( station.hasServiceRequest() )
			requestCount++;
	}

	@Override
	public void remove(Station station) {
		stations.remove(station);
		if( station.hasServiceRequest() )
			requestCount--;
	}
	
	public void remove(int position) {
		Station s = stations.remove(position);

		if( s != null && s.hasServiceRequest() )
			requestCount--;
	}

	@Override
	public void clear() {
		stations.clear();
		requestCount = 0;
	}

	@Override
	public Station getItem(int position) {
		return stations.get(position);
	}

	public void sort() {
		Collections.sort(stations);
	}
	
	public boolean hasServiceRequests() {
		return requestCount > 0;
	}
	
	public boolean addRequest(ServiceRequest sr) {
		for( Station s : stations) {
			if( s.getStationID() == sr.getStationID() ) {
				s.setServiceRequest(sr);
				sort();
				requestCount++;
				return true;
			}
		}
		return false;
	}
	
	public long removeRequest(int position) {
		long id = stations.get(position).removeServiceRequest();
		if( id > 0 )
			requestCount--;
		return id;
	}
	
	public boolean removeRequest(ServiceRequest sr) {
		for( Station s : stations) {
			if( s.getStationID() == sr.getStationID() ) {
				s.setServiceRequest(null);
				sort();
				requestCount--;
				return true;
			}
		}
		return false;
	}
	
	public boolean viewShouldUpdate(int idx, long currentTimeUTC) {
		Station s = stations.get(idx);
		
		if( s.hasServiceRequest() )
			return false;
		
		long delta = -1;
		try { // null pointer for unknown reason, put in UI thread?
			delta = currentTimeUTC - s.getServiceRequest().getRequestTime();
		} catch(Exception e){
			return false;
		}
		
		long diff = Constants.SUBMIT_TIME - delta;
		if( diff > 0 && diff < 1000 ) 
			return true;

		diff = Constants.WAITING_TIME - delta;
		if( diff > 0 && diff < 1000 ) 
			return true;
		
		
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if( convertView == null ) {
			convertView = inflator.inflate(layoutID, null);
			
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.station_icon);
			holder.stationName = (TextView) convertView.findViewById(R.id.station_number);
			holder.stationText = (TextView) convertView.findViewById(R.id.station_text);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		int sn = stations.get(position).getStationNumber();
		String str = context.getString(R.string.table_number, sn);
		holder.stationName.setText(str);
		
		if( stations.get(position).hasServiceRequest() ) {
			long requestTime = new DateTime(stations.get(position)
					.getServiceRequest().getRequestTime()).withZone(DateTimeZone.getDefault()).getMillis();
			long diff =  System.currentTimeMillis() - requestTime;
			String request = TimeFormatter.SimpleTimeFormatter.print(diff);
			String color = "#FFFFFF";
			
			if( diff < Constants.SUBMIT_TIME ) {
				holder.icon.setImageResource(R.drawable.ic_server_submit);
				color = context.getResources().getString(R.string.submitted_color);
			} else if( diff < Constants.WAITING_TIME ) {
				holder.icon.setImageResource(R.drawable.ic_server_waiting);
				color = context.getResources().getString(R.string.waiting_color);
			} else {
				holder.icon.setImageResource(R.drawable.ic_server_late);
				color = context.getResources().getString(R.string.late_color);
			}
			
			holder.stationText.setText(Html.fromHtml(context.getString(R.string.guest_called, color, request)));
			holder.stationText.setVisibility(View.VISIBLE);
		} else {
			holder.stationText.setVisibility(View.INVISIBLE);
			holder.icon.setImageResource(R.drawable.ic_server_submit);
		}
		
		return convertView;
	}
	
	
	static class ViewHolder {
		ImageView icon;
		TextView stationName;
		TextView stationText;	
	}
}
