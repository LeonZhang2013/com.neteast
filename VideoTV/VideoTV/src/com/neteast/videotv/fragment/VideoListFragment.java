package com.neteast.videotv.fragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.neteast.lib.bean.MenuRaw;
import com.neteast.lib.bean.SearchResult;
import com.neteast.lib.config.VideoType;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.controller.APIBuilder;
import com.neteast.videotv.controller.VideoFlowController;
import com.neteast.videotv.controller.VideoListController;
import com.neteast.videotv.io.XmlRequest;
import com.neteast.videotv.listener.DPadListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.VideoDetailActivity;
import com.neteast.videotv.ui.widget.ChannelList;
import com.neteast.videotv.ui.widget.ChannelView;
import com.neteast.videotv.ui.widget.PosterView;
import com.neteast.videotv.utils.FilterHelper;
import com.neteast.videotv.utils.Utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-18
 * Time: 下午3:53
 */
public class VideoListFragment extends VideoFlowFragment {

    private VideoListController mController;
    private String mCurrentQuery;

    private void initVidoeListUI(){
        mSortSelection.setVisibility(View.VISIBLE);
        mFilter.setVisibility(View.VISIBLE);
        mFilterInfo.setVisibility(View.VISIBLE);
        mFilterInfo.setText("");

        mSortSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reLoadPageData();
            }
        });
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFilter();
            }
        });
    }

    private void initFilter(){
        mCurrentQuery = null;
        FilterHelper.clear(getActivity());
        mFilterInfo.setText("");
        if (showFilterButton()){
            mFilter.setVisibility(View.VISIBLE);
        }else {
            mFilter.setVisibility(View.GONE);
        }
    }

    private boolean showFilterButton(){
        int type = getCurrentChannelTypeAndDrama()[0];
        switch (type){
            case VideoType.MOVIE:
            case VideoType.TV:
            case VideoType.VARIETY:
            case VideoType.CARTOON:
            case VideoType.RECORD:
                return true;
            default:
                return false;
        }
    }

    private void doFilter() {
        MenuRaw item = (MenuRaw) mChannelList.getSelectionItem();
        int type=Integer.valueOf(item.getDescription());
        FilterDialog filterDialog = FilterDialog.newDialog(type);
        filterDialog.setOnFilterListener(new FilterDialog.FilterListener() {
            @Override
            public void onFilter(String query) {
               onFilterSuccess(query);
            }
        });
        filterDialog.show(getFragmentManager(), "Filter");
    }

    private void onFilterSuccess(String query){

        if (TextUtils.isEmpty(query)){
            mFilterInfo.setText("");
            if (mCurrentQuery != null){
                mCurrentQuery = null;
                reLoadPageData();
            }
            return;
        }

        String[] savedFilterItems = FilterHelper.getSavedFilterItems(getActivity());
        if (savedFilterItems==null){
            mCurrentQuery =null;
            return;
        }

        String[] labels = savedFilterItems[1].split("\\|");
        StringBuilder result=new StringBuilder();
        for (String label : labels) {
            if (!label.equals("全部")){
                result.append(label).append("|");
            }
        }
        if (result.length()>1){
            result.deleteCharAt(result.length()-1);
        }

        mFilterInfo.setText(result.toString());
        mCurrentQuery = query;
        reLoadPageData();
    }

    private void reLoadPageData() {
        showLoading();
        mController.reset();
        mController.execute(generateApi(),SearchResult.class);
    }

    private int[] getCurrentChannelTypeAndDrama(){
        int type=-1;
        int drama=-1;
        MenuRaw menuRaw = (MenuRaw) mChannelList.getSelectionItem();
        String description = menuRaw.getDescription();
        if (description.contains("*")){
            String[] split = description.split("\\*");
            if (split.length>1){
                type = Integer.parseInt(split[0]);
                drama = Integer.parseInt(split[1]);
            }
        }else {
            type =Integer.parseInt(description);
        }
        return new int[]{type,drama};
    }

    private String generateApi() {
        APIBuilder builder = new APIBuilder();
        int hasLoadedPage = mController.getHasLoadedPage();

        builder.preLoadPage(PRE_LOAD_PAGE_NUMBER)
                .singlePageItem(getSinglePageNumber())
                .currentPage(hasLoadedPage+1);

        int[] typeAndDrama = getCurrentChannelTypeAndDrama();
        if (typeAndDrama[0]!=-1){
            builder.type(typeAndDrama[0]);
        }
        if (typeAndDrama[1]!=-1){
            builder.drama(typeAndDrama[1]);
        }
        if (mSortSelection.getCheckedRadioButtonId() == R.id.sortByHot){
            builder.sortByHot();
        }else {
            builder.sortByTime();
        }
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            builder.filter(mCurrentQuery);
        }
        return builder.create();
    }

    @Override
    protected void initController() {
        mController = new VideoListController(getActivity());
        mController.setSinglePageItem(getSinglePageNumber());
        mController.setPreLoadPageNumber(PRE_LOAD_PAGE_NUMBER);
        mController.setPageListener(new VideoFlowController.PageListener<SearchResult.SearchRaw>() {
            @Override
            public void onPageChanged(int currentPage, int totalPage, List currentPageData) {
                hiddenLoading();
                mCountInfo.setText(currentPage + "/" + totalPage);
                updatePage(currentPage, currentPageData);
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void prepareChannelList() {
        String api= String.format(TVApplication.API_MENU_LIST,"LSTV_channel");
        
        XmlRequest<MenuRaw.Result> request =
                new XmlRequest<MenuRaw.Result>(
                        XmlRequest.Method.GET,
                        api,
                        MenuRaw.Result.class,
                        onGetChannelSuccess,
                        onGetChannelError);
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
    protected int getSinglePageNumber() {
        return 10;
    }

    @Override
    protected void onViewClicked(Object data) {
        SearchResult.SearchRaw raw = (SearchResult.SearchRaw) data;
        VideoDetailActivity.newVideoDetail(getActivity(), raw.getMovieID());
    }

    @Override
    protected boolean performScrollNext() {
        if (!mController.hasNextPage()){
            return true;
        }
        if (mController.needLoadData()) {
            showLoading();
            mController.execute(generateApi(),SearchResult.class);
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
    protected void setView(Object data, PosterView posterView) {
        SearchResult.SearchRaw raw = (SearchResult.SearchRaw) data;
        posterView.setTitle(raw.getMovieName());
        if (raw.getTypeID() == VideoType.SHORT_VIDEO) {
        	posterView.setImage(raw.getPoster2(),mImageLoader);
        	posterView.getImage().setScaleType(ScaleType.FIT_CENTER);
		}else{
			posterView.setImage(raw.getPoster(),mImageLoader);
			posterView.getImage().setScaleType(ScaleType.FIT_XY);
		}
        switch (raw.getTypeID()){
            case VideoType.TV:
            case VideoType.CARTOON:
                try{
                    posterView.setCount(raw.getCount(),Integer.parseInt(raw.getMaxSeries()));
                }catch (Exception e){
                    posterView.setCount(0,0);
                }
                break;
        }
    }


    private Response.Listener<MenuRaw.Result> onGetChannelSuccess = new Response.Listener<MenuRaw.Result>() {
        @Override
        public void onResponse(MenuRaw.Result response) {
            initVidoeListUI();
            updateLeftPanel(response.getMenuList());
        }

        private void updateLeftPanel(List<MenuRaw> menuList) {
            mChannelList.setSelectionChangedListener(new ChannelList.SelectionChangedListener() {
                @Override
                public void onSelectionChanged(int lastSelectedIndex, int currentSelectedIndex) {
                    initFilter();
                    reLoadPageData();
                }
            });
            mChannelList.setAdapter(new ChannelAdapter(getActivity(), menuList));
            mChannelList.setDefaultSelected();
        }
    };

    private Response.ErrorListener onGetChannelError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(),"获取频道信息失败",Toast.LENGTH_LONG).show();
        }
    };

    public class ChannelAdapter extends ArrayAdapter<MenuRaw> {

        public ChannelAdapter(Context context, List<MenuRaw> datas) {
            super(context, 0, 0, datas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ChannelView channelView=new ChannelView(getContext());
            channelView.setChannel(getItem(position).getTitle());
            channelView.setDpadListener(mChannelDpadListener);
            channelView.setOnFocusChangeListener(mFocusChangeListener);
            LinearLayout.LayoutParams params=
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            Utils.dp2px(getContext(), 55)
                    );
            params.gravity= Gravity.CENTER_VERTICAL;
            channelView.setLayoutParams(params);
            return channelView;
        }

        private DPadListener mChannelDpadListener = new DPadListener() {
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

        private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
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
