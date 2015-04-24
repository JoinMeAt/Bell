/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taco.bell.util;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taco.bell.util.Logger;
import com.taco.bell.rest.RestMethod;
import com.taco.bell.rest.RestMethodFactory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public final class GCMUtilities {

    private static final String TAG = "GCMUtilities";
	static GoogleCloudMessaging gcm = null;
    
    public static boolean register(final String regId) {
        Logger.d(TAG, "Calling GCMUtilities.register: gcmID = " + regId);

        try {
        	RestMethod gcmQuery = RestMethodFactory.RegisterDevice(Constants.DEVICE_UUID, regId);
            new GCMRegisterTask().execute(gcmQuery);
            
        	return true;
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    }

	private static class GCMRegisterTask extends AsyncTask<RestMethod, Void, String> {
		private static final String TAG = "GCMRegisterQuery";
		
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(RestMethod... cmq) {
			Logger.d(TAG,"doInBackground ...");
			Logger.d(TAG,"cmQuery: " + cmq[0].toString());
			
			String response = null;
			try {
				response = cmq[0].execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Logger.d(TAG, "GCMRQuery response: " + result);
		}
	}	
	
	public static void unregister(String gcmID) {
        Logger.d(TAG, "Calling GCMUtilities.unregister: gcmID = " + gcmID);

    	RestMethod gcmQuery = RestMethodFactory.UnregisterDevice(gcmID);
        new GCMUnregisterTask().execute(gcmQuery);
	}
	
	private static class GCMUnregisterTask extends AsyncTask<RestMethod, Void, String> {

		@Override
		protected String doInBackground(RestMethod... params) {
			String result = null;
			
			try {
				result = params[0].execute();
			} catch( Exception e ) {
				e.printStackTrace();
			}
			
			return result;
		}
	}
	
	public static void registerBackground(final Context context) {
	    new AsyncTask<Void, Void, Boolean>() {
			@Override
	        protected Boolean doInBackground(Void... params) {
	            try {
	                if (GCMUtilities.gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                Constants.GCM_ID = gcm.register(Constants.GCM_SENDER_ID);	                
	                
	            } catch (Exception ex) {
	            	return false;
	            }
	            return true;
	        }

	        @Override
	        protected void onPostExecute(Boolean pass) {
	        	String msg = "Device registered, registration id=" + Constants.GCM_ID;
	            Logger.d("gcm.onPostExecute", msg + "\n");

//	            register(Constants.GCM_ID);
	        }
	    }.execute(null, null, null);
	}
}
