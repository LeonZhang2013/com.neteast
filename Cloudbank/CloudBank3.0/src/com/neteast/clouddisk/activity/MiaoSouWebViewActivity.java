package com.neteast.clouddisk.activity;

import com.lib.cloud.LibCloud;
import com.neteast.clouddisk.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class MiaoSouWebViewActivity extends Activity {
	

	WebView mWebView;
	private String name =null;
	private LibCloud libCloud;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.miaosou);
        System.out.println("WebViewActivity  onCreate" );
        libCloud = LibCloud.getInstance(this);
        
        Intent i = getIntent();
        name = i.getStringExtra("name");
        mWebView = (WebView) findViewById(R.id.miaosouwebview); 
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new HelloWebViewClient());
        mWebView.getSettings().setSupportZoom(true); 
        // mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR); 
        mWebView.getSettings().setBuiltInZoomControls(true);
        
        WebSettings.PluginState localPluginState = WebSettings.PluginState.ON;
        mWebView.getSettings().setPluginState(localPluginState);
        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.220 Safari/535.1");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new CallJava(), "CallJava");
        
        Button returnbt = (Button) findViewById(R.id.miaosou_back);
        returnbt.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				MiaoSouWebViewActivity.this.finish();
			}
		});
        
        String userid = libCloud.GetUserID();
        String token = libCloud.GetToken();
        String appcode = libCloud.GetAppcode();
        //String url = "http://118.144.81.24:8888/Search/miaosou/mname/" + name + "/userid/" + userid + "/token/" +token + "/appcode/" + appcode;
       
        String url = "http://miaosoo.cbb.wasu.com.cn/Search/clouddisk/search/"+name+"/tid/1"+ "/userid/" + userid + "/token/" +token + "/appcode/" + appcode;
        mWebView.loadUrl(url);
    }
    
    private class HelloWebViewClient extends WebViewClient { 
        @Override 
        public boolean shouldOverrideUrlLoading(WebView view, String url) { 
        	System.out.println("shouldOverrideUrl loading !!!!!!1 url = " + url);
        	view.loadUrl(url); 
            return true; 
        } 
    }

	 public void Dialog(String message){
		 if(MiaoSouWebViewActivity.this!=null){
		     AlertDialog.Builder builder = new Builder(MiaoSouWebViewActivity.this);
			     if(builder!=null){
			     Log.d(this.toString(), "Dialog message =" + message);
			     builder.setMessage(message);
			     builder.setTitle(R.string.tips);
			     builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				      @Override
					  public void onClick(DialogInterface dialog, int which) {
					       dialog.dismiss();
				      }
		
			     });
			     builder.create().show();
		     }
		 }
	}
    
    private final class CallJava{
    	public void DownloadDialog(String message){
			Log.d(this.toString(), "Call Java DownloadDialog message =" +message);
			Dialog(message);
		}
	}
    
}