package com.neteast.videotv.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.collect.Maps;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.Filter;
import com.neteast.lib.config.VideoType;
import com.neteast.lib.utils.PageUtils;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.io.FilterRequest;
import com.neteast.videotv.utils.FilterHelper;
import com.neteast.videotv.utils.Utils;

import java.util.List;
import java.util.Map;

import static android.widget.GridLayout.spec;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-13
 * Time: 下午4:37
 */
@EFragment(R.layout.frag_filter)
public class FilterDialog extends TVDialog {


    private String api;

    public static interface FilterListener{
        void onFilter(String query);
    }

    private static final Map<Integer, String> KeyWordDict;

    static {
        KeyWordDict = Maps.newHashMap();
        KeyWordDict.put(VideoType.MOVIE, "LSTV_movie_index");
        KeyWordDict.put(VideoType.TV, "LSTV_TV_index");
        KeyWordDict.put(VideoType.VARIETY, "LSTV_variety_index");
        KeyWordDict.put(VideoType.CARTOON, "LSTV_cartoon_index");
        KeyWordDict.put(VideoType.RECORD, "LSTV_documentary_index");
        KeyWordDict.put(VideoType.SHORT_VIDEO, "LSTV_videoclip_index");
    }

    @ViewById(R.id.filterGrids)
    GridLayout mFilterGrids;
    @ViewById(R.id.filterOk)
    Button mFilterOk;
    private Filter mFilter;
    public static final int COLUMN_COUNT = 14;

    private Map<String,Integer> currentSelectionItems=Maps.newHashMap();
    private FilterListener mListener;

