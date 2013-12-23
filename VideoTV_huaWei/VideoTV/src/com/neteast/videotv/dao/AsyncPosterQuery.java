package com.neteast.videotv.dao;

import android.database.sqlite.SQLiteDatabase;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.common.collect.Lists;
import nl.qbusict.cupboard.QueryResultIterable;

import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-15
 * Time: 下午2:04
 */
public class AsyncPosterQuery extends Request<Poster.Result> {

    private final SQLiteDatabase db;
    private final String selection;
    private final String[] selectionArgs;
    private final Response.Listener<Poster.Result> listener;

    public AsyncPosterQuery(Response.Listener<Poster.Result> listener,SQLiteDatabase db,String selection, String ... selectionArgs) {
        super(Method.GET,"http://www.baidu.com",null);
        this.db=db;
        this.selection=selection;
        this.selectionArgs=selectionArgs;
        this.listener = listener;
    }

    @Override
    protected Response<Poster.Result> parseNetworkResponse(NetworkResponse response) {
        QueryResultIterable<Poster> query = cupboard().withDatabase(db).
                query(Poster.class).withSelection(selection, selectionArgs)
                .orderBy("modified DESC").query();
        List<Poster> posters= Lists.newArrayList();
        for (Poster poster : query) {
            posters.add(poster);
        }
        query.close();
        Poster.Result result = new Poster.Result();
        result.setPosters(posters);
        return Response.success(result,null);
    }

    @Override
    protected void deliverResponse(Poster.Result response) {
        listener.onResponse(response);
    }
}
