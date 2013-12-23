package com.neteast.videotv.io;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.github.kevinsawicki.http.HttpRequest;
import com.neteast.lib.bean.Filter;
import com.neteast.lib.bean.FilterRaw;
import com.neteast.lib.bean.MenuRaw;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.videotv.TVApplication;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-17
 * Time: 下午4:08
 */
public class FilterRequest extends Request<Filter> {

    private final Response.Listener<Filter> mListener;
    private final String menuDocApi = TVApplication.API_MENU_DOC;

    public FilterRequest(String url,Response.Listener<Filter> listener, Response.ErrorListener error) {
        super(Method.GET, url, error);
        mListener = listener;
    }

    @Override
    protected Response<Filter> parseNetworkResponse(NetworkResponse response) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(response.data);
            Serializer serializer=new Persister();
            List<MenuRaw> menuList = serializer.read(MenuRaw.Result.class, bais, false).getMenuList();
            FilterRaw filterRaw=new FilterRaw();
            for (MenuRaw menuRaw : menuList) {
                String url=String.format(menuDocApi,menuRaw.getKeywords());
                VideoRaw.Result result = getFromNet(VideoRaw.Result.class, url);
                filterRaw.addTypes(menuRaw.getTitle(),result.getVideoRawList());
            }
            return Response.success(new Filter(filterRaw),HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            VolleyLog.e(e, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Filter response) {
        mListener.onResponse(response);
    }


    private <T> T getFromNet(Class<T> t,String api) throws Exception {
        HttpRequest request = HttpRequest.get(api);
        InputStream in = request.getConnection().getInputStream();
        Serializer serializer= new Persister();
        return serializer.read(t, in,false);
    }
}
