package com.neteast.videotv.controller;

import android.app.Activity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.neteast.videotv.io.XmlRequest;
import com.neteast.videotv.utils.VolleyCallback;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-15
 * Time: 下午4:09
 */
public abstract class VideoFlowController<PAGE_ENTITY,REQUEST_ENTITY> {

    protected static final int LOAD_ALL =Integer.MAX_VALUE;


    protected static final int REQUEST_TIME_OUT=10*1000;
    protected final Activity mActivity;
    protected PageListener<PAGE_ENTITY> mPageListener;
    protected final PageDataHandler<PAGE_ENTITY> mPageDataHandler;
    protected final RequestQueue mRequestQueue;
    protected String mUrl;
    protected int mHasLoadedPage;
    protected int mPreLoadPageNumber = LOAD_ALL;

    public VideoFlowController(Activity activity) {
        this.mActivity = activity;
        VolleyCallback callback = (VolleyCallback) mActivity.getApplication();
        mRequestQueue = callback.getRequestQueue();
        mPageDataHandler = new PageDataHandler<PAGE_ENTITY>();
    }

    public void setSinglePageItem(int itemNumber) {
        mPageDataHandler.setSinglePageItem(itemNumber);
    }

    //
    public void execute(String url,Class<REQUEST_ENTITY> clazz){
        this.mUrl = url;
        System.out.println("VideoFlowController   =====================          "+url);
        XmlRequest<REQUEST_ENTITY> request = new XmlRequest<REQUEST_ENTITY>(XmlRequest.Method.GET,url,clazz,mListener,mError);
        request.setTag(url);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        if (mPreLoadPageNumber!=LOAD_ALL){
            mHasLoadedPage += mPreLoadPageNumber;
        }
        mRequestQueue.add(request);
        mRequestQueue.start();
    }


    public List<PAGE_ENTITY> getCurrentPageData() {
        return mPageDataHandler.getCurrentPageData();
    }

    public boolean hasNextPage() {
        return mPageDataHandler.hasNextPage();
    }

    public List<PAGE_ENTITY> getNextPageData() {
        return mPageDataHandler.getNextPageData();
    }

    public boolean hasPrevPage() {
        return mPageDataHandler.hasPrevPage();
    }

    public List<PAGE_ENTITY> getPrevPageData() {
        return mPageDataHandler.getPrevPageData();
    }

    public void nextPage(){
        List<PAGE_ENTITY> videoRaws = mPageDataHandler.nextPage();
        notifyDataChanged(videoRaws);
    }

    protected void notifyDataChanged(List<PAGE_ENTITY> videoRaws) {
        if (mPageListener!=null){
            mPageListener.onPageChanged(getCurrentPage(), getTotalPage(),videoRaws);
        }
    }

    public void prevPage(){
        List<PAGE_ENTITY> videoRaws = mPageDataHandler.prevPage();
        notifyDataChanged(videoRaws);
    }

    public void reset(){
        if (mUrl !=null){
            mRequestQueue.cancelAll(mUrl);
        }
        mHasLoadedPage = 0;
        mPageDataHandler.reset();
    }

    public int getHasLoadedPage() {
        return mHasLoadedPage;
    }

    public int getPreLoadPageNumber() {
        return mPreLoadPageNumber;
    }

    public boolean needLoadData(){
        return mPreLoadPageNumber!=LOAD_ALL && getCurrentPage() == mHasLoadedPage;
    }

    public void setPreLoadPageNumber(int preLoadPageNumber) {
        this.mPreLoadPageNumber = preLoadPageNumber;
    }

    protected abstract void onLoadDataSuccess(REQUEST_ENTITY response);

    public int getCurrentPage() {
        return mPageDataHandler.getCurrentPage();
    }

    public int getTotalPage() {
        return mPageDataHandler.getTotalPage();
    }

    public PageListener getPageListener() {
        return mPageListener;
    }

    public void setPageListener(PageListener<PAGE_ENTITY> pageListener) {
        this.mPageListener = pageListener;
    }

    protected Response.Listener<REQUEST_ENTITY> mListener =new Response.Listener<REQUEST_ENTITY>() {
        @Override
        public void onResponse(REQUEST_ENTITY response) {
            onLoadDataSuccess(response);
        }
    };

    protected Response.ErrorListener mError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (mPageListener!=null){
                mPageListener.onError(error.toString());
            }
        }
    };

    public static interface PageListener<E>{
        void onPageChanged(int currentPage, int totalPage, List<E> currentPageData);
        void onError(String msg);
    }

}
