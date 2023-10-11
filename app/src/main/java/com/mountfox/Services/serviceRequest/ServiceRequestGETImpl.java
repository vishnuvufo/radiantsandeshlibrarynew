package com.mountfox.Services.serviceRequest;


import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URLEncoder;

public class ServiceRequestGETImpl implements ServiceRequest {
	
  static String sb;
	
	@SuppressWarnings("deprecation")
  public String requestService(String getUrl, String val) {
		
	  try {
			
			HttpClient client = new DefaultHttpClient();
			HttpGet req = new HttpGet(getUrl+ URLEncoder.encode(val));
			ResponseHandler<String> respHandler = new BasicResponseHandler();
			sb = client.execute(req, respHandler);
			client.getConnectionManager().closeExpiredConnections();
			client.getConnectionManager().shutdown();
		
	  } catch (Exception e) {
			e.printStackTrace();
		}

		return sb;
	}
	
}

