package com.neteast.cloudtv2;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.neteast.cloudtv2.adapter.MyPageAdapter;
import com.neteast.cloudtv2.bean.Channelbean;
import com.neteast.cloudtv2.bean.UserBean;
import com.neteast.cloudtv2.listener.PageListener;
import com.neteast.cloudtv2.tools.ImageLoader;
import com.neteast.cloudtv2.tools.LogUtil;
import com.neteast.cloudtv2.tools.MD5;
import com.neteast.cloudtv2.tools.MyLog;
import com.neteast.cloudtv2.tools.NetUtils;
import com.neteast.cloudtv2.tools.Tools;

public class MainActivity extends FragmentActivity implements PageListener {

	private MyPageAdapter mPageAdapter;
	private ViewPager mViewPager;
	private List<Channelbean> mListData = new ArrayList<Channelbean>();
	private int mEachPageNumber = 6;
	private PopupWindow mLoginWindows;
	private int mCurrentPage = R.id.menu_1;
	private boolean isSave = false;
	private ProgressBar mProgressBar;
	private final int LOADING_DATA_THREAD_SCCUESS = 10;
	private final int LOADING_DATA_THREAD_FAILED = 11;
	private final int VERIFY_USER_THREAD_SCCUESS = 20;
	private final int VERIFY_USER_THREAD_FAILED = 21;
	private LinearLayout mLogotView;
	private TextView mUserName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.deleteLog();
		setContentView(R.layout.activity_main);
		startActivity(new Intent(this, ToLeadViewActivity.class));
		if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this)) {
			return;
		}
		initIp();
		initUserAccount();
		initLayout();
		initViewPager();
		initMenu(mCurrentPage);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// 不能删除此方法，该方法用语阻断主界面自动保存数据。
		// 如删掉，横竖屏切换就会报错
	}

	private Handler mHandler;

	private void initLayout() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (mProgressBar.isShown())
					mProgressBar.setVisibility(View.GONE);
				switch (msg.what) {
				case LOADING_DATA_THREAD_SCCUESS:
					List<Channelbean> result = (List<Channelbean>) msg.obj;
					if (result.size() == 0) {
						Toast.makeText(getApplicationContext(), "没有获取到数据，数据为空", Toast.LENGTH_LONG).show();
						return;
					}
					if (mListData != null)
						mListData.clear();
					mListData.addAll(result);
					initPageFlag();
					mPageAdapter.notifyDataSetChanged();
					break;
				case VERIFY_USER_THREAD_SCCUESS:
					boolean isSccuess = (Boolean) msg.obj;
					if (isSccuess) {
						saveUserInfo(isSave);
						Toast.makeText(getApplicationContext(), "登录成功~！", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "用户名或密码错误~！", Toast.LENGTH_LONG).show();
					}
					checkLogin();
					break;
				case LOADING_DATA_THREAD_FAILED:
					String error = (String) msg.obj;
					MyLog.writeLog("LOADING_DATA_THREAD_FAILED  " + error);
					Toast.makeText(getApplicationContext(), "请求失败~！", Toast.LENGTH_LONG).show();
					break;
				case VERIFY_USER_THREAD_FAILED:
					checkLogin();
					String error1 = (String) msg.obj;
					MyLog.writeLog("VERIFY_USER_THREAD_FAILED  " + error1);
					Toast.makeText(getApplicationContext(), "用户名或密码错误~！", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};

		// =========添加动画=============
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);
		LayoutTransition lt = new LayoutTransition();
		lt.setAnimator(LayoutTransition.CHANGE_APPEARING, null);
		lt.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, null);
		rl.setLayoutTransition(lt);
		// =============================
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		mProgressBar.setAnimation(new AlphaAnimation(1.0f, 0.0f));
		mLogotView = (LinearLayout) findViewById(R.id.logout_view);
		mUserName = (TextView) findViewById(R.id.user_name);
	}

	/** 初始化用户账号和密码。如果用户上次选择了记录 */
	private void initUserAccount() {
		SharedPreferences preferences = getSharedPreferences("flytv2_user", Context.MODE_PRIVATE);
		if (preferences != null) {
			isSave = preferences.getBoolean("issave", isSave);
			if (!isSave)
				return;
			String carNo = preferences.getString("userName", "");
			String passWordord = preferences.getString("passWord", "");
			Constant.USER_BEAN = new UserBean(carNo, passWordord);
		}
	}

	private void initViewPager() {
		mPageAdapter = new MyPageAdapter(getSupportFragmentManager(), this);
		mViewPager = (ViewPager) findViewById(R.id.channel_display_layout);
		LayoutTransition transition = new LayoutTransition();
		mViewPager.setLayoutTransition(transition);
		mViewPager.setAdapter(mPageAdapter);
		pagePoint = (RadioGroup) findViewById(R.id.page_flag_layout);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int index) {
				if (pagePoint.getChildCount() == 0)
					return;
				pagePoint.check(pagePoint.getChildAt(index).getId());
			}

			@Override
			public void onPageScrolled(int index, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int index) {

			}
		});
	}

	private void initMenu(int currentPagde) {
		RadioGroup menu = (RadioGroup) findViewById(R.id.menu_layout);
		menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (mProgressBar.isShown())
					mProgressBar.setVisibility(View.GONE);
				mListData.clear();
				if (mPageAdapter != null) {
					mPageAdapter.notifyDataSetChanged();
				}
				if (pagePoint != null)
					pagePoint.removeAllViews();
				mCurrentPage = checkedId;
				stopThread();
				switch (checkedId) {
				case R.id.menu_1:
					// LoadingData(Constant.PROGROM_PATH + Constant.EDUCATION);
					LoadingData(Constant.EDUCATION);
					break;
				case R.id.menu_2:
					LoadingData(Constant.PROGROM_PATH + Constant.EPG);
					break;
				case R.id.menu_3:
					LoadingData(Constant.DEMAND_PATH + Constant.RECREATION);
					break;
				case R.id.menu_4:
				case R.id.menu_5:
					checkLogin();
					break;
				}
			}
		});
		menu.check(mCurrentPage);
	}

	private void stopThread() {
		if (mLoadingDataThread != null) {
			mLoadingDataThread.stopThread();
			mLoadingDataThread = null;
		}
		if (mVerifyUserThread != null) {
			mVerifyUserThread.stopThread();
			mVerifyUserThread = null;
		}
	}

	private LoadingDataThread mLoadingDataThread;

	private void LoadingData(String path) {
		MyLog.writeLog("主界面路径 " + path);
		if (!mProgressBar.isShown())
			mProgressBar.setVisibility(View.VISIBLE);
		mLoadingDataThread = new LoadingDataThread(path);
		mLoadingDataThread.start();
	}

	class LoadingDataThread extends Thread {
		private String path;
		private boolean isStop = false;

		public LoadingDataThread(String path) {
			this.path = path;
		}

		public void stopThread() {
			isStop = true;
		}

		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			InputStream in;
			List<Channelbean> listData = null;
			try {
				if (path.startsWith("http")) {
					in = NetUtils.requestData(path);
					MyLog.writeLog("in = " + in);
					listData = Channelbean.parserData(in);
					MyLog.writeLog("listData = " + listData);
				} else {
					in = MainActivity.class.getClassLoader().getResourceAsStream(path);
					listData = Channelbean.parserData(in);
				}
				msg.obj = listData;
				msg.what = LOADING_DATA_THREAD_SCCUESS;
			} catch (Exception e) {
				msg.obj = e.getMessage();
				msg.what = LOADING_DATA_THREAD_FAILED;
			}
			if (!isStop)
				mHandler.sendMessage(msg);
		}
	}

	private RadioGroup pagePoint;

	private void initPageFlag() {
		int pageCount = getPages();
		if (pageCount <= 1)
			return;
		LayoutInflater inflater = LayoutInflater.from(this);
		for (int i = 0; i < pageCount; i++) {
			RadioButton rb = (RadioButton) inflater.inflate(R.layout.radio_button, null);
			RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(15, 15);
			params.leftMargin = 15;
			pagePoint.addView(rb, params);
			if (i == 0)
				rb.setChecked(true);
		}
	}

	@Override
	public int getPages() {
		return mListData.size() % mEachPageNumber == 0 ? mListData.size() / mEachPageNumber : mListData.size() / mEachPageNumber
				+ 1;
	}

	@Override
	public List<Channelbean> getPageData(int page) {
		int end = mEachPageNumber * (page + 1);
		if (mListData.size() > end) {
			return mListData.subList(mEachPageNumber * page, end);
		} else {
			return mListData.subList(mEachPageNumber * page, mListData.size());
		}
	}

	private Thread mExitThread;
	private boolean flag = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mExitThread == null || !mExitThread.isAlive()) {
				mExitThread = new Thread() {
					@Override
					public void run() {
						flag = true;
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						flag = false;
					}
				};
				mExitThread.start();
			}
			if (flag) {
				this.finish();
			} else {
				Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
			}
		}
		return false;
	}

	/** 验证是否登录
	 * 
	 * @param id */
	private void checkLogin() {
		if (Constant.USER_BEAN == null || !Constant.USER_BEAN.isLogin()) {
			showLogin();
		} else {
			refreashUserName();
			swithPage();
		}
	}

	private void saveUserInfo(boolean issave) {
		if (issave) {
			SharedPreferences preferences = getSharedPreferences("flytv2_user", Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean("issave", isSave);
			editor.putString("userName", Constant.USER_BEAN.getUsername());
			editor.putString("passWord", Constant.USER_BEAN.getPassword());
			editor.commit();
		} else {
			SharedPreferences preferences = getSharedPreferences("flytv2_user", Context.MODE_PRIVATE);
			preferences.edit().clear().commit();
		}
	}

	/** 验证成功后跳转页面
	 * 
	 * @param id */
	private void swithPage() {
		switch (mCurrentPage) {
		case R.id.menu_4:
			LoadingData(Constant.MEDICAL_SERVICE);
			break;
		case R.id.menu_5:

			break;
		}
	}

	private VerifyUserThread mVerifyUserThread;

	private void showLogin() {
		final View contentView = LayoutInflater.from(this).inflate(R.layout.login_layout, null);
		final EditText nameEdit = (EditText) contentView.findViewById(R.id.user_name_ed);
		final EditText passEdit = (EditText) contentView.findViewById(R.id.password_ed);
		final CheckBox radio = (CheckBox) contentView.findViewById(R.id.radio_box);
		radio.setChecked(isSave);
		if (Constant.USER_BEAN != null) {
			nameEdit.setText(Constant.USER_BEAN.getUsername());
			passEdit.setText(Constant.USER_BEAN.getPassword());
			nameEdit.setSelection(Constant.USER_BEAN.getUsername().length());
			passEdit.setSelection(Constant.USER_BEAN.getPassword().length());
		}
		if (mLoginWindows == null) {
			mLoginWindows = new PopupWindow(contentView, 521, 392, true);
		} else {
			mLoginWindows.setContentView(contentView);
		}
		mLoginWindows.setOutsideTouchable(false);
		mLoginWindows.setFocusable(true);
		mLoginWindows.setBackgroundDrawable(new BitmapDrawable());
		Button button = (Button) contentView.findViewById(R.id.login_ok_btn);
		Button button1 = (Button) contentView.findViewById(R.id.login_cancel_btn);
		mLoginWindows.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mProgressBar.isShown())
					mProgressBar.setVisibility(View.VISIBLE);
				Constant.USER_BEAN = new UserBean(nameEdit.getText().toString(), passEdit.getText().toString());
				isSave = radio.isChecked();
				mVerifyUserThread = new VerifyUserThread(Constant.USER_BEAN);
				mVerifyUserThread.start();
				mLoginWindows.dismiss();
			}
		});

		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initUserAccount();
				mLoginWindows.dismiss();
			}
		});
	}

	class VerifyUserThread extends Thread {
		private UserBean mUserBean;
		private boolean isStop = false;

		public VerifyUserThread(UserBean mUserBean) {
			this.mUserBean = mUserBean;
		}

		public void stopThread() {
			isStop = true;
		}

		@Override
		public void run() {
			Message msg = mHandler.obtainMessage();
			try {
				boolean result = mUserBean.verifyPassword(mUserBean);
				msg.obj = result;
				msg.what = VERIFY_USER_THREAD_SCCUESS;
			} catch (Exception e) {
				msg.obj = e.toString();
				msg.what = VERIFY_USER_THREAD_FAILED;
			}
			if (!isStop)
				mHandler.sendMessage(msg);
		}
	}

	/** 初始化IP */
	private void initIp() {
		SharedPreferences preferences = getSharedPreferences("cloudvt2_ip", Context.MODE_PRIVATE);
		if (preferences != null) {
			Constant.DEMAND_PATH = preferences.getString("demand", Constant.DEMAND_PATH);
			Constant.PROGROM_PATH = preferences.getString("epg", Constant.PROGROM_PATH);
			Constant.PHOTO_PATH = preferences.getString("photo", Constant.PHOTO_PATH);
			Constant.PLAY_PATH = preferences.getString("play", Constant.PLAY_PATH);
			Constant.HIS = preferences.getString("his", Constant.HIS);
			Constant.IS_CH = preferences.getBoolean("isch", Constant.IS_CH);
			Constant.SERVER_PATH = Constant.HIS + Constant.WSDL_END;
		}
		View v = findViewById(R.id.ip_configer);
		v.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				ipConfigerView();
				return false;
			}
		});
	}

	public void ipConfigerView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dadLayout = inflater.inflate(R.layout.chang_ip_dialog, null);
		final EditText epg = (EditText) dadLayout.findViewById(R.id.epg_path);
		final EditText photo = (EditText) dadLayout.findViewById(R.id.photo_path);
		final EditText play = (EditText) dadLayout.findViewById(R.id.play_path);
		final EditText demand = (EditText) dadLayout.findViewById(R.id.demand_path);
		final EditText his = (EditText) dadLayout.findViewById(R.id.his_path);
		final ToggleButton ischBtn = (ToggleButton) dadLayout.findViewById(R.id.isch_btn);
		final Button change = (Button) dadLayout.findViewById(R.id.change_ip);

		epg.setText(Constant.PROGROM_PATH);
		photo.setText(Constant.PHOTO_PATH);
		play.setText(Constant.PLAY_PATH);
		demand.setText(Constant.DEMAND_PATH);
		his.setText(Constant.HIS);
		ischBtn.setChecked(Constant.IS_CH);

		change.setOnClickListener(new View.OnClickListener() {
			int i = 0;

			@Override
			public void onClick(View v) {
				if (i >= Constant.PROGROM_PATHS.length)
					i = 0;
				epg.setText(Constant.PROGROM_PATHS[i]);
				photo.setText(Constant.PHOTO_PATHS[i]);
				play.setText(Constant.PLAY_PATHS[i]);
				demand.setText(Constant.DEMAND_PATHS[i]);
				his.setText(Constant.HISES[i]);
				i++;
			}
		});

		new AlertDialog.Builder(MainActivity.this).setTitle("请输入新的IP地址：").setIcon(android.R.drawable.ic_dialog_info)
				.setView(dadLayout).setPositiveButton("保存", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Constant.PROGROM_PATH = epg.getText().toString();
						Constant.PHOTO_PATH = photo.getText().toString();
						Constant.PLAY_PATH = play.getText().toString();
						Constant.DEMAND_PATH = demand.getText().toString();
						Constant.HIS = his.getText().toString();
						Constant.IS_CH = ischBtn.isChecked();
						Constant.SERVER_PATH = Constant.HIS + Constant.WSDL_END;

						SharedPreferences preferences = getSharedPreferences("cloudvt2_ip", Context.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putString("epg", Constant.PROGROM_PATH);
						editor.putString("photo", Constant.PHOTO_PATH);
						editor.putString("play", Constant.PLAY_PATH);
						editor.putString("demand", Constant.DEMAND_PATH);
						editor.putString("his", Constant.HIS);
						editor.putBoolean("isch", Constant.IS_CH);
						editor.commit();
					}
				}).setNegativeButton("取消", null).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Constant.USER_BEAN = null;
		ImageLoader.getInstanse(this).clearCache();
		if (!isScreenOrientationLandscape()) {
			System.exit(0);
		}
	}

	private boolean isScreenOrientationLandscape() {
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		if (width > height) {
			return true;
		} else {
			return false;
		}
	}

	public void logout(View v) {
		new AlertDialog.Builder(this).setTitle("注销登录框").setMessage("你确定要注销用户吗？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Constant.USER_BEAN.logout();
						refreashUserName();
						if (mCurrentPage == R.id.menu_4 || mCurrentPage == R.id.menu_5) {
							mListData.clear();
							mPageAdapter.notifyDataSetChanged();
						}
						checkLogin();
					}
				}).setNegativeButton("否", null).show();
	}

	private void refreashUserName() {
		if (Constant.USER_BEAN.isLogin()) {
			mLogotView.setVisibility(View.VISIBLE);
			mUserName.setText(Constant.USER_BEAN.getName() + " |");
		} else {
			mLogotView.setVisibility(View.GONE);
		}
	}

	/**
	 * post实体数据
	 * @param account
	 * @param passWordord
	 * @return
	 */
	public UrlEncodedFormEntity generatePostEntity(String account, String passWordord) {
		passWordord = MD5.getMD5(passWordord);
		String reqstr = Tools.encodeBase64("user=" + account + "&passWordord=" + passWordord);
		String verify = MD5.getMD5("user=" + account + "&passWordord=" + passWordord + Constant.KEY);

		ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
		BasicNameValuePair value = new BasicNameValuePair("appcode", Constant.APPCODE);
		valuePairs.add(value);
		value = new BasicNameValuePair("reqstr", reqstr);
		valuePairs.add(value);
		value = new BasicNameValuePair("verify", verify);
		valuePairs.add(value);
		value = new BasicNameValuePair("datatype", "j");
		valuePairs.add(value);
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogUtil.printException(e);// 涓嶅彲鑳藉彂鐢� }
			return entity;
		}
		return entity;
	}
	
