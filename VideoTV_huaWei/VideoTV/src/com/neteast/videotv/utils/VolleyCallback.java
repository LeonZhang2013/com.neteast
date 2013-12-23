package com.neteast.videotv.utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-14
 * Time: 上午10:57
 */
public interface VolleyCallback {
    public ImageLoader getImageLoader();
    public RequestQueue getRequestQueue();
}
