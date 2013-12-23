package com.neteast.androidclient.newscenter.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Utils {
    /**
     * 从输入流里读取数据
     * @param in
     * @param charsetName 默认为UTF-8编码
     * @return
     */
    public static String readDataFromStream(InputStream in,String charsetName){
        if (charsetName==null) {
            charsetName="UTF-8";
        }
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        BufferedInputStream inputStream=new BufferedInputStream(in);
        byte[] buffer=new byte[1024];
        int len=-1;
        try {
            while ((len=inputStream.read(buffer))>0) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
        String data="";
        try {
            data = new String(baos.toByteArray(), charsetName);
        } catch (UnsupportedEncodingException e) {}
        return data;
    }
    /**
     * 执行HttpGet请求，返回字符串
     * @param path
     * @return
     * @throws IOException 
     */
    public static String doGet(String path) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            return new String(readDataFromStream(is, null));
        } finally {
            if (is != null) {
                try {  is.close(); } catch (IOException e) {}
            }
        }
    }
    
    /**
     * 执行HttpPost请求
     * @param path
     * @return
     * @throws IOException 
     */
    public static String doPost(String path,HttpEntity reqEntity) throws IOException{
       return doPost(new DefaultHttpClient(), path, reqEntity);
    }
    
    /**
     * 执行HttpsPost请求
     * @param path
     * @return
     * @throws IOException 
     */
    public static String doHttpsPost(String path,HttpEntity reqEntity) throws IOException{
        return doPost(getHttpsClient(), path, reqEntity);
    }
    
    private static String doPost(HttpClient client,String path,HttpEntity reqEntity) throws IOException {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpPost httpPost=new HttpPost(path);
            httpPost.setParams(params);
            httpPost.setEntity(reqEntity);
            HttpResponse response = client.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            return EntityUtils.toString(resEntity);
        } finally{
            client.getConnectionManager().shutdown();
        }
    }
    
    
    public static class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port,
                boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host,
                    port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
    
    public static DefaultHttpClient getHttpsClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
            truststore.load(null, null);
            SSLSocketFactory socketFactory = new MySSLSocketFactory(truststore);
            Scheme sch = new Scheme("https", socketFactory, 443);
            client.getConnectionManager().getSchemeRegistry().register(sch);
            client.getParams().setParameter(HttpProtocolParams.HTTP_CONTENT_CHARSET, "UTF-8");
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
        return client;
    }
    /**
     * 解析Url，将其中的查询字符串和引用部分的key=value&key=value拆分成key-value的映射表</BR>
     * 并对key和value进行URLDecoder解码，解码格式是UTF-8
     * @param url
     * @return
     */
    public static Bundle parseUrl(String url) {
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }
    /**
     * 将key=value&key=value的字符串拆分成 key-value的映射表</BR>
     * 并对key和value进行URLDecoder解码，解码格式是UTF-8
     * @param s
     * @return
     */
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                try {
                    if (v.length>1) {
                        params.putString(URLDecoder.decode(v[0],"UTF-8"), URLDecoder.decode(v[1],"UTF-8"));
                    }else {
                        params.putString(URLDecoder.decode(v[0],"UTF-8"), "");
                    }
                } catch (UnsupportedEncodingException e) {}
            }
        }
        return params;
    }
    /**
     * 将key-value的Map，组拼成 key=value&key=value的形式。</BR>
     * key和value都用URLEncoder编码，编码格式是UTF-8
     * @param params
     * @return
     */
    public static String encoderUrl(Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            
            if (index != 0) {
                sb.append("&");
            }
            
            String key=entry.getKey();
            String value=entry.getValue();
            
            try {
                key=URLEncoder.encode(key, "UTF-8");
                value =  ( ( value== null)  ?  ""  :  URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {}
            
            sb.append(key).append("=").append(value);
            index++;
        }
        return sb.toString();
    }
    
    public static void clearAllCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    public static void clearSessionCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
    }
    /**
     * 将数据保存到文件
     * @param in
     * @param out
     * @throws IOException 
     */
    public static void downloadFile(InputStream in,FileOutputStream out) throws IOException {
        BufferedInputStream input=null;
        BufferedOutputStream output=null;
        try{
            byte[] buffer=new byte[1024];
            input =new BufferedInputStream(in);
            output =new BufferedOutputStream(out);
            int len=-1;
            while ((len=input.read(buffer))>0) {
                output.write(buffer, 0, len);
                output.flush();
            }
        }finally{
            if (input!=null) {
                input.close();
            }
            if (output!=null) {
                output.close();
            }
        }
    }
    /**
     * 将独立像素转换为像素，通常用于不得已的硬编码
     * @param context
     * @param dpInput
     * @return
     */
    public static int dp2px(Context context,int dpInput){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpInput * scale + 0.5f);
    }
    /**
     * 显示土司提示
     * @param context
     * @param text
     */
    public static void showToast(Context context,String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 显示关闭提示对话框
     * 只有一个确定按钮，用户点击，结束当前activity
     * @param activity
     * @param message
     */
    public static void showCancelDialog(final Activity activity, String message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        }).create().show();
    }
    /**
     * 显示关闭确认对话框，点击确定的话，关闭当前activtiy，点击取消，则效果
     * @param activity
     */
    public static void showExitConfirmDialog(final Activity activity) {
        new Builder(activity).setTitle("提示").setMessage("确定退出？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }
    /**
     * 显示提示对话框
     * @param activity
     * @param message
     */
    public static final void showAlert(final Activity activity, String message) {
        new AlertDialog.Builder(activity).setTitle("提示").setMessage(message)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
    /**
     * 某应用是否安装
     * @param context
     * @param cn
     * @return
     */
    public static boolean isApplicationInstalled(Context context,ComponentName cn) {
        PackageManager pm = context.getPackageManager();
        Intent intent=new Intent();
        intent.setComponent(cn);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return list.size()>0;
    }
    
    public static String getVersionInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        StringBuilder sb=new StringBuilder();
        try {
            sb.append("当前版本：");
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append(packageInfo.versionName);
            return sb.toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String base64(String input) {
        String result = input;
        try {
            result = Base64.encodeToString(input.getBytes("UTF-8"),
                    Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 进行MD5编码
     * @param message
     * @return
     */
    public static String md5(String message) {
        String result = message;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            BigInteger hash = new BigInteger(1, md.digest());
            result = hash.toString(16);
            if ((result.length() % 2) != 0) {
                result = "0" + result;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }
}
