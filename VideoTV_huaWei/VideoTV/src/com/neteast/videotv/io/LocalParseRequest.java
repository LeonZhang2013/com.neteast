package com.neteast.videotv.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.neteast.videotv.TVApplication;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-17
 * Time: 下午2:54
 */
public class LocalParseRequest extends Request<String> {


    private final Response.Listener<String> mListener;
    private final String mTag;

    public LocalParseRequest(String url, String tag, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.mListener  = listener;
        this.mTag = tag;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.data)));
            String result;
            if (mTag.equals("优酷")){
                result= youkuParse(reader);
            }else if(mTag.equals("土豆")){
                result= tudouParse(reader);
            }else if(mTag.equals("搜狐")){
            	result = sohuParse(reader);
            }else if (mTag.equals("my搜狐")) {
				result = mySohuParse(reader);
			}
            else {
                throw new Exception("TAG："+mTag+" 不合法");
            }
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            VolleyLog.e(e, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }
    
    private String mySohuParse(BufferedReader reader) throws Exception {
		try {
			String jsonText = reader.readLine();
			String m3u8 = parseJson(jsonText, "oriVid");
			if (TextUtils.isEmpty(m3u8)) {
				m3u8 = parseJson(jsonText, "highVid");
			}
			if (TextUtils.isEmpty(m3u8)) {
				m3u8 = parseJson(jsonText, "superVid");
			}
			if (TextUtils.isEmpty(m3u8)) {
				m3u8 = parseJson(jsonText, "norVid");
			}
			if (TextUtils.isEmpty(m3u8)) {
				throw new RuntimeException("没有可播放资源");
			}
			Log.i("test", m3u8);
			m3u8 = m3u8.substring("http://".length());
			return TVApplication.SOHU_HOST+m3u8;
		} finally {
			silentClose(reader);
		}
	}
    
    private String parseJson(String jsonText ,String key){
        key = key+":\"";
        int index = jsonText.indexOf(key) + key.length();
        return jsonText.substring(index, jsonText.indexOf("\",",index));
    }
    
	

    private String sohuParse(BufferedReader reader) throws Exception {
    	String reg = "var videoData =(.+)";
    	String jsonText = parseHTML(reader, reg);
    	if (jsonText.endsWith(";")) {
			jsonText = jsonText.substring(0,jsonText.length()-1);
			jsonText = jsonText.trim();
		}
    	JSONObject jobj = new JSONObject(jsonText);
    	
    	String m3u8 = jobj.getString("oriVid");
    	if (TextUtils.isEmpty(m3u8)) {
    		m3u8= jobj.getString("highVid");
		}
    	if (TextUtils.isEmpty(m3u8)) {
			m3u8= jobj.getString("superVid");
		}
    	if (TextUtils.isEmpty(m3u8)) {
			m3u8= jobj.getString("norVid");
		}
    	if (TextUtils.isEmpty(m3u8)) {
    		throw new RuntimeException("没有可用播放资源");
		}
    	m3u8 = m3u8.substring("http://".length());
		return TVApplication.SOHU_HOST + m3u8;
	}

	@Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    private String youkuParse(BufferedReader reader) throws Exception {
        String reg="^var\\s+videoId\\s+=\\s+'(\\d+)';$";
        String videoId = parseHTML(reader, reg);
        if (videoId!=null){
            String playUrl="http://pl.youku.com/playlist/m3u8?vid=%s&type=hd2&ts=%d&keyframe=1";
            return String.format(playUrl,videoId,System.currentTimeMillis()/1000);
        }else {
           throw new Exception("本地解析"+getUrl()+"失败");
        }
    }

    private String tudouParse(BufferedReader reader) throws Exception {
        String reg = "^,vcode: .(\\w+).$";
        String videoId = parseHTML(reader, reg);
        if (videoId!=null){
            String playUrl="http://v.youku.com/player/getM3U8/vid/%s/type/hd2/ts/%d/sc/2/video.m3u8";
            return String.format(playUrl,videoId,System.currentTimeMillis()/1000);
        }else {
           throw new Exception("本地解析"+getUrl()+"失败");
        }
    }

    private String parseHTML(BufferedReader reader, String reg) throws IOException {
        String result = null;
        try {
            Pattern pattern=Pattern.compile(reg);
            Matcher matcher;
            String lineValue;
            while ((lineValue=reader.readLine())!=null){
                matcher = pattern.matcher(lineValue);
                if (matcher.find()){
                    result=matcher.group(1);
                    Log.i("test", "videoId=" + result);
                    break;
                }
            }
        } finally {
        	silentClose(reader);
        }
        return result;
    }

    private void silentClose(Reader reader){
        if (reader!=null){
            try {
                reader.close();
            } catch (IOException e) {}
        }
    }
}
