package com.neteast.androidclient.newscenter.domain;

import com.neteast.androidclient.newscenter.R;
import com.neteast.androidclient.newscenter.exception.AuthorizeDialogError;
import com.neteast.androidclient.newscenter.exception.AuthorizeException;
import com.neteast.androidclient.newscenter.exception.SnsPublishException;
import com.neteast.androidclient.newscenter.utils.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.IOException;


public abstract class SNSEntity {

    public static enum SnsType{
        QQWeiBo,SinaWeiBo,RenRen,KaiXin;
    }
    
    public static SNSEntity getEntity(Context context,SnsType type) {
        switch (type) {
            case QQWeiBo:
                return QQWeiBo.getInstance(context);
            case SinaWeiBo:
                return SinaWeiBo.getInstance(context);
            case RenRen:
                return RenRen.getInstance(context);
            case KaiXin:
                return KaiXin.getInstance(context);
            default:
                throw new RuntimeException("错误的类型："+type);
        }
    }
    
    protected SNSEntity(Context context) {
        mContext=context;
    }
    
    protected Context mContext;
    /**
     * 授权
     * @param manager
     * @param listener
     */
    public void authorize(FragmentManager fm,final AuthListener listener){
        AuthorizeDialog authorizeDialog = new AuthorizeDialog(this,listener);
        authorizeDialog.show(fm, "authorize");
    }
    /**
     * 取消授权
     */
    public abstract void deauthorize();
    /**
     * 当前授权是否有效
     * @return
     */
    public abstract boolean isSessionValid();
    /**
     * 发布一条消息
     * @param message 消息内容
     * @throws IOException,SnsPublishException 
     */
    public abstract void publishMessage(String message,PublishListener listener) throws SnsPublishException;
    /**
     * 发布一条带图片的消息
     * @param message 消息内容
     * @param filePath 图片路径
     * @throws IOException,SnsPublishException 
     */
    public abstract void publishMessageWithPicture(String message,String filePath,PublishListener listener) throws SnsPublishException;
    /**
     * 处理授权完成时，服务器回调的url
     * @param url
     * @param mListener
     */
    protected abstract void handleRedirectUrl(String url, AuthListener mListener);
    /**
     * 重定向地址
     * @return
     */
    protected abstract String getRedirectUrl();
    /**
     * 授权访问地址
     * @return
     */
    protected abstract String getAuthorizeUrl();
    
    
    
    /**
     * 授权的对话框
     * @author emellend
     *
     */
    @SuppressLint("SetJavaScriptEnabled")
    private static class AuthorizeDialog extends DialogFragment implements OnClickListener{
        
        private WebView mWeb;
        private View mLoading;
        private AuthListener mListener;
        private SNSEntity mSnsEntity;
        private TextView mTitle;
        
        public AuthorizeDialog(SNSEntity snsEntity,AuthListener listener) {
            mListener=listener;
            mSnsEntity=snsEntity;
        }
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            CookieSyncManager.createInstance(getActivity());
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoFrameDialog);
        }
        
        
        @Override
        public void onStop() {
            super.onStop();
            CookieSyncManager.getInstance().stopSync();
            mWeb.stopLoading();
        }
        
        @Override
        public void onDestroy() {
            super.onDestroy();
            mWeb.destroy();
        }
        
        @Override
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            mListener.onCancel();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.frag_authorize_dialog, container, false);
            //取消按钮
            contentView.findViewById(R.id.authorize_back).setOnClickListener(this);
            //进度条
            mLoading=contentView.findViewById(R.id.authorize_loading);
            //对话框标题
            mTitle=(TextView) contentView.findViewById(R.id.authorize_title);
            //浏览器
            mWeb=(WebView) contentView.findViewById(R.id.authorize_web);
            mWeb.setVerticalScrollBarEnabled(false);
            mWeb.setHorizontalScrollBarEnabled(false);
            mWeb.getSettings().setJavaScriptEnabled(true);
            mWeb.getSettings().setPluginState(PluginState.ON);
            mWeb.getSettings().setPluginsEnabled(true);
            mWeb.setWebViewClient(new AuthorizeWebViewClient());
            mWeb.setWebChromeClient(new AuthorizeChrome());
            mWeb.loadUrl(mSnsEntity.getAuthorizeUrl());
            mWeb.requestFocus();
            mWeb.requestFocusFromTouch();
            return contentView;
        }
        
        public class AuthorizeChrome extends WebChromeClient{
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                getActivity().getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
                if (newProgress==100) {
                    CookieSyncManager.getInstance().sync();
                }
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (TextUtils.isEmpty(title)) {
                    title="";
                }
                mTitle.setText(title);
            }
        }
        
        public class AuthorizeWebViewClient extends WebViewClient {

            @Override
            public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LogUtil.i("onReceivedError=" + failingUrl);
                mListener.onError(new AuthorizeDialogError(description, errorCode, failingUrl));
                dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.i("onPageStarted=" + url);
                if (url.startsWith(mSnsEntity.getRedirectUrl())) {
                    handleRedirectUrl(url);
                    view.stopLoading();
                    dismiss();
                    return;
                }
                super.onPageStarted(view, url, favicon);
                showLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtil.i("onPageFinished URL: " + url);
                super.onPageFinished(view, url);
                hidenLoading();
                CookieSyncManager.getInstance().sync();
            }
            
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        }
        
        private void showLoading() {
            mLoading.setVisibility(View.VISIBLE);
            mWeb.setVisibility(View.GONE);
        }
        
        private void hidenLoading() {
            mLoading.setVisibility(View.GONE);
            mWeb.setVisibility(View.VISIBLE);
        }

        public void handleRedirectUrl(String url) {
            mSnsEntity.handleRedirectUrl(url,mListener);
        }

        @Override
        public void onClick(View v) {
            if (v.getId()==R.id.authorize_back) {
                dismiss();
                mListener.onCancel();
            }
        }
    }
    
     public static interface PublishListener{
         /**
          * 当发布成功后调用此方法
          * @param message
          */
         public void onPublishComplete(String message);
     }
     
     public static interface AuthListener {

        /**
         * 认证结束后将调用此方法
         */
        public void onComplete();

        /**
         * 当认证过程中捕获到AuthorizeException时调用
         * @param e AuthorizeException
         * 
         */
        public void onAuthorizeException(AuthorizeException e);

        /**
         * Oauth2.0认证过程中，当认证对话框中的webview接收数据出现错误时调用此方法
         * @param e AuthorizeDialogError
         * 
         */
        public void onError(AuthorizeDialogError e);

        /**
         * Oauth2.0认证过程中，如果认证窗口被关闭或认证取消时调用
         */
        public void onCancel();

    }
}
