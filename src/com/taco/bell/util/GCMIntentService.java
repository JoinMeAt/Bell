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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";
    private Intent intent;
    
    public GCMIntentService() {
        super(Constants.GCM_SENDER_ID);
    }
    
    @Override
	public void onStart(Intent intent, int startId)
    {
    	this.intent = intent;
    	super.onStart(intent, startId);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.d(TAG, "Device registered: regId = " + registrationId);
        
        if( registrationId == null || registrationId.length() == 0 ) {
        	Log.d(TAG, "GCM Registrations failed, no ID");
        	return;
        }
        
        GCMUtilities.register(registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        //displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
        	GCMRegistrar.setRegisteredOnServer(context, false);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.d(TAG, "Ignoring unregister callback.");
        }
    }
    
    @Override
    public void onError(Context context, String errorId) {
        Log.d(TAG, "Received error: " + errorId);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "Received message");

        String message = intent.getExtras()
        		.getString("message");
        
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("gcm.received");
         
        broadcastIntent.putExtra("gcm.message", message);
         
        context.sendBroadcast(broadcastIntent);
    }

}
