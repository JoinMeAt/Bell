package com.taco.bell.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RestMethod {
	String uri;
	String params;
	Types methodType;
		
	public static enum Types {
		GET, POST, PUT, DELETE
	}
	
//	static {
//		trustAllHosts();
//	}
	
	public RestMethod(String uri, String params, Types methodType) {
		this.uri = uri;
		this.params = params;
		this.methodType = methodType;
	}
	
	public String execute() throws IOException {
		String response = null;		

		if( uri == null || uri.length() == 0 ) 
			throw( new UnsupportedOperationException("Query not implemented."));

		if( params == null || params.length() == 0 )
			throw( new UnsupportedOperationException("Query not initialized."));
		
    	HttpURLConnection conn = null;
    	
    	URL url = null;
    	if( methodType == Types.GET ) {
    		url = new URL(uri + "?" + params);
    	} else {
    		url = new URL(uri);    		
    	}
    	
    	if( url.getProtocol().toLowerCase().equals("https") ) { // verify SSL cert
    		HttpsURLConnection https = (HttpsURLConnection) url.openConnection(); 
    		https.setHostnameVerifier(DO_NOT_VERIFY);
    		conn = https;
    	} else {
    		conn = (HttpURLConnection) url.openConnection();
    	}
		
        switch(methodType) {
		case DELETE:
			break;
		case GET:
        	conn.setRequestMethod("GET");
			break;
		case POST:
    		conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        	conn.setRequestMethod("POST");

            OutputStreamWriter request = new OutputStreamWriter(conn.getOutputStream());
            request.write(params);
            request.flush();
            request.close();	
			break;
		case PUT:
			break;
		default:
			break;
        }
        
        response = InputStreamToString(conn.getInputStream());
        
        conn.disconnect();	
		
		return response;
	}
	
	public Types getMethodType() {
		return methodType;
	}
	
	public String getURI() {
		return uri;
	}
	
	public String getParameters() {
		return params;
	}
	
	public String toString() {
		return 	this.uri + "\n" + 
				this.methodType + ": " + this.params;
	}

	private static String InputStreamToString(InputStream is) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = "";
		
		while( (line = in.readLine()) != null ) {
			sb.append(line);
		}
		
		in.close();
		
		return sb.toString();
	}
	
	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// always verify the host - dont check for certificate
	protected final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}
