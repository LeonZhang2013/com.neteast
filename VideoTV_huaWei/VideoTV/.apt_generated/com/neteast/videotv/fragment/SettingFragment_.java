//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.neteast.videotv.R.layout;

public final class SettingFragment_
    extends SettingFragment
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
        mVersionInfo = ((TextView) findViewById(com.neteast.videotv.R.id.versionInfo));
        mShareSinaBtn = ((Button) findViewById(com.neteast.videotv.R.id.shareSinaBtn));
        mShareSinaTitle = ((TextView) findViewById(com.neteast.videotv.R.id.shareSinaTitle));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        if (contentView_ == null) {
            contentView_ = inflater.inflate(layout.frag_setting, container, false);
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

    public static SettingFragment_.FragmentBuilder_ builder() {
        return new SettingFragment_.FragmentBuilder_();
    }

    public static class FragmentBuilder_ {

        private Bundle args_;

        private FragmentBuilder_() {
            args_ = new Bundle();
        }

        public SettingFragment build() {
            SettingFragment_ fragment_ = new SettingFragment_();
            fragment_.setArguments(args_);
            return fragment_;
        }

    }

}
