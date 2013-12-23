package com.neteast.videotv.fragment;

import android.os.Bundle;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.neteast.videotv.R;

/**
 * Created with IntelliJ IDEA.
 * User: emellend
 * Date: 13-8-4
 * Time: 下午3:47
 * To change this template use File | Settings | File Templates.
 */
@EFragment(R.layout.frag_description)
public class DescriptionDialog extends TVDialog {

    @ViewById(R.id.text)TextView mDescriptionView;

    private String mDescription;

    public static final DescriptionDialog_ newDialog(String description){
        DescriptionDialog_ dialog=new DescriptionDialog_();
        Bundle args=new Bundle();
        args.putString("description",description);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDescription = getArguments().getString("description");
    }

    @AfterViews
    void initUI(){
        mDescriptionView.setText(mDescription);
    }
}
