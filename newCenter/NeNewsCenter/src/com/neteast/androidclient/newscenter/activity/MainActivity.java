package com.neteast.androidclient.newscenter.activity;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.domain.CloudAccount;
import com.neteast.androidclient.newscenter.domain.Information;
import com.neteast.androidclient.newscenter.fragment.AuthorizeCenterFragment;
import com.neteast.androidclient.newscenter.fragment.ConfigFragment;
import com.neteast.androidclient.newscenter.fragment.InformationListFragment;
import com.neteast.androidclient.newscenter.fragment.UserCenterFragment;
import com.neteast.androidclient.newscenter.service.ReceiveInfoService;
import com.neteast.androidclient.newscenter.service.WidgetService;
import com.neteast.androidclient.newscenter.utils.ApkManager;
import com.neteast.androidclient.newscenter.utils.ApkManager.ApkUpdateListener;
import com.neteast.androidclient.newscenter.utils.Utils;
import com.neteast.androidclient.newscenter.view.NewsWidget;

public class MainActivity extends FragmentActivity implements Observer{

    private Activity mContext;
    private Animation mOutAnimation;
    private View mRootView;
    private TextView mSysNum;
    private TextView mAppNum;
    private TextView mUserNum;
    private RadioButton mSysBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setGravity(Gravity.LEFT);
        mContext = this;
        intiContent();
        startBackgroundService();
        updateApplication(false);
    }
    
    /**
     * 打开授权中心
     */
    public void openAuthorizeCenter() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag_container, new AuthorizeCenterFragment());
        ft.commit();
    }
    /**
     * 打开用户中心界面
     */
    public void openUserCenterPage() {
        if (CloudAccount.getInstance(mContext).isGuest()) {
            login();
            backSysInfoPage();
        }else {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frag_container, new UserCenterFragment());
            ft.commit();
        }
    }
    /**
     * 返回系统消息页面
     */
    public void backSysInfoPage() {
        mSysBtn.setChecked(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Utils.isApplicationInstalled(mContext, new ComponentName("com.neteast.oh", "com.neteast.oh.MainActivity"))) {
            MenuItem help = menu.findItem(R.id.menu_help);
            help.setEnabled(true);
        }
        if (Utils.isApplicationInstalled(mContext, new ComponentName("com.wasu.feedback", "com.wasu.feedback.FeedbackActivity"))) {
            MenuItem feedback = menu.findItem(R.id.menu_feedback);
            feedback.setEnabled(true);
        }
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_about:
            showAboutDialog();
            break;
        case R.id.menu_feedback:
            Intent feedback=new Intent();
            feedback.setComponent(new ComponentName("com.wasu.feedback", "com.wasu.feedback.FeedbackActivity"));
            feedback.putExtra("appcode", ConfigManager.APPCODE);
            startActivity(feedback);
            break;
        case R.id.menu_help:
            Intent help=new Intent();
            help.setComponent(new ComponentName("com.neteast.oh", "com.neteast.oh.MainActivity"));
            startActivity(help);
            break;
        case R.id.menu_upgrade:
            updateApplication(true);
            break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finishWithAnimation();
        overridePendingTransition(0, R.anim.out_left);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            finishWithAnimation();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        registerInfoNewsObserver();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            unRegisterInfoNewsObserver();
        }
    }
    /**
     * 退出前执行退出动画
     */
    public void finishWithAnimation() {
        if (mOutAnimation==null) {
            mOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.out_left);
            mOutAnimation.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {}
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationEnd(Animation animation) {
                    finish();
                }
            });
        }
        mRootView.startAnimation(mOutAnimation);
    }

    @Override
    public void update(Observable observable, Object data) {
        refresh();
    }
    /**
     * 注册监听消息数目变化的观察者
     */
    private void registerInfoNewsObserver() {
        NewsWidget.getIntance(mContext).registerNewsWidgetObserver(this);
    }
    /**
     * 取消注册监听消息数目变化的观察者
     */
    private void unRegisterInfoNewsObserver() {
        NewsWidget.getIntance(mContext).unRegisterNewsWidgetObserver(this);
    }
    
    private void intiContent() {
        mRootView = findViewById(R.id.mainRootView);
        findViewById(R.id.main_close_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finishWithAnimation();
			}
		});
        mSysNum = (TextView) findViewById(R.id.main_sysNum);
        mAppNum = (TextView) findViewById(R.id.main_appNum);
        mUserNum = (TextView) findViewById(R.id.main_userNum);
        
        RadioGroup controller=(RadioGroup) findViewById(R.id.main_controller);
        controller.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mainSys:
                        openSysInfoPage();
                        break;
                    case R.id.mainApp:
                        openAppInfoPage();
                        break;
                    case R.id.mainUser:
                        openUserInfoPage();
                        break;
                    case R.id.mainConfig:
                        openConfigPage();
                        break;
                    case R.id.mainUserCenter:
                        openUserCenterPage();
                        break;
                    default:
                        break;
                }
            }
        });
        mSysBtn = (RadioButton) findViewById(R.id.mainSys);
        backSysInfoPage();
