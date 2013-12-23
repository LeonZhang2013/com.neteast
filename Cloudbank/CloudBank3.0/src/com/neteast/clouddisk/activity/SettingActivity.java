package com.neteast.clouddisk.activity;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lib.cloud.LibCloud;
import com.lib.net.WeiboException;
import com.neteast.clouddisk.R;
import com.neteast.clouddisk.handler.MyConfigHandler;
import com.neteast.clouddisk.handler.MyHandler;
import com.neteast.clouddisk.param.Params;
import com.neteast.clouddisk.utils.DataHelpter;
import com.neteast.clouddisk.utils.DownLoadApplication;
import com.neteast.clouddisk.utils.UIHelper;

public class SettingActivity extends Activity {
	PopupWindow pop = null;
	
	private LibCloud libCloud; 
	TextView totalText1 =null;
	TextView usedText1 =null;
	TextView nousedText1 = null;
	String versionUrl =Params.APP_VERSION_URL;
	String apkUrl =Params.APP_APK_URL;
	String verCode =null;
	String verName =null;
	String verInfo =null;
	int mCurrentVerCode = 0;
	String mCurrentVerName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settinglayout);
		
		libCloud = LibCloud.getInstance(this);
		
		TextView baseSetting = (TextView) findViewById(R.id.basesetting);
		TextPaint tp = baseSetting.getPaint(); 
		tp.setFakeBoldText(true); 
		totalText1 = (TextView) findViewById(R.id.totalText1);
		usedText1 = (TextView) findViewById(R.id.usedText1);
		nousedText1 = (TextView) findViewById(R.id.nousedText1);
		
		TextView curVersion = (TextView) findViewById(R.id.curver);
		
		String totalSpace = null;
		String usedSpace = null;
		Map<String, Object> retmap=null;
		try {
			retmap = libCloud.Get_capacity();
			if(retmap.get("code").equals("1")){
				totalSpace = (String) retmap.get("totalspace");
				usedSpace = (String) retmap.get("usedspace");
				if(totalSpace==null){
					totalSpace="0";
				}if(usedSpace==null){
					usedSpace="0";
				}
				System.out.println("mydownloadPicture totalSpace :" + totalSpace + "usedSpace" + usedSpace);
				//int usedMB = (int) Long.parseLong(usedSpace)/(1024*1204);
				String usedMB = "";
				long size = Long.parseLong(usedSpace);
				long total = Long.parseLong(totalSpace);
				usedMB = DataHelpter.getSpaceStr(size);
				
				totalText1.setText(totalSpace+"GB");				
				
				//capacityText1.setText("容量： 共"+totalSpace+"GB,"+usedMB + "已用,"+"剩余"+leave+"GB" );
				
				String leave = DataHelpter.getSpaceStr(total*1024*1024*1024-size);
				usedText1.setText(usedMB);
				nousedText1.setText(leave);
			}
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String curver = ((DownLoadApplication)getApplication()).getVersion();
		String verinfo = String.format(getResources().getString(R.string.curver_text),curver);
		curVersion.setText(verinfo);
		
		
		/*
		if(isUpdate()){
			String ver = verCode+"."+verName;
			String str = String.format(getResources().getString(R.string.verupgrade_text),ver);
			String curver = "V" + mCurrentVerName;
			String verinfo = String.format(getResources().getString(R.string.curver_text),curver);
			curVersion.setText(verinfo);
		}else{
			String curver = "V" + mCurrentVerName;
			String verinfo = String.format(getResources().getString(R.string.curver_text),curver);
			curVersion.setText(verinfo);
		}
		*/
	}

	/**
	 * 注销用户
	 * 
	 * @param view
	 */
	public void logoutOnclick(View view) {

	}

	/**
	 * 更改密码
	 * 
	 * @param view
	 */
	public void changePSWOnclick(View view) {
		View v = LayoutInflater.from(this).inflate(R.layout.changepsw, null);
		pop = new PopupWindow(v, 470, 450);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation((View) view.getParent().getParent().getParent(),
				Gravity.CENTER, 0, 0);
		pop.update();
		final TextView tip1 = (TextView)v.findViewById(R.id.tip1);
		final TextView tip2 = (TextView)v.findViewById(R.id.tip2);
		tip1.setVisibility(View.INVISIBLE);
		tip2.setVisibility(View.INVISIBLE);
		
		final EditText oldpsw = (EditText) v.findViewById(R.id.pswcontent);
		final EditText newpsw = (EditText) v.findViewById(R.id.newpswcontent1);
		final EditText newpswconfim = (EditText) v.findViewById(R.id.newpswcontent2);
		ImageButton okbt = (ImageButton)v.findViewById(R.id.changepswokbtn);
		okbt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String oldpswStr = oldpsw.getText().toString();
				String newpswStr = newpsw.getText().toString();
				String newpswConfimStr = newpswconfim.getText().toString();
				
				if(!newpswStr.trim().equals(newpswConfimStr)){
					tip2.setVisibility(View.VISIBLE);
					tip1.setVisibility(View.INVISIBLE);
					return ;
				}	
				try {
					Map<String, Object> retmap=null;
					System.out.println("change pswd :" + oldpswStr.trim() + "newpaswd" + newpswStr.trim());
					retmap = libCloud.UpdatePassword(oldpswStr.trim(), newpswStr.trim());
					if(retmap.get("code").equals("1")){
						UIHelper.displayToast(getResources().getString(R.string.changepswok_text),SettingActivity.this);
						if (pop != null) {
							pop.dismiss();
						}
					}else if(retmap.get("code").equals("301")){
						tip1.setVisibility(View.VISIBLE);
						tip2.setVisibility(View.INVISIBLE);
					}else{
						UIHelper.displayToast(getResources().getString(R.string.changepswfailed_text),SettingActivity.this);
					}
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		ImageButton cancelbt = (ImageButton)v.findViewById(R.id.changepswcancelbtn);
		cancelbt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (pop != null) {
					pop.dismiss();
				}
			}
		});
		
	}

	/**
	 * 升级
	 * 
	 * @param view
	 */
	public void ungradeOnclick(View v) {
		RelativeLayout rl = (RelativeLayout) SettingActivity.this.getParent()
				.getWindow().findViewById(R.id.datacontainer);
		rl.removeAllViews();
		Intent intent = new Intent(SettingActivity.this, UpgradeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Window sub = ((ActivityGroup) SettingActivity.this.getParent())
				.getLocalActivityManager().startActivity("upgradeactivity",
						intent);
		View view = sub.getDecorView();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		view.setLayoutParams(params);
		rl.addView(view);
	}

	/**
	 * 取消按钮
	 * 
	 * @param view
	 */
	public void cancelOnclick(View view) {
		if (pop != null) {
			pop.dismiss();
		}
	}

	/**
	 * 确定按钮
	 * 
	 * @param view
	 */
	public void okOnclick(View view) {
		if (pop != null) {
			pop.dismiss();
		}
	}

	/**
	 * 清楚缓存
	 * 
	 * @param view
	 */
	public void clearbufferOnclick(View view) {
		View v = LayoutInflater.from(this).inflate(R.layout.clearbufferlayout,
				null);
		pop = new PopupWindow(v, 470, 450);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation((View) view.getParent().getParent().getParent(),
				Gravity.CENTER, 0, 0);
		pop.update();
		RadioButton rbacount = (RadioButton) v.findViewById(R.id.rbacount);
		final RadioButton rbimg = (RadioButton) v.findViewById(R.id.rbimg);
		final RadioButton rbfile = (RadioButton) v.findViewById(R.id.rbfile);
		ImageButton okbt = (ImageButton)v.findViewById(R.id.clearbufferOkbtn);
		okbt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(rbimg.isChecked()||rbfile.isChecked()){
					DataHelpter.deleteSDcardCacheAll();
				}
				if (pop != null) {
					pop.dismiss();
				}
			}
		});
	}

	/**
	 * 推荐给朋友
	 * 
	 * @param view
	 */
	public void recommandOnclick(View view) {
		View v = LayoutInflater.from(this).inflate(R.layout.recommandtofriend,
				null);
		pop = new PopupWindow(v, 470, 450);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation((View) view.getParent().getParent().getParent(),
				Gravity.CENTER, 0, 0);
		pop.update();
	}

	/**
	 * 升级应用
	 * 
	 * @param view
	 */
	public void upgradeAppOnclick(View view) {
		View v = LayoutInflater.from(this).inflate(R.layout.upgradeapplayout,
				null);
		TextView upgradecontent = (TextView)v.findViewById(R.id.upgradecontent);
		upgradecontent.setText(verInfo);
		ImageButton upgradebtn =  (ImageButton)v.findViewById(R.id.upgradebtn);
		upgradebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//DownloadOSTask dlos = new DownloadOSTask();
				//dlos.execute(apkUrl);
				if (pop != null) {
					pop.dismiss();
				}
				
			}
		});
		pop = new PopupWindow(v, 470, 450);
		pop.setOutsideTouchable(true);
		pop.setFocusable(true);
		pop.showAtLocation((View) view.getParent().getParent().getParent(),
				Gravity.CENTER, 0, 0);
		pop.update();
	}
	
	private boolean isUpdate() {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> urlmap = new HashMap<String, Object>();
		String versionName = null;

		InputStream inStream = null;
		/*read url from local config*/
		try { 
			 File file = new File("/sdcard/clouddisk/upgrade/upgrade.xml");
			 if(file.exists()){
				 inStream = new FileInputStream("/sdcard/clouddisk/upgrade/upgrade.xml");
				 Log.d("MainActivity","open InputStream !!!!!!!!!!!!");
				 if(inStream!=null){
					 SAXParserFactory spf = SAXParserFactory.newInstance(); 
					 SAXParser saxParser = spf.newSAXParser(); //创建解析器 
					 //设置解析器的相关特性，http://xml.org/sax/features/namespaces = true 表示开启命名空间特性   
					 // saxParser.setProperty("http://xml.org/sax/features/namespaces",true); 
					 MyConfigHandler handler = new MyConfigHandler(urlmap); 
					 saxParser.parse(inStream, handler); 
					 inStream.close(); 
					 versionUrl = (String) urlmap.get("versionurl");
					 apkUrl = (String) urlmap.get("apkurl");
					 Log.d("MainActivity","versionurl = " + versionUrl + "apkUrl" + apkUrl);
				 }else{
					 Log.d("MainActivity","open InputStream is NULL");
				 }
			 }
			 
		} catch (Exception e) { 
			 e.printStackTrace(); 

		} 
		
		int vercode=-1;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			XMLReader reader = factory.newSAXParser().getXMLReader();

			MyHandler handler = new MyHandler(map);//创建事件处理器
			reader.setContentHandler(handler);
			reader.parse(versionUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		for(int i=0;i<packages.size();i++) {         
			PackageInfo packageInfo = packages.get(i);     
			if(packageInfo.packageName.equals("com.neteast.clouddisk")){
				//versionName = packageInfo.versionName;
				mCurrentVerName = packageInfo.versionName;
				mCurrentVerCode = packageInfo.versionCode;
				vercode = packageInfo.versionCode;
				break;
			}
		}
		System.out.println("ver:"+vercode+"sver:"+map.get("versionCode"));
		System.out.println("vername:"+ map.get("versionName"));
		verCode = (String) map.get("versionCode");
		verName = (String) map.get("versionName");
		verInfo = (String) map.get("versionInfo");
		String strver=(String)map.get("versionCode");
		int sver=-1;
		if(map.get("versionCode")!=null)
			sver= Integer.parseInt(strver);
		if(sver > vercode){
			/*
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			params.width = 200;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			dialog.getWindow().setAttributes(params);
			dialog.setTitle("升级");
			dialog.setMessage("有新的版本，要升级吗？");
			dialog.setButton("升级", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DownloadOSTask dlos = new DownloadOSTask();
					dlos.execute(apkUrl);
				}
			});
			dialog.setButton2("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
			*/
			return true;
		}
		return false;
	}
	class DownloadOSTask extends AsyncTask<String, Integer, String>{

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(SettingActivity.this);
			progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progress.setCancelable(false);
			progress.setMessage("下载中...");
			
			progress.getWindow().setLayout(200, LayoutParams.WRAP_CONTENT);
			progress.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			InputStream in = null;
			RandomAccessFile file = null;
			String fileName = null;
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestProperty("Accept-Language",
						Params.ACCEPT_LANGUAGE);
				conn.setRequestProperty("Charset", Params.ChARSET);
				conn.setRequestProperty("Connetion", "Keep-Alive");
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "NetFox");
				in = conn.getInputStream();
				int totalLength = conn.getContentLength();
				int urlIndex = url.toString().lastIndexOf("/");
				fileName = url.toString().substring(urlIndex + 1);
				file = new RandomAccessFile(
						Params.DOWNLOAD_FILE_PATH + fileName, "rw");
				byte[] buffer = new byte[20 * 1024];
				int len = -1;
				int count = 0;
				while ((len = in.read(buffer)) != -1) {
					file.write(buffer, 0, len);
					count += len;
					publishProgress((int) ((float)count / totalLength * 100));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (file != null) {
						file.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return fileName;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progress.setMessage("下载中" + values[0] + "%");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			File file = new File(Params.DOWNLOAD_FILE_PATH + result);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + file.toString()),
			"application/vnd.android.package-archive");
			progress.dismiss();
			startActivityForResult(intent, 0);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
