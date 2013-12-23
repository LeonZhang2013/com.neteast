package com.neteast.videotv.io;

import android.database.sqlite.SQLiteDatabase;
import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.neteast.lib.bean.MenuRaw;
import com.neteast.videotv.dao.Menu;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.util.List;

import static com.neteast.videotv.dao.MenuDao.*;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-16
 * Time: 上午11:07
 */
public class TopicChannelRequest extends Request<Menu.Result> {

    private final Response.Listener<Menu.Result> listener;
    SQLiteDatabase db;
    String api;

    public TopicChannelRequest(SQLiteDatabase db,String api,Response.Listener<Menu.Result> listener,Response.ErrorListener errorListener) {
        super(Method.GET,api,errorListener);
        this.db=db;
        this.api=api;
        this.listener = listener;
    }

    @Override
    protected Response<Menu.Result> parseNetworkResponse(NetworkResponse response) {

        ByteArrayInputStream bais = new ByteArrayInputStream(response.data, 0, response.data.length);
        Serializer serializer=new Persister();
        try {
            List<MenuRaw> menuList = serializer.read(MenuRaw.Result.class, bais, false).getMenuList();
            boolean hasNewChannel = false;
            for (int i=0,size=menuList.size();i<size;i++){
                MenuRaw raw=menuList.get(i);
                Menu menu = convert(i, raw);
                if (!exist(db,menu)){
                    hasNewChannel = true;
                    break;
                }
            }
            if (hasNewChannel){
                deleteAll(db);
                for (int i=0,size=menuList.size();i<size;i++){
                    MenuRaw raw=menuList.get(i);
                    Menu menu = convert(i, raw);
                    addMenu(db,menu);
                }
            }
            List<Menu> allMenu = getAllMenu(db);
            Menu.Result result = new Menu.Result();
            result.setMenus(allMenu);
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            VolleyLog.e(e, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(Menu.Result response) {
        listener.onResponse(response);
    }
}
