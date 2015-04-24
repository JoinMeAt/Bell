package com.taco.bell.activity;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTimeZone;

import com.taco.bell.R;
import com.taco.bell.Station;
import com.taco.bell.util.TimeFormatter;

import android.content.Context;
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
	
	public StationAdapter(Context context, int resource, List<Station> objects) {
		super(context, resource, objects);
		this.context = context;
		layoutID = resource;
		stations = objects;
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
	}

	@Override
	public void insert(Station object, int index) {
		stations.add(index, object);
	}

	@Override
	public void remove(Station object) {
		stations.remove(object);
	}
	
	public void remove(int position) {
		stations.remove(position);
	}

	@Override
	public void clear() {
		stations.clear();
	}

	@Override
	public Station getItem(int position) {
		return stations.get(position);
	}

	public void sort() {
		Collections.sort(stations);
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
			if( stations.get(position).hasServiceRequest() ) {
				holder.requestTimeLocal = 
						DateTimeZone.getDefault().convertUTCToLocal(
								stations.get(position).getServiceRequest().getRequestTime()
						);
			} else {
				holder.requestTimeLocal = -1;
			}
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.stationName.setText(context.getString(R.string.table_number, stations.get(position).getStationNumber()));
		
		if( stations.get(position).hasServiceRequest() ) {
			long diff =  System.currentTimeMillis() - holder.requestTimeLocal;
			String request = TimeFormatter.SimpleTimeFormatter.print(diff);
			String color = "#FFFFFF";
			
			if( diff < 60000 ) {
//				color = context.getString(R.string.submitted_color);
				convertView.setBackgroundColor(context.getResources().getColor(R.color.SUBMITTED));
			} else if( diff < 120000 ) {
//				color = context.getString(R.string.waiting_color);		
				convertView.setBackgroundColor(context.getResources().getColor(R.color.WAITING));
			} else {
//				color = context.getString(R.string.late_color);				
				convertView.setBackgroundColor(context.getResources().getColor(R.color.LATE));
			}
			
			holder.stationText.setText(context.getString(R.string.guest_called, color, request));
		} else {
			holder.stationText.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	
	static class ViewHolder {
		ImageView icon;
		TextView stationName;
		TextView stationText;	
		long requestTimeLocal;
	}
}
