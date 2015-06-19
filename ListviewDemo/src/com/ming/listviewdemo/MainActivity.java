package com.ming.listviewdemo;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.ming.listviewdemo.bean.DataBean;
import com.ming.listviewdemo.bean.ItemBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends Activity {
	
	private final static String TAG = "MainActivity";
	
	// after move RestClient class into MainActivity,
	// since AsyncHttpClient's callback methods are all running on non-UI thread,
	// there's no need to use handler anymore, i just comment these codes.
//	public final static int REQUEST_STARTED = 0;
//	public final static int REQUEST_FINISHED = 1;
//	public final static int REQUEST_SUCCESS = 2;
//	public final static int REQUEST_FAILED = 3;
	
	private final static String HOST = "https://dl.dropboxusercontent.com";
	private final static String URL_DATA = "/u/746330/facts.json";
	
	private AsyncHttpClient mClient = null;
	
	private LayoutInflater mInflater = null;
	private ActionBar mActionBar = null;
	private ListView mListView = null;
	private DataAdapter mAdapter = null;
	private ArrayList<ItemBean> mDataList = null;
	
//	Handler mHandler = new Handler() {
//		
//		 public void handleMessage(Message msg) {
//			 int what = msg.what;
//			 switch (what) {
//			case REQUEST_STARTED:
//				// TODO show loading here
//				break;
//			case REQUEST_FINISHED:
//				// TODO hide loading here
//				break;
//			case REQUEST_SUCCESS:
//				DataBean bean = (DataBean) msg.obj;
//				showData(bean);
//				break;
//			case REQUEST_FAILED:
//				showErrorMessage((String) msg.obj);
//				break;
//			default:
//				break;
//			}
//		 };
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// using the following way to init async http client in order to accept all ssl cert 
		mClient = new AsyncHttpClient(true, 80, 443);
		
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(MainActivity.this);
		ImageLoader.getInstance().init(config);
		
		initView();
		loadData();
	}
	
	private void initView() {
		// init action bar
		mActionBar = this.getActionBar();
		
		// init list view
		mListView = (ListView) findViewById(R.id.main_list);
		mDataList = new ArrayList<ItemBean>();
		mAdapter = new DataAdapter();
		mListView.setAdapter(mAdapter);
		// we can also add empty view to this listview in order to make it looks nice
		// mListView.setEmptyView();
	}

	private void showData(DataBean bean) {
		mActionBar.setTitle(bean.title);
		
		mDataList = bean.items;
		mAdapter.notifyDataSetChanged();
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
//				mHandler.sendEmptyMessage(MainActivity.REQUEST_STARTED);
				showLoading();
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// parse response to java obj here
				Log.d(TAG, "" + response);
				DataBean bean = convert2DataBean(response);
//				Message m = mHandler.obtainMessage(MainActivity.REQUEST_SUCCESS, bean);
//				mHandler.sendMessage(m);
				showData(bean);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Log.d(TAG, "" + errorResponse);
				// here should parse the errorResponse and get the detail erro message/info
				// since i don't know what will be returned in case of error situation
				// i just return the message of "error"
//				Message m = mHandler.obtainMessage(MainActivity.REQUEST_FAILED, "Your request was failed!");
//				mHandler.sendMessage(m);
				showErrorMessage("Your request was failed!");
			}
			
			@Override
			public void onFinish() {
				// hide loading here
//				mHandler.sendEmptyMessage(MainActivity.REQUEST_FINISHED);
				hideLoading();
			}
		});
	}
	
	private void showLoading() {
		// TODO show loading spinner
		// i leave it empty since there's no requeirment in the document for this feature
	}
	
	private void hideLoading() {
		// TODO hide loading spinner
		// i leave it empty since there's no requeirment in the document for this feature
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			mDataList = new ArrayList<ItemBean>();
			mAdapter.notifyDataSetChanged();
			
			loadData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class DataAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item, null);
				convertView.setTag(mHolder);
				// convertView.setPadding(15, 15, 15, 15);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}

			mHolder.title = (TextView) convertView
					.findViewById(R.id.title);
			mHolder.title.setText(mDataList.get(position).title);
			
			mHolder.desc = (TextView) convertView
					.findViewById(R.id.desc);
			mHolder.desc.setText(mDataList.get(position).desc);
			
			mHolder.icon = (ImageView) convertView
					.findViewById(R.id.icon);
			String path = mDataList.get(position).imagePath;
			ImageLoader mLoader = ImageLoader.getInstance();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					// icon for empty uri
					.showImageForEmptyUri(R.drawable.ic_launcher)
					// icon for fail
					.showImageOnFail(R.drawable.ic_launcher)
					// icon for loading
					.showStubImage(R.drawable.ic_launcher)
					.cacheInMemory(true)
					.cacheOnDisc(true)
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.ARGB_8888)
					.build();
			mLoader.displayImage(path, mHolder.icon, options);

			return convertView;
		}
		
		class ViewHolder {
			TextView title;
			TextView desc;
			ImageView icon;
		}
		
	}
}
