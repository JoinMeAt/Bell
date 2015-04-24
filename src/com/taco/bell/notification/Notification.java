package com.taco.bell.notification;

import com.taco.bell.ServiceRequest;
import com.taco.bell.util.Serializer;

public class Notification {
	int id;
	String message;
	
	public Notification(int id, String message) {
		this.id = id;
		this.message = message;
	}
	
	public Notification(ServiceRequest sr) {
		this.id = Types.SERVICE_REQUEST;
		this.message = Serializer.serialize(sr);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public class Types {
		public static final int SERVICE_REQUEST = 1;
		public static final int SERVICE_COMPLETED = 2;
	}
}
