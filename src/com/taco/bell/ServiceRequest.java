package com.taco.bell;

import org.joda.time.DateTime;

public class ServiceRequest {
	long requestID;
	long requesterID;
	long stationID;
	long stationNumber;
	long requestTime;
	String serverName;
	String note;

	public long getRequestID() {
		return requestID;
	}
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}
	public long getRequesterID() {
		return requesterID;
	}
	public void setRequesterID(long requesterID) {
		this.requesterID = requesterID;
	}
	public long getStationID() {
		return stationID;
	}
	public void setStationID(long stationID) {
		this.stationID = stationID;
	}
	public long getStationNumber() {
		return stationNumber;
	}
	public void setStationNumber(long stationNumber) {
		this.stationNumber = stationNumber;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public long getRequestTime() {
		return this.requestTime;
	}
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
	
}
