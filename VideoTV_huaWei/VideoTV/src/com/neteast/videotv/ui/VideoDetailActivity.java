package com.neteast.videotv.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView.ScaleType;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.RelationRaw;
import com.neteast.lib.bean.StreamingMediaRaw;
import com.neteast.lib.bean.VideoDetailRaw;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.bean.VideoDetail;
import com.neteast.videotv.dao.MySQLiteOpenHelper;
import com.neteast.videotv.dao.PosterDao;
import com.neteast.videotv.fragment.*;
import com.neteast.videotv.io.XmlRequest;
import com.neteast.videotv.utils.MySohuParse;
import com.neteast.videotv.utils.VolleyCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-7-31
 * Time: 下午4:12
 * To change this template use File | Settings | File Templates.
 */
@EActivity(R.layout.activity_detail)
public class VideoDetailActivity extends Activity implements ChoiceOriginDialog.OriginChangedListener{
    protected static final int REQUEST_TIME_OUT=10*1000;
    private List<RelationRaw> mRelationRaws;
    private SQLiteDatabase db;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static void newVideoDetail(Context context,long movieId){
        Intent intent=new Intent(context,VideoDetailActivity_.class);
        intent.putExtra("movieId",movieId);
        context.startActivity(intent);
    }

    @ViewById(R.id.detail_movie_image) NetworkImageView mMovieImage;
    @ViewById(R.id.detail_movie_title) TextView mTitle;
    @ViewById(R.id.detail_mark) TextView mMark;
    @ViewById(R.id.detail_movie_info1) TextView mBaseInfo1;
    @ViewById(R.id.detail_movie_info2) TextView mBaseInfo2;
    @ViewById(R.id.detail_movie_info3) TextView mBaseInfo3;
    @ViewById(R.id.detail_movie_info4) TextView mBaseInfo4;
    @ViewById(R.id.detail_movie_info5) TextView mBaseInfo5;
    @ViewById(R.id.detail_movie_info6) TextView mBaseInfo6;
    @ViewById(R.id.detail_description) TextView mDescription;
    @ViewById(R.id.detail_more_description) Button mMoreDescription;
    @ViewById(R.id.detail_play) Button mPlay;
    @ViewById(R.id.detail_choice_series) Button mChoiceSeries;
    @ViewById(R.id.detail_collection) Button mCollection;
    @ViewById(R.id.detail_share) Button mShare;
    @ViewById(R.id.detail_choice_origin) Button mChoiceOrigin;

    @ViewById(R.id.detail_recommend_title) TextView mRecommendTitle;
    @ViewById(R.id.detail_recommend_container) LinearLayout mRecommendContainer;



    private TextView[] mBaseInfoViews;

    private VideoDetail mVideoDetail;
    private DialogFragment mChoiceSeriesDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleyCallback callback = (VolleyCallback) getApplication();
        mRequestQueue = callback.getRequestQueue();
        mImageLoader = callback.getImageLoader();
        long movieId = getIntent().getLongExtra("movieId",0);

        if (movieId!=0){
            String api = String.format(TVApplication.API_MOVIE_DETAIL, movieId);
            Log.i("test", api);
            executeXmlRequest(api, VideoDetailRaw.class, mLoadDetailSuccess);
            System.out.println(api);
        }

        MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this);
        db = mySQLiteOpenHelper.getWritableDatabase();
    }

    @AfterViews
    void parpareViews(){
        mBaseInfoViews=new TextView[]{
           mBaseInfo1,mBaseInfo2,mBaseInfo3,mBaseInfo4,
           mBaseInfo5,mBaseInfo6
        };
        addRelationUI();
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.cancelAll(this);
        if (db!=null){
            db.close();
        }
        super.onDestroy();
    }

    void updateUI(){
       executeMovieRelation();
       mTitle.setText(mVideoDetail.getTitle());

       if (mVideoDetail.showMask()){
           mMark.setVisibility(View.VISIBLE);
           mMark.setText(mVideoDetail.getMark());
       }else {
           mMark.setVisibility(View.INVISIBLE);
       }
       if (mVideoDetail.getType() == VideoType.SHORT_VIDEO) {
    	   mMovieImage.setScaleType(ScaleType.FIT_CENTER);
       }else{
    	   mMovieImage.setScaleType(ScaleType.FIT_XY);
       }
       mMovieImage.setImageUrl(mVideoDetail.getImageUrl(),mImageLoader);
       setBaseInfo();
       setDescription();
       //setPlayButton();
       //setChoiceSeries();
       setCollectionText();
       //setChoiceOrigin();
       initVideoBtn();
    }
    
    private List<View> mRelationUI;
    private void addRelationUI(){
    	mRelationUI = new ArrayList<View>();
        for(int i=0,size=6;i<size;i++){
            View ralationView = getLayoutInflater().inflate(R.layout.view_relation, mRecommendContainer, false);
            mRelationUI.add(ralationView);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin=19;
            mRecommendContainer.addView(ralationView,params);
        }
    }

    private void updateRelationUI() {
        for(int i=0,size=6;i<size;i++){
        	if(mRelationRaws.size()>i){
	            RelationRaw raw=mRelationRaws.get(i);
	            View ralationView = mRelationUI.get(i);
	            NetworkImageView image = (NetworkImageView) ralationView.findViewById(R.id.image);
	            if (raw.getTypeID() == VideoType.SHORT_VIDEO) {
	            	image.setImageUrl(raw.getPoster2(),mImageLoader);
	            	image.setScaleType(ScaleType.FIT_CENTER);
	    		}else{
	    			image.setImageUrl(raw.getPoster(),mImageLoader);
	            	image.setScaleType(ScaleType.FIT_XY);
	    		}
	            TextView countInfo = (TextView) ralationView.findViewById(R.id.countInfo);
	            int count = raw.getCount();
	            if (count<=0){
	                countInfo.setVisibility(View.INVISIBLE);
	            }else {
	                countInfo.setVisibility(View.VISIBLE);
	                countInfo.setText(String.format("更新至%d集",count));
	            }
	            TextView title = (TextView) ralationView.findViewById(R.id.title);
	            title.setText(raw.getMovieName());
	            ralationView.setTag(raw);
	            ralationView.setOnClickListener(mClickRelationListener);
        	}else{
        		mRelationUI.get(i).setVisibility(View.GONE);
        	}
        }
        
        if (mRecommendContainer.getChildCount()>0){
            mRecommendTitle.setVisibility(View.VISIBLE);
        }
    }

    private TextView getTitle(View parent){
        return (TextView) parent.findViewById(R.id.title);
    }

    private TextView getCountInfo(View parent){
        return (TextView) parent.findViewById(R.id.countInfo);
    }

    private ImageView getImage(View parent){
        return (ImageView) parent.findViewById(R.id.image);
    }

    private void setPlayButton() {

        if (!mVideoDetail.hasPlayResource()){
            mPlay.setVisibility(View.GONE);
            return;
        }

        mPlay.setVisibility(View.VISIBLE);
        mPlay.setText(mVideoDetail.getPlayButtonText());
        mPlay.requestFocus();
    }

    private void setChoiceOrigin() {
        List<String> origin = mVideoDetail.getOrigin();
        if (origin.size()<1){
            return;
        }
        mChoiceOrigin.setVisibility(View.VISIBLE);
        mChoiceOrigin.setText(mVideoDetail.getCurrentOrigin());
        
        System.out.println(mVideoDetail.getCurrentOrigin());
        if (mVideoDetail.getOrigin().size()==1){
            mChoiceOrigin.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mChoiceOrigin.setEnabled(false);
        }
    }

    private void setChoiceSeries() {
        if (mVideoDetail.needSpit()){
            mChoiceSeries.setVisibility(View.VISIBLE);
        }
    }

    private void setDescription() {
        mDescription.setText(mVideoDetail.getShortDescription().trim());

        if (mVideoDetail.showDescriptionButton()){
            mMoreDescription.setVisibility(View.VISIBLE);
        }
    }

    private void setBaseInfo() {
        List<String> baseInfo = mVideoDetail.getBaseInfo();

        int usedIndex=0;

        for(int i=0,size=baseInfo.size();i<size;i++){
            TextView view=mBaseInfoViews[i];
            view.setText(baseInfo.get(i));
            usedIndex=i;
        }

        usedIndex++;

        for(int i=usedIndex;i<mBaseInfoViews.length;i++){
            mBaseInfoViews[i].setVisibility(View.GONE);
        }
    }

    private void setCollectionText() {
        mCollection.setVisibility(View.VISIBLE);
       if (isCollection()){
          mCollection.setText(mVideoDetail.getHasCollectionText());
          mCollection.setEnabled(false);
       }else {
          mCollection.setText(mVideoDetail.getCollectionText());
          mCollection.setEnabled(true);
       }
    }

    boolean isCollection(){
        String collectionText = mVideoDetail.getCollectionText();
        if (collectionText.equals("收 藏")){
            return PosterDao.hasCollection(db,mVideoDetail.getMovieId());
        }else {
            return PosterDao.hasFollow(db,mVideoDetail.getMovieId());
        }
    }


    @Click(R.id.detail_play)
    void play(){
        StreamingMediaRaw.MediaRaw media = mVideoDetail.getDefaultPlayMedia();
        onPlay(media);
    }
    
    public void onPlay(StreamingMediaRaw.MediaRaw media) {
    	if (media!=null){
            if (media.getTagName().equals("优酷") || media.getTagName().equals("土豆")){
                FlvCdDialog.newLocalParse(media.getUrl(),media.getTagName()).setVideoDetail(mVideoDetail).show(getFragmentManager(),"flvcd");
            }/*else if(media.getTagName().equals("搜狐")){
            	String url  = media.getUrl();
            	if (url.startsWith("http://my.tv.sohu.com/")) {
            		url = MySohuParse.generateUrl(url);
            		FlvCdDialog.newLocalParse(url, "my搜狐").setVideoDetail(mVideoDetail).show(getFragmentManager(), "flvcd");
				}else {
					url = url.substring(0, 7)+"pad."+url.substring(7, url.length());
	            	FlvCdDialog.newLocalParse(url, media.getTagName()).setVideoDetail(mVideoDetail).show(getFragmentManager(), "flvcd");
				}
            }*/else if(media.getTagName().equals("1080")){
                playMp4(media.getUrl());
            }else {
                String api=TVApplication.API_FLVCD+media.getMediaId();
                System.out.println("api = "+api);
                FlvCdDialog.newDialog(api).setVideoDetail(mVideoDetail).show(getFragmentManager(),"flvcd");
            }
        }
	}

	public void playMp4(String url){
        record();
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(url),"video/mp4");
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }else {
            Toast.makeText(this, "请安装视频播放器", Toast.LENGTH_SHORT).show();
        }
    }

    private void record() {
        PosterDao.savePlayHistory(db,mVideoDetail);
    }

    @Click(R.id.detail_choice_series)
    void choiceSeries(){
        if(mVideoDetail.isSpitByCount()){
        	if (mVideoDetail.getType() == VideoType.RECORD) {
        		mChoiceSeriesDialog= ChoiceByMonthDialog_.newDialog(mVideoDetail);
			}else {
				mChoiceSeriesDialog= ChoiceByCountDialog_.newDialog(mVideoDetail);
			}
        }else{
            mChoiceSeriesDialog= ChoiceByMonthDialog_.newDialog(mVideoDetail);
        }
        mChoiceSeriesDialog.show(getFragmentManager(), "choiceSeries");
    }

    @Click(R.id.detail_choice_origin)
    void choiceOrigin(){
        ChoiceOriginDialog.newDialog(mVideoDetail.getCurrentOrigin(),mVideoDetail.getOrigin())
                .show(getFragmentManager(), "choiceOrigins");
    }

    @Click(R.id.detail_more_description)
    void showMoreDescription(){
        DescriptionDialog.newDialog(mVideoDetail.getDescription()).show(getFragmentManager(),"description");
    }

    @Click(R.id.detail_collection)
    void collect(){
        String collectionText = mVideoDetail.getCollectionText();
        if (collectionText.equals("收 藏")){
            if (PosterDao.collect(db,mVideoDetail)){
                mCollection.setText(mVideoDetail.getHasCollectionText());
                mCollection.setEnabled(false);
            }
        }else {
            if (PosterDao.follow(db,mVideoDetail)){
                mCollection.setText(mVideoDetail.getHasCollectionText());
                mCollection.setEnabled(false);
            }
        }
    }

    private void executeMovieRelation(){
        String api=String.format(TVApplication.API_MOVIE_RELATION, mVideoDetail.getMovieId());
        executeXmlRequest(api, RelationRaw.Result.class, mLoadRelationSuccess);
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

    private Response.Listener<VideoDetailRaw> mLoadDetailSuccess = new Response.Listener<VideoDetailRaw>() {
        @Override
        public void onResponse(VideoDetailRaw response) {
            mVideoDetail =VideoDetail.newVideoDetail(response);
            updateUI();
        }
    };

    private Response.Listener<RelationRaw.Result> mLoadRelationSuccess = new Response.Listener<RelationRaw.Result>() {
        @Override
        public void onResponse(RelationRaw.Result response) {
            mRelationRaws = response.getRelationRaws();
            updateRelationUI();
        }
    };

    private Response.ErrorListener onLoadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "获取数据失败", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onOriginChanged(String newOrigin) {
        mVideoDetail.setCurrentOrigin(newOrigin);
        mChoiceOrigin.setText(newOrigin);
        mPlay.setText(mVideoDetail.getPlayButtonText());
    }

    private final View.OnClickListener mClickRelationListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             RelationRaw raw= (RelationRaw) v.getTag();
             VideoDetailActivity.newVideoDetail(VideoDetailActivity.this,raw.getMovieID());
        }
    };
    
    
    private Button[] mVideoBtns;
	@ViewById(R.id.functionlayout)
	LinearLayout mFunctionlayout;// 主要是添
    /**
     * @author LiYinBo
	 * 初始化视频网站按钮
	 */
	private void initVideoBtn() {
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		ButtonOnclickListener l = new ButtonOnclickListener();
		if (mVideoDetail != null && mVideoDetail.getOrigin().size() > 0) {
			// 获取视频按钮个数
			int videoBtn = mVideoDetail.getOrigin().size();
			mVideoBtns = new Button[videoBtn];
			for (int i = 0; i < mVideoDetail.getOrigin().size(); i++) {
				params.leftMargin = 15;
				Button btn = new Button(this);
				mVideoBtns[i] = btn;
				mVideoBtns[i].setTag(i);
				mVideoBtns[i].setTextSize(8);
				mVideoBtns[i].setTag(mVideoDetail.getOrigin().get(i));
				mVideoBtns[i].setTextAppearance(VideoDetailActivity.this, R.style.Detail_ButtonText);
				mVideoBtns[i].setText(mVideoDetail.getOrigin().get(i)+"   "+"8.7分");// 设置button名称
				mVideoBtns[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_btn_texture));
				mVideoBtns[i].setOnClickListener(l);// 视频网站点击事件
				mFunctionlayout.addView(mVideoBtns[i], params);
			}
		} else {
			return;
		}

	}

	private class ButtonOnclickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			System.out.println("ButtonOnclickListener"+mVideoDetail.getCurrentOrigin());
			onOriginChanged(String.valueOf(v.getTag()));
			StreamingMediaRaw.MediaRaw media = mVideoDetail.getDefaultPlayMedia();
			if (mVideoDetail.needSpit()){
				choiceSeries();
		    }else{
		    	onPlay(media);
			}
		}
	}


}