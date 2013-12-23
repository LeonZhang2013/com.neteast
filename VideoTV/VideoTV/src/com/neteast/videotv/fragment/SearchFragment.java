package com.neteast.videotv.fragment;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Response;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.lib.bean.VideoRaw;
import com.neteast.videotv.R;
import com.neteast.videotv.TVApplication;
import com.neteast.videotv.listener.BackPressListener;
import com.neteast.videotv.ui.MainActivity;
import com.neteast.videotv.ui.VideoDetailActivity;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-4
 * Time: 下午6:16
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_search)
public class SearchFragment extends NetFragment implements BackPressListener{

    @ViewById(R.id.search_input)
    EditText mInput;
    @ViewById(R.id.panel1)
    LinearLayout mPanel1;
    @ViewById(R.id.panel2)
    LinearLayout mPanel2;
    @ViewById(R.id.panel3)
    LinearLayout mPanel3;
    @ViewById(R.id.search_by_actor)
    Button mSearchByActor;

    @AfterViews
    void initSearchInout(){
        mInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    hiddenSoftInput();
                    mSearchByActor.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Click(R.id.search_by_actor)
    void searchByActor() {
        String s = mInput.getText().toString();
        Log.i("test",s);
        MainActivity activity = (MainActivity) getActivity();
        activity.doSearch(s);
    }

    @Click(R.id.search_by_name)
    void searchByName() {
        String s = mInput.getText().toString();
        MainActivity activity = (MainActivity) getActivity();
        activity.doSearch(s);
    }


    @Override
    public void onResume() {
        super.onResume();
        String api = String.format(TVApplication.API_MENU_DOC, "LSTV_keyword");
        executeXmlRequest(api, VideoRaw.Result.class, onSearchKeySuccess);
    }

    private void hiddenSoftInput(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
    }
    private void updateUI(List<VideoRaw> videoRawList) {
        if (videoRawList == null || videoRawList.size() == 0) {
            return;
        }
        initPanel(0,mPanel1,videoRawList);
        initPanel(1,mPanel2,videoRawList);
        initPanel(2,mPanel3,videoRawList);
    }

    private void initPanel(int dataIndex, LinearLayout panel, List<VideoRaw> videoRawList) {
        for(int i=0;i<6;i++){
            View child = panel.getChildAt(i);

            if (dataIndex > videoRawList.size()-1){
                child.setVisibility(View.INVISIBLE);
            } else {
                VideoRaw raw = videoRawList.get(dataIndex);
                getText(child).setText(raw.getTitle());
                child.setVisibility(View.VISIBLE);
                child.setTag(raw);
                child.setOnClickListener(onClickKeyWordsListener);
            }
            dataIndex+=3;
        }
    }


    private TextView getText(View view){
        return (TextView) view.findViewById(R.id.text);
    }

    private View.OnClickListener onClickKeyWordsListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VideoRaw raw= (VideoRaw) v.getTag();
            if (raw==null){
               return;
            }
            if (raw.getMovieID()!=0){
                VideoDetailActivity.newVideoDetail(getActivity(),raw.getMovieID());
            }else {
                mInput.setText(raw.getTitle());
            }
        }
    };

    private Response.Listener<VideoRaw.Result> onSearchKeySuccess = new Response.Listener<VideoRaw.Result>() {
        @Override
        public void onResponse(VideoRaw.Result response) {
            updateUI(response.getVideoRawList());
        }
    };

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