/*    private void login() {
        final String account = mAccount.getText().toString();
        String passWordord = mPassword.getText().toString();
        boolean validate = validateInput(account, passWordord);
        if (!validate) {
            warning("甯愬彿鎴栬�瀵嗙爜涓嶈兘涓虹┖");
            return;
        }
        disableInput();
        showProgress();
        warning("鐧诲綍涓�);
        final UrlEncodedFormEntity reqEntity = generatePostEntity(account, passWord);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path=ConfigManager.URL_USERCENTER+"/Mobile/login";
                try {
                    String result = Utils.doPost(path, reqEntity);
                    LoginData loginData = parseLoginResultJson(result);
                    loginData.account=account;
                    mUIHandler.obtainMessage(UIHandler.LOGIN_SUCCESS, loginData).sendToTarget();
                } catch (IOException e) {
                    LogUtil.printException(e);
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, "缃戠粶寮傚父锛岃閲嶆柊鐧诲綍").sendToTarget();
                } catch (JSONException e) {
                    LogUtil.printException(e);
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, "鏈嶅姟鍣ㄨ繑鍥炴暟鎹紓甯革紝璇烽噸鏂扮櫥褰�).sendToTarget();
                } catch (LoginException e) {
                    LogUtil.printException(e);
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, e.getMessage()).sendToTarget();
                }
            }
        }).start();
    }*/
}
