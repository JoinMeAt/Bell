package com.taco.bell.activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.taco.bell.notification.Notification;
import com.taco.bell.rest.RestMethod;
import com.taco.bell.rest.RestMethodFactory;
import com.taco.bell.util.Constants;
import com.taco.bell.util.GCMUtilities;
import com.taco.bell.util.Logger;
import com.taco.bell.util.Serializer;
import com.taco.bell.util.TimeFormatter;
import com.taco.bell.R;
import com.taco.bell.Server;
import com.taco.bell.ServiceRequest;
import com.taco.bell.User;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnTouchListener {
	private static final String tag = "LoginActivity";
	
	private ImageView callWaiter;
	private ImageView waiterIcon;
	private TextView infoText;
	private TextView staffLogin;
	private TextView timerText;
	private EditText emailInput;
	private EditText passwordInput;
	
	private Animation rotate;
	private Animation shrink;
	
	private boolean isLoginReady = false;
	private boolean isScreenPressed = false;
	private boolean shrinkContinue = false;
	private ServiceRequest request;
	
	private IntentFilter gcmFilter;
	private GcmBroadcastReceiver gcmReceiver;
	private Timer T=new Timer();
	private final Handler handler = new Handler();
	private final Runnable checkPressed = new Runnable() {
		@Override
		public void run() {
			if( isScreenPressed ) {
				shrinkContinue = false;
	            new CallServerTask().execute();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		callWaiter = (ImageView) findViewById(R.id.call_waiter);
		waiterIcon = (ImageView) findViewById(R.id.icon_waiter);
		infoText = (TextView) findViewById(R.id.infoText);
		staffLogin = (TextView) findViewById(R.id.staff_login);
		timerText = (TextView) findViewById(R.id.timerText);
		timerText.setVisibility(View.INVISIBLE);
		emailInput = (EditText) findViewById(R.id.email_input);
		passwordInput = (EditText) findViewById(R.id.password_input);
		

		Constants.DEBUG = ( 0 != ( getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );		
		Constants.DEVICE_UUID =  Secure.getString(this.getContentResolver(),
		                Secure.ANDROID_ID);

		rotate = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.rotate);
		rotate.setFillAfter(true);
		
		shrink = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shrink);
		rotate.setFillAfter(true);
		
		waiterIcon.setOnTouchListener(this);
		staffLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( isLoginReady)
					login();
				else
					showLogin();
			}
		});
		
		hideLogin();

        if( Constants.GCM_ID == null )
        	GCMUtilities.registerBackground(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideLogin();

		if( gcmFilter == null ) {
	        gcmFilter = new IntentFilter();
	        gcmFilter.addAction(Constants.GCM_MESSAGE_RX);
        }
		
		if( gcmReceiver == null )
			gcmReceiver =  new GcmBroadcastReceiver();

        registerReceiver(gcmReceiver, gcmFilter);
        
        if( request != null )
        	startAnimation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unregisterReceiver(gcmReceiver);
	}

	@Override
	public void onBackPressed() {
		if( isLoginReady ) {
			hideLogin();
		} else {
			super.onBackPressed();
		}
	}

	private void hideLogin() {
		waiterIcon.setVisibility(View.VISIBLE);
		callWaiter.setVisibility(View.INVISIBLE);
		emailInput.setVisibility(View.GONE);
		passwordInput.setVisibility(View.GONE);
		staffLogin.setText(getString(R.string.staff_login));
		staffLogin.setTextColor(Color.WHITE);
		staffLogin.setVisibility(View.VISIBLE);
		timerText.setVisibility(View.INVISIBLE);
		infoText.setText(getString(R.string.instruction));
		
//		waiterIcon.clearAnimation();
//		waiterIcon.getLayoutParams().height = 150;
//		waiterIcon.getLayoutParams().width = 150;
		isLoginReady = false;
	}
	
	private void showLogin() {
		callWaiter.clearAnimation();
		waiterIcon.setVisibility(View.INVISIBLE);
		callWaiter.setVisibility(View.INVISIBLE);
		emailInput.setVisibility(View.VISIBLE);
		passwordInput.setVisibility(View.VISIBLE);
		staffLogin.setText(getString(R.string.login));
		staffLogin.setTextColor(Color.BLACK);
		infoText.setText(getString(R.string.enter_credentials));
		
		isLoginReady = true;
	}
	
	private void login() {
		RestMethod method = RestMethodFactory.Login(emailInput.getText().toString(), 
							passwordInput.getText().toString());
		new LoginTask().execute(method);		
	}
	
	private void startAnimation() {
		callWaiter.startAnimation(rotate);		
		timerText.setVisibility(View.VISIBLE);
		timerText.setText("00:00");
		infoText.setText(getString(R.string.server_called,request.getServerName()));
		callWaiter.setVisibility(View.VISIBLE);
		staffLogin.setVisibility(View.INVISIBLE);
	}
	
	private void stopAnimation() {
		callWaiter.clearAnimation();
		hideLogin();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    if (action == MotionEvent.ACTION_DOWN) {
	    	handler.postDelayed(checkPressed, 2000);
	        isScreenPressed = true;
//	        waiterIcon.startAnimation(shrink);
//	        shrinkContinue = true;
	    }
	    else if (action == MotionEvent.ACTION_UP) {
	    	handler.removeCallbacks(checkPressed);
	    	isScreenPressed = false;
//	    	if( shrinkContinue ) {
//	    		waiterIcon.clearAnimation();
//	    		shrinkContinue = false;
//	    	}
	    }
		return false;
	}
	
	private class GcmBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			/* process GCM message */
			String newMessage = intent.getExtras().getString(Constants.GCM_MESSAGE);
			
			Notification msg = (Notification) Serializer.deserialize(newMessage, Notification.class);
			
			Logger.d(tag,"GCM Broadcast: " + msg.getId());
			
			switch(msg.getId()) {
			case Notification.Types.SERVICE_COMPLETED:
				request = null;
				if( T != null )
					T.cancel();
				stopAnimation();
				
				break;
			default:
				Logger.d(tag, "Unknown notification received: " + msg.getId());
			}
		}
	}
	
	private class LoginTask extends AsyncTask<RestMethod, Void, String> {
		String restCall = null;

		@Override
		protected String doInBackground(RestMethod ... params) {
			Logger.d(tag, "Login in background");
			restCall = params[0].getURI();
			
			String response = null;
			try {
				response = params[0].execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return response;
			
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Logger.d("LoginTask", result);

			if( TextUtils.isEmpty(result) ) {
				Logger.d(tag, "Login no response");
				return;
			}			
			Constants.ME = (Server) Serializer.deserialize(result, Server.class);
			
			if( restCall.equals(RestMethodFactory.LoginURL)
					&& Constants.ME != null ) { // server login
				Intent i = new Intent();
				i.setClass(LoginActivity.this, ServerActivity.class);
				startActivity(i);
				LoginActivity.this.finish();
			} else if( restCall.equals(RestMethodFactory.GuestLoginURL ) ) {
				
			}
		}
	}
	
	private class CallServerTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			Logger.d("CallServerTask", result);
			
			request = (ServiceRequest) Serializer.deserialize(result, ServiceRequest.class);
			startAnimation();

			
			T = new Timer();
			T.scheduleAtFixedRate(new TimerTask() {
			        @Override
			        public void run() {
			            runOnUiThread(new Runnable()
			            {
			                @Override
			                public void run()
			                {
			                	if( request == null ) return;
			                	
			    				long localTime = DateTime.now(DateTimeZone.UTC).getMillis();
			    				
			    				long diff = localTime - request.getRequestTime(); 
			    				if( diff < Constants.SUBMIT_TIME ) {
			    					timerText.setTextColor(getResources().getColor(R.color.SUBMITTED));
			    				} else if( diff < Constants.WAITING_TIME ) {
			    					timerText.setTextColor(getResources().getColor(R.color.WAITING));
			    				} else {
			    					timerText.setTextColor(getResources().getColor(R.color.LATE));
			    				}
			                	
			                    timerText.setText(TimeFormatter.SimpleTimeFormatter.print(localTime - request.getRequestTime()));
			                }
			            });
			        }
			    }, 1000, 1000);			
		}

		@Override
		protected String doInBackground(Void... params) {
			RestMethod method = RestMethodFactory.RequestServer(Constants.DEVICE_UUID, "test");
			String response = null;
			try {
				response = method.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return response;
		}
		
	}
}