//     刷新消息数目
        refresh();
    }

    /**
     * 开启后台服务
     */
    private void startBackgroundService() {
        Intent widgetService=new Intent(mContext, WidgetService.class);
        startService(widgetService);
        Intent receiveInfoService=new Intent(mContext, ReceiveInfoService.class);
        startService(receiveInfoService);
    }
    /**
     * 打开系统消息页面
     */
    private void openSysInfoPage() {
        NewsWidget.getIntance(mContext).clearSysNumsNum();
        openInfoPage(Information.SYS_INFO);
        refreshNewsColor(mSysNum);
    }
    
    /**
     * 打开应用消息页面
     */
    private void openAppInfoPage() {
        if (CloudAccount.getInstance(mContext).isGuest()) {
            login();
            backSysInfoPage();
        }else {
            NewsWidget.getIntance(mContext).clearAppNumsNum();
            openInfoPage(Information.APP_INFO);
        }
        refreshNewsColor(mAppNum);
    }
    /**
     * 打开用户消息页面
     */
    private void openUserInfoPage() {
        if (CloudAccount.getInstance(mContext).isGuest()) {
            login();
            backSysInfoPage();
        }else {
            NewsWidget.getIntance(mContext).clearUserNumsNum();
            openInfoPage(Information.USER_INFO);
        }
        refreshNewsColor(mUserNum);
    }
    
    private void refreshNewsColor(TextView currentNums) {
		mSysNum.setTextColor(Color.WHITE);
		mAppNum.setTextColor(Color.WHITE);
		mUserNum.setTextColor(Color.WHITE);
		int checkedColor = getResources().getColor(R.color.darkGreen);
		currentNums.setTextColor(checkedColor);
	}

    private void openInfoPage(int infoType) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag_container, InformationListFragment.newInstance(infoType));
        ft.commit();
    }
    
    private void login() {
        Intent intent=new Intent(mContext, LoginActivity.class);
        startActivity(intent);
    }
    
    private void openConfigPage() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag_container, new ConfigFragment());
        ft.commit();
    }

    //刷新界面
    private void refresh() {
        NewsWidget widget = NewsWidget.getIntance(mContext);
        refreshNewsNum(widget.getSysNewsNum(),mSysNum);
        refreshNewsNum(widget.getAppNewsNum(),mAppNum);
        refreshNewsNum(widget.getUserNewsNum(),mUserNum);
    }
    
    /**
     * 刷新消息数目，若消息数目为0 ，则隐藏提示
     * @param newsNum
     * @param numView
     */
    private void refreshNewsNum(int newsNum,TextView numView) {
        if (newsNum>0) {
            numView.setText("（"+newsNum+"）");
            numView.setVisibility(View.VISIBLE);
        }else {
            numView.setVisibility(View.GONE);
        }
    }
    /**
     * 升级应用程序
     */
    private void updateApplication(final boolean prompt) {
        new ApkManager(mContext, new ApkUpdateListener() {
            @Override
            public void onComplete(String message) {
                if (prompt) {
                    Utils.showToast(mContext, message);
                }
            }
        }).startUpdate();
    }
    /**
     * 显示当前版本号对话框
     */
    private void showAboutDialog() {
        Utils.showAlert(mContext, Utils.getVersionInfo(mContext));
    }
}
