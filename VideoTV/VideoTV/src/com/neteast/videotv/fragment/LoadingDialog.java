package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.neteast.videotv.R;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-10-23
 * Time: 下午1:58
 */
public class LoadingDialog extends TVDialog {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.dialog_loading,container,false);
    	
        return v;
    }
}
