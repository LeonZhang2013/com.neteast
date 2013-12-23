package com.neteast.videotv.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Date: 13-7-25
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class NetFragment extends Fragment {
    protected static final int REQUEST_TIME_OUT=10*1000;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VolleyCallback callback = (VolleyCallback) getActivity().getApplication();
        mRequestQueue = callback.getRequestQueue();
        mImageLoader = callback.getImageLoader();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    protected ImageLoader getImageLoader() {
        return mImageLoader;
    }

    protected <T> void executeXmlRequest(String url,Class<T> clazz,Response.Listener<T> listener){
        XmlRequest<T> request = new XmlRequest<T>(XmlRequest.Method.GET,url,clazz,listener,onLoadError);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    @Override
    public void onDetach() {
        mRequestQueue.cancelAll(this);
        super.onDetach();
    }

    private Response.ErrorListener onLoadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
        }
    };

    public boolean handleBackPressed(){return false;}

}
