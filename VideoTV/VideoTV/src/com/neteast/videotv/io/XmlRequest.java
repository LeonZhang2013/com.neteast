package com.neteast.videotv.io;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-9-2
 * Time: 下午3:06
 */
public class XmlRequest<T> extends Request<T>{

    private final Class<T> mClazz;
    private Response.Listener<T> mListener;

    public XmlRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener=listener;
        mClazz=clazz;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        ByteArrayInputStream bais = new ByteArrayInputStream(response.data, 0, response.data.length);
        Serializer serializer=new Persister();
        try {
            return Response.success(serializer.read(mClazz, bais,false), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            VolleyLog.e(e,e.getMessage());
            return Response.error(new ParseError(e));
        }

    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
