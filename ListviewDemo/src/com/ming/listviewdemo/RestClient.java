package com.ming.listviewdemo;

import org.apache.http.Header;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class RestClient {
	
	private static RestClient mInstance = null;
	
	private final static String HOST = "https://dl.dropboxusercontent.com";
	private final static String URL_DATA = "/u/746330/facts.json";
	
	private AsyncHttpClient mClient = null;
	
	private RestClient() {
		mClient = new AsyncHttpClient();
	}
	
	public static RestClient getInstance() {
		if (mInstance == null) {
			mInstance = new RestClient();
		}
		
		return mInstance;
	}
	
	private void loadData() {
		mClient.get(HOST + URL_DATA, new JsonHttpResponseHandler() {
			
			@Override
			public void onStart() {
				// show loading here
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				// TODO parse response to java obj here
				
			}
			
			@Override
			public void onFinish() {
				// hide loading here
			}
		});
	}

}
