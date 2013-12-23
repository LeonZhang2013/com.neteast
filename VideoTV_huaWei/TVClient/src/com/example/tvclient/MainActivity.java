package com.example.tvclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neteast.longtv.aidl.ISearchTV;


/**
 * @author zll
 *
 * 该应用使用了 AIDL 进程间通信来调去 视频天下。
 * 
 * 查询使用方式：
 * 	1、拷贝 com.neteast.longtv.aidl 文件夹和文件到 自己的应用目录，eclipse会自动在跟文件生成一个借口文件。
 * 	2、启动服务 使用 SERVER_SEARCH 来隐式启动服务。
 * 	3、实现 ServiceConnection类，服务连接后，onServiceConnected（） 方法会返回 ISearchTV 对象。通过对象可以调去 视频天下的查询方法。
 * 	4、注意： 查询方法是 IO操作，需要一定时间，不要在主线程中查询。这里使用QueryVideo 类完成查询操作，返回一个 xml详细 String。
 * 
 * 调用视频天下详细界面：
 * 	1、得到xml文件后，解析 里面的moveID 
 *	2、使用Intent(ACTION_DESC) 传递  intent.putExtra("movieId",movieId); long movieId = 对应对象的MovieId，就可以调用详细界面。
 * 注意，这里我直接传递的是 123 做的演示。
 */
public class MainActivity extends Activity {

	private final String SERVER_SEARCH = "com.netest.search.TV";
	private final String ACTION_DESC = "com.neteast.longtv.VIDEODESC";
	private SearchVideoServiceConnection conn = new SearchVideoServiceConnection();
	private ISearchTV mbinder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService();
	}

	public void serchTV(View v) {
		EditText edit = (EditText) findViewById(R.id.video_name);
		String value = edit.getText().toString();
		new QueryVideo().execute(value);
	
	}
	
	public void openDesc(View v){
		Intent intent = new Intent(ACTION_DESC);
		long movieId = 123;
        intent.putExtra("movieId",movieId);
        startActivity(intent);
	}

	class QueryVideo extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			String reuslt = null;
			try {
				String aa = params[0];
				reuslt = mbinder.queryName(aa);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return reuslt;
		}

		@Override
		protected void onPostExecute(String result) {
			TextView t = (TextView) findViewById(R.id.show_content);
			t.setText(Html.fromHtml(result));
			super.onPostExecute(result);
		}

	}

	class SearchVideoServiceConnection implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			if (service != null) {
				try {
					mbinder = ISearchTV.Stub.asInterface(service);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "出错啦",
							Toast.LENGTH_LONG).show();
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mbinder = null;
		}
	}

	private void startService() {
		Intent intent = new Intent(SERVER_SEARCH);
		bindService(intent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy();
	}

}
