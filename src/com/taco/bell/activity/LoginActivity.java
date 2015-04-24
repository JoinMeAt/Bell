package com.taco.bell.activity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.taco.bell.rest.RestMethod;
import com.taco.bell.rest.RestMethodFactory;
import com.taco.bell.util.Constants;
import com.taco.bell.util.Logger;
import com.taco.bell.util.Serializer;
import com.taco.bell.util.TimeFormatter;
import com.taco.bell.R;
import com.taco.bell.ServiceRequest;
import com.taco.bell.User;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
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
	
	private boolean isLoginReady = false;
	private boolean isScreenPressed = false;
	private ServiceRequest request;
	
	private Timer T;
	private final Handler handler = new Handler();
	private final Runnable checkPressed = new Runnable() {
		@Override
		public void run() {
			if( isScreenPressed ) {
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
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideLogin();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		infoText.setText(getString(R.string.instruction));
		
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
		
		
	}
	
	private void startAnimation() {
		callWaiter.startAnimation(rotate);		
	}
	
	private void stopAnimation() {
		callWaiter.clearAnimation();
	}
	
	private class LoginAsyncTask extends AsyncTask<RestMethod, Void, String> {
		String restCall = null;

		@Override
		protected String doInBackground(RestMethod ... params) {
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

			if( TextUtils.isEmpty(result) ) {
				Logger.d(tag, "Login no response");
				return;
			}			
			Constants.ME = (User) Serializer.deserialize(result, User.class);
			
			if( restCall.equals(RestMethodFactory.LoginURL) ) { // server login
				Intent i = new Intent();
				i.setClass(LoginActivity.this, ServerActivity.class);
				startActivity(i);
				LoginActivity.this.finish();
			} else if( restCall.equals(RestMethodFactory.GuestLoginURL) ) {
				
			}
		}
		
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
	    int action = event.getAction();
	    if (action == MotionEvent.ACTION_DOWN) {
	    	handler.postDelayed(checkPressed, 2000);
	        isScreenPressed = true;
	    }
	    else if (action == MotionEvent.ACTION_UP) {
	    	handler.removeCallbacks(checkPressed);
	    	isScreenPressed = false;
	    }
		return false;
	}
	
	private class CallServerTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			callWaiter.setVisibility(View.VISIBLE);
			startAnimation();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			 request = (ServiceRequest) Serializer.deserialize(result, ServiceRequest.class);
			 timerText.setVisibility(View.VISIBLE);
			
			T=new Timer();
			T.scheduleAtFixedRate(new TimerTask() {
				        
			        @Override
			        public void run() {
			            runOnUiThread(new Runnable()
			            {
			                @Override
			                public void run()
			                {
			                	if( request == null ) return;
			                	
			    				long localTime = new LocalDateTime().now().toDateTime().getMillis();
			                	
			                    timerText.setText(TimeFormatter.simpleTimeFormatter.print(localTime - request.getRequestTime()));
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