    public static final FilterDialog newDialog(int type) {
        FilterDialog_ dialog = new FilterDialog_();
        Bundle args = new Bundle();
        args.putInt("type", type);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int type = getArguments().getInt("type", VideoType.MOVIE);
        String keyword = KeyWordDict.get(type);
        api = String.format(TVApplication.API_MENU_LIST, keyword);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FilterRequest request = new FilterRequest(api, onLoadSuccess, onLoadError);
        request.setTag(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                REQUEST_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        getRequestQueue().add(request);
        getRequestQueue().start();

    }

    public void setOnFilterListener(FilterListener listener) {
        this.mListener = listener;
    }

    private void updateUI() {
        generateFilterCells();
        setDefaultSelection();
        setDefaultFocus();
        mFilterOk.setEnabled(true);
    }

    private void setDefaultFocus() {
        String firstType = mFilter.getTypeNames().get(0);
        Integer index = currentSelectionItems.get(firstType);
        mFilterGrids.getChildAt(index).requestFocus();
    }

    private void setDefaultSelection() {

        String[] savedFilterItems = FilterHelper.getSavedFilterItems(getActivity());

        if (savedFilterItems!=null){
            String[] typeArray = savedFilterItems[0].split("\\|");
            String[] labelArray = savedFilterItems[1].split("\\|");
            for(int i=0;i<typeArray.length;i++){
                String type=typeArray[i];
                int index=getItemIndex(type,labelArray[i]);
                currentSelectionItems.put(type,index);
            }
        }

        for (Integer index : currentSelectionItems.values()) {
            mFilterGrids.getChildAt(index).setActivated(true);
        }
    }

    private int getItemIndex(Filter.FilterItem item){
        return getItemIndex(item.getType(),item.getTitle());
    }

    private int getItemIndex(String type,String label){
        int index=0;
        for(int i=0, size=mFilterGrids.getChildCount(); i<size; i++){
            View child = mFilterGrids.getChildAt(i);
            Object tag = child.getTag();
            if (tag!=null && tag instanceof Filter.FilterItem){
                Filter.FilterItem item= (Filter.FilterItem) tag;
                if (item.getType().equals(type) && item.getTitle().equals(label)){
                    index=i;
                    break;
                }
            }
        }
        return index;
    }

    private String generateQueryString(){
        StringBuilder result=new StringBuilder();
        for (Integer integer : currentSelectionItems.values()) {
            Object tag = mFilterGrids.getChildAt(integer).getTag();
            if (tag!=null && tag instanceof Filter.FilterItem){
                Filter.FilterItem item= (Filter.FilterItem) tag;
                result.append(item.getLink());
            }
        }
        return result.toString();
    }

    @Click(R.id.filterOk)
    void clickOk(){
        String queryString = generateQueryString();

        StringBuilder types=new StringBuilder();
        StringBuilder labels=new StringBuilder();
        for (String type : mFilter.getTypeNames()) {
            types.append(type).append("|");
            Object tag = mFilterGrids.getChildAt(currentSelectionItems.get(type)).getTag();
            Filter.FilterItem item= (Filter.FilterItem) tag;
            labels.append(item.getTitle()).append("|");
        }

        types.deleteCharAt(types.length()-1);
        labels.deleteCharAt(labels.length()-1);

        FilterHelper.clear(getActivity());
        FilterHelper.saveFilterItems(getActivity(), types.toString(), labels.toString());

        if (mListener!=null){
            mListener.onFilter(queryString);
        }

        dismiss();
    }


    private void generateFilterCells() {
        int rowCount = computeTotalRows();

        mFilterGrids.setColumnCount(COLUMN_COUNT);
        mFilterGrids.setRowCount(rowCount);
        mFilterGrids.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

        int childCount=-1;
        int rowIndex = 0;
        for (String type : mFilter.getTypeNames()) {
            int columnIndex = 0;
            childCount++;
            addLabel("按"+type+"：", rowIndex, columnIndex);
            int row = 0;
            List<Filter.FilterItem> items = mFilter.getTypeItems(type);
            for (int i = 0, size = items.size(); i < size; i++) {
                row = i / (COLUMN_COUNT - 1);
                columnIndex = i % (COLUMN_COUNT - 1) + 1;
                childCount++;
                addCell(items.get(i), row + rowIndex, columnIndex);
                if (i==0){
                    currentSelectionItems.put(type,childCount);
                }
            }
            row++;
            rowIndex += row;
        }
    }

    private void addLabel(String label, int row, int col) {
        TextView textView = generateTypeNameView(label);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(textView.getLayoutParams());
        params.rowSpec = spec(row);
        params.columnSpec = spec(col);
        mFilterGrids.addView(textView, params);
    }

    private void addCell(Filter.FilterItem item, int row, int col) {
        TextView textView = generateItemView(item);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(textView.getLayoutParams());
        params.rowSpec = spec(row);
        params.columnSpec = spec(col);
        mFilterGrids.addView(textView, params);
    }

    private int computeTotalRows() {
        int singleLineCount = COLUMN_COUNT - 1;
        int row = 0;
        for (String type : mFilter.getTypeNames()) {
            int i = PageUtils.computePages(mFilter.getTypeItems(type).size(), singleLineCount);
            row += i;
        }
        return row;
    }

    private TextView generateItemView(Filter.FilterItem item) {
        TextView textView = generateTypeNameView(item.getTitle());
        textView.setClickable(true);
        textView.setFocusable(true);
        textView.setTag(item);
        textView.setOnClickListener(mClickFilterItemListener);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        textView.setLayoutParams(params);
        return textView;
    }

    private TextView generateTypeNameView(String type) {
        int textSize = Utils.dp2px(getActivity(), 18);
        TextView textView = new TextView(getActivity());
        textView.setTextSize(textSize);
        textView.setTextColor(Color.WHITE);
        textView.setText(type);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.bg_blue_transparent);
        textView.setTextColor(getResources().getColorStateList(R.color.channel_text_color));
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.FILL_HORIZONTAL;
        params.setMargins(0, 0, 20, 8);
        textView.setLayoutParams(params);
        return textView;
    }

    private View.OnClickListener mClickFilterItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag!=null && tag instanceof Filter.FilterItem){
                Filter.FilterItem item= (Filter.FilterItem) tag;
                int itemIndex = getItemIndex(item);
                setSelection(item.getType(),itemIndex);
            }

        }
    };


    private void setSelection(String type,int index){
        if (currentSelectionItems.get(type)!=index){
            mFilterGrids.getChildAt(currentSelectionItems.get(type)).setActivated(false);
            mFilterGrids.getChildAt(index).setActivated(true);
            currentSelectionItems.put(type,index);
        }
    }
    private Response.Listener<Filter> onLoadSuccess = new Response.Listener<Filter>() {
        @Override
        public void onResponse(Filter response) {
            mFilter = response;
            updateUI();
        }
    };

    private Response.ErrorListener onLoadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity(), "下载分类失败", Toast.LENGTH_SHORT).show();
        }
    };
}
