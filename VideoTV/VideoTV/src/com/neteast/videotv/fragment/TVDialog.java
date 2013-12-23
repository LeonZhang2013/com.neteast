package com.neteast.videotv.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.neteast.videotv.io.XmlRequest;
import com.neteast.videotv.utils.VolleyCallback;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-3
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class TVDialog extends DialogFragment{
    protected static final int REQUEST_TIME_OUT=10*1000;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Panel);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        VolleyCallback callback = (VolleyCallback) getActivity().getApplication();
        mRequestQueue = callback.getRequestQueue();
        mImageLoader = callback.getImageLoader();
    }

    @Override
    public void onDetach() {
        mRequestQueue.cancelAll(this);
        super.onDetach();
    }

    protected RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    protected ImageLoader getImageLoader() {
        return mImageLoader;
    }

    protected <T> void executeXmlRequest(String url,Class<T> clazz,Response.Listener<T> listener){
        executeXmlRequest(url,clazz,listener,onLoadError);
    }

    protected <T> void executeXmlRequest(String url,Class<T> clazz,Response.Listener<T> listener,Response.ErrorListener errorListener){
        XmlRequest<T> request = new XmlRequest<T>(XmlRequest.Method.GET,url,clazz,listener,errorListener);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    private Response.ErrorListener onLoadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
        }
    };
}
