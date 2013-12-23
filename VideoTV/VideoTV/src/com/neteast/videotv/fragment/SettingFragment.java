package com.neteast.videotv.fragment;

import android.widget.Button;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;
import com.neteast.videotv.listener.BackPressListener;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-4
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_setting)
public class SettingFragment extends NetFragment implements BackPressListener{

    @ViewById(R.id.shareSinaTitle) TextView mShareSinaTitle;
    @ViewById(R.id.shareSinaBtn) Button mShareSinaBtn;
    @ViewById(R.id.versionInfo) TextView mVersionInfo;

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
