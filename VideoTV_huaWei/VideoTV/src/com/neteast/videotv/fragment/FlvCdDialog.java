package com.neteast.videotv.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.FLVCDRaw;
import com.neteast.videotv.R;
import com.neteast.videotv.bean.VideoDetail;
import com.neteast.videotv.io.LocalParseRequest;
import com.neteast.videotv.ui.VideoDetailActivity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-5
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_flvcd)
public class FlvCdDialog extends TVDialog {
    @ViewById(R.id.info) TextView mInfoView;
    @ViewById(R.id.loading) View mLoading;
    @ViewById(R.id.failure) View mFailure;


    public VideoDetail mVideoDetail;
    private String api;
    private String tag;

    public static final FlvCdDialog newDialog(String api){
        FlvCdDialog dialog=new FlvCdDialog_();
        Bundle args=new Bundle();
        args.putString("api",api);
        dialog.setArguments(args);
        return dialog;
    }

    public static final FlvCdDialog newLocalParse(String api,String tag){
        FlvCdDialog dialog=new FlvCdDialog_();
        Bundle args=new Bundle();
        args.putString("api",api);
        args.putString("tag",tag);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = getArguments().getString("api");
        tag = getArguments().getString("tag");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (tag !=null){
            executeLocalParse();
        }else{
            executeXmlRequest(api,FLVCDRaw.class,onLoadSuccess,onLoadError);
        }
    }

    private void executeLocalParse() {
        LocalParseRequest request = new LocalParseRequest(api, tag, onLocalParseSuccess, onLoadError);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        getRequestQueue().add(request);
        getRequestQueue().start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private Response.Listener<String> onLocalParseSuccess =new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
        	VideoDetailActivity activity = (VideoDetailActivity) getActivity();
        	activity.playMp4(response);
        	dismiss();
        }
    };
    private Response.Listener<FLVCDRaw> onLoadSuccess =new Response.Listener<FLVCDRaw>() {
        @Override
        public void onResponse(FLVCDRaw response) {
            List<FLVCDRaw.V> items = response.getItems();
            if (items!=null && items.size()>0){
            	VideoDetailActivity activity = (VideoDetailActivity) getActivity();
            	activity.playMp4(items.get(0).getPlayUrl());
            	dismiss();
            }else {
            	mLoading.setVisibility(View.INVISIBLE);
                mFailure.setVisibility(View.VISIBLE);
                mInfoView.setText("解析视频资源失败了，看看别的吧！");
			}
        }
    };

    private Response.ErrorListener onLoadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mLoading.setVisibility(View.INVISIBLE);
            mFailure.setVisibility(View.VISIBLE);
            mInfoView.setText("解析视频资源失败了，看看别的吧！");
        }
    };

    public FlvCdDialog setVideoDetail(VideoDetail videoDetail){
        this.mVideoDetail=videoDetail;
        return this;
    }
}
