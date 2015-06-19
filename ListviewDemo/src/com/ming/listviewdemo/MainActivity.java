package com.ming.listviewdemo;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ming.listviewdemo.bean.DataBean;
import com.ming.listviewdemo.bean.ItemBean;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final static String TAG = "MainActivity";
	
	public final static int REQUEST_STARTED = 0;
	public final static int REQUEST_FINISHED = 1;
	public final static int REQUEST_SUCCESS = 2;
	public final static int REQUEST_FAILED = 3;
	
	private final static String HOST = "https://dl.dropboxusercontent.com";
	private final static String URL_DATA = "/u/746330/facts.json";
	
	private AsyncHttpClient mClient = null;
	
	private ActionBar mActionBar = null;
	private ListView mListView = null;
	private DataAdapter mAdapter = null;
	private ArrayList<ItemBean> dataList = null;
	
	Handler mHandler = new Handler() {
		
		 public void handleMessage(Message msg) {
			 int what = msg.what;
			 switch (what) {
			case REQUEST_STARTED:
				// TODO show loading here
				break;
			case REQUEST_FINISHED:
				// TODO hide loading here
				break;
			case REQUEST_SUCCESS:
				DataBean bean = (DataBean) msg.obj;
				showData(bean);
				break;
			case REQUEST_FAILED:
				showErrorMessage((String) msg.obj);
				break;
			default:
				break;
			}
		 };
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// using the following way to init async http client in order to accept all ssl cert 
		mClient = new AsyncHttpClient(true, 80, 443);
		
		initView();
		loadData();
	}
	
	private void initView() {
		// init action bar
		mActionBar = this.getActionBar();
		
		// init list view
		
	}

	private void showData(DataBean bean) {
		mActionBar.setTitle(bean.title);
		
		
	}
	
	private void showErrorMessage(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.alert_title);
		builder.setMessage(message);
		builder.setNeutralButton(R.string.alert_button, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	private void loadData() {
		mClient.get(HOST + URL_DATA, new JsonHttpResponseHandler() {
			
			@Override
			public void onStart() {
				
				mHandler.sendEmptyMessage(MainActivity.REQUEST_STARTED);
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// parse response to java obj here
				Log.d(TAG, "" + response);
				DataBean bean = convert2DataBean(response);
				Message m = mHandler.obtainMessage(MainActivity.REQUEST_SUCCESS, bean);
				mHandler.sendMessage(m);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.d(TAG, "" + errorResponse);
				// here should parse the errorResponse and get the detail erro message/info
				// since i don't know what will be returned in case of error situation
				// i just return the message of "error"
				Message m = mHandler.obtainMessage(MainActivity.REQUEST_FAILED, "Your request was failed!");
				mHandler.sendMessage(m);
			}
			
			@Override
			public void onFinish() {
				// hide loading here
				mHandler.sendEmptyMessage(MainActivity.REQUEST_FINISHED);
			}
		});
	}
	
	/**
	 * Parse the reponse json string into Java Object
	 * 
	 * @param response
	 * the response json object from back end web service
	 * @return DataBean
	 * Java object for the data
	 */
	private DataBean convert2DataBean(JSONObject response) {
		DataBean data = new DataBean();
		if (response != null) {
			try {
				data.title = response.getString("title");
				JSONArray rows = response.getJSONArray("rows");
				for (int i = 0; i < rows.length(); i++) {
					JSONObject row = rows.getJSONObject(i);
					ItemBean item = new ItemBean();
					item.title = row.getString("title");
					item.desc = row.getString("description");
					item.imagePath = row.getString("imageHref");
					
					data.items.add(item);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	class DataAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			return null;
		}
		
		class ViewHolder {
			TextView title;
			TextView desc;
			ImageView icon;
		}
		
	}
}
