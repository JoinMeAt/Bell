package com.taco.bell;

public class Station implements Comparable<Station> {
	long stationID;
	int stationNumber;
	ServiceRequest request;
	long primaryServer;
	
	public Station(long stationID, int stationNumber, long primaryServer) {
		this.stationID = stationID;
		this.stationNumber = stationNumber;
		this.primaryServer = primaryServer;
	}

	public long getStationID() {
		return stationID;
	}

	public void setStationID(long stationID) {
		this.stationID = stationID;
	}

	public int getStationNumber() {
		return stationNumber;
	}

	public void setStationNumber(int stationNumber) {
		this.stationNumber = stationNumber;
	}

	public ServiceRequest getServiceRequest() {
		return request;
	}

	public void setServiceRequest(ServiceRequest request) {
		this.request = request;
	}
	
	public void removeServiceRequest() {
		this.request = null;
	}
	
	public boolean hasServiceRequest() {
		return this.request != null;
	}

	public long getPrimaryServer() {
		return primaryServer;
	}

	public void setPrimaryServer(long primaryServer) {
		this.primaryServer = primaryServer;
	}
	
	public String toString() {
		String str = stationNumber + ": " + primaryServer;
		if( request != null )
			str += " {has request)";
		return str;
	}

	@Override
	public int compareTo(Station another) {
		// equal if the stationID or station Nubmer are the same
		if( this.stationID == another.stationID
				|| this.stationNumber == another.stationNumber )  {
			return 0;
		}
		
		if( this.hasServiceRequest() && !another.hasServiceRequest() ) { // service requests have priority
			return -1;
		} else if( !this.hasServiceRequest() && another.hasServiceRequest() ) {
			return 1;
		} else if( this.hasServiceRequest() && another.hasServiceRequest() ) { // oldest requests have priority
			return this.request.requestTime > another.request.requestTime ? 1 : -1;
		} else { // rank by station number
			return this.stationNumber > another.stationNumber ? 1 : -1;
		}
	}
}
