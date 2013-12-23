//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.neteast.videotv.R.layout;

public final class SearchFragment_
    extends SearchFragment
{

    private View contentView_;

    private void init_(Bundle savedInstanceState) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    private void afterSetContentView_() {
        mPanel1 = ((LinearLayout) findViewById(com.neteast.videotv.R.id.panel1));
        mSearchByActor = ((Button) findViewById(com.neteast.videotv.R.id.search_by_actor));
        mInput = ((EditText) findViewById(com.neteast.videotv.R.id.search_input));
        mPanel3 = ((LinearLayout) findViewById(com.neteast.videotv.R.id.panel3));
        mPanel2 = ((LinearLayout) findViewById(com.neteast.videotv.R.id.panel2));
        {
            View view = findViewById(com.neteast.videotv.R.id.search_by_actor);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchFragment_.this.searchByActor();
                    }

                }
                );
            }
        }
        {
            View view = findViewById(com.neteast.videotv.R.id.search_by_name);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SearchFragment_.this.searchByName();
                    }

                }
                );
            }
        }
        initSearchInout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        if (contentView_ == null) {
            contentView_ = inflater.inflate(layout.frag_search, container, false);
        }
        afterSetContentView_();
        return contentView_;
    }

    public View findViewById(int id) {
        if (contentView_ == null) {
            return null;
        }
        return contentView_.findViewById(id);
    }

    public static SearchFragment_.FragmentBuilder_ builder() {
        return new SearchFragment_.FragmentBuilder_();
    }

    public static class FragmentBuilder_ {

        private Bundle args_;

        private FragmentBuilder_() {
            args_ = new Bundle();
        }

        public SearchFragment build() {
            SearchFragment_ fragment_ = new SearchFragment_();
            fragment_.setArguments(args_);
            return fragment_;
        }

    }

}
