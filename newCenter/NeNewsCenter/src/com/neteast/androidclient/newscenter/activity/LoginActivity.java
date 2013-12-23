package com.neteast.androidclient.newscenter.activity;

import com.neteast.androidclient.newscenter.ConfigManager;
import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.exception.LoginException;
import com.neteast.androidclient.newscenter.provider.CloudAccountColumns;
import com.neteast.androidclient.newscenter.utils.JsonHelper;
import com.neteast.androidclient.newscenter.utils.LogUtil;
import com.neteast.androidclient.newscenter.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LoginActivity extends Activity implements OnClickListener {
    /**只有其他云宽带应用调用消息盒子登录模块时会通过改意图隐式调用。
     * 消息盒子本身登录时，显示调用本Activity
     * */
    private static final String ACTION = "com.neteast.androidclient.newscenter.login";
    
    private EditText mPassword;
    private EditText mAccount;
    private View mLoading;
    private TextView mWarning;

    private Button mBtnLogin;

    private UIHandler mUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_login);
        mUIHandler = new UIHandler(this);
        initContent();
    }

    private void initContent() {
        mWarning = (TextView) findViewById(R.id.login_warning);
        mLoading = findViewById(R.id.login_progress);
        mAccount = (EditText) findViewById(R.id.login_account);
        mPassword = (EditText) findViewById(R.id.login_password);
        mBtnLogin = (Button) findViewById(R.id.login_ok);
        mBtnLogin.setOnClickListener(this);
        findViewById(R.id.login_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_ok:
                login();
                break;
            case R.id.login_cancel:
                cancel();
                break;
            default:
                break;
        }
    }


    /**
     * 执行登录操作
     */
    private void login() {
        final String account = mAccount.getText().toString();
        String password = mPassword.getText().toString();
        boolean validate = validateInput(account, password);
        if (!validate) {
            warning("帐号或者密码不能为空");
            return;
        }
        disableInput();
        showProgress();
        warning("登录中");
        final UrlEncodedFormEntity reqEntity = generatePostEntity(account, password);
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
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, "网络异常，请重新登录").sendToTarget();
                } catch (JSONException e) {
                    LogUtil.printException(e);
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, "服务器返回数据异常，请重新登录").sendToTarget();
                } catch (LoginException e) {
                    LogUtil.printException(e);
                    mUIHandler.obtainMessage(UIHandler.LOGIN_ERROR, e.getMessage()).sendToTarget();
                }
            }
        }).start();
    }
    /**
     * 取消退出
     */
    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
    /**
     * 禁止输入
     */
    private void disableInput() {
        mAccount.setEnabled(false);
        mPassword.setEnabled(false);
        mBtnLogin.setEnabled(false);
    }
    /**
     * 允许输入
     */
    private void enableInput() {
        mAccount.setEnabled(true);
        mPassword.setEnabled(true);
        mBtnLogin.setEnabled(true);
    }
    /**
     * 显示进度条
     */
    private void showProgress(){
        mLoading.setVisibility(View.VISIBLE);
    }
    /**
     * 显示登录进度
     */
    private void hidenProgress(){
        mLoading.setVisibility(View.GONE);
    }
    /**
     * 提示用户
     * @param text
     */
    private void warning(String text) {
        mWarning.setVisibility(View.VISIBLE);
        mWarning.setText(text);
    }
    /**
     * 检查用户输入是否合法
     * @param account 帐号
     * @param password 密码
     * @return 若帐号或者密码为空，则不合法
     */
    private boolean validateInput(String account,String password) {
        return !TextUtils.isEmpty(account)&& !TextUtils.isEmpty(password);
    }
    
    /**
     * 生成POST请求需要的参数
     * @param account
     * @param password
     * @return
     */
    private UrlEncodedFormEntity  generatePostEntity(String account,String password) {
        password=Utils.md5(password);
        String reqstr = Utils.base64("user=" + account+"&password=" + password);
        String verify = Utils.md5("user="+ account + "&password=" + password + ConfigManager.KEY);
        
        ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
        BasicNameValuePair value = new BasicNameValuePair("appcode", ConfigManager.APPCODE);
        valuePairs.add(value);
        value=new BasicNameValuePair("reqstr", reqstr);
        valuePairs.add(value);
        value=new BasicNameValuePair("verify", verify);
        valuePairs.add(value);
        value=new BasicNameValuePair("datatype", "j");
        valuePairs.add(value);
        UrlEncodedFormEntity entity=null;
        try {
            entity = new UrlEncodedFormEntity(valuePairs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtil.printException(e);//不可能发生
        }
        return entity;
    }
    
    /**
     * 解析登录后服务器返回的json，得到一个LoginData的数据对象
     * @param json
     * @return LoginData
     * @throws JSONException
     * @throws LoginException
     */
    private LoginData parseLoginResultJson(String json) throws JSONException, LoginException {
        JSONObject jobj = new JSONObject(json);
        int code = JsonHelper.readInt(jobj, "code");
        switch (code) {
            case 1:
                LoginData loginData = new LoginData();
                loginData.userId = JsonHelper.readInt(jobj, "userid");
                loginData.token = JsonHelper.readString(jobj, "token");
                return loginData;
            case 301:
                throw new LoginException("用户不存在");
            case 302:
                throw new LoginException("用户名或者密码错误");
            default:
                throw new LoginException("网络异常，请重新登录");
        }
    }
    /**
     * 登录成功
     * @param loginData
     */
    private void onLoginSuccess(LoginData loginData) {
        hidenProgress();
        warning("登录成功");
        sendLoginBroadcast(loginData);
        if (shouldLaunchApplicaion()) {
            launchApplication();
        }
        finish();
    }
    
    /**
     * 启动需要调用的应用
     */
    private void launchApplication() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getLaunchAppPackage(), getLaunchAppClass()));
        startActivity(intent);
    }

    /**
     * 发送登录成功的广播
     * @param loginData
     */
    private void sendLoginBroadcast(LoginData loginData) {
        Intent intent=new Intent(ConfigManager.ACTION_LOGIN_CHANGED);
        intent.putExtra(CloudAccountColumns.USERID, loginData.userId);
        intent.putExtra(CloudAccountColumns.TOKEN, loginData.token);
        intent.putExtra(CloudAccountColumns.ACCOUNT, loginData.account);
        sendBroadcast(intent);
    }

    /**
     * 是否应该调用别的应用？</BR>
     * 若是隐式调用本类，并且携带了包名和类名的，那么应该调用
     * @return
     */
    private boolean shouldLaunchApplicaion() {
        if (ACTION.equals(getIntent().getAction())) {
            String pkg = getLaunchAppPackage();
            String cls = getLaunchAppClass();
            return !TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(cls);
        }
        return false;
    }
    
    /**
     * 得到调用本activity的调用者的包名
     * @return 若没有，则为null
     */
    private String getLaunchAppPackage() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            return args.getString("pkg");
        }
        return null;
    }
    /**
     * 得到调用本activity的调用者的完整类名
     * @return 若没有，则为null
     */
    private String getLaunchAppClass() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            return args.getString("cls");
        }
        return null;
    }

    /**
     * 登录失败
     * @param description
     */
    private void onLoginError(String description) {
        hidenProgress();
        warning(description);
        enableInput();
    }
    
    /**
     * 登录结果的数据集
     *
     */
    private class LoginData{
        int userId;
        String token;
        String account;
    }
    
    private static class UIHandler extends Handler{
        public static final int LOGIN_SUCCESS=1;
        public static final int LOGIN_ERROR=2;
        
        private LoginActivity mActivity;
        
        public UIHandler(LoginActivity activity) {
            this.mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    mActivity.onLoginSuccess((LoginData)msg.obj);
                    break;
                case LOGIN_ERROR:
                    mActivity.onLoginError((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}
