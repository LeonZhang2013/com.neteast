package com.neteast.videotv.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Function;
import com.neteast.lib.bean.SearchResult;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.controller.TopicController;
import com.neteast.videotv.controller.VideoFlowController;
import com.neteast.videotv.dao.Menu;
import com.neteast.videotv.dao.MenuDao;
import com.neteast.videotv.dao.MySQLiteOpenHelper;
import com.neteast.videotv.io.TopicChannelRequest;
import com.neteast.videotv.listener.DPadListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.ResetChannelActivity_;
import com.neteast.videotv.ui.VideoDetailActivity;
import com.neteast.videotv.ui.widget.ChannelList;
import com.neteast.videotv.ui.widget.ChannelView;
import com.neteast.videotv.ui.widget.PosterView;
import com.neteast.videotv.utils.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-18
 * Time: 上午11:33
 */
public class TopicFragment extends VideoFlowFragment {
    private static final int NO_NEED_FILTER = 0 ;

    private int mFilterType= NO_NEED_FILTER;
    private TopicController mController;
    private SQLiteDatabase db;
    private String mSelectedChannelName;
    private boolean mNeedGenerateFilterView = false;

    public void selectChannel(String channel) {
        this.mSelectedChannelName = channel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mSearchInfo.setVisibility(View.VISIBLE);
        mSearchInfo.setText("按\"菜单\"键可调整频道顺序");
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(android.view.Menu menu) {
        showResetChannel();
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void initController() {
        mController = new TopicController(getActivity());
        mController.setSinglePageItem(getSinglePageNumber());
        mController.setPageListener(new VideoFlowController.PageListener<VideoRaw>() {
            @Override
            public void onPageChanged(int currentPage, int totalPage, List currentPageData) {
            	hiddenLoading();
                if (mNeedGenerateFilterView){
                    mNeedGenerateFilterView = false;
                    generateSearchFilterContent();
                    Menu menu = (Menu) mChannelList.getSelectionItem();
                    if ("1080专区".equals(menu.getTitle())){
                    	mSearchResultFilterContainer.getChildAt(0).performClick();
                    }
                }else{
                	mCountInfo.setText(currentPage + "/" + totalPage);
                    updatePage(currentPage, currentPageData);
                }
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generateSearchFilterContent() {
        List<SearchResult.Type> searchFilers = sortEntityByTypeId(mController.getTypes(),new Function<SearchResult.Type, Integer>() {
            @Override
            public Integer apply(SearchResult.Type input) {
                return input.getTypeid();
            }
        });
        for (SearchResult.Type filter : searchFilers) {
            String title = VideoType.getNameByType(filter.getTypeid()) + " " + filter.getTypecount();
            addFilterView(title, filter.getTypeid());
        }
    }

    private void addFilterView(String title, int type) {
        TextView allView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.view_search_filter_item, mSearchResultFilterContainer, false);
        allView.setText(title);
        allView.setTag(type);
        allView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterType = (Integer) v.getTag();
                updateFilterView(v);
                mController.filter(mFilterType);
            }
        });
        mSearchResultFilterContainer.addView(allView);
    }

    @Override
    protected void prepareChannelList() {
        MySQLiteOpenHelper helper=new MySQLiteOpenHelper(getActivity());
        db = helper.getWritableDatabase();
        String api= String.format(TVApplication.API_MENU_LIST,"LSTV_topic");
        System.out.println("topic  prepareChannelList api ==========="+api);
        TopicChannelRequest request = new TopicChannelRequest(db, api, onGetChannelSuccess,onGetChannelError);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue.add(request);
        mRequestQueue.start();
    }

    private void updateChannelList(List<Menu> menus) {
    	for(int i=0; i<menus.size(); i++){
    		System.out.println(menus.get(i).getAction());
    	}
        addChannelListHeadView();
        mChannelList.setSelectionChangedListener(new ChannelList.SelectionChangedListener() {
            @Override
            public void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex) {
                updateUI();
                reLoadPageData();
            }
        });
        mChannelList.setAdapter(new ChannelAdapter(getActivity(), menus));

        if (mSelectedChannelName!=null){
            setChannelSelection(menus,mSelectedChannelName);
        }else {
            mChannelList.setDefaultSelected();
        }
    }

    private void updateUI() {
        Menu menu = (Menu) mChannelList.getSelectionItem();
        if ("1080专区".equals(menu.getTitle())){
            mSearchResultFilterScroller.setVisibility(View.VISIBLE);
            mNeedGenerateFilterView = true;
        }else {
            mSearchResultFilterScroller.setVisibility(View.GONE);
            mNeedGenerateFilterView = false;
        }
        mFilterType = NO_NEED_FILTER;
        mSearchResultFilterContainer.removeAllViews();
    }

    private void reLoadPageData() {
        showLoading();
        mController.reset();
        Menu menu = (Menu) mChannelList.getSelectionItem();
        System.out.println("Topicfragment menu ============================="+menu.getAction());
        mController.execute(menu.getAction(),VideoRaw.Result.class);
    }

    private void setChannelSelection(List<Menu> menus,String channelName){
        for(int i=0,size=menus.size();i<size;i++){
            if (menus.get(i).getTitle().equals(channelName)){
                mChannelList.setSelection(i+1);
                mChannelList.requestFocus();
                break;
            }
        }
    }

    private void addChannelListHeadView() {
        final ChannelView reset=new ChannelView(getActivity());
        reset.setVisibility(View.GONE);
        reset.setChannel("重置频道");
        reset.setDpadListener(new DPadListener() {
            @Override
            public boolean onClickDown() {
                reset.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onClickLeft() {
                reset.setVisibility(View.GONE);
                return false;
            }
            @Override
            public boolean onClickRight() {
                reset.setVisibility(View.GONE);
                return false;
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetChannel();
            }
        });
        mChannelList.addHeadView(reset);
    }

    private void showResetChannel(){
        Intent intent=new Intent(getActivity(), ResetChannelActivity_.class);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1 && resultCode== Activity.RESULT_OK){
            List<Menu> menus = MenuDao.getAllMenu(db);
            System.out.println("topicFragment = onActivityResult ===== "+menus.get(0).getAction());
            updateChannelList(menus);
            mChannelList.requestFocus();
        }
    }

    @Override
    protected int getSinglePageNumber() {
        return 10;
    }

    @Override
    protected void onViewClicked(Object data) {
        VideoRaw item = (VideoRaw) data;
        VideoDetailActivity.newVideoDetail(getActivity(), item.getMovieID());
    }

    @Override
    public void onDetach() {
        if (db!=null){
            db.close();
        }
        mController.reset();
        super.onDetach();
    }

    @Override
    protected boolean performScrollNext() {
        if (!mController.hasNextPage()){
            return true;
        }
        if (mController.needLoadData()) {
            showLoading();
            Menu menu = (Menu) mChannelList.getSelectionItem();
            mController.execute(menu.getAction(),VideoRaw.Result.class);
            return true;
        }
        mController.nextPage();
        return true;
    }

    @Override
    protected boolean performScrollPrev() {
        if (mController.hasPrevPage()){
            mController.prevPage();
        }else {
            mChannelList.requestFocus();
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (mFilterType != NO_NEED_FILTER){
            mFilterType = NO_NEED_FILTER;
            updateFilterView(null);
            mController.filter(mFilterType);
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    protected void setView(Object data, PosterView posterView) {
        VideoRaw videoRaw = (VideoRaw) data;
        posterView.setImage(videoRaw.getImage(),mImageLoader);
        posterView.setTitle(videoRaw.getTitle());
        String periods = videoRaw.getPeriods();
        try {
            String[] values = periods.split("\\*");
            posterView.setCount(Integer.parseInt(values[0]),Integer.parseInt(values[0]));
        }catch (Exception e){
            posterView.setCount(0,0);
        }
    }

    private Response.Listener<Menu.Result> onGetChannelSuccess = new Response.Listener<Menu.Result>() {
        @Override
        public void onResponse(Menu.Result response) {
            updateChannelList(response.getMenus());
        }
    };

    private Response.ErrorListener onGetChannelError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(),"获取频道信息失败",Toast.LENGTH_LONG).show();
        }
    };


    public class ChannelAdapter extends ArrayAdapter<Menu> {

        public ChannelAdapter(Context context, List<Menu> datas) {
            super(context, 0, 0, datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChannelView channelView=new ChannelView(getContext());
            Menu item = getItem(position);
            channelView.setChannel(item.getTitle());
            if (position == 0){
                channelView.setDpadListener(mChannelFirstDpadListener);
            }else {
                channelView.setDpadListener(mChannelNormalDpadListener);
            }
            channelView.setOnFocusChangeListener(mFocusChangedListener);
            LinearLayout.LayoutParams params=
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Utils.dp2px(getContext(), 55)
                    );
            params.gravity= Gravity.CENTER_VERTICAL;
            channelView.setLayoutParams(params);
            return channelView;
        }


        private DPadListener mChannelNormalDpadListener = new DPadListener() {
            @Override
            public boolean onClickLeft() {
                MainActivity activity= (MainActivity) getActivity();
                activity.showLeftNav();
                return false;
            }

            @Override
            public boolean onClickRight() {
                MainActivity activity= (MainActivity) getActivity();
                activity.hiddenLeftNav();
                return false;
            }
        };

        private DPadListener mChannelFirstDpadListener = new DPadListener() {
            @Override
            public boolean onClickUp() {
                mChannelList.getChildAt(0).setVisibility(View.VISIBLE);
                mChannelList.getChildAt(0).requestFocus();
                return true;
            }

            @Override
            public boolean onClickLeft() {
                MainActivity activity= (MainActivity) getActivity();
                activity.showLeftNav();
                return false;
            }

            @Override
            public boolean onClickRight() {
                MainActivity activity= (MainActivity) getActivity();
                activity.hiddenLeftNav();
                return false;
            }
        };

        private View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    MainActivity activity= (MainActivity) getActivity();
                    activity.hiddenLeftNav();
                }
            }
        };
    }
}
