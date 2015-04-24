package com.taco.bell.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class Logger {	
	public static void d(String TAG, String msg) {
		if( msg != null && Constants.DEBUG )
			android.util.Log.d(TAG, msg);
		else
			android.util.Log.d(TAG, "log without text");
	}
	
	/**
	 * Only Logger.i will display if the DEBUG flag isn't set.
	 * @param TAG
	 * @param msg
	 */
	public static void i(String TAG, String msg) {
		if( msg != null )
			android.util.Log.i(TAG, msg);
		else
			android.util.Log.i(TAG, "log without text");
	}
	
	public static void wtf(String TAG, String msg) {
		if( msg != null && Constants.DEBUG  )
			android.util.Log.wtf(TAG, msg);
		else
			android.util.Log.wtf(TAG, "log without text");
	}
	
	public static void e(String TAG, String msg) {
		if( msg != null && Constants.DEBUG  )
			android.util.Log.e(TAG, msg);
		else
			android.util.Log.e(TAG, "log without text");
	}
}
