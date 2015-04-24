package com.taco.bell.util;

import com.taco.bell.User;

public class Constants {
	
	public static boolean DEBUG = false;
	public static String DEVICE_UUID = null;

	public static final String GCM_SENDER_ID = "461976639558";	
	
	public static User ME = null;
	
	/* ReST */
	public static final String DOMAIN = "https://app.joinmeatapp.com/Bell/";
	public static final String DEVICE_DOMAIN = DOMAIN + "device/";
	public static final String USER_DOMAIN = DOMAIN + "user/";
	public static final String REQUEST_DOMAIN = DOMAIN + "request/";
	
	public static final String GCM_MESSAGE_RX = "com.taco.bell.gcm";
	public static final String GCM_MESSAGE = "com.taco.bell.gcm.message";
	public static String GCM_ID = null;
	
	
	public static final long SUBMIT_TIME = 60000;
	public static final long WAITING_TIME =180000;
	
	
}
