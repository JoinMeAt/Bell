package com.taco.bell.rest;

import java.net.URLEncoder;

import android.text.TextUtils;
import android.util.Log;

import static  com.taco.bell.util.Constants.*;

public class RestMethodFactory {
	
	/* Device ReST URLs */
	public static final String RegisterDeviceURL = DEVICE_DOMAIN + "register";
	public static final String UnregisterDeviceURL = DEVICE_DOMAIN + "unregister";
	
	/* User ReST URLs */
	public static final String LoginURL = USER_DOMAIN + "login";
	public static final String GuestLoginURL = USER_DOMAIN + "login/guest";
	
	/* Request ReST URLs */
	public static final String CreateRequestURL = REQUEST_DOMAIN + "create";
	public static final String CompleteRequestURL = REQUEST_DOMAIN + "complete";
	public static final String GetAllRequestsURL = REQUEST_DOMAIN + "all";
	
	
	public static RestMethod RegisterDevice(String deviceUUID, String messagerID ) {
		RestMethod method = null;
		
		String params = "deviceUUID=" + deviceUUID
				+ "&messagerID=" + messagerID
				+ "&deviceType=1";
		
		method = new RestMethod(RegisterDeviceURL, params, RestMethod.Types.POST);
		
		return method;
	}
	
	public static RestMethod UnregisterDevice(String messagerID ) {
		RestMethod method = null;
		
		String params = "messagerID=" + messagerID
				+ "&deviceType=1";
		
		method = new RestMethod(UnregisterDeviceURL, params, RestMethod.Types.POST);
		
		return method;
	}

	public static RestMethod Login(String email, String password) {
		RestMethod method = null;
		
		String params = "email=" + email
				+ "&password=" + password;
		
		method = new RestMethod(LoginURL, params, RestMethod.Types.GET);
		
		return method;
	}
	
	public static RestMethod GuestLogin(String deviceUUID) {
		RestMethod method = null;
		
		String params = "deviceUUID=" + deviceUUID;
		
		method = new RestMethod(GuestLoginURL, params, RestMethod.Types.POST);
		
		return method;
	}
	
	public static RestMethod RequestServer(String deviceUUID, String beaconID) {
		RestMethod method = null;
		
		String params = "deviceUUID=" + deviceUUID
				+ "&beaconID=" + beaconID;
		
		method = new RestMethod(CreateRequestURL, params, RestMethod.Types.POST);
		
		return method;
	}
	
	public static RestMethod CompleteRequest(long serverID, long requestID) {
		RestMethod method = null;
		
		String params = "serverID=" + Long.toString(serverID)
				+ "&requestID=" + Long.toString(requestID);
		
		method = new RestMethod(CompleteRequestURL, params, RestMethod.Types.POST);
		
		return method;
	}

	
	public static RestMethod GetAllRequests(long serverID) {
		RestMethod method = null;
		
		String params = "serverID=" + Long.toString(serverID);
		
		method = new RestMethod(GetAllRequestsURL, params, RestMethod.Types.GET);
		
		return method;
	}
	
}
