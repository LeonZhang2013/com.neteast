package com.example.testadil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huawei.iptv.stb.asr.aidl.neteast.Callback;
import com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService;

public class MainActivity extends Activity {

	private String SERVER_START = "com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService";
	private MyServerConnection conn = new MyServerConnection();
	private IAsrNeteastService mIAsrNeteastService;
	private Callback mCallbask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		inintLinster();
		startService();
	}

	private EditText mEditText;
	private void inintLinster() {
		
		
		mEditText = (EditText) findViewById(R.id.content_view);
		
		mCallbask = new Callback.Stub() {
			@Override
			public IBinder asBinder() {
				//mIAsrNeteastService.setCallback(callback)
				//return (IBinder) mIAsrNeteastService;
				return null;
			}
			@Override
			public void onResult(String json) throws RemoteException {
				Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
			}
		};
	}
	
	public void search(View v){
		String a = mEditText.getText().toString();
		try {
			String aaa = mIAsrNeteastService.execute(a);
			Toast.makeText(getApplicationContext(), aaa, Toast.LENGTH_LONG).show();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void startService() {
		Intent intent = new Intent(SERVER_START);
		bindService(intent, conn, BIND_AUTO_CREATE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MyServerConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIAsrNeteastService = IAsrNeteastService.Stub.asInterface(service);
			try {
				mIAsrNeteastService.setCallback(mCallbask);
				//String json = "{\"cmd\": \"startSearch\",\"data\":{\"keyword\": \"天天向上\"}}";
				String json1 = "{\"cmd\": \"play\",\"data\":{\"_id\": \"天天向上\",\"_id\": \"content://com.neteast.longtv.desc/movieid/123\"}}";

				Toast.makeText(getApplicationContext(), json1, Toast.LENGTH_LONG).show();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

	}
	
	@Override
	protected void onDestroy() {
		unbindService(conn);
		super.onDestroy();
	}

}
