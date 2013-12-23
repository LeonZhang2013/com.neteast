package com.neteast.videotv.controller;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import com.android.volley.DefaultRetryPolicy;
import com.google.common.collect.Lists;
import com.neteast.lib.bean.SearchResult.Type;
import com.neteast.videotv.dao.AsyncPosterQuery;
import com.neteast.videotv.dao.Poster;
import com.neteast.videotv.dao.PosterDao;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-21
 * Time: 上午10:29
 */
public class MyVideoController extends VideoFlowController<Poster,Poster.Result> {

    private List<Type> mTypes = Lists.newArrayList();
    private List<Poster> mPosters = Lists.newArrayList();

    public MyVideoController(Activity activity) {
        super(activity);
    }

    @Override
    public void execute(String url, Class<Poster.Result> clazz) {}

    public void execute(SQLiteDatabase db, int panelId){
        AsyncPosterQuery request = PosterDao.generatePanelDataRequest(mListener, db, panelId);
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
    protected void onLoadDataSuccess(Poster.Result response) {
        List<Poster> posters = response.getPosters();
        mPosters.addAll(posters);
        setTypeOnlyFirstTime();
        mPageDataHandler.setData(posters);
        notifyDataChanged(getCurrentPageData());
    }

    private void setTypeOnlyFirstTime() {
        if (mTypes.isEmpty()) {
            for (Poster poster : mPosters) {
                Type type = getType(poster.getType());
                if (type == null){
                    type = new Type();
                    type.setTypeid(poster.getType());
                    type.setTypecount(1);
                    mTypes.add(type);
                }else {
                    type.setTypecount(type.getTypecount()+1);
                }
            }
        }
    }

    @Override
    public void reset() {
        super.reset();
        mPosters.clear();
        mTypes.clear();
    }

    public List<Type> getSearchFilters(){
        return mTypes;
    }

    public void filter(int type){
        if (type==0){
            mPageDataHandler.setData(mPosters);
            notifyDataChanged(getCurrentPageData());
            return;
        }

        mPageDataHandler.setData(getPosterByType(type));
        notifyDataChanged(getCurrentPageData());
    }

    private List<Poster> getPosterByType(int typeId){
        List<Poster> data = Lists.newArrayList();
        for (Poster poster : mPosters) {
           if (poster.getType() == typeId){
               data.add(poster);
           }
        }
        return data;
    }

    private Type getType(int typeId){
        for (Type type : mTypes) {
            if (type.getTypeid() == typeId){
                return type;
            }
        }
        return null;
    }

    public int getResultCount(){
        return mPosters.size();
    }
}
