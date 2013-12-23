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
 * ��Ӧ��ʹ���� AIDL ���̼�ͨ������ȥ ��Ƶ���¡�
 * 
 * ��ѯʹ�÷�ʽ��
 * 	1������ com.neteast.longtv.aidl �ļ��к��ļ��� �Լ���Ӧ��Ŀ¼��eclipse���Զ��ڸ��ļ�����һ������ļ���
 * 	2���������� ʹ�� SERVER_SEARCH ����ʽ��������
 * 	3��ʵ�� ServiceConnection�࣬�������Ӻ�onServiceConnected���� �����᷵�� ISearchTV ����ͨ��������Ե�ȥ ��Ƶ���µĲ�ѯ������
 * 	4��ע�⣺ ��ѯ������ IO��������Ҫһ��ʱ�䣬��Ҫ�����߳��в�ѯ������ʹ��QueryVideo ����ɲ�ѯ����������һ�� xml��ϸ String��
 * 
 * ������Ƶ������ϸ���棺
 * 	1���õ�xml�ļ��󣬽��� �����moveID 
 *	2��ʹ��Intent(ACTION_DESC) ����  intent.putExtra("movieId",movieId); long movieId = ��Ӧ�����MovieId���Ϳ��Ե�����ϸ���档
 * ע�⣬������ֱ�Ӵ��ݵ��� 123 ������ʾ��
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
					Toast.makeText(getApplicationContext(), "������",
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
